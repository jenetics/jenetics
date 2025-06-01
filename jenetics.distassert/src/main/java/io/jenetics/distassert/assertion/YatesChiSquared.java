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
package io.jenetics.distassert.assertion;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.distassert.assertion.PearsonsChiSquared.sqr;

import io.jenetics.distassert.distribution.Distribution;
import io.jenetics.distassert.observation.Histogram;

/**
 * Implements the Yates's chi-squared test.
 * <blockquote>
 * In statistics, Yates's correction for continuity (or Yates's chi-squared test)
 * is used in certain situations when testing for independence in a contingency
 * table. It aims at correcting the error introduced by assuming that the
 * discrete probabilities of frequencies in the table can be approximated by a
 * continuous distribution (chi-squared). Unlike the standard Pearson chi-squared
 * statistic, it is approximately unbiased.
 * <br>
 * The effect of Yates's correction is to prevent overestimation of statistical
 * significance for small data. This formula is chiefly used when at least one
 * cell of the table has an expected count smaller than 5.
 * <em>Wikipedia: <a
 * href="https://en.wikipedia.org/wiki/Yates%27s_correction_for_continuity">
 * Yates's correction for continuity</a></em>
 * </blockquote>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Yates%27s_correction_for_continuity">
 *     Wikipedia: Yates's correction for continuity</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record YatesChiSquared(double pValue) implements ChiSquared {

	/**
	 * A Yates's chi-squared tester with the most commonly used p-value of
	 * <em>0.05</em>.
	 */
	public static final YatesChiSquared P0_05 = new YatesChiSquared(0.05);

	/**
	 * A Yates's chi-squared tester with a p-value of <em>0.01</em>
	 */
	public static final YatesChiSquared P0_01 = new YatesChiSquared(0.01);

	/**
	 * Create a new Yates's chi-squared tester.
	 *
	 * @param pValue the p-value used for the hypothesis tester
	 */
	public YatesChiSquared {
		if (pValue < 0.0 || pValue > 1.0) {
			throw new IllegalArgumentException(format(
				"The given p-value is not in the range [0, 1]: %f", pValue
			));
		}
	}

	@Override
	public double chiSquared(
		final Histogram observation,
		final Distribution hypothesis
	) {
		requireNonNull(observation);
		requireNonNull(hypothesis);

		final var samples = observation.samples();
		if (samples == 0) {
			return Double.POSITIVE_INFINITY;
		}

		final double epsilon = 1.0/samples;
		final var cdf = hypothesis.cdf();

		final var result = observation.buckets().stream()
			.mapToDouble(bucket -> {
				final var e = cdf.probability(bucket.interval())*samples;
				final var o = bucket.count();
				return e > epsilon || o > 1 ? sqr(Math.abs(o - e) - 0.5)/e : 0;
			})
			.sum();

		return Double.isFinite(result) ? result : Double.POSITIVE_INFINITY;
	}
}
