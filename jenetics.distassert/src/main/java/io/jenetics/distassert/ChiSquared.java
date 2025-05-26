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
package io.jenetics.distassert;

/**
 * Common interface for 𝜒<sup>2</sup> hypothesis tester.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Chi-squared_test">
 *     Wikipedia: Chi-squared test</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface ChiSquared extends HypothesisTester {

	@Override
	default Result test(final Histogram observation, final Distribution hypothesis) {
		final var maxChi2 = maxChiSquared(observation.degreesOfFreedom());
		final var chi2 = chiSquared(observation, hypothesis);

		if (chi2 > maxChi2) {
			return new Reject(
				hypothesis, observation,
				"Data doesn't follow %s: [max-\uD835\uDF12²=%f, \uD835\uDF12²=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		} else {
			return new Accept(
				hypothesis, observation,
				"Data follows %s: [max-\uD835\uDF12²=%f, \uD835\uDF12²=%f]."
					.formatted(hypothesis, maxChi2, chi2)
			);
		}
	}

	/**
	 * Return the <em>p-value</em> used by the hypothesis tester.
	 * <blockquote>
	 * The p-value is the probability of obtaining test results at least as extreme
	 * as the result actually observed, under the assumption that the null
	 * hypothesis is correct. A very small p-value means that such an extreme
	 * observed outcome would be very unlikely under the null hypothesis. Even
	 * though reporting p-values of statistical tests is common practice in academic
	 * publications of many quantitative fields, misinterpretation and misuse of
	 * p-values is widespread and has been a major topic in mathematics and
	 * metascience.
	 * <em>Wikipedia: <a href="https://en.wikipedia.org/wiki/P-value">
	 * p-value</a></em>
	 * </blockquote>
	 *
	 * @see <a href="https://en.wikipedia.org/wiki/P-value">Wikipedia: p-value</a>
	 *
	 * @return the <em>p-value</em> used by the hypothesis tester
	 */
	double pValue();

	/**
	 * Calculate the chi-squared value for the given {@code observation} and
	 * {@code hypothesis}.
	 *
	 * @param observation the observation to test
	 * @param hypothesis the zero-hypothesis
	 * @return the chi-squared value
	 */
	double chiSquared(final Histogram observation, final Distribution hypothesis);

	/**
	 * Return the maximal allowed chi-squared value for the given degrees of
	 * freedom.
	 *
	 * @param degreesOfFreedom the degree of freedom
	 * @return the maximal allowed chi-squared value
	 */
	default double maxChiSquared(final int degreesOfFreedom) {
		final var gd = new GammaDistribution(degreesOfFreedom/2.0, 2.0);
		return gd.icdf().apply(1 - pValue());
/*
		return ChiSquaredDistribution.of(degreesOfFreedom)
			.inverseCumulativeProbability(1 - pValue());

 */
	}

}
