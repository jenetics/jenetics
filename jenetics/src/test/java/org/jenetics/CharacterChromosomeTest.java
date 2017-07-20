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
import static org.jenetics.util.RandomRegistry.using;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.dist;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
			final CharacterChromosome chromosome = new CharacterChromosome(characters, 5000);

			final Histogram<Long> histogram = Histogram.ofLong(0L, 10L, 10);
			chromosome.toSeq().stream()
				.map(g -> Long.valueOf(g.getAllele().toString()))
				.forEach(histogram::accept);

			final double[] expected = dist.uniform(histogram.length());
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

}
