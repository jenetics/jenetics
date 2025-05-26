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

import java.util.random.RandomGeneratorFactory;
import java.util.stream.IntStream;

import org.apache.commons.statistics.distribution.ChiSquaredDistribution;
import org.assertj.core.data.Percentage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PearsonsChiSquaredTest {

	@Test(dataProvider = "parameters")
	public void maxChiSquared(final int dof, final double pValue) {
		final var pcs = new PearsonsChiSquared(pValue);
		final var expected = apacheMaxChiSquared(dof, pValue);

		assertThat(pcs.maxChiSquared(dof))
			.isCloseTo(expected, Percentage.withPercentage(0.00001));
	}

	private static double apacheMaxChiSquared(final int dof, final double pValue) {
		return ChiSquaredDistribution.of(dof)
			.inverseCumulativeProbability(1 - pValue);
	}

	@DataProvider
	public Object[][] parameters() {
		final var random = RandomGeneratorFactory.getDefault().create(123);
		return IntStream.range(0, 50)
			.mapToObj(i -> new Object[] {
				random.nextInt(1, 1000),
				random.nextDouble()}
			)
			.toArray(Object[][]::new);
	}

}
