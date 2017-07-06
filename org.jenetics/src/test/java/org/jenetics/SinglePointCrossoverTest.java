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

import static org.jenetics.TestUtils.newDoubleGenePopulation;
import static org.jenetics.util.RandomRegistry.using;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.LongMomentStatistics;
import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SinglePointCrossoverTest extends AltererTester {

	private static final class ConstRandom extends Random {
		private static final long serialVersionUID = 1L;
		private final int _value;

		public ConstRandom(final int value) {
			_value = value;
		}

		@Override
		public int nextInt() {
			return _value;
		}

		@Override
		public int nextInt(int n) {
			return _value;
		}

	}

	@Override
	public Alterer<DoubleGene, Double> newAlterer(final double p) {
		return new SinglePointCrossover<>(p);
	}

	@Test
	public void crossover() {
		final CharSeq chars = CharSeq.of("a-zA-Z");

		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		final int rv1 = 12;
		using(new ConstRandom(rv1), r -> {
			final SinglePointCrossover<CharacterGene, Double>
			crossover = new SinglePointCrossover<>();

			MSeq<CharacterGene> g1c = g1.copy();
			MSeq<CharacterGene> g2c = g2.copy();
			crossover.crossover(g1c, g2c);

			Assert.assertEquals(g1c.subSeq(0, rv1), g1.subSeq(0, rv1));
			Assert.assertEquals(g1c.subSeq(rv1), g2.subSeq(rv1));
			Assert.assertNotEquals(g1c, g2);
			Assert.assertNotEquals(g2c, g1);

			final int rv2 = 0;
			using(new ConstRandom(rv2), r2 -> {
				MSeq<CharacterGene> g1c2 = g1.copy();
				MSeq<CharacterGene> g2c2 = g2.copy();
				crossover.crossover(g1c2, g2c2);
				Assert.assertEquals(g1c2, g2);
				Assert.assertEquals(g2c2, g1);
				Assert.assertEquals(g1c2.subSeq(0, rv2), g1.subSeq(0, rv2));
				Assert.assertEquals(g1c2.subSeq(rv2), g2.subSeq(rv2));

				final int rv3 = 1;
				using(new ConstRandom(rv3), r3 -> {
					MSeq<CharacterGene> g1c3 = g1.copy();
					MSeq<CharacterGene> g2c3 = g2.copy();
					crossover.crossover(g1c3, g2c3);
					Assert.assertEquals(g1c3.subSeq(0, rv3), g1.subSeq(0, rv3));
					Assert.assertEquals(g1c3.subSeq(rv3), g2.subSeq(rv3));

					final int rv4 = g1.length();
					using(new ConstRandom(rv4), r4 -> {
						MSeq<CharacterGene> g1c4 = g1.copy();
						MSeq<CharacterGene> g2c4 = g2.copy();
						crossover.crossover(g1c4, g2c);
						Assert.assertEquals(g1c4, g1);
						Assert.assertEquals(g2c4, g2);
						Assert.assertEquals(g1c4.subSeq(0, rv4), g1.subSeq(0, rv4));
						Assert.assertEquals(g1c4.subSeq(rv4), g2.subSeq(rv4));
					});
				});
			});
		});
	}

	@Test(dataProvider = "alterProbabilityParameters", groups = {"statistics"})
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final Population<DoubleGene, Double> population = newDoubleGenePopulation(
				ngenes, nchromosomes, npopulation
			);

		// The mutator to test.
		final SinglePointCrossover<DoubleGene, Double> crossover = new SinglePointCrossover<>(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 200;
		final double mean = crossover.getOrder()*npopulation*p;

		final long min = 0;
		final long max = nallgenes;
		final Range<Long> domain = new Range<>(min, max);

		final Histogram<Long> histogram = Histogram.ofLong(min, max, 10);
		final LongMomentStatistics variance = new LongMomentStatistics();

		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accept(alterations);
			variance.accept(alterations);
		}

		// Normal distribution as approximation for binomial distribution.
		System.out.println(histogram);
		// TODO: Implement test
		//assertDistribution(histogram, new NormalDistribution<>(domain, mean, variance.getVariance()));
	}


	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}

}
