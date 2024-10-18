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
package io.jenetics.stat;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.testfixtures.stat.LinearDistribution;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.Factory;
import io.jenetics.util.ObjectTester;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinearDistributionTest
	extends ObjectTester<LinearDistribution>
{

	@Override
	protected Factory<LinearDistribution> factory() {
		return () -> {
			final var random = RandomRegistry.random();

			final double min = random.nextInt(100) + 100;
			final double max = random.nextInt(100) + 100 + min;
			final double y2 = random.nextDouble();
			return new LinearDistribution(DoubleRange.of(min, max), y2);
		};
	}

	@Test
	public void pdf() {
		final var domain = DoubleRange.of(0.0, 1.0);
		final var dist = new LinearDistribution(domain, 0);
		final var pdf = dist.pdf();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			Assert.assertEquals(x*2, pdf.apply(x), 0.00001);
		}
	}

	@Test
	public void cdf() {
		final var domain = DoubleRange.of(0.0, 1.0);
		final var dist = new LinearDistribution(domain, 0);
		final var cdf = dist.cdf();

		for (int i = 0; i <= 10; ++i) {
			final double x = i/10.0;
			final double y = cdf.apply(x);
			Assert.assertEquals(x*x, y, 0.0001);
		}
	}

}
