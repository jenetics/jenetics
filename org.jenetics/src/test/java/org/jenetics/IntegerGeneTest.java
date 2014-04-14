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
package org.jenetics;

import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.testng.Assert.assertEquals;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-03-24 $</em>
 */
public class IntegerGeneTest extends NumericGeneTester<Integer, IntegerGene> {

	private final Factory<IntegerGene> _factory = IntegerGene.of(0, Integer.MAX_VALUE);
	@Override protected Factory<IntegerGene> getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		try (Scoped<?> s = RandomRegistry.scope(new Random(12345))) {

			final Integer min = 0;
			final Integer max = Integer.MAX_VALUE;
			final Factory<IntegerGene> factory = IntegerGene.of(min, max);

			final Variance<Integer> variance = new Variance<>();

			final Histogram<Integer> histogram = Histogram.of(min, max, 10);

			final int samples = 10000;
			for (int i = 0; i < samples; ++i) {
				final IntegerGene g1 = factory.newInstance();
				final IntegerGene g2 = factory.newInstance();

				Assert.assertTrue(g1.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g1.getAllele().compareTo(max) <= 0);
				Assert.assertTrue(g2.getAllele().compareTo(min) >= 0);
				Assert.assertTrue(g2.getAllele().compareTo(max) <= 0);
				Assert.assertNotSame(g1, g2);

				variance.accumulate(g1.getAllele());
				variance.accumulate(g2.getAllele());
				histogram.accumulate(g1.getAllele());
				histogram.accumulate(g2.getAllele());
			}

			assertDistribution(histogram, new UniformDistribution<>(min, max));
		}
	}

	@Test
	public void createNumber() {
		IntegerGene gene = IntegerGene.of(1, 0, 12);
		IntegerGene g2 = gene.newInstance(5L);

		assertEquals(g2.getAllele().longValue(), 5);
		assertEquals(g2.getMin().longValue(), 0);
		assertEquals(g2.getMax().longValue(), 12);
	}

	@Test
	public void createInvalidNumber() {
		final IntegerGene gene = IntegerGene.of(0, 1, 2);
		Assert.assertFalse(gene.isValid());
	}

	@Test
	public void mean() {
		final int min = -Integer.MAX_VALUE;
		final int max = Integer.MAX_VALUE;
		final IntegerGene template = IntegerGene.of(min, max);

		for (int i = 1; i < 500; ++i) {
			final IntegerGene a = template.newInstance(i - 50L);
			final IntegerGene b = template.newInstance((i - 100L) *3);
			final IntegerGene c = a.mean(b);

			assertEquals(a.getMin().longValue(), min);
			assertEquals(a.getMax().longValue(), max);
			assertEquals(b.getMin().longValue(), min);
			assertEquals(b.getMax().longValue(), max);
			assertEquals(c.getMin().longValue(), min);
			assertEquals(c.getMax().longValue(), max);
			assertEquals(c.getAllele().longValue(), ((i - 50) + ((i - 100)*3))/2);
		}
	}

	@Test
	public void set() {
		IntegerGene gene = new IntegerGene(5, 0, 10);
		Assert.assertEquals(gene.getAllele().intValue(), 5);
		Assert.assertEquals(gene.getMin().intValue(), 0);
		Assert.assertEquals(gene.getMax().intValue(), 10);
	}

}
