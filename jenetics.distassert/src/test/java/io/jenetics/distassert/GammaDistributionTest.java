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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.random.RandomGenerator;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GammaDistributionTest {

	private final RandomGenerator random = RandomGenerator.getDefault();

	@Test(invocationCount = 10)
	public void pdf() {
		final var shape = random.nextDouble();
		final var scale = random.nextDouble();

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble();
		assertThat(jgd.pdf().apply(x)).isEqualTo(agd.density(x));
	}

	@Test(invocationCount = 10)
	public void cdf() {
		final var shape = random.nextDouble();
		final var scale = random.nextDouble();

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble();
		assertThat(jgd.cdf().apply(x)).isEqualTo(agd.cumulativeProbability(x));
	}

	@Test(invocationCount = 10)
	public void icdf() {
		final var shape = random.nextDouble(2, 1000);
		final var scale = 2.0;//random.nextDouble(0.1, 1000);

		final var agd =
			org.apache.commons.statistics.distribution.GammaDistribution.of(shape, scale);
		final var jgd = new GammaDistribution(shape, scale);

		final var x = random.nextDouble(0.1, 0.9);
		System.out.println("shape=%s, scale=%s, x=%s".formatted(shape, scale, x));
		assertThat(jgd.icdf().apply(x))
			.isCloseTo(agd.inverseCumulativeProbability(x), Offset.offset(0.0001));
	}

}
