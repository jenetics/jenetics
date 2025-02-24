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
package io.jenetics.incubator.stat;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import io.jenetics.incubator.stat.Histogram.Partition;

/**
 * Task object for evaluating a histogram from a given sampling function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObservationTask implements Observation {
	private final Sampling sampling;
	private final Partition partition;
	private final Executor executor;

	private Histogram histogram;

	/**
	 * Create a new sampling observation task.
	 *
	 * @param sampling the sampling function
	 * @param partition the partition used for the created histogram
	 * @param executor the executor used for creating the histogram
	 */
	public ObservationTask(
		final Sampling sampling,
		final Partition partition,
		final Executor executor
	) {
		this.sampling = requireNonNull(sampling);
		this.partition = requireNonNull(partition);
		this.executor = requireNonNull(executor);
	}

	/**
	 * Create a new sampling observation task.
	 *
	 * @param sampling the sampling function
	 * @param partition the partition used for the created histogram
	 */
	public ObservationTask(final Sampling sampling, final Partition partition) {
		this(sampling, partition, Runnable::run);
	}

	@Override
	public synchronized Histogram histogram() {
		if (histogram == null) {
			try {
				histogram = CompletableFuture
					.supplyAsync(this::build, executor)
					.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CancellationException(e.getMessage());
			} catch (ExecutionException e) {
				switch (e.getCause()) {
					case RuntimeException rte -> throw rte;
					case Error err -> throw err;
					case Exception exp -> throw new IllegalStateException(exp);
					case null -> throw new RuntimeException(e);
					default -> throw new RuntimeException(e.getCause());
				}
			}
		}

		return histogram;
	}

	private Histogram build() {
		return new Histogram.Builder(partition).build(sampling);
	}

}
