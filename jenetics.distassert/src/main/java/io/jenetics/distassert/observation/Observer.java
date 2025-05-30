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
package io.jenetics.distassert.observation;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import io.jenetics.distassert.observation.Histogram.Partition;

/**
 * An observer runs a sampler with a given {@link Executor}, which allows
 * creating reproducible observations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Observer {

	private final Executor executor;

	private Observer(final Executor executor) {
		this.executor = requireNonNull(executor);
	}

	/**
	 * Executes the given {@code sampler} with the observers {@link Executor}.
	 *
	 * @param sampler the sampler to be executed
	 * @return return the sampler observation
	 */
	public Observation observe(final Sampler sampler) {
		final var future = new FutureTask<>(sampler);
		executor.execute(future);
		try {
			return future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CancellationException(e.getMessage());
		} catch (ExecutionException e) {
			throw switch (e.getCause()) {
				case RuntimeException rte -> rte;
				case Throwable t -> new IllegalStateException(
					"Unexpected exception", t
				);
			};
		}
	}

	/**
	 * Executes the given {@code sampling} with the observers {@link Executor}.
	 *
	 * @param sample the sampling data
	 * @param partition the histogram partition to be used for the sampling.
	 * @return the sampler observation
	 */
	public Observation observe(final Sample sample, final Partition partition) {
		return observe(new Sampler(sample, partition));
	}

	/**
	 * Create a new observer using the given {@code executor}. The sampler will
	 * be run on the given executor.
	 *
	 * @param executor the executor used for running the sampler.
	 * @return a new observer using the given {@code executor}
	 */
	public static Observer using(Executor executor) {
		return new Observer(executor);
	}

}
