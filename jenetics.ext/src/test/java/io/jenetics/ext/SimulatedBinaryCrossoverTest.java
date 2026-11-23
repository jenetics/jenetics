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
package io.jenetics.ext;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.5
 * @since 3.5
 */
public class SimulatedBinaryCrossoverTest {

	@Test(dataProvider = "crossovers")
	public void crossover(
		final double first,
		final double second,
		final double sample,
		final boolean lower,
		final double expected
	) {
		final var random = new Random() {
			@Override
			public int nextInt() {
				return Integer.MIN_VALUE;
			}

			@Override
			public double nextDouble() {
				return sample;
			}

			@Override
			public boolean nextBoolean() {
				return lower;
			}
		};

		RandomRegistry.with(random).run(() -> {
			final var that = MSeq.of(DoubleGene.of(first, -10, 10));
			final var other = MSeq.of(DoubleGene.of(second, -10, 10));
			final var crossover =
				new SimulatedBinaryCrossover<DoubleGene, Double>(1, 0);

			Assert.assertEquals(crossover.crossover(that, other), 1);
			Assert.assertEquals(that.get(0).doubleValue(), expected, 1e-12);
			Assert.assertEquals(other.get(0).doubleValue(), second);
		});
	}

	@DataProvider
	public Object[][] crossovers() {
		return new Object[][] {
			{2.0, 6.0, 0.25, true, 3.0},
			{2.0, 6.0, 0.25, false, 5.0},
			{2.0, 6.0, 0.5, true, 2.0},
			{2.0, 6.0, 0.5, false, 6.0},
			{2.0, 6.0, 0.75, true, 0.0},
			{2.0, 6.0, 0.75, false, 8.0},
			{6.0, 2.0, 0.25, true, 3.0},
			{6.0, 2.0, 0.25, false, 5.0},
			{-6.0, -2.0, 0.25, true, -5.0},
			{-6.0, -2.0, 0.25, false, -3.0},
			{4.0, 4.0, 0.25, true, 4.0},
			{4.0, 4.0, 0.25, false, 4.0}
		};
	}
}
