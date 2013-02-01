/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;

import javolution.context.ConcurrentContext;
import javolution.context.LocalContext;

import org.jenetics.util.Factory;
import org.jenetics.util.ForkJoinContext;
import org.jenetics.util.RandomRegistry;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-02-01 $</em>
 */
public class GeneticAlgorithmTest {

	static {
		ForkJoinContext.setForkkJoinPool(new ForkJoinPool(5));
	}

	private static class FF
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 618089611921083000L;

		@Override
		public Float64 apply(final Genotype<Float64Gene> genotype) {
			return genotype.getGene().getAllele();
		}
	}

	@Test
	public void optimize() {
		final int concurrency = ConcurrentContext.getConcurrency();
		ConcurrentContext.setConcurrency(0);
		LocalContext.enter();
		try {
			RandomRegistry.setRandom(new Random(12345));

			final Factory<Genotype<Float64Gene>> factory = Genotype.valueOf(new Float64Chromosome(0, 1));
			final Function<Genotype<Float64Gene>, Float64> ff = new FF();

			final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<Float64Gene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<Float64Gene, Float64>());
			ga.setSurvivorSelector(new TournamentSelector<Float64Gene, Float64>());

			ga.setup();
			ga.evolve(100);

			Statistics<Float64Gene, Float64> s = ga.getBestStatistics();
			Reporter.log(s.toString());
			Assert.assertEquals(s.getAgeMean(), 20.775000000000002);
			Assert.assertEquals(s.getAgeVariance(), 363.4918341708541);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness().doubleValue(), 0.997787124427267, 0.00000001);
			Assert.assertEquals(s.getWorstFitness().doubleValue(), 0.0326815029742894, 0.00000001);

			s = ga.getStatistics();
			Reporter.log(s.toString());

			Assert.assertEquals(s.getAgeMean(), 23.550000000000008, 0.000001);
			Assert.assertEquals(s.getAgeVariance(), 76.31909547738691, 0.000001);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness().doubleValue(), 0.997787124427267, 0.00000001);
			Assert.assertEquals(s.getWorstFitness().doubleValue(), 0.997787124427267, 0.00000001);

//			Assert.assertEquals(s.getAgeMean(), 39.175000000000026, 0.000001);
//			Assert.assertEquals(s.getAgeVariance(), 366.18530150753793, 0.000001);
//			Assert.assertEquals(s.getSamples(), 200);
//			Assert.assertEquals(s.getBestFitness().doubleValue(), 0.9800565233548408, 0.00000001);
//			Assert.assertEquals(s.getWorstFitness().doubleValue(), 0.9800565233548408, 0.00000001);
		} finally {
			ConcurrentContext.setConcurrency(concurrency);
			LocalContext.exit();
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
		Predicate<Statistics<? extends Float64Gene, ? extends Base>> until = null;
		GeneticAlgorithm<Float64Gene, Derived> ga = null;

		ga.evolve(until);
		ga.evolve(termination.Generation(1));

		GeneticAlgorithm<Float64Gene, Float64> ga2 = null;
		ga2.evolve(termination.<Float64>SteadyFitness(10));
	}

	@Test(invocationCount = 10)
	public void evolveForkJoinPool() {
		final ForkJoinPool pool = new ForkJoinPool(10);

		try {
			final Factory<Genotype<Float64Gene>> factory = Genotype.valueOf(new Float64Chromosome(-1, 1));
			final Function<Genotype<Float64Gene>, Float64> ff = new FF();

			final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<Float64Gene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<Float64Gene, Float64>());
			ga.setSurvivorSelector(new StochasticUniversalSelector<Float64Gene, Float64>());

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
			final Factory<Genotype<Float64Gene>> factory = Genotype.valueOf(new Float64Chromosome(-1, 1));
			final Function<Genotype<Float64Gene>, Float64> ff = new FF();

			final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<Float64Gene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new BoltzmannSelector<Float64Gene, Float64>(0.001));
			ga.setSurvivorSelector(new ExponentialRankSelector<Float64Gene, Float64>(0.675));

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
		final Factory<Genotype<Float64Gene>> factory = Genotype.valueOf(new Float64Chromosome(-1, 1));
		final Function<Genotype<Float64Gene>, Float64> ff = new FF();

		final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(factory, ff);
		ga.setPopulationSize(1000);
		ga.setAlterer(new MeanAlterer<Float64Gene>());
		ga.setOffspringFraction(0.3);
		ga.setOffspringSelector(new RouletteWheelSelector<Float64Gene, Float64>());
		ga.setSurvivorSelector(new LinearRankSelector<Float64Gene, Float64>());

		ga.setup();
		for (int i = 0; i < 10; ++i) {
			ga.evolve();
		}
	}

}




