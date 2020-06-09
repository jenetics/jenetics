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
import static io.jenetics.internal.math.DoubleAdder.sum;
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;
import io.jenetics.stat.MinMax;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IntegerChromosomeTest
	extends NumericChromosomeTester<Integer, IntegerGene>
{

	private final IntegerChromosome _factory = IntegerChromosome.of(
		0, Integer.MAX_VALUE, 500
	);

	@Override
	protected IntegerChromosome factory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		using(new Random(12345), r -> {
			final int min = 0;
			final int max = 10000000;

			final MinMax<Integer> mm = MinMax.of();
			final Histogram<Integer> histogram = Histogram.ofInteger(min, max, 10);

			for (int i = 0; i < 1000; ++i) {
				final IntegerChromosome chromosome = IntegerChromosome.of(min, max, 500);

				chromosome.forEach(g -> {
					mm.accept(g.allele());
					histogram.accept(g.allele());
				});
			}

			Assert.assertTrue(mm.min().compareTo(0) >= 0);
			Assert.assertTrue(mm.max().compareTo(100) <= 100);
			assertUniformDistribution(histogram);
		});
	}

	@Test(dataProvider = "chromosomes")
	public void chromosomeLength(
		final IntegerChromosome dc,
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
			{IntegerChromosome.of(0, 1000), IntRange.of(1)},
			{IntegerChromosome.of(IntRange.of(0, 1000)), IntRange.of(1)},
			{IntegerChromosome.of(0, 1000, 1), IntRange.of(1)},
			{IntegerChromosome.of(0, 1000, 2), IntRange.of(2)},
			{IntegerChromosome.of(0, 1000, 20), IntRange.of(20)},
			{IntegerChromosome.of(0, 1000, IntRange.of(2, 10)), IntRange.of(2, 10)},
			{IntegerChromosome.of(IntRange.of(0, 1000), IntRange.of(2, 10)), IntRange.of(2, 10)}
		};
	}

	@Test
	public void intStream() {
		final IntegerChromosome chromosome = IntegerChromosome.of(0, 10_000, 1000);
		final int[] values = chromosome.intStream().toArray();

		Assert.assertEquals(values.length, 1000);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(chromosome.get(i).intValue(), values[i]);
			Assert.assertEquals(chromosome.intValue(i), values[i]);
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes1() {
		IntegerChromosome.of(
			IntegerGene.of(1, 2),
			IntegerGene.of(3, 4),
			IntegerGene.of(5, 6)
		);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void ofAmbiguousGenes2() {
		IntegerChromosome.of(
			ISeq.of(
				IntegerGene.of(1, 2),
				IntegerGene.of(3, 4),
				IntegerGene.of(5, 6)
			)
		);
	}

	@Test
	public void map() {
		final var ch1 = IntegerChromosome.of(0, 10_000, 100);

		final var ch2 = ch1.map(IntegerChromosomeTest::half);

		Assert.assertNotSame(ch2, ch1);
		Assert.assertEquals(ch2.toArray(), half(ch1.toArray()));
	}

	static int[] half(final int[] values) {
		for (int i = 0; i < values.length; ++i) {
			values[i] /= 2;
		}
		return values;
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void mapNull() {
		final var ch = IntegerChromosome.of(0, 1);
		ch.map(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mapEmptyArray() {
		final var ch = IntegerChromosome.of(0, 1);
		ch.map(v -> new int[0]);
	}

}
