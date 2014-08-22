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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.stat;

import static java.lang.String.format;
import static org.jenetics.internal.math.arithmetic.normalize;

import java.util.Arrays;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.GTest;
import org.testng.Assert;
import org.testng.Reporter;

import org.jenetics.internal.util.require;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-22 $</em>
 */
public final class StatisticsAssert {

	private StatisticsAssert() {require.noInstance();}

	public static <C extends Comparable<? super C>> void assertDistribution(
		final Histogram<C> histogram,
		final Distribution<C> distribution
	) {
		final double χ2 =  histogram.χ2(distribution.getCDF());
		final int degreeOfFreedom = histogram.length();
		assert (degreeOfFreedom > 0);

		final double maxChi = chi(0.999, degreeOfFreedom)*2;

		if (χ2 > maxChi) {
			System.out.println(format(
				"The histogram %s doesn't follow the distribution %s. \n" +
					"χ2 must be smaller than %f but was %f",
				histogram, distribution,
				maxChi, χ2
			));
		}

		Assert.assertTrue(
				χ2 <= maxChi,
				format(
					"The histogram %s doesn't follow the distribution %s. \n" +
						"χ2 must be smaller than %f but was %f",
					histogram, distribution,
					maxChi, χ2
				)
			);
	}

	public static <C extends Comparable<? super C>> void assertDistribution(
		final Histogram<C> distribution,
		final double[] expected
	) {
		final double[] exp = Arrays.stream(expected)
			.map(v -> Math.max(v, 1.0/1000000000000000.0))
			.toArray();

		normalize(exp);

		final long[] dist = Arrays.stream(distribution.getHistogram())
			//.map(v -> Math.max(v, 1))
			.toArray();

		final double χ2 = new ChiSquareTest().chiSquare(exp, dist);
		System.out.println(format("CHISQR: %f", χ2));

		final double p = new ChiSquareTest().chiSquareTest(exp, dist);
		System.out.println("PPPPPPPP: " + p);

		final double g = new GTest().gTest(exp, dist);
		System.out.println("GGGGGGGG: " + g);

		final int degreeOfFreedom = distribution.length();
		assert (degreeOfFreedom > 0);

		final double maxChi = chi(0.999, degreeOfFreedom);

		if (χ2 > maxChi) {
			format(
				"The histogram doesn't follow the given distribution." +
					"χ2 must be smaller than %f but was %f",
				maxChi, χ2
			);
		}

		Reporter.log(format("chi=%s, maxChi=%s", χ2, maxChi));

		Assert.assertTrue(
			χ2 <= maxChi,
			format(
				"The histogram doesn't follow the given distribution." +
					"χ2 must be smaller than %f but was %f",
				maxChi, χ2
			)
		);
	}

	public static double chi(final double p, final int degreeOfFreedom) {
		return new ChiSquaredDistribution(degreeOfFreedom)
			.inverseCumulativeProbability(p);
	}

}
