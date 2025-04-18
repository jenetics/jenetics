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
import static io.jenetics.incubator.stat.Assurance.assertThatObservation;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.math.BigInteger;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram;
import io.jenetics.incubator.stat.RunnableObservation;
import io.jenetics.incubator.stat.Sampling;
import io.jenetics.util.Factory;
import io.jenetics.util.StableRandomExecutor;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LongGeneTest extends NumericGeneTester<Long, LongGene> {

	private final Factory<LongGene> _factory = LongGene.of(0, Long.MAX_VALUE);
	@Override protected Factory<LongGene> factory() {
		return _factory;
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(LongGene.class).verify();
	}

	@Test(dataProvider = "seeds")
	public void newInstanceDistribution(final long seed) {
		final long min = 0;
		final long max = Integer.MAX_VALUE;

		final var observation = new RunnableObservation(
			Sampling.repeat(200_000, samples ->
				samples.add(LongGene.of(min, max).doubleValue())
			),
			Histogram.Partition.of(min, max, 20)
		);
		new StableRandomExecutor(seed).execute(observation);

		assertThatObservation(observation).isUniform();
	}

	@DataProvider
	public Object[][] seeds() {
		return new Random(123456782).longs(20)
			.mapToObj(seed -> new Object[]{seed})
			.toArray(Object[][]::new);
	}

	@Test
	public void mean() {
		final long min = -Integer.MAX_VALUE;
		final long max = Integer.MAX_VALUE;
		final LongGene template = LongGene.of(min, max);

		for (int i = 1; i < 500; ++i) {
			final LongGene a = template.newInstance(i - 50L);
			final LongGene b = template.newInstance((i - 100L) *3);
			final LongGene c = a.mean(b);

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
		final long a = Long.MAX_VALUE - 1;
		final long b = Long.MIN_VALUE;
		final long mean = BigInteger.valueOf(a)
			.add(BigInteger.valueOf(b))
			.divide(BigInteger.valueOf(2))
			.longValue();

		final var g1 = LongGene.of(a, Long.MIN_VALUE, Long.MAX_VALUE);
		final var g2 = LongGene.of(b, Long.MIN_VALUE, Long.MAX_VALUE);
		final var g3 = g1.mean(g2);

		assertThat(g3.allele()).isEqualTo(mean);
	}

	@Test
	public void createNumber() {
		LongGene gene = LongGene.of(1, 0, 12);
		LongGene g2 = gene.newInstance(5L);

		assertEquals(g2.allele().longValue(), 5);
		assertEquals(g2.min().longValue(), 0);
		assertEquals(g2.max().longValue(), 12);
	}

	@Test
	public void createInvalidNumber() {
		final LongGene gene = LongGene.of(0, 1, 2);
		Assert.assertFalse(gene.isValid());
	}

	@Test
	public void set() {
		LongGene gene = LongGene.of(5L, 0L, 10L);
		Assert.assertEquals(gene.allele().longValue(), 5L);
		Assert.assertEquals(gene.min().longValue(), 0L);
		Assert.assertEquals(gene.max().longValue(), 10L);
	}

}
