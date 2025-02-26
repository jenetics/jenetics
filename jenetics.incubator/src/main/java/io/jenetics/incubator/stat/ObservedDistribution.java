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

import java.util.Arrays;

import io.jenetics.incubator.stat.Histogram.Buckets;
import io.jenetics.incubator.stat.Histogram.Partition;
import io.jenetics.internal.math.DoubleAdder;

/**
 * Distribution object, based on an observation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObservedDistribution implements Distribution {
	private final Histogram observation;
	private final long samples;

	public ObservedDistribution(final Histogram observation) {
		this.observation = requireNonNull(observation);
		this.samples = observation.samples();
	}

	@Override
	public Cdf cdf() {
		return value -> {
			final int index = observation.partition().indexOf(value);
			if (index < 0) {
				return 0;
			}
			if (index >= observation.partition().size()) {
				return 1;
			}

			final double base = observation.buckets().stream()
				.limit(index)
				.mapToLong(Histogram.Bucket::count)
				.sum();

			final var bucket = observation.buckets().get(index);
			final var itv = bucket.interval();
			final var rest = (value - itv.min())/
				(itv.max() - itv.min())*bucket.count();

			return (base + rest)/samples;
		};
	}

	@Override
	public Pdf pdf() {
		return value -> {
			final int index = observation.partition().indexOf(value);
			if (index < 0 || index >= observation.partition().size()) {
				return 0;
			}

			return ((double)observation.buckets().get(index).count())/samples;
		};
	}

	public static ObservedDistribution of(
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
			if (value < 0) {
				throw new IllegalArgumentException(
					"Expected values must be non-negative: %s."
						.formatted(Arrays.toString(expected)));
			}
		}

		final var sum = DoubleAdder.sum(expected);
		final var normalized = expected.clone();
		for (int i = 0; i < normalized.length; ++i) {
			normalized[i] /= sum;
		}

		final var frequencies = Arrays.stream(normalized)
			.mapToLong(v -> (long)(v*Integer.MAX_VALUE))
			.toArray();
		final var histogram = new Histogram(new Buckets(partition, frequencies));


		return new ObservedDistribution(histogram);
	}

	public static ObservedDistribution of(
		final Interval interval,
		final double[] expected
	) {
		return of(Partition.of(interval, expected.length), expected);
	}

}
