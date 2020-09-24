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

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionInterceptor;
import io.jenetics.engine.EvolutionParams;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStart;
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
public final class ReactiveRegression<T> extends SubmissionPublisher<RegressionResult<T>>
	implements Flow.Processor<List<? extends Sample<T>>, RegressionResult<T>>
{
	private final Object _lock = new Object() {};

	private final SampleBuffer<T> _samples;
	private final FitnessNullifier<ProgramGene<T>, Double> _nullifier;
	private final Engine<ProgramGene<T>, Double> _engine;

	private EvolutionRunner<EvolutionResult<ProgramGene<T>, Double>> _submitter;
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
		_nullifier = new FitnessNullifier<>();

		_engine = Engine
			.builder(Regression.of(codec, error, _samples))
			.evolutionParams(params)
			.interceptor(_nullifier)
			.minimizing()
			.build();
	}

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
		synchronized (_lock) {
			if (_submitter != null) {
				_samples.addAll(samples);
				publish();
			}
		}
		_subscription.request(1);
	}

	private void doSubmit(final EvolutionResult<ProgramGene<T>, Double> result) {
		final var bpt = result.bestPhenotype();
		submit(
			new RegressionResult<>(
				bpt.genotype().gene(),
				bpt.fitness(),
				result.generation()
			)
		);
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
		_submitter.lock();
		try {
			_samples.publish();
			_nullifier.nullifyFitness();
			_submitter.reset();
		} finally {
			_submitter.unlock();
		}
	}

	private void start() {
		synchronized (_lock) {
			if (_submitter != null) {
				throw new IllegalStateException("Processor already started.");
			}

			_submitter = new EvolutionRunner<>(_engine.stream(), this::doSubmit);
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


	/**
	 * This class takes a stream and submits the currently best element to the also
	 * given element sink {@code Consumer<T>}.
	 *
	 * @param <T> the element type
	 */
	private static final class EvolutionRunner<T extends Comparable<? super T>>
		implements
			EvolutionInterceptor<ProgramGene<T>, Double>,
			Runnable
	{

		private final Stream<? extends T> _stream;
		private final Consumer<? super T> _sink;

		private final AtomicBoolean _reset = new AtomicBoolean(false);
		private final AtomicBoolean _proceed = new AtomicBoolean(true);
		private final Lock _lock = new ReentrantLock();

		private T _best;

		EvolutionRunner(
			final Stream<? extends T> stream,
			final Consumer<? super T> sink
		) {
			_stream = requireNonNull(stream);
			_sink = requireNonNull(sink);
		}

		@Override
		public void run() {
			_stream
				.takeWhile(e -> _proceed.get())
				.forEach(this::submit);
		}

		private void submit(final T result) {
			if (_reset.getAndSet(false)) {
				_best = null;
			}

			final T best = min(_best, result);
			if (best == _best) {
				// nicht besser
			} else {
				// besser;
			}

			_best = best;
		}

		private T min(final T a, final T b) {
			if (a == null && b == null) return null;
			if (a == null) return b;
			if (b == null) return a;
			return a.compareTo(b) <= 0 ? a : b;
		}

		void lock() {
			_lock.lock();
		}

		void unlock() {
			_lock.unlock();
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

		@Override
		public EvolutionStart<ProgramGene<T>, Double>
		before(final EvolutionStart<ProgramGene<T>, Double> start) {
			return start;
		}

		@Override
		public EvolutionResult<ProgramGene<T>, Double>
		after(final EvolutionResult<ProgramGene<T>, Double> result) {
			return result;
		}

	}

}
