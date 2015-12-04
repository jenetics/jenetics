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

import static org.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static org.jenetics.util.RandomRegistry.using;
import static org.testng.Assert.assertEquals;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class IntegerGeneTest extends NumericGeneTester<Integer, IntegerGene> {

	private final Factory<IntegerGene> _factory = IntegerGene.of(0, Integer.MAX_VALUE);
	@Override protected Factory<IntegerGene> factory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		final Integer min = 0;
		final Integer max = Integer.MAX_VALUE;
		final Histogram<Integer> histogram = Histogram.ofInteger(min, max, 10);

		using(new Random(12345), r ->
			IntStream.range(0, 200_000)
				.mapToObj(i -> IntegerGene.of(min, max).getAllele())
				.forEach(histogram::accept)
		);

		assertUniformDistribution(histogram);
	}

	@Test
	public void parameters() {
		final IntegerGene gene = IntegerGene.of(10, 10);
		Assert.assertEquals(gene.getMin().intValue(), 10);
		Assert.assertEquals(gene.getMax().intValue(), 10);
		Assert.assertEquals(gene.getAllele().intValue(), 10);
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
	public void set() {
		IntegerGene gene = new IntegerGene(5, 0, 10);
		Assert.assertEquals(gene.getAllele().intValue(), 5);
		Assert.assertEquals(gene.getMin().intValue(), 0);
		Assert.assertEquals(gene.getMax().intValue(), 10);
	}

}
