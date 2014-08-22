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

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-16 $</em>
 */
public class RouletteWheelSelectorTest
	extends ProbabilitySelectorTester<RouletteWheelSelector<DoubleGene, Double>>
{

	@Override
	protected Distribution<Double> getDistribution() {
		return new LinearDistribution<>(getDomain(), 0);
	}

	@Override
	protected boolean isSorted() {
		return false;
	}

	@Override
	protected Factory<RouletteWheelSelector<DoubleGene, Double>> factory() {
		return RouletteWheelSelector::new;
	}

	@Test
	public void minimize() {
		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () ->
				Phenotype.of(Genotype.of(IntegerChromosome.of(0, 100)), ff, 1);

			final Population<IntegerGene, Integer> population = IntStream.range(0, 1000)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			final RouletteWheelSelector<IntegerGene, Integer> selector =
				new RouletteWheelSelector<>();

			final double[] p = selector.probabilities(population, 100, Optimize.MINIMUM);
			Assert.assertTrue(RouletteWheelSelector.sum2one(p), Arrays.toString(p) + " != 1");
		}
	}

	@Test
	public void maximize() {
		try (Scoped<Random> sr = RandomRegistry.scope(new LCG64ShiftRandom(7345))) {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () ->
				Phenotype.of(Genotype.of(IntegerChromosome.of(0, 100)), ff, 1);

			final Population<IntegerGene, Integer> population = IntStream.range(0, 1000)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			final RouletteWheelSelector<IntegerGene, Integer> selector =
				new RouletteWheelSelector<>();

			final double[] p = selector.probabilities(population, 100, Optimize.MAXIMUM);
			Assert.assertTrue(RouletteWheelSelector.sum2one(p), Arrays.toString(p) + " != 1");
		}
	}

}
