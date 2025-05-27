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

import java.util.NoSuchElementException;

import org.apache.commons.statistics.descriptive.DoubleStatistics;
import org.apache.commons.statistics.descriptive.Statistic;

import io.jenetics.distassert.observation.Histogram.Partition;

/**
 * This object allows evaluating a given sampling within a given execution
 * environment, which might be useful for creating reproducible results.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class RunnableObservation implements Runnable, Observation {
	private final Sampling sampling;
	private final Partition partition;

	private Histogram histogram;
	private Statistics statistics;
	private volatile boolean evaluated = false;

	/**
	 * Create a new sampling observation task.
	 *
	 * @param sampling the sampling function
	 * @param partition the partition used for the created histogram
	 */
	public RunnableObservation(final Sampling sampling, final Partition partition) {
		this.sampling = requireNonNull(sampling);
		this.partition = requireNonNull(partition);
	}

	@Override
	public void run() {
		if (!evaluated) {
			evaluate();
		}
	}

	private synchronized void evaluate() {
		final var summary = DoubleStatistics.of(
			Statistic.MIN,
			Statistic.MAX,
			Statistic.MEAN,
			Statistic.SUM,
			Statistic.VARIANCE
		);

		histogram = new Histogram.Builder(partition)
			.observer(summary)
			.accept(sampling)
			.build();

		statistics = new Statistics(
			summary.getCount(),
			summary.getAsDouble(Statistic.MIN),
			summary.getAsDouble(Statistic.MAX),
			summary.getAsDouble(Statistic.SUM),
			summary.getAsDouble(Statistic.MEAN),
			summary.getAsDouble(Statistic.VARIANCE)
		);

		evaluated = true;
	}

	@Override
	public synchronized Histogram histogram() {
		if (!evaluated) {
			throw new NoSuchElementException();
		}
		return histogram;
	}

	@Override
	public Statistics statistics() {
		return statistics;
	}

}
