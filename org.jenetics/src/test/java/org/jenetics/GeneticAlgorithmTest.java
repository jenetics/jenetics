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

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Concurrency;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-06-02 $</em>
 */
public class GeneticAlgorithmTest {

	@Test
	public void optimize() {
		final Random random = new Random(123456);
		try (Scoped<Random> rs = RandomRegistry.scope(random)) {
			Assert.assertSame(random, RandomRegistry.getRandom());
			Assert.assertSame(random, rs.get());

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> gt.getGene().getAllele(),
				Concurrency.SERIAL_EXECUTOR
			);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new TournamentSelector<>());

			ga.setup();
			ga.evolve(100);

			Statistics<DoubleGene, Double> s = ga.getBestStatistics();
			Reporter.log(s.toString());
			Assert.assertEquals(s.getAgeMean(), 19.895);
			Assert.assertEquals(s.getAgeVariance(), 521.9135427135678);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness(), 0.9928706649747477, 0.00000001);
			Assert.assertEquals(s.getWorstFitness(), 0.02638061798078739, 0.00000001);

			s = ga.getStatistics();
			Reporter.log(s.toString());
		}
	}

	@Test(invocationCount = 10)
	public void evolveForkJoinPool() {
		final ForkJoinPool pool = new ForkJoinPool(10);

		try {
			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(
				Genotype.of(DoubleChromosome.of(-1, 1)),
				gt -> gt.getGene().getAllele(),
				pool
			);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new StochasticUniversalSelector<>());

			ga.setup();
			for (int i = 0; i < 10; ++i) {
				ga.evolve();
			}
		} finally {
			pool.shutdown();
		}
	}

}
