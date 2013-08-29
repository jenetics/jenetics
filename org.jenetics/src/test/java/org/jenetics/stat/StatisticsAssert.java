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

import org.testng.Assert;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public final class StatisticsAssert {

	private StatisticsAssert() {
	}

	public static <C extends Comparable<? super C>> void assertDistribution(
		final Histogram<C> histogram,
		final Distribution<C> distribution
	) {
		final double χ2 =  histogram.χ2(distribution.getCDF());
		final int degreeOfFreedom = histogram.length();
		assert (degreeOfFreedom > 0);

		final double maxChi = ChiSquare.chi_999(degreeOfFreedom)*2;

		if (χ2 > maxChi) {
			System.out.println(String.format(
					"The histogram %s doesn't follow the distribution %s. \n" +
					"χ2 must be smaller than %f but was %f",
					histogram, distribution,
					maxChi, χ2
				));
		}

		Assert.assertTrue(
				χ2 <= maxChi,
				String.format(
						"The histogram %s doesn't follow the distribution %s. \n" +
						"χ2 must be smaller than %f but was %f",
						histogram, distribution,
						maxChi, χ2
					)
			);
	}

}
