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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static io.jenetics.stat.StatisticsAssert.assertDistribution;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Named;
import io.jenetics.stat.Histogram;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
		using(new Random(7345), r -> {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () -> {
				final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(0, 100));
				return Phenotype.of(gt, 1, gt.getGene().intValue());
			};

			final ISeq<Phenotype<IntegerGene, Integer>> population =
				IntStream.range(0, 1000)
					.mapToObj(i -> ptf.newInstance())
					.collect(ISeq.toISeq());

			final RouletteWheelSelector<IntegerGene, Integer> selector =
				new RouletteWheelSelector<>();

			final double[] p = selector.probabilities(population, 100, Optimize.MINIMUM);
			Assert.assertTrue(RouletteWheelSelector.sum2one(p), Arrays.toString(p) + " != 1");
		});
	}

	@Test
	public void maximize() {
		using(new Random(7345), r -> {
			final Function<Genotype<IntegerGene>, Integer> ff =
				g -> g.getChromosome().getGene().getAllele();

			final Factory<Phenotype<IntegerGene, Integer>> ptf = () -> {
				final Genotype<IntegerGene> gt = Genotype.of(IntegerChromosome.of(0, 100));
				return Phenotype.of(gt, 1, gt.getGene().intValue());
			};

			final ISeq<Phenotype<IntegerGene, Integer>> population =
				IntStream.range(0, 1000)
					.mapToObj(i -> ptf.newInstance())
					.collect(ISeq.toISeq());

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

			final Random random = new Random();
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
			"/io/jenetics/selector/distribution/RouletteWheelSelector";

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
		final Random random = new Random();
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
