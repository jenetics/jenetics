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

import static org.jenetics.util.RandomRegistry.using;

import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class UniformCrossoverTest extends AltererTester {

	@Override
	public Alterer<DoubleGene, Double> newAlterer(final double p) {
		return new UniformCrossover<>(p);
	}

	@Test
	public void crossover() {
		final ISeq<CharacterGene> g1 = CharacterChromosome.of("1234567890").toSeq();
		final ISeq<CharacterGene> g2 = CharacterChromosome.of("abcdefghij").toSeq();

		final int rv1 = 12;
		using(new Random(10), r -> {
			final UniformCrossover<CharacterGene, Double>
				crossover = new UniformCrossover<>(0.5, 0.5);

			MSeq<CharacterGene> g1c = g1.copy();
			MSeq<CharacterGene> g2c = g2.copy();
			final int changed = crossover.crossover(g1c, g2c);

			Assert.assertEquals(changed,
				IntStream.range(0, g2c.length())
					.filter(i -> Character.isDigit(g2c.get(i).charValue()))
					.count()
			);
		});
	}

	@Test
	public void crossoverChanges() {
		final ISeq<CharacterGene> g1 = CharacterChromosome.of("1234567890").toSeq();
		final ISeq<CharacterGene> g2 = CharacterChromosome.of("abcdefghij").toSeq();

		final int rv1 = 12;
		using(new Random(10), r -> {
			final UniformCrossover<CharacterGene, Double>
				crossover = new UniformCrossover<>(0.5, 0.5);

			final DoubleMomentStatistics statistics = new DoubleMomentStatistics();

			for (int j = 0; j < 1000; ++j) {
				MSeq<CharacterGene> g1c = g1.copy();
				MSeq<CharacterGene> g2c = g2.copy();
				final int changed = crossover.crossover(g1c, g2c);

				Assert.assertEquals(changed,
					IntStream.range(0, g2c.length())
						.filter(i -> Character.isDigit(g2c.get(i).charValue()))
						.count()
				);

				statistics.accept(changed);
			}

			Assert.assertEquals(statistics.getMean(), 5.0, 0.0001);
		});
	}

}
