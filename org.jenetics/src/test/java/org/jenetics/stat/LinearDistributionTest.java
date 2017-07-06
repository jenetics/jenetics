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

import java.util.Random;
import java.util.function.ToDoubleFunction;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LinearDistributionTest extends ObjectTester<LinearDistribution<Double>> {

	@Override
	protected Factory<LinearDistribution<Double>> factory() {
		return () -> {
			final Random random = RandomRegistry.getRandom();

			final double min = random.nextInt(100) + 100;
			final double max = random.nextInt(100) + 100 + min;
			final double y2 = random.nextDouble();
			final LinearDistribution<Double> dist =
				new LinearDistribution<>(new Range<>(min, max), y2);

			return dist;
		};
	}

	@Test
	public void pdf() {
		final Range<Double> domain = new Range<>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<>(domain, 0);
		final ToDoubleFunction<Double> pdf = dist.getPDF();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			Assert.assertEquals(x*2, pdf.applyAsDouble(x), 0.00001);
		}
	}

	@Test
	public void cdf() {
		final Range<Double> domain = new Range<>(0.0, 1.0);
		final LinearDistribution<Double> dist = new LinearDistribution<>(domain, 0);
		final ToDoubleFunction<Double> cdf = dist.getCDF();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			final double y = cdf.applyAsDouble(x);
			Assert.assertEquals(x*x, y, 0.0001);
		}
	}

}
