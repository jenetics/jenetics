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

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.LCG64ShiftRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class QuantileTest {

	@Test
	public void median() {
		final Quantile quantile = Quantile.median();
		for (int i = 0; i < 1000; ++i) {
			quantile.accept(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}
	}

	@Test
	public void parallelMedian() {
		final Quantile quantile = IntStream.range(0, 1000)
			.asDoubleStream().parallel()
			.collect(
				Quantile::median,
				Quantile::accept,
				Quantile::combine);

		Assert.assertEquals(quantile.getValue(), floor(1000/2.0), 1.5);
	}

	@Test(dataProvider = "quantiles")
	public void quantile(final Double q) {
		final int N = 2_000_000;
		final Quantile quantile = new Quantile(q);

		new LCG64ShiftRandom(1234).doubles().limit(N).forEach(quantile);

		Assert.assertEquals(quantile.getSamples(), N);
		Assert.assertEquals(quantile.getValue(), q, 1.0/sqrt(N));
	}

	@Test(dataProvider = "quantiles")
	public void parallelQuantile(final Double q) {
		final int N = 3_000_000;
		final Quantile quantile = new LCG64ShiftRandom(1234).doubles().limit(N).parallel()
			.collect(
				() -> new Quantile(q),
				Quantile::accept,
				Quantile::combine
			);

		Assert.assertEquals(quantile.getSamples(), N);
		Assert.assertEquals(
			quantile.getValue(), q, q*0.1
		);
	}

	@DataProvider(name = "quantiles")
	public Object[][] getQuantiles() {
		return new Double[][] {
			{0.0},
			{0.01},
			{0.0123},
			{0.1},
			{0.25},
			{0.33},
			{0.45},
			{0.5},
			{0.57},
			{0.83},
			{0.93},
			{1.0}
		};
	}

	@Test
	public void reset() {
		final Quantile quantile = Quantile.median();
		for (int i = 0; i < 1000; ++i) {
			quantile.accept(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}

		quantile.reset();

		for (int i = 0; i < 1000; ++i) {
			quantile.accept(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}
	}

	@Test
	public void sameState() {
		final Quantile q1 = Quantile.median();
		final Quantile q2 = Quantile.median();

		final Random random = new Random();
		for (int i = 0; i < 100; ++i) {
			final double value = random.nextInt(1_000_000);
			q1.accept(value);
			q2.accept(value);

			Assert.assertTrue(q1.sameState(q2));
			Assert.assertTrue(q2.sameState(q1));
			Assert.assertTrue(q1.sameState(q1));
			Assert.assertTrue(q2.sameState(q2));
		}
	}

}
