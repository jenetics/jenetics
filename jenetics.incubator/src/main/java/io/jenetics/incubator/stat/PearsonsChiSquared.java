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
 * investigated by Karl Pearson in 1900.[1] In contexts where it is important to
 * improve a distinction between the test statistic and its distribution, names
 * similar to Pearson χ-squared test or statistic are used.
 * <em>Wikipedia: <a
 * href="https://en.wikipedia.org/wiki/Pearson%27s_chi-squared_test">
 * Pearson's chi-squared test</a></em>
 * </blockquote>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
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

		final var count = observation.samples();
		if (count == 0) {
			return Double.POSITIVE_INFINITY;
		}

		final var cdf = hypothesis.cdf();

		final var chi2 = observation.buckets().stream()
			.mapToDouble(bucket -> {
				final double a = bucket.count()*bucket.count();
				final double b = cdf.probability(bucket.interval())*count;
				return a/b;
			})
			.sum();

		return chi2 - count;
	}

}
