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

import static java.lang.String.format;
import static io.jenetics.incubator.stat.StatisticsAssert.assertThatObservation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LongChromosomeTest
	extends NumericChromosomeTester<Long, LongGene>
{

	private final LongChromosome _factory = LongChromosome.of(
		0L, Long.MAX_VALUE, 500
	);

	@Override
	protected LongChromosome factory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		final long min = 0;
		final long max = 10000000;

		final var histogram = Histogram.Builder.of(min, max, 20);
		for (int i = 0; i < 1000; ++i) {
			final var chromosome = LongChromosome.of(min, max, 500);
			for (var gene : chromosome) {
				histogram.accept(gene.allele());
			}
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(dataProvider = "chromosomes")
	public void chromosomeLength(
		final LongChromosome dc,
		final IntRange length
	) {
		Assert.assertTrue(
			dc.length() >= length.min() && dc.length() < length.max(),
			format("Chromosome length %s not in range %s.", dc.length(), length)
		);
	}

	@DataProvider(name = "chromosomes")
	public Object[][] chromosomes() {
		return new Object[][] {
			{LongChromosome.of(0, 1000), IntRange.of(1)},
			{LongChromosome.of(LongRange.of(0, 1000)), IntRange.of(1)},
			{LongChromosome.of(0, 1000, 1), IntRange.of(1)},
			{LongChromosome.of(0, 1000, 2), IntRange.of(2)},
			{LongChromosome.of(0, 1000, 20), IntRange.of(20)},
			{LongChromosome.of(0, 1000, IntRange.of(2, 10)), IntRange.of(2, 10)},
			{LongChromosome.of(LongRange.of(0, 1000), IntRange.of(2, 10)), IntRange.of(2, 10)}
		};
	}

	@Test
	public void longStream() {
		final LongChromosome chromosome = LongChromosome.of(0, 10_000, 1000);
		final long[] values = chromosome.longStream().toArray();

		Assert.assertEquals(values.length, 1000);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(chromosome.get(i).longValue(), values[i]);
			Assert.assertEquals(chromosome.longValue(i), values[i]);
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes1() {
		LongChromosome.of(
			LongGene.of(1, 2),
			LongGene.of(3, 4),
			LongGene.of(5, 6)
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes2() {
		LongChromosome.of(
			ISeq.of(
				LongGene.of(1, 2),
				LongGene.of(3, 4),
				LongGene.of(5, 6)
			)
		);
	}

	@Test
	public void map() {
		final var ch1 = LongChromosome.of(0, 10_000, 100);

		final var ch2 = ch1.map(LongChromosomeTest::half);

		Assert.assertNotSame(ch2, ch1);
		Assert.assertEquals(ch2.toArray(), half(ch1.toArray()));
	}

	static long[] half(final long[] values) {
		for (int i = 0; i < values.length; ++i) {
			values[i] /= 2;
		}
		return values;
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void mapNull() {
		final var ch = LongChromosome.of(0, 1);
		ch.map(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mapEmptyArray() {
		final var ch = LongChromosome.of(0, 1);
		ch.map(v -> new long[0]);
	}

}
