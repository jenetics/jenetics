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

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Named;

import org.jenetics.stat.Histogram;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class RouletteWheelSelectorTest
	extends ProbabilitySelectorTester<RouletteWheelSelector<DoubleGene, Double>>
{

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
		using(new LCG64ShiftRandom(7345), r -> {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () ->
				Phenotype.of(Genotype.of(IntegerChromosome.of(0, 100)), 1, ff);

			final Population<IntegerGene, Integer> population = IntStream.range(0, 1000)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			final RouletteWheelSelector<IntegerGene, Integer> selector =
				new RouletteWheelSelector<>();

			final double[] p = selector.probabilities(population, 100, Optimize.MINIMUM);
			Assert.assertTrue(RouletteWheelSelector.sum2one(p), Arrays.toString(p) + " != 1");
		});
	}

	@Test
	public void maximize() {
		using(new LCG64ShiftRandom(7345), r -> {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () ->
				Phenotype.of(Genotype.of(IntegerChromosome.of(0, 100)), 1, ff);

			final Population<IntegerGene, Integer> population = IntStream.range(0, 1000)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			final RouletteWheelSelector<IntegerGene, Integer> selector =
				new RouletteWheelSelector<>();

			final double[] p = selector.probabilities(population, 100, Optimize.MAXIMUM);
			Assert.assertTrue(RouletteWheelSelector.sum2one(p), Arrays.toString(p) + " != 1");
		});
	}

	@Test(dataProvider = "expectedDistribution", groups = {"statistics"})
	public void selectDistribution(final Named<double[]> expected, final Optimize opt) {
		retry(3, () -> {
			final int loops = 50;
			final int npopulation = POPULATION_COUNT;

			final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
			using(random, r -> {
				final Histogram<Double> distribution = SelectorTester.distribution(
					new RouletteWheelSelector<>(),
					opt,
					npopulation,
					loops
				);

				assertDistribution(distribution, expected.value, 0.001, 5);
			});
		});
	}

	@DataProvider(name = "expectedDistribution")
	public Object[][] expectedDistribution() {
		final String resource =
			"/org/jenetics/selector/distribution/RouletteWheelSelector";

		return Arrays.stream(Optimize.values())
			.map(opt -> {
				final TestData data = TestData.of(resource, opt.toString());
				final double[] expected = data.stream()
					.map(line -> line[0])
					.mapToDouble(Double::parseDouble)
					.toArray();

				return new Object[]{Named.of("distribution", expected), opt};
			}).toArray(Object[][]::new);
	}

	public static void main(final String[] args) {
		writeDistributionData(Optimize.MAXIMUM);
		writeDistributionData(Optimize.MINIMUM);
	}

	private static void writeDistributionData(final Optimize opt) {
		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		using(random, r -> {
			final int npopulation = POPULATION_COUNT;
			//final int loops = 2_500_000;
			final int loops = 5_000_000;

			printDistributions(
				System.out,
				Arrays.asList(""),
				value -> new RouletteWheelSelector<DoubleGene, Double>(),
				opt,
				npopulation,
				loops
			);
		});
	}

}
