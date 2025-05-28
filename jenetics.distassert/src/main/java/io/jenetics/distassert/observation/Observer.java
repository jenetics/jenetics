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

import io.jenetics.distassert.observation.Histogram.Partition;
import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import static java.util.Objects.requireNonNull;

/**
 * An observer is responsible for creating an observation from an observable
 * class.
 */
@FunctionalInterface
public interface Observer {

	Observer DEFAULT = using(Runnable::run);

	Observation observe(final Observable observable);

	default Observation observe(final Sampling sampling, final Partition partition) {
		return observe(new Observable(sampling, partition));
	}

	static Observer using(Executor executor) {
		requireNonNull(executor);

		return observable -> {
			requireNonNull(observable);

			final Callable<Observation> task = () -> {
				final var summary = DoubleStatistics.of(
					Statistic.MIN,
					Statistic.MAX,
					Statistic.MEAN,
					Statistic.SUM,
					Statistic.VARIANCE
				);

				final var histogram = new Histogram.Builder(observable.partition())
					.observer(summary)
					.accept(observable.sampling())
					.build();

				final var statistics = new Statistics(
					summary.getCount(),
					summary.getAsDouble(Statistic.MIN),
					summary.getAsDouble(Statistic.MAX),
					summary.getAsDouble(Statistic.SUM),
					summary.getAsDouble(Statistic.MEAN),
					summary.getAsDouble(Statistic.VARIANCE)
				);

				return new Observation(histogram, statistics);
			};

			final var future = new FutureTask<>(task);
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
		};
	}

}
