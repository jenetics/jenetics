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
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static io.jenetics.util.RandomRegistry.using;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IntegerGeneTest extends NumericGeneTester<Integer, IntegerGene> {

	private final Factory<IntegerGene> _factory = IntegerGene.of(0, Integer.MAX_VALUE);
	@Override protected Factory<IntegerGene> factory() {
		return _factory;
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(IntegerGene.class).verify();
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		final int min = 0;
		final int max = Integer.MAX_VALUE;
		final Histogram<Integer> histogram = Histogram.ofInteger(min, max, 10);

		using(new Random(12345), r ->
			IntStream.range(0, 200_000)
				.mapToObj(i -> IntegerGene.of(min, max).allele())
				.forEach(histogram)
		);

		assertUniformDistribution(histogram);
	}

	@Test
	public void parameters() {
		final IntegerGene gene = IntegerGene.of(10, 11);
		Assert.assertEquals(gene.min().intValue(), 10);
		Assert.assertEquals(gene.max().intValue(), 11);
		Assert.assertEquals(gene.allele().intValue(), 10);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidParameters() {
		IntegerGene.of(10, 10);
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

			assertEquals(a.min().longValue(), min);
			assertEquals(a.max().longValue(), max);
			assertEquals(b.min().longValue(), min);
			assertEquals(b.max().longValue(), max);
			assertEquals(c.min().longValue(), min);
			assertEquals(c.max().longValue(), max);
			assertEquals(c.allele().longValue(), ((i - 50) + ((i - 100)*3))/2);
		}
	}

	@Test
	public void meanOverflow() {
		final int a = Integer.MAX_VALUE - 1;
		final int b = Integer.MIN_VALUE;
		final long mean = (a + b)/2;

		final var g1 = IntegerGene.of(a, Integer.MIN_VALUE, Integer.MAX_VALUE);
		final var g2 = IntegerGene.of(b, Integer.MIN_VALUE, Integer.MAX_VALUE);
		final var g3 = g1.mean(g2);

		assertThat(g3.allele()).isEqualTo((int)mean);
	}

	@Test
	public void createNumber() {
		IntegerGene gene = IntegerGene.of(1, 0, 12);
		IntegerGene g2 = gene.newInstance(5L);

		assertEquals(g2.allele().longValue(), 5);
		assertEquals(g2.min().longValue(), 0);
		assertEquals(g2.max().longValue(), 12);
	}

	@Test
	public void createInvalidNumber() {
		final IntegerGene gene = IntegerGene.of(0, 1, 2);
		Assert.assertFalse(gene.isValid());
	}

	@Test
	public void set() {
		IntegerGene gene = IntegerGene.of(5, 0, 10);
		Assert.assertEquals(gene.allele().intValue(), 5);
		Assert.assertEquals(gene.min().intValue(), 0);
		Assert.assertEquals(gene.max().intValue(), 10);
	}

}
