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

import org.apache.commons.statistics.distribution.ChiSquaredDistribution;

import io.jenetics.incubator.stat.Histogram.Bucket;
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
 * investigated by Karl Pearson in 1900.[1] In contexts where it is important
 * to improve a distinction between the test statistic and its distribution,
 * names similar to Pearson χ-squared test or statistic are used.
 * <em>Wikipedia: <a href="https://en.wikipedia.org/wiki/Pearson%27s_chi-squared_test">
 *     Pearson's chi-squared test</a></em>
 * </blockquote>
 * This tester takes the p-value as parameter.
 * <blockquote>
 * The p-value is the probability of obtaining test results at least
 * as extreme as the result actually observed, under the assumption that
 * the null hypothesis is correct. A very small p-value means that such
 * an extreme observed outcome would be very unlikely under the null
 * hypothesis. Even though reporting p-values of statistical tests is
 * common practice in academic publications of many quantitative fields,
 * misinterpretation and misuse of p-values is widespread and has been a
 * major topic in mathematics and metascience.
 * <em>Wikipedia: <a href="https://en.wikipedia.org/wiki/P-value">
 *     p-value</a></em>
 * </blockquote>
 *
 * @param p the p-value used for the hypothesis tester
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record PearsonsChiSquared(double p) implements HypothesisTester {

	public static final PearsonsChiSquared P_001 = new PearsonsChiSquared(0.01);

	public static final PearsonsChiSquared P_0001 = new PearsonsChiSquared(0.001);

	/**
	 * Create a new Pearson's chi-squared tester.
	 *
	 * @param p the p-value used for the hypothesis tester
	 */
	public PearsonsChiSquared {
		Requires.probability(p);
	}

	@Override
	public Result test(final Histogram observation, final Distribution hypothesis) {
		final var maxChi2 = maxChi2(observation.degreesOfFreedom());
		final var chi2 = chi2(observation, hypothesis);

		if (chi2 > maxChi2) {
			return new Reject(
				hypothesis,
				observation,
				"Data doesn't follow %s: [max-chi2=%f, chi2=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		} else {
			return new Accept(
				hypothesis,
				observation,
				"Data follows %s: [max-chi2=%f, chi2=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		}
	}

	double chi2(final Histogram observation, final Distribution hypothesis) {
		requireNonNull(observation);

		final var count = observation.samples();
		final var cdf = hypothesis.cdf();

		final var chi2 = observation.buckets().stream()
			.map(bucket -> new double[] {
					bucket.count()*bucket.count(),
					probability(cdf, bucket)*count
				}
			)
			.filter(values -> values[0] != 0.0)
			.mapToDouble(values -> values[0]/values[1])
			.sum();

		return chi2 - count;
	}

	double maxChi2(final int degreesOfFreedom) {
		return ChiSquaredDistribution.of(degreesOfFreedom)
			.inverseCumulativeProbability(1 - p);
	}

	static double probability(final Cdf cdf, final Bucket bucket) {
		final var interval = bucket.interval();
		return cdf.apply(interval.max()) - cdf.apply(interval.min());
	}

}
