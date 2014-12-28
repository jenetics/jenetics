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

//import static java.lang.String.format;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.ForkJoinPool;
//import java.util.function.Function;
//
//import org.testng.Assert;
//import org.testng.Reporter;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import org.jenetics.internal.util.Concurrency;
//
//import org.jenetics.util.Factory;
//import org.jenetics.util.LCG64ShiftRandom;
//import org.jenetics.util.RandomRegistry;
//import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-09-17 $</em>
 */
public class GeneticAlgorithmTest {

//	@Test
//	public void optimize() {
//		final Random random = new Random(123456);
//		try (Scoped<Random> rs = RandomRegistry.scope(random)) {
//			Assert.assertSame(random, RandomRegistry.getRandom());
//			Assert.assertSame(random, rs.get());
//
//			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(
//				Genotype.of(DoubleChromosome.of(0, 1)),
//				gt -> gt.getGene().getAllele(),
//				Concurrency.SERIAL_EXECUTOR
//			);
//			ga.setPopulationSize(200);
//			ga.setAlterer(new MeanAlterer<>());
//			ga.setOffspringFraction(0.3);
//			ga.setOffspringSelector(new RouletteWheelSelector<>());
//			ga.setSurvivorSelector(new TournamentSelector<>());
//
//			ga.setup();
//			ga.evolve(100);
//
//			Statistics<DoubleGene, Double> s = ga.getBestStatistics();
//			Reporter.log(s.toString());
//			Assert.assertEquals(s.getAgeMean(), 19.895);
//			Assert.assertEquals(s.getAgeVariance(), 521.9135427135678);
//			Assert.assertEquals(s.getSamples(), 200);
//			Assert.assertEquals(s.getBestFitness(), 0.9928706649747477, 0.00000001);
//			Assert.assertEquals(s.getWorstFitness(), 0.02638061798078739, 0.00000001);
//
//			s = ga.getStatistics();
//			Reporter.log(s.toString());
//		}
//	}
//
//	@Test(dataProvider = "configuration")
//	public void testMaximize(
//		final Factory<Genotype<IntegerGene>> gtf,
//		final Selector<IntegerGene, Long> survivorsSelector,
//		final Selector<IntegerGene, Long> offspringSelector,
//		final Alterer<IntegerGene, Long> alterer,
//		final Double offspringFraction,
//		final Integer populationSize
//	) {
//		final Function<Genotype<IntegerGene>, Long> ff = gt -> {
//			long sum = 0;
//			for (int i = 0, n = gt.length(); i < n; ++i) {
//				final Chromosome<IntegerGene> ch = gt.getChromosome(i);
//				for (int j = 0, m = ch.length(); j < m; ++j) {
//					sum += ch.getGene(j).longValue()*(i + 1)*(j + 1);
//				}
//			}
//
//			return sum;
//		};
//
//		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
//			final GeneticAlgorithm<IntegerGene, Long> ga = new GeneticAlgorithm<>(
//				gtf,
//				ff,
//				Optimize.MAXIMUM,
//				Concurrency.SERIAL_EXECUTOR
//			);
//			ga.setPopulationSize(populationSize);
//			ga.setAlterer(alterer);
//			ga.setOffspringFraction(offspringFraction);
//			ga.setOffspringSelector(offspringSelector);
//			ga.setSurvivorSelector(survivorsSelector);
//
//			ga.setup();
//			final Phenotype<IntegerGene, Long> start = ga.getBestPhenotype();
//			Phenotype<IntegerGene, Long> last = start;
//			for (int i = 0; i < 500; ++i) {
//				ga.evolve();
//
//				final Phenotype<IntegerGene, Long> value = ga.getBestPhenotype();
//				if (value.compareTo(last) < 0) {
//					throw new AssertionError(format(
//						"Value %s is smaller than last value %s.", value, last
//					));
//				}
//
//				last = value;
//			}
//
//			if (last.compareTo(start) <= 0) {
//				throw new AssertionError(format(
//					"Evolved value %s is smaller or equal than start value %s.",
//					last, start
//				));
//			}
//		}
//	}
//
//	@Test(dataProvider = "configuration")
//	public void testMinimize(
//		final Factory<Genotype<IntegerGene>> gtf,
//		final Selector<IntegerGene, Long> survivorsSelector,
//		final Selector<IntegerGene, Long> offspringSelector,
//		final Alterer<IntegerGene, Long> alterer,
//		final Double offspringFraction,
//		final Integer populationSize
//	) {
//		final Function<Genotype<IntegerGene>, Long> ff = gt -> {
//			long sum = 0;
//			for (int i = 0, n = gt.length(); i < n; ++i) {
//				final Chromosome<IntegerGene> ch = gt.getChromosome(i);
//				for (int j = 0, m = ch.length(); j < m; ++j) {
//					sum += ch.getGene(j).longValue()*(i + 1)*(j + 1);
//				}
//			}
//
//			return sum;
//		};
//
//		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
//			final GeneticAlgorithm<IntegerGene, Long> ga = new GeneticAlgorithm<>(
//				gtf,
//				ff,
//				Optimize.MINIMUM,
//				Concurrency.SERIAL_EXECUTOR
//			);
//			ga.setPopulationSize(populationSize);
//			ga.setAlterer(alterer);
//			ga.setOffspringFraction(offspringFraction);
//			ga.setOffspringSelector(offspringSelector);
//			ga.setSurvivorSelector(survivorsSelector);
//
//			ga.setup();
//			final Phenotype<IntegerGene, Long> start = ga.getBestPhenotype();
//			Phenotype<IntegerGene, Long> last = start;
//			for (int i = 0; i < 500; ++i) {
//				ga.evolve();
//
//				final Phenotype<IntegerGene, Long> value = ga.getBestPhenotype();
//				if (value.compareTo(last) > 0) {
//					throw new AssertionError(format(
//						"Generation %d: value %s is smaller than last value %s.",
//						i, value, last
//					));
//				}
//
//				last = value;
//			}
//
//			if (last.compareTo(start) >= 0) {
//				throw new AssertionError(format(
//					"Evolved value %s is smaller or equal than start value %s.",
//					last, start
//				));
//			}
//		}
//	}
//
//	@DataProvider(name = "configuration")
//	public Object[][] configuration() {
//		List<Factory<Genotype<IntegerGene>>> factories = null;
//		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(17345))) {
//			factories = Arrays.asList(
//				Genotype.of(IntegerChromosome.of(0, 100_000, 25)),
//				Genotype.of(IntegerChromosome.of(0, 100_000, 50))
//			);
//		}
//		final List<Selector<IntegerGene, Long>> selectors = Arrays.asList(
//			new RouletteWheelSelector<>(),
//			new TournamentSelector<>(),
//			new BoltzmannSelector<>(),
//			new ExponentialRankSelector<>(0.12),
//			new LinearRankSelector<>(0.12),
//			new MonteCarloSelector<>(),
//			new StochasticUniversalSelector<>(),
//			new TournamentSelector<>(),
//			new TruncationSelector<>()
//		);
//		final List<Alterer<IntegerGene, Long>> alterers = Arrays.asList(
//			CompositeAlterer.<IntegerGene, Long>of(new GaussianMutator<>(), new Mutator<>(0.1)),
//			CompositeAlterer.<IntegerGene, Long>of(new MeanAlterer<>(), new Mutator<>(0.1)),
//			CompositeAlterer.<IntegerGene, Long>of(new MultiPointCrossover<>(), new Mutator<>(0.1)),
//			CompositeAlterer.<IntegerGene, Long>of(new SwapMutator<>(), new Mutator<>(0.1)),
//			CompositeAlterer.<IntegerGene, Long>of(new SinglePointCrossover<>(), new Mutator<>(0.1))
//		);
//		final List<Double> fractions = Arrays.asList(0.4, 0.7);
//		final List<Integer> sizes = Arrays.asList(70, 150);
//
//		final List<Object[]> result = new ArrayList<>();
//		for (Object gtf : factories) {
//			for (Object selector : selectors) {
//				for (Object alterer : alterers) {
//					for (Object fraction : fractions) {
//						for (Object size : sizes) {
//							result.add(new Object[] {
//								gtf, selector, selector, alterer, fraction, size
//							});
//						}
//					}
//				}
//			}
//		}
//
//		return result.toArray(new Object[0][]);
//	}
//
//	@Test(invocationCount = 10)
//	public void evolveForkJoinPool() {
//		final ForkJoinPool pool = new ForkJoinPool(10);
//
//		try {
//			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(
//				Genotype.of(DoubleChromosome.of(-1, 1)),
//				gt -> gt.getGene().getAllele(),
//				pool
//			);
//			ga.setPopulationSize(1000);
//			ga.setAlterer(new MeanAlterer<>());
//			ga.setOffspringFraction(0.3);
//			ga.setOffspringSelector(new RouletteWheelSelector<>());
//			ga.setSurvivorSelector(new StochasticUniversalSelector<>());
//
//			ga.setup();
//			for (int i = 0; i < 10; ++i) {
//				ga.evolve();
//			}
//		} finally {
//			pool.shutdown();
//		}
//	}

}
