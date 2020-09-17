/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.incubator.timeseries;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionParams;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.FitnessNullifier;
import io.jenetics.util.Streams;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.SampleBuffer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class TimeSeriesProcessor<T> extends SubmissionPublisher<Tree<Op<T>, ?>>
	implements Flow.Processor<Sample<T>, Tree<Op<T>, ?>>
{
	private final Object _lock = new Object(){};

	private final SampleBuffer<T> _samples = new SampleBuffer<>(50);
	private final FitnessNullifier<ProgramGene<T>, Double> _nullifier = new FitnessNullifier<>();
	private final Engine<ProgramGene<T>, Double> _engine;

	private ElementSubmitter<EvolutionResult<ProgramGene<T>, Double>> _submitter;
	private Thread _thread;

	private Flow.Subscription _subscription;

	TimeSeriesProcessor(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final EvolutionParams<ProgramGene<T>, Double> params,
		final Executor executor,
		final int maxBufferCapacity
	) {
		super(executor, maxBufferCapacity);

		_engine = Engine
			.builder(Regression.of(codec, error, _samples))
			.evolutionParams(params)
			.interceptor(_nullifier)
			.minimizing()
			.build();
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
		(this._subscription = subscription).request(1);
	}

	@Override
	public void onNext(final Sample<T> item) {
		synchronized (_lock) {
			if (_submitter != null) {
				_samples.add(item);
				publish();
			}
		}
		_subscription.request(1);
	}

	private void doSubmit(final EvolutionResult<ProgramGene<T>, Double> result) {
		final Tree<Op<T>, ?> item = result.bestPhenotype().genotype().gene();
		submit(item);
	}

	@Override
	public void onError(final Throwable throwable) {
		closeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		close();
	}

	private void publish() {
		_samples.publish();
		_nullifier.nullifyFitness();
		_submitter.reset();
	}

	private void start() {
		synchronized (_lock) {
			if (_submitter != null) {
				throw new IllegalStateException("Processor already started.");
			}

			_submitter = new ElementSubmitter<>(_engine.stream(), this::doSubmit);
			_thread = new Thread(_submitter);
			_thread.setUncaughtExceptionHandler((thread, throwable) ->
				closeExceptionally(throwable)
			);
			_thread.start();
		}
	}

	/**
	 * Unless already closed, issues {@code onComplete} signals to current
	 * subscribers, and disallows subsequent attempts to publish. Upon return,
	 * this method does NOT guarantee that all subscribers have yet completed.
	 */
	@Override
	public void close() {
		synchronized (_lock) {
			if (_submitter != null) {
				_submitter.stop();
				_thread.interrupt();
			}
			_submitter = null;
			_thread = null;
		}
		super.close();
	}

}

final class ElementSubmitter<T extends Comparable<? super T>>
	implements Runnable
{

	private final Stream<T> _stream;
	private final Consumer<? super T> _sink;

	private final AtomicBoolean _reset = new AtomicBoolean(false);
	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	ElementSubmitter(
		final Stream<T> stream,
		final Consumer<? super T> sink
	) {
		_stream = requireNonNull(stream);
		_sink = requireNonNull(sink);
	}

	@Override
	public void run() {
		_stream
			.takeWhile(e -> _proceed.get())
			.flatMap(Streams.<T>toStrictlyDecreasing(this::reset))
			.forEach(_sink);
	}

	private boolean reset(final T element) {
		return _reset.getAndSet(false);
	}

	void reset() {
		_reset.set(true);
	}

	void stop() {
		_proceed.set(false);
	}

}
