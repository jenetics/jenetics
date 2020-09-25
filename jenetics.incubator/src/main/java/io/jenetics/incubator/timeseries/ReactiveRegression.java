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

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;

import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionParams;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.ProgramGene;
import io.jenetics.prog.op.Op;
import io.jenetics.prog.regression.Error;
import io.jenetics.prog.regression.Regression;
import io.jenetics.prog.regression.Sample;
import io.jenetics.prog.regression.SampleBuffer;

/**
 * Implementation of a <em>reactive</em> regression processor. It takes a
 * constant stream of sample points and emits regression analysis result objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ReactiveRegression<T> extends SubmissionPublisher<RegressionResult<T>>
	implements Flow.Processor<List<? extends Sample<T>>, RegressionResult<T>>
{
	private final Object _lock = new Object() {};

	private final RegressionRunner<T> _runner = new RegressionRunner<>();

	private final SampleBuffer<T> _samples;
	private final Engine<ProgramGene<T>, Double> _engine;

	private Thread _thread;
	private Flow.Subscription _subscription;

	/**
	 * Create a new time series processor with the given parameters.
	 *
	 * @param codec the codec used for the regression analyses
	 * @param error the error function used for the regression analyses
	 * @param params the evolution engine parameters
	 * @param sampleBufferSize the buffer size of the time series samples
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's buffer
	 *       (the enforced capacity may be rounded up to the nearest power of
	 *       two and/or bounded by the largest value supported by this
	 *       implementation; method {@link SubmissionPublisher#getMaxBufferCapacity()}
	 *       returns the actual value)
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code maxBufferCapacity} or
	 *         {@code sampleBufferSize} not positive
	 */
	public ReactiveRegression(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final EvolutionParams<ProgramGene<T>, Double> params,
		final int sampleBufferSize,
		final Executor executor,
		final int maxBufferCapacity
	) {
		super(executor, maxBufferCapacity);

		_samples = new SampleBuffer<>(sampleBufferSize);

		_engine = Engine
			.builder(Regression.of(codec, error, _samples))
			.evolutionParams(params)
			.interceptor(_runner)
			.minimizing()
			.build();
	}

	/**
	 * Create a new time series processor with the given parameters.
	 *
	 * @param codec the codec used for the regression analyses
	 * @param error the error function used for the regression analyses
	 * @param params the evolution engine parameters
	 * @param sampleBufferSize the buffer size of the time series samples
	 * @throws IllegalArgumentException if @code sampleBufferSize} not positive
	 */
	public ReactiveRegression(
		final Codec<Tree<Op<T>, ?>, ProgramGene<T>> codec,
		final Error<T> error,
		final EvolutionParams<ProgramGene<T>, Double> params,
		final int sampleBufferSize
	) {
		this(
			codec,
			error,
			params,
			sampleBufferSize,
			ForkJoinPool.commonPool(),
			Flow.defaultBufferSize()
		);
	}

	@Override
	public void onSubscribe(final Subscription subscription) {
		(_subscription = subscription).request(1);
	}

	@Override
	public void onNext(final List<? extends Sample<T>> samples) {
		_samples.addAll(samples);

		synchronized (_lock) {
			if (_thread != null) {
				_runner.onNextGeneration(this::publish);
			}
		}

		_subscription.request(1);
	}

	private void publish() {
		_samples.publish();
		_runner.samples(_samples.samples());
		_runner.reset();
	}

	@Override
	public void onError(final Throwable throwable) {
		closeExceptionally(throwable);
	}

	@Override
	public void onComplete() {
		close();
	}

	public void start() {
		synchronized (_lock) {
			if (_thread != null) {
				throw new IllegalStateException(
					"Regression analysis has been already started."
				);
			}

			_runner.init(_engine.stream(), this::submit);
			_thread = new Thread(_runner);
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
			if (_thread != null) {
				_runner.stop();
				_thread.interrupt();
			}
			_thread = null;
		}
		super.close();
	}

}
