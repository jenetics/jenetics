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

import io.jenetics.internal.util.Requires;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record YatesChiSquared(double pValue) implements ChiSquared {

	/**
	 * Create a new Pearson's chi-squared tester.
	 *
	 * @param pValue the p-value used for the hypothesis tester
	 */
	public YatesChiSquared {
		Requires.probability(pValue);
	}

	@Override
	public double chiSquared(
		final Histogram observation,
		final Distribution hypothesis
	) {
		requireNonNull(observation);
		requireNonNull(hypothesis);

		final var count = observation.samples();
		if (count == 0) {
			return Double.POSITIVE_INFINITY;
		}

		final var cdf = hypothesis.cdf();

		return observation.buckets().stream()
			.mapToDouble(bucket -> {
				final var e = cdf.probability(bucket.interval());
				final var o = (double)bucket.count()/count;
				return Math.pow(Math.abs(o - e) - 0.5, 2)/e;
			})
			.sum();
	}
}
