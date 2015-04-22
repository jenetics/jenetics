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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class DoubleGeneTest extends NumericGeneTester<Double, DoubleGene> {

	@Override
	protected Factory<DoubleGene> factory() {
		return () -> DoubleGene.of(0, Double.MAX_VALUE);
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		final double min = 0;
		final double max = 100;
		final Histogram<Double> histogram = Histogram.ofDouble(min, max, 10);

		using(new Random(12345), r -> {
			IntStream.range(0, 200_000)
				.mapToObj(i -> DoubleGene.of(min, max).getAllele())
				.forEach(histogram::accept);
		});

		assertUniformDistribution(histogram);
	}

	@Test
	public void mean() {
		final double min = -Double.MAX_VALUE;
		final double max = Double.MAX_VALUE;
		final DoubleGene template = DoubleGene.of(min, max);

		for (int i = 1; i < 500; ++i) {
			final DoubleGene a = template.newInstance(i - 50.0);
			final DoubleGene b = template.newInstance((i - 100)*3.0);
			final DoubleGene c = a.mean(b);

			assertEquals(a.getMin(), min);
			assertEquals(a.getMax(), max);
			assertEquals(b.getMin(), min);
			assertEquals(b.getMax(), max);
			assertEquals(c.getMin(), min);
			assertEquals(c.getMax(), max);
			assertEquals(c.getAllele(), ((i - 50) + ((i - 100)*3))/2.0);
		}
	}

	@Test
	public void doubleGeneIntegerIntegerInteger() {
		DoubleGene gene = new DoubleGene(1.234, 0.345, 2.123);
		assertEquals(gene.getAllele(), 1.234);
		assertEquals(gene.getMin(), 0.345);
		assertEquals(gene.getMax(), 2.123);

		try {
			gene = new DoubleGene(0.1, 2.1, 4.1);
			assertFalse(gene.isValid());
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void doubleGeneIntegerInteger() {
		DoubleGene gene = DoubleGene.of(-10.567, 10.567);
		assertEquals(gene.getMin(), -10.567);
		assertEquals(gene.getMax(), 10.567);
	}

	@Test
	public void createNumber() {
		DoubleGene gene = new DoubleGene(1.2345, -1234.1234, 1234.1234);
		DoubleGene g2 = gene.newInstance(5.0);

		assertEquals(g2.getAllele().intValue(), 5);
		assertEquals(g2.getMin(), -1234.1234);
		assertEquals(g2.getMax(), 1234.1234);
	}

	@Test
	public void createInvalidNumber() {
		final DoubleGene gene = new DoubleGene(0.0, 1.0, 2.0);
		Assert.assertFalse(gene.isValid());
	}

	@Test
	public void getMinValue() {
		DoubleGene g1 = new DoubleGene(3.1, 0.1, 5.1);
		DoubleGene g2 = new DoubleGene(4.1, 1.1, 7.1);
		DoubleGene g3 = new DoubleGene(3.1, 0.1, 5.1);

		assertEquals(g1.getMin(), 0.1);
		assertEquals(g2.getMin(), 1.1);
		assertEquals(g3.getMin(), 0.1);
	}

	@Test
	public void getMaxValue() {
		DoubleGene g1 = new DoubleGene(3.2, 0.2, 5.2);
		DoubleGene g2 = new DoubleGene(4.2, 1.2, 7.2);
		DoubleGene g3 = new DoubleGene(3.2, 0.2, 5.2);

		assertEquals(g1.getMax(), 5.2);
		assertEquals(g2.getMax(), 7.2);
		assertEquals(g3.getMax(), 5.2);
	}

}
