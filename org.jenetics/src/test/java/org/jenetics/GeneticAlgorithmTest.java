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

import static java.lang.String.format;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Concurrency;

import org.jenetics.util.IO;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-08 $</em>
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

	@Test
	public void testMaximize() {
		final Function<Genotype<IntegerGene>, Integer> ff = gt ->
			gt.getChromosome().toSeq().stream()
				.mapToInt(IntegerGene::intValue)
				.sum();

		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
			final GeneticAlgorithm<IntegerGene, Integer> ga = new GeneticAlgorithm<>(
				Genotype.of(IntegerChromosome.of(0, 10_000, 5)),
				ff,
				Optimize.MAXIMUM,
				Concurrency.SERIAL_EXECUTOR
			);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new TournamentSelector<>());

			ga.setup();
			final Phenotype<IntegerGene, Integer> start = ga.getBestPhenotype();
			Phenotype<IntegerGene, Integer> last = start;
			for (int i = 0; i < 1000; ++i) {
				ga.evolve();

				final Phenotype<IntegerGene, Integer> value = ga.getBestPhenotype();
				if (value.compareTo(last) < 0) {
					throw new AssertionError(format(
						"Value %s is smaller than last value %s.", value, last
					));
				}

				last = value;
			}

			if (last.compareTo(start) <= 0) {
				throw new AssertionError(format(
					"Evolved value %s is smaller or equal than start value %s.",
					last, start
				));
			}
		}
	}

	@Test
	public void testMinimize() {
		final Function<Genotype<IntegerGene>, Integer> ff = gt ->
			gt.getChromosome().toSeq().stream()
				.mapToInt(IntegerGene::intValue)
				.sum();

		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
			final GeneticAlgorithm<IntegerGene, Integer> ga = new GeneticAlgorithm<>(
				Genotype.of(IntegerChromosome.of(0, 10_000, 5)),
				ff,
				Optimize.MINIMUM,
				Concurrency.SERIAL_EXECUTOR
			);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new TournamentSelector<>());

			ga.setup();
			final Phenotype<IntegerGene, Integer> start = ga.getBestPhenotype();
			Phenotype<IntegerGene, Integer> last = start;
			for (int i = 0; i < 1000; ++i) {
				ga.evolve();

				final Phenotype<IntegerGene, Integer> value = ga.getBestPhenotype();
				if (value.compareTo(last) > 0) {
					throw new AssertionError(format(
						"Value %s is smaller than last value %s.", value, last
					));
				}

				last = value;
			}

			if (last.compareTo(start) >= 0) {
				throw new AssertionError(format(
					"Evolved value %s is smaller or equal than start value %s.",
					last, start
				));
			}
		}
	}

	public static void main(final String[] args) throws Exception {
		final Function<Genotype<IntegerGene>, Integer> ff = gt ->
			gt.getChromosome().toSeq().stream()
				.mapToInt(c -> c.getAllele())
				.sum();

		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
			final GeneticAlgorithm<IntegerGene, Integer> ga = new GeneticAlgorithm<>(
				Genotype.of(IntegerChromosome.of(0, 10_000, 5)),
				ff,
				Optimize.MINIMUM,
				Concurrency.SERIAL_EXECUTOR
			);
			ga.setPopulationSize(50);

			ga.setup();
			IO.jaxb.write(ga.getBestPhenotype(), System.out);
			for (int i = 0; i < 20; ++i) {
				ga.evolve();
				IO.jaxb.write(ga.getBestPhenotype(), System.out);
			}
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
