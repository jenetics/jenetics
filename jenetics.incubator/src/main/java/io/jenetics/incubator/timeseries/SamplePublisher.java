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
public final class SamplePublisher<T>
	extends SubmissionPublisher<List<? extends Sample<T>>>
{

	private final ScheduledFuture<?> _task;
	private final ScheduledExecutorService _scheduler;

	public SamplePublisher(
		final Executor executor,
		final int maxBufferCapacity,
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		super(executor, maxBufferCapacity);
		requireNonNull(supplier);
		requireNonNull(period);

		_scheduler = new ScheduledThreadPoolExecutor(1);
		_task = _scheduler.scheduleAtFixedRate(
			() -> submit(supplier.get()),
			0,
			period.toMillis(),
			MILLISECONDS
		);
	}

	public SamplePublisher(
		final Supplier<? extends List<? extends Sample<T>>> supplier,
		final Duration period
	) {
		this(
			ForkJoinPool.commonPool(),
			Flow.defaultBufferSize(),
			supplier,
			period
		);
	}

	public void close() {
		_task.cancel(false);
		_scheduler.shutdown();
		super.close();
	}
}
