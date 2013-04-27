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

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.IO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class CharacterChromosomeTest extends ChromosomeTester<CharacterGene> {

	private final Factory<Chromosome<CharacterGene>>
	_factory = new CharacterChromosome(500);
	@Override protected Factory<Chromosome<CharacterGene>> getFactory() {
		return _factory;
	}


	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));

			final CharSeq characters = new CharSeq("0123456789");
			final CharacterChromosome chromosome = new CharacterChromosome(characters, 5000);

			final Histogram<Long> histogram = Histogram.valueOf(0L, 10L, 10);

			for (CharacterGene gene : chromosome) {
				histogram.accumulate(Long.valueOf(gene.getAllele().toString()));
			}

			assertDistribution(histogram, new UniformDistribution<>(0L, 10L));
		} finally {
			LocalContext.exit();
		}
    }

	@Test(dataProvider = "genes")
	public void newCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("0123456789");
		CharacterChromosome chromosome = new CharacterChromosome(genes, characters);

		Assert.assertEquals(new String(new StringBuilder(chromosome)), genes);
	}

	@Test(dataProvider = "genes", expectedExceptions = IllegalArgumentException.class)
	public void newIllegalCharacterChromosome(final String genes) {
		final CharSeq characters = new CharSeq("012356789");
		CharacterChromosome chromosome = new CharacterChromosome(genes, characters);

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
	public void objectSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final Object chromosome = new CharacterChromosome(1000);

			final String resource = "/org/jenetics/CharacterChromosome.object";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.object.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}

	@Test
	public void xmlSerializationCompatibility() throws IOException {
		final Random random = new LCG64ShiftRandom.ThreadSafe(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(random);
			final Object chromosome = new CharacterChromosome(1000);

			final String resource = "/org/jenetics/CharacterChromosome.xml";
			try (InputStream in = getClass().getResourceAsStream(resource)) {
				final Object object = IO.xml.read(in);

				Assert.assertEquals(object, chromosome);
			}
		} finally {
			LocalContext.exit();
		}
	}


}




