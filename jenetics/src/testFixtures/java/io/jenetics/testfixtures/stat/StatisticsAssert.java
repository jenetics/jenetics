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
package io.jenetics.testfixtures.stat;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class StatisticsAssert {
	private StatisticsAssert() {
	}

	public static final class DistributionAssert {
		private final Histogram _histogram;

		private HypothesisTester _tester;

		private DistributionAssert(final Histogram histogram) {
			_histogram = requireNonNull(histogram);
		}

		public void follows(final Distribution distribution) {
			assertDistribution(_histogram, distribution);
		}

		public void isUniform() {
			follows(new UniformDistribution(_histogram.range()));
		}

		public void isNormal(double mean, double stddev) {
			follows(new NormalDistribution(mean, stddev));
		}

	}

	public static DistributionAssert assertHistogram(Histogram histogram) {
		return new DistributionAssert(histogram);
	}

	private static void assertDistribution(
		final Histogram histogram,
		final Distribution distribution
	) {
		final double chi2 = histogram.chi2(distribution.cdf());
		final int degreeOfFreedom = histogram.binCount();
		assert (degreeOfFreedom > 0);

		final double maxChi = chi(0.999, degreeOfFreedom);
		System.out.println("MAX_CHI: " + maxChi + ", chi2: " + chi2);

		assertThat(chi2)
			.withFailMessage("""
				The histogram %s doesn't follow the distribution %s.
				χ2 must be smaller than %f but was %f.
				""",
				histogram, distribution,
				maxChi, chi2
			)
			.isLessThanOrEqualTo(maxChi);
	}

	public static <C extends Comparable<? super C>> void assertDistribution(
		final Histogram distribution,
		final double[] expected
	) {
		assertDistribution(distribution, expected, 0.05);
	}

	public static void assertDistribution(
		final Histogram histogram,
		final double[] expected,
		final double alpha,
		final double safety
	) {
		final double[] exp = Arrays.stream(expected)
			.map(v -> Math.max(v, Double.MIN_VALUE))
			.toArray();

		final long[] dist = histogram.hist();

		final double χ2 = new ChiSquareTest().chiSquare(exp, dist);
		final double max_χ2 = chi(1 - alpha, dist.length);
		final boolean reject = χ2 > max_χ2*safety;
		//final boolean reject = new ChiSquareTest().chiSquareTest(exp, dist, alpha);

		assertThat(reject)
			.withFailMessage(
				"The histogram doesn't follow the given distribution." +
				"χ2 must be smaller than %f but was %f", max_χ2, χ2
			)
			.isFalse();
	}

	public static void assertDistribution(
		final Histogram distribution,
		final double[] expected,
		final double alpha
	) {
		assertDistribution(distribution, expected, alpha, 1.75);
	}

	private static double chi(final double p, final int degreeOfFreedom) {
		return new ChiSquaredDistribution(degreeOfFreedom)
			.inverseCumulativeProbability(p);
	}

	public static void assertUniformDistribution(final Histogram histogram) {
		final double[] expected = dist.uniform(histogram.binCount() - 2);
		assertDistribution(histogram, expected);
	}

}
