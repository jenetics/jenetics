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
package io.jenetics.example.timeseries;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionParams;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.FitnessNullifier;

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
	private final AtomicBoolean _proceed = new AtomicBoolean(true);

	private final Codec<Tree<Op<T>, ?>, ProgramGene<T>> _codec;
	private final Error<T> _error;
	private final EvolutionParams<ProgramGene<T>, Double> _params;

	private final SampleBuffer<T> _samples = new SampleBuffer<>(50);
	private final FitnessNullifier<ProgramGene<T>, Double> _nullifier = new FitnessNullifier<>();

	private final Regression<T> _regression;
	private final Engine<ProgramGene<T>, Double> _engine;

	private Stream<? extends Tree<Op<T>, ?>> _stream;
	private Thread _thread;

	Flow.Subscription subscription;

	TimeSeriesProcessor(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final EvolutionParams<ProgramGene<T>, Double> params,
		final Executor executor,
		final int maxBufferCapacity
	) {
		super(executor, maxBufferCapacity);
		_codec = requireNonNull(codec);
		_error = requireNonNull(error);
		_params = requireNonNull(params);

		_regression = Regression.of(_codec, _error, _samples);

		_engine = Engine
			.builder(_regression)
			.evolutionParams(_params)
			.interceptor(_nullifier)
			.minimizing()
			.build();
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
		(this.subscription = subscription).request(1);
	}

	@Override
	public void onNext(final Sample<T> item) {
		_samples.add(item);
		_nullifier.nullifyFitness();
		subscription.request(1);
		//submit(function.apply(item));
	}

	private void start() {
		synchronized (_lock) {
			if (_stream != null) {
				throw new IllegalStateException(
					"Already attached evolution stream."
				);
			}

			_stream = _engine.stream()
				.limit(e -> _proceed.get())
				.map(EvolutionResult::bestPhenotype)
				.map(Phenotype::genotype)
				.map(Genotype::gene);

			_thread = new Thread(() -> {
				try {
					_stream.forEach(this::submit);
					close();
				} catch(CancellationException e) {
					Thread.currentThread().interrupt();
					close();
				} catch (Throwable e) {
					closeExceptionally(e);
				}
			});
			_thread.start();
		}
	}

	@Override
	public void onError(final Throwable throwable) {
		closeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		close();
	}

	/**
	 * Unless already closed, issues {@code onComplete} signals to current
	 * subscribers, and disallows subsequent attempts to publish. Upon return,
	 * this method does NOT guarantee that all subscribers have yet completed.
	 */
	@Override
	public void close() {
		synchronized (_lock) {
			_proceed.set(false);
			if (_thread != null) {
				_thread.interrupt();
			}
		}
		super.close();
	}

}
