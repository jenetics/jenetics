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

import java.util.concurrent.Callable;

import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;

import io.jenetics.distassert.observation.Histogram.Partition;

/**
 * Combines a data sampling with the partitioning of the observed data.
 *
 * @param sampling the sampling data
 * @param partition the partitioning of the observation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Sampler(Sampling sampling, Partition partition)
	implements Callable<Observation>
{
	public Sampler {
		requireNonNull(sampling);
		requireNonNull(partition);
	}

	@Override
	public Observation call() {
		final var summary = DoubleStatistics.of(
			Statistic.MIN,
			Statistic.MAX,
			Statistic.MEAN,
			Statistic.SUM,
			Statistic.VARIANCE
		);

		final var histogram = new Histogram.Builder(partition)
			.observer(summary)
			.accept(sampling)
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
	}

	public static Observation observe(final Sampling sampling, final Partition partition) {
		return new Sampler(sampling, partition).call();
	}

}
