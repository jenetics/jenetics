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

import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;

import io.jenetics.distassert.observation.Histogram.Partition;

/**
 * An observation contains of a {@link Histogram} and the descriptive
 * {@link Statistics} values of the samples.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Observation {

	/**
	 * Return the histogram of the observation.
	 *
	 * @return the histogram of the observation
	 */
	Histogram histogram();

	/**
	 * Return the statics of the sample values.
	 *
	 * @return the statistics of the sample values
	 */
	Statistics statistics();

	/**
	 * Create a new observation object from the given input.
	 *
	 * @param histogram the histogram of the sample points
	 * @param statistics descriptive statistic values of the sample points
	 * @return a new observation object
	 */
	static Observation of(
		final Histogram histogram,
		final Statistics statistics
	) {
		record SimpleObservation(Histogram histogram, Statistics statistics)
			implements Observation
		{
			public SimpleObservation {
				requireNonNull(histogram);
				requireNonNull(statistics);
			}
		}

		return new SimpleObservation(histogram, statistics);
	}

	/**
	 * Create a new sampling observation task.
	 *
	 * @param sampling the sampling function
	 * @param partition the partition used for the created histogram
	 */
	static Observation of(final Sampling sampling, final Partition partition) {
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

		return Observation.of(histogram, statistics);
	}

}
