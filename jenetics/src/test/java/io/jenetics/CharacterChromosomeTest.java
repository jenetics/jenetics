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

import static io.jenetics.stat.StatisticsAssert.assertDistribution;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.dist;
import io.jenetics.testfixtures.stat.Histogram;
import io.jenetics.util.CharSeq;
import io.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CharacterChromosomeTest extends ChromosomeTester<CharacterGene> {

	@Override
	protected Factory<Chromosome<CharacterGene>> factory() {
		return () -> CharacterChromosome.of(500);
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void newInstanceDistribution() {
		using(new Random(12345), r -> {
			final CharSeq characters = new CharSeq("0123456789");
			final CharacterChromosome chromosome = CharacterChromosome.of(characters, 5000);

			final var histogram = Histogram.of(0L, 10L, 10);
			chromosome.stream()
				.map(g -> Long.parseLong(g.allele().toString()))
				.forEach(histogram::accept);

			final double[] expected = dist.uniform(histogram.binCount());
			assertDistribution(histogram, expected);
		});
    }

	@Test(dataProvider = "genes")
	public void newCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("0123456789");
		CharacterChromosome chromosome = CharacterChromosome.of(genes, characters);

		Assert.assertEquals(new String(new StringBuilder(chromosome)), genes);
	}

	@Test(dataProvider = "genes")
	public void newIllegalCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("012356789");
		CharacterChromosome chromosome = CharacterChromosome.of(genes, characters);

		Assert.assertFalse(chromosome.isValid(), "Chromosome must not be valid");
		Assert.assertEquals(new String(new StringBuilder(chromosome)), genes);
	}

	@DataProvider(name = "genes")
	public Object[][] genes() {
		return new Object[][] {
				{"54374"},
				{"543794578"},
				{"54374546"},
				{"543345647"},
				{"54304897"},
				{"5433457245"}
		};
	}

	@Test
	public void map() {
		final var ch1 = CharacterChromosome.of(1000);

		final var ch2 = ch1.map(CharacterChromosomeTest::uppercase);

		Assert.assertNotSame(ch2, ch1);
		Assert.assertEquals(ch2.toArray(), uppercase(ch1.toArray()));
	}

	static char[] uppercase(final char[] values) {
		for (int i = 0; i < values.length; ++i) {
			values[i] = Character.toUpperCase(values[i]);
		}
		return values;
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void mapNull() {
		final var ch = CharacterChromosome.of(1000);
		ch.map(null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void mapEmptyArray() {
		final var ch = CharacterChromosome.of(1000);
		ch.map(v -> new char[0]);
	}

}
