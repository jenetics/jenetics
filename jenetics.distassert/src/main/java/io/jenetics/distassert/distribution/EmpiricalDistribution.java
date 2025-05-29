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
package io.jenetics.distassert.distribution;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

import org.apache.commons.numbers.core.Sum;

import io.jenetics.distassert.observation.Histogram;
import io.jenetics.distassert.observation.Histogram.Buckets;
import io.jenetics.distassert.observation.Histogram.Partition;
import io.jenetics.distassert.observation.Interval;

/**
 * Distribution object, based on an observation. The observation is defined by
 * the {@link Histogram} of the distribution (quantized PDF).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class EmpiricalDistribution implements Distribution {

	private static final long MAX_SAMPLES = Long.MAX_VALUE/2;

	private final Histogram observation;

	private final long samples;
	private final double[] sums;

	/**
	 * Create a new distribution object from the given {@code observation}.
	 *
	 * @param observation the observed distribution as histogram
	 */
	public EmpiricalDistribution(final Histogram observation) {
		this.observation = requireNonNull(observation);
		this.samples = observation.samples();

		final var buckets = observation.buckets();
		this.sums = new double[buckets.size()];
		double sum = 0;
		for (int i = 0; i < buckets.size(); ++i) {
			this.sums[i] = sum;
			sum += buckets.get(i).count();
		}
	}

	/**
	 * Return the observed distribution.
	 *
	 * @return the observed distribution
	 */
	public Histogram observation() {
		return observation;
	}

	@Override
	public Cdf cdf() {
		return x -> {
			final int index = observation.partition().indexOf(x);
			if (index < 0) {
				return 0;
			}
			if (index >= observation.partition().size()) {
				return 1;
			}

			final var bucket = observation.buckets().get(index);
			final var interval = bucket.interval();
			final var rest = (x - interval.min())/(interval.size())*bucket.count();

			return (sums[index] + rest)/samples;
		};
	}

	@Override
	public Pdf pdf() {
		return x -> {
			final int index = observation.partition().indexOf(x);
			if (index < 0 || index >= observation.partition().size()) {
				return 0;
			}

			return ((double)observation.buckets().get(index).count())/samples;
		};
	}

	@Override
	public int hashCode() {
		return observation.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EmpiricalDistribution dist &&
			dist.observation.equals(observation);
	}

	@Override
	public String toString() {
		return "ObservedDistribution[observation=%s]".formatted(observation);
	}

	/**
	 * Create a new <em>observed</em> distribution object for the given
	 * {@code partition} and the {@code expected} frequencies.
	 *
	 * @param partition the partition of the observation
	 * @param expected the observed frequencies
	 * @return a newly created distribution object
	 * @throws IllegalArgumentException if {@code partition.size() != expected.length}
	 *         or the frequencies are not {@link Double#isInfinite(double)} or
	 *         smaller than zero
	 */
	public static EmpiricalDistribution of(
		final Partition partition,
		final double[] expected
	) {
		if (partition.size() != expected.length) {
			throw new IllegalArgumentException(
				"Different partition and expected values size: %s != %s"
					.formatted(partition.size(), expected.length)
			);
		}
		for (var value : expected) {
			if (!Double.isFinite(value)) {
				throw new IllegalArgumentException(
					"Expected values must be finite: %s."
						.formatted(Arrays.toString(expected)));
			}
			if (value < 0) {
				throw new IllegalArgumentException(
					"Expected values must be non-negative: %s."
						.formatted(Arrays.toString(expected)));
			}
		}

		final var sum = Sum.of(expected).getAsDouble();
		final var normalized = expected.clone();
		for (int i = 0; i < normalized.length; ++i) {
			normalized[i] /= sum;
		}

		final var frequencies = Arrays.stream(normalized)
			.mapToLong(v -> (long)(v*MAX_SAMPLES))
			.toArray();
		final var histogram = new Histogram(new Buckets(partition, frequencies));

		return new EmpiricalDistribution(histogram);
	}

	/**
	 * Create a new <em>observed</em> distribution object for the given
	 * {@code partition} and the {@code expected} frequencies.
	 *
	 * @param partition the histogram partition
	 * @param frequencies the histogram frequencies
	 * @return a newly created distribution object
	 * @throws IllegalArgumentException if the partition size and the length
	 *         of the frequency array are not equal, or if any of the
	 *         frequencies is negative
	 */
	public static EmpiricalDistribution of(
		final Partition partition,
		final long[] frequencies
	) {
		final var buckets = new Buckets(partition, frequencies);
		final var histogram = new Histogram(buckets);
		return new EmpiricalDistribution(histogram);
	}

	/**
	 * Create a new distribution within the given {@code interval} and the
	 * {@code expected} frequencies within the interval. The number of
	 * subintervals is determined by the length of the frequency array.
	 *
	 * @param interval the overall distribution interval
	 * @param expected the expected frequencies
	 * @return a new distribution object
	 */
	public static EmpiricalDistribution of(
		final Interval interval,
		final double[] expected
	) {
		return of(Partition.of(interval, expected.length), expected);
	}

}
