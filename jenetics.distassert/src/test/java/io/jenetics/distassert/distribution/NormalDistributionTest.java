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
package io.jenetics.distassert.distribution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.random.RandomGenerator;

import org.assertj.core.data.Percentage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class NormalDistributionTest {

	@Test(dataProvider = "parameters")
	public void pdf(final double mean, final double stddev) {
		final var pdf = new NormalDistribution(mean, stddev).pdf();
		final var dist = org.apache.commons.statistics.distribution
			.NormalDistribution.of(mean, stddev);

		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 100; ++i) {
			final var x = random.nextGaussian(mean, stddev);

			assertThat(pdf.apply(x))
				.isCloseTo(dist.density(x), Percentage.withPercentage(1));
		}
	}

	@Test(dataProvider = "parameters")
	public void cdf(final double mean, final double stddev) {
		final var cdf = new NormalDistribution(mean, stddev).cdf();
		final var dist = org.apache.commons.statistics.distribution
			.NormalDistribution.of(mean, stddev);

		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 100; ++i) {
			final var x = random.nextGaussian(mean, stddev);

			assertThat(cdf.apply(x))
				.isCloseTo(dist.cumulativeProbability(x), Percentage.withPercentage(1));
		}
	}

	@Test(dataProvider = "parameters")
	public void icdf(final double mean, final double stddev) {
		final var icdf = new NormalDistribution(mean, stddev).icdf();
		final var dist = org.apache.commons.statistics.distribution
			.NormalDistribution.of(mean, stddev);

		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 100; ++i) {
			final var x = random.nextDouble();

			assertThat(icdf.apply(x))
				.isCloseTo(dist.inverseCumulativeProbability(x), Percentage.withPercentage(1));
		}
	}

	@DataProvider
	public static Object[][] parameters() {
		return new Object[][] {
			{0.0, 1.0},
			{0.0, 1.5},
			{0.0, 0.5},
			{-3, 1.5},
			{3.5, 9.0},
			{1.5, 17.0}
		};
	}

}
