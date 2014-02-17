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

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import javolution.context.ConcurrentContext;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-17 $</em>
 */
public class GeneticAlgorithmTest {

	private static class FF
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 618089611921083000L;

		@Override
		public Double apply(final Genotype<DoubleGene> genotype) {
			return genotype.getGene().getAllele();
		}
	}

	@Test
	public void optimize() {
		final int concurrency = ConcurrentContext.getConcurrency();
		try (Scoped<Random> scope = RandomRegistry.scope(new Random(12345))) {
			ConcurrentContext.setConcurrency(0);
			RandomRegistry.setRandom(new Random(123456));

			final Factory<Genotype<DoubleGene>> factory = Genotype.of(
				DoubleChromosome.of(0, 1)
			);
			final Function<Genotype<DoubleGene>, Double> ff = new FF();

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<DoubleGene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<DoubleGene, Double>());
			ga.setSurvivorSelector(new TournamentSelector<DoubleGene, Double>());

			ga.setup();
			ga.evolve(100);

			Statistics<DoubleGene, Double> s = ga.getBestStatistics();
			Reporter.log(s.toString());
			Assert.assertEquals(s.getAgeMean(), 21.40500000000002);
			Assert.assertEquals(s.getAgeVariance(), 648.051231155779);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness().doubleValue(), 0.9955101231254028, 0.00000001);
			Assert.assertEquals(s.getWorstFitness().doubleValue(), 0.03640144995042627, 0.00000001);

			s = ga.getStatistics();
			Reporter.log(s.toString());

			Assert.assertEquals(s.getAgeMean(), 23.15500000000001, 0.000001);
			Assert.assertEquals(s.getAgeVariance(), 82.23213567839196, 0.000001);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness().doubleValue(), 0.9955101231254028, 0.00000001);
			Assert.assertEquals(s.getWorstFitness().doubleValue(), 0.9955101231254028, 0.00000001);
		} finally {
			ConcurrentContext.setConcurrency(concurrency);
		}

	}

	private static class Base implements Comparable<Base> {
		@Override public int compareTo(Base o) {
			return 0;
		}
	}

	public static class Derived extends Base {
	}

	@SuppressWarnings("null")
	public void evolve() {
		Function<Statistics<? extends DoubleGene, ? extends Base>, Boolean> until = null;
		GeneticAlgorithm<DoubleGene, Derived> ga = null;

		ga.evolve(until);
		ga.evolve(termination.Generation(1));

		GeneticAlgorithm<DoubleGene, Double> ga2 = null;
		ga2.evolve(termination.<Double>SteadyFitness(10));
	}

	@Test(invocationCount = 10)
	public void evolveForkJoinPool() {
		final ForkJoinPool pool = new ForkJoinPool(10);

		try {
			final Factory<Genotype<DoubleGene>> factory = Genotype.of(DoubleChromosome.of(-1, 1));
			final Function<Genotype<DoubleGene>, Double> ff = new FF();

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<DoubleGene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<DoubleGene, Double>());
			ga.setSurvivorSelector(new StochasticUniversalSelector<DoubleGene, Double>());

			ga.setup();
			for (int i = 0; i < 10; ++i) {
				ga.evolve();
			}
		} finally {
			pool.shutdown();
		}
	}

	@Test(invocationCount = 10)
	public void evolveThreadPool() {
		final ExecutorService pool = Executors.newFixedThreadPool(10);

		try {
			final Factory<Genotype<DoubleGene>> factory = Genotype.of(DoubleChromosome.of(-1, 1));
			final Function<Genotype<DoubleGene>, Double> ff = new FF();

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<DoubleGene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new BoltzmannSelector<DoubleGene, Double>(0.001));
			ga.setSurvivorSelector(new ExponentialRankSelector<DoubleGene, Double>(0.675));

			ga.setup();
			for (int i = 0; i < 10; ++i) {
				ga.evolve();
			}
		} finally {
			pool.shutdown();
		}
	}

	@Test(invocationCount = 10)
	public void evolveConcurrent() {
		final Factory<Genotype<DoubleGene>> factory = Genotype.of(DoubleChromosome.of(-1, 1));
		final Function<Genotype<DoubleGene>, Double> ff = new FF();

		final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
		ga.setPopulationSize(1000);
		ga.setAlterer(new MeanAlterer<DoubleGene>());
		ga.setOffspringFraction(0.3);
		ga.setOffspringSelector(new RouletteWheelSelector<DoubleGene, Double>());
		ga.setSurvivorSelector(new LinearRankSelector<DoubleGene, Double>());

		ga.setup();
		for (int i = 0; i < 10; ++i) {
			ga.evolve();
		}
	}

}
