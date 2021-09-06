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
package io.jenetics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static io.jenetics.util.RandomRegistry.using;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class DoubleGeneTest extends NumericGeneTester<Double, DoubleGene> {

	@Override
	protected Factory<DoubleGene> factory() {
		return () -> DoubleGene.of(0, Double.MAX_VALUE);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(DoubleGene.class).verify();
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		final double min = 0;
		final double max = 100;
		final Histogram<Double> histogram = Histogram.ofDouble(min, max, 10);

		using(new Random(12345), r -> IntStream.range(0, 200_000)
			.mapToObj(i -> DoubleGene.of(min, max).allele())
			.forEach(histogram));

		assertUniformDistribution(histogram);
	}

	@Test
	public void mean() {
		final double min = 0;
		final double max = Double.MAX_VALUE;
		final DoubleGene template = DoubleGene.of(min, max);

		for (int i = 1; i < 500; ++i) {
			final DoubleGene a = template.newInstance(i - 50.0);
			final DoubleGene b = template.newInstance((i - 100)*3.0);
			final DoubleGene c = a.mean(b);

			assertEquals(a.min().doubleValue(), min);
			assertEquals(a.max().doubleValue(), max);
			assertEquals(b.min().doubleValue(), min);
			assertEquals(b.max().doubleValue(), max);
			assertEquals(c.min().doubleValue(), min);
			assertEquals(c.max().doubleValue(), max);
			assertEquals(c.allele().doubleValue(), ((i - 50) + ((i - 100)*3))/2.0);
		}
	}

	@Test
	public void meanOverflow() {
		final var a = Double.MIN_VALUE*1;
		final var b = Double.MIN_VALUE*10;
		final var mean = new BigDecimal(a)
			.add(new BigDecimal(b))
			.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
			.doubleValue();

		final var g1 = DoubleGene.of(a, Double.MIN_VALUE, 0);
		final var g2 = DoubleGene.of(b, Double.MIN_VALUE, 0);
		final var g3 = g1.mean(g2);

		assertThat(g3.allele()).isEqualTo(mean);
	}

	@Test
	public void doubleGeneIntegerIntegerInteger() {
		DoubleGene gene = DoubleGene.of(1.234, 0.345, 2.123);
		assertEquals(gene.allele().doubleValue(), 1.234);
		assertEquals(gene.min().doubleValue(), 0.345);
		assertEquals(gene.max().doubleValue(), 2.123);

		try {
			gene = DoubleGene.of(0.1, 2.1, 4.1);
			assertFalse(gene.isValid());
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void doubleGeneIntegerInteger() {
		DoubleGene gene = DoubleGene.of(-10.567, 10.567);
		assertEquals(gene.min().doubleValue(), -10.567);
		assertEquals(gene.max().doubleValue(), 10.567);
	}

	@Test
	public void createNumber() {
		DoubleGene gene = DoubleGene.of(1.2345, -1234.1234, 1234.1234);
		DoubleGene g2 = gene.newInstance(5.0);

		assertEquals(g2.allele().intValue(), 5);
		assertEquals(g2.min().doubleValue(), -1234.1234);
		assertEquals(g2.max().doubleValue(), 1234.1234);
	}

	@Test
	public void createInvalidNumber() {
		final DoubleGene gene = DoubleGene.of(0.0, 1.0, 2.0);
		Assert.assertFalse(gene.isValid());
	}

	@Test
	public void getMinValue() {
		DoubleGene g1 = DoubleGene.of(3.1, 0.1, 5.1);
		DoubleGene g2 = DoubleGene.of(4.1, 1.1, 7.1);
		DoubleGene g3 = DoubleGene.of(3.1, 0.1, 5.1);

		assertEquals(g1.min().doubleValue(), 0.1);
		assertEquals(g2.min().doubleValue(), 1.1);
		assertEquals(g3.min().doubleValue(), 0.1);
	}

	@Test
	public void getMaxValue() {
		DoubleGene g1 = DoubleGene.of(3.2, 0.2, 5.2);
		DoubleGene g2 = DoubleGene.of(4.2, 1.2, 7.2);
		DoubleGene g3 = DoubleGene.of(3.2, 0.2, 5.2);

		assertEquals(g1.max().doubleValue(), 5.2);
		assertEquals(g2.max().doubleValue(), 7.2);
		assertEquals(g3.max().doubleValue(), 5.2);
	}

	@Test(dataProvider = "validationData")
	public void isValid(final DoubleGene gene, final boolean valid) {
		Assert.assertEquals(gene.isValid(), valid);
	}

	@DataProvider
	public Object[][] validationData() {
		return new Object[][] {
			{DoubleGene.of(0, 0, 1), true},
			{DoubleGene.of(1, 0, 1), false},
			{DoubleGene.of(1, 1, 0), false},
			{DoubleGene.of(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE), false},
			{DoubleGene.of(Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE), false},
			{DoubleGene.of(0.5, 1, 0), false},
			{DoubleGene.of(0, Double.MAX_VALUE), true},
			{DoubleGene.of(0.5, Double.NaN, 1), false},
			{DoubleGene.of(0.5, Double.POSITIVE_INFINITY, 1), false},
			{DoubleGene.of(0.5, Double.NEGATIVE_INFINITY, 1), false},
			{DoubleGene.of(0.5, 0, Double.NaN), false},
			{DoubleGene.of(0.5, 0, Double.POSITIVE_INFINITY), false},
			{DoubleGene.of(0.5, 0, Double.NEGATIVE_INFINITY), false}
		};
	}

}
