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
 * Implements the Pearson's chi-squared test.
 * <blockquote>
 * Pearson's chi-squared test is a statistical test applied to sets of
 * categorical data to evaluate how likely it is that any observed difference
 * between the sets arose by chance. It is the most widely used of many
 * chi-squared tests (e.g., Yates, likelihood ratio, portmanteau test in time
 * series, etc.) – statistical procedures whose results are evaluated by
 * reference to the chi-squared distribution. Its properties were first
 * investigated by Karl Pearson in 1900. In contexts where it is important to
 * improve a distinction between the test statistic and its distribution, names
 * similar to Pearson χ-squared test or statistic are used.
 * <em>Wikipedia: <a
 * href="https://en.wikipedia.org/wiki/Pearson%27s_chi-squared_test">
 * Pearson's chi-squared test</a></em>
 * </blockquote>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Pearson%27s_chi-squared_test">
 *     Wikipedia: Pearson's chi-squared test</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.2
 * @since 8.2
 */
public record PearsonsChiSquared(double pValue) implements ChiSquared {

	/**
	 * A Pearson's chi-squared tester with the most commonly used p-value of
	 * <em>0.05</em>.
	 */
	public static final PearsonsChiSquared P0_05 = new PearsonsChiSquared(0.05);

	/**
	 * A Pearson's chi-squared tester with a p-value of <em>0.01</em>
	 */
	public static final PearsonsChiSquared P0_01 = new PearsonsChiSquared(0.01);


	/**
	 * Create a new Pearson's chi-squared tester.
	 *
	 * @param pValue the p-value used for the hypothesis tester
	 */
	public PearsonsChiSquared {
		Requires.probability(pValue);
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
				return e > epsilon || o > 1 ? sqr(o - e)/e : 0;
			})
			.sum();

		return Double.isFinite(result) ? result : Double.POSITIVE_INFINITY;
	}

	static double sqr(final double x) {
		return x*x;
	}

}
