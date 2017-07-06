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
import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.stream.IntStream;

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
public class BoltzmannSelectorTest
	extends ProbabilitySelectorTester<BoltzmannSelector<DoubleGene, Double>>
{

	@Override
	protected boolean isSorted() {
		return false;
	}

	@Override
	protected Factory<BoltzmannSelector<DoubleGene, Double>> factory() {
		return BoltzmannSelector::new;
	}

	@Test
	public void parameters() {
		final BoltzmannSelector<DoubleGene, Double> selector = new BoltzmannSelector<>(2);

		// Create population with zero fitness.
		final Population<DoubleGene, Double> population =
			TestUtils.newDoublePopulation(20, 0, 0);

		// Must select without exception.
		selector.probabilities(population, 10);
	}

	@Test(dataProvider = "expectedDistribution", groups = {"statistics"})
	public void selectDistribution(
		final Double b,
		final Named<double[]> expected,
		final Optimize opt
	) {
		retry(3, () -> {
			final int loops = 50;
			final int npopulation = POPULATION_COUNT;

			final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
			using(random, r -> {
				final Histogram<Double> distribution = SelectorTester.distribution(
					new BoltzmannSelector<>(b),
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
			"/org/jenetics/selector/distribution/BoltzmannSelector";

		return Arrays.stream(Optimize.values())
			.flatMap(opt -> {
				final TestData data = TestData.of(resource, opt.toString());
				final double[][] csv = data.stream()
					.map(TestData::toDouble)
					.toArray(double[][]::new);

				return IntStream.range(0, csv[0].length)
					.mapToObj(i -> new Object[]{
						csv[0][i],
						Named.of(
							format("distribution[%f]", csv[0][i]),
							expected(csv, i)
						),
						opt
					});
			}).toArray(Object[][]::new);
	}

	private static double[] expected(final double[][] csv, final int c) {
		final double[] col = new double[csv.length - 1];
		for (int i = 0; i < col.length; ++i) {
			col[i] = Math.max(csv[i + 1][c], Double.MIN_VALUE);
		}
		return col;
	}

	/*
	public static void main(final String[] args) {
		writeDistributionData(Optimize.MAXIMUM);
		writeDistributionData(Optimize.MINIMUM);
	}
	*/

	private static void writeDistributionData(final Optimize opt) {
		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		using(random, r -> {
			final int npopulation = POPULATION_COUNT;
			//final int loops = 2_500_000;
			final int loops = 100_000;

			printDistributions(
				System.out,
				Arrays.asList(-4.0, -2.0, 0.0, 2.0, 3.0, 5.0),
				BoltzmannSelector::new,
				opt,
				npopulation,
				loops
			);
		});
	}

}
