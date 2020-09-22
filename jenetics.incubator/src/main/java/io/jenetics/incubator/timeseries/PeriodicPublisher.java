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
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Supplier;

import io.jenetics.prog.regression.Sample;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PeriodicPublisher<T>
	extends SubmissionPublisher<List<? extends Sample<T>>>
{

	private final Object _lock = new Object() {};

	private final ScheduledExecutorService _scheduler;
	private final Supplier<? extends List<? extends Sample<T>>> _supplier;
	private final Duration _period;

	private ScheduledFuture<?> _task;

	/**
	 * Create a new sample publisher with the given parameters.
	 *
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's buffer
	 *       (the enforced capacity may be rounded up to the nearest power of
	 *       two and/or bounded by the largest value supported by this
	 *       implementation; method {@link SubmissionPublisher#getMaxBufferCapacity()}
	 *       returns the actual value)
	 * @param scheduler the scheduling executor service used for periodically
	 *        fetching the sample data
	 * @param supplier the supplier for the sample data.
	 * @param period the time period between two sample data fetches
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code maxBufferCapacity} not positive
	 */
	public PeriodicPublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final ScheduledExecutorService scheduler,
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		super(executor, maxBufferCapacity);

		_scheduler = requireNonNull(scheduler);
		_supplier = requireNonNull(supplier);
		_period = requireNonNull(period);
	}

	/**
	 * Create a new sample publisher with the given parameters.
	 *
	 * @param executor the executor to use for async delivery, supporting
	 *        creation of at least one independent thread
	 * @param maxBufferCapacity the maximum capacity for each subscriber's buffer
	 *       (the enforced capacity may be rounded up to the nearest power of
	 *       two and/or bounded by the largest value supported by this
	 *       implementation; method {@link SubmissionPublisher#getMaxBufferCapacity()}
	 *       returns the actual value)
	 * @param supplier the supplier for the sample data.
	 * @param period the time period between two sample data fetches
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if {@code maxBufferCapacity} not positive
	 */
	public PeriodicPublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		this(
			executor,
			maxBufferCapacity,
			new ScheduledThreadPoolExecutor(1),
			supplier,
			period
		);
	}

	/**
	 * Create a new sample publisher with the given parameters.
	 *
	 * @param scheduler the scheduling executor service used for periodically
	 *        fetching the sample data
	 * @param supplier the supplier for the sample data.
	 * @param period the time period between two sample data fetches
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public PeriodicPublisher(
		final ScheduledExecutorService scheduler,
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		this(
			ForkJoinPool.commonPool(),
			Flow.defaultBufferSize(),
			scheduler,
			supplier,
			period
		);
	}

	/**
	 * Create a new sample publisher with the given parameters.
	 *
	 * @param supplier the supplier for the sample data.
	 * @param period the time period between two sample data fetches
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public PeriodicPublisher(
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		this(
			ForkJoinPool.commonPool(),
			Flow.defaultBufferSize(),
			null,
			supplier,
			period
		);
	}

	/**
	 * Start the sample point production with the defined period.
	 *
	 * @throws IllegalStateException if {@code this} <em>publisher</em> has
	 *         already been started
	 * @throws RejectedExecutionException if {@code this} <em>publisher</em>
	 *         has already been closed
	 */
	public void start() {
		synchronized (_lock) {
			if (_task != null) {
				throw new IllegalStateException("Publisher already started.");
			}

			_task = _scheduler.scheduleAtFixedRate(
				() -> submit(_supplier.get()),
				0,
				_period.toMillis(),
				MILLISECONDS
			);
		}
	}

	/**
	 * Stops the sample point production. Once stopped it is possible to
	 * re-start the sample point production by calling {@link #start()}.
	 *
	 * @param mayInterruptIfRunning {@code true} if the thread producing the
	 *        samples should be interrupted; otherwise, in-progress sample
	 *        production are allowed to complete
	 */
	public void stop(final boolean mayInterruptIfRunning) {
		synchronized (_lock) {
			if (_task != null) {
				_task.cancel(mayInterruptIfRunning);
				_task = null;
			}
		}
	}

	/**
	 * Stop the sample point production and let the currently running production
	 * task complete. This call is equivalent to {@code stop(false)}. Once
	 * stopped it is possible to re-start the sample point production by calling
	 * {@link #start()}.
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Closes the sample point publisher. Once closed, it is no longer possible
	 * to start it again.
	 */
	@Override
	public void close() {
		stop(true);
		_scheduler.shutdown();
		super.close();
	}

}
