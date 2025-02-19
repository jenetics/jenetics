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

import static java.lang.String.format;
import static io.jenetics.incubator.stat.StatisticsAssert.assertThatObservation;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Named;
import io.jenetics.util.Factory;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LinearRankSelectorTest
	extends ProbabilitySelectorTester<LinearRankSelector<DoubleGene, Double>>
{

	@Override
	protected boolean isSorted() {
		return true;
	}

	@Override
	protected Factory<LinearRankSelector<DoubleGene, Double>> factory() {
		return LinearRankSelector::new;
	}

	@Override
	protected LinearRankSelector<DoubleGene, Double> selector() {
		return new LinearRankSelector<>(0.0);
	}

	@Test(
		dataProvider = "expectedDistribution",
		invocationCount = 10, successPercentage = 70
	)
	public void selectDistribution(
		final Double nminus,
		final Named<double[]> expected,
		final Optimize opt
	) {
		final var distribution = SelectorTester.distribution(
			new LinearRankSelector<>(nminus),
			opt,
			POPULATION_COUNT,
			50
		);

		assertThatObservation(distribution).isLike(expected.value);
	}

	@DataProvider(name = "expectedDistribution")
	public Object[][] expectedDistribution() {
		final String resource =
			"/io/jenetics/selector/distribution/LinearRankSelector";

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

	public static void main(final String[] args) {
		writeDistributionData(Optimize.MAXIMUM);
		writeDistributionData(Optimize.MINIMUM);
	}

	private static void writeDistributionData(final Optimize opt) {
		final Random random = new Random();
		using(random, r -> {
			final int npopulation = POPULATION_COUNT;
			//final int loops = 2_500_000;
			final int loops = 100_000;

			printDistributions(
				System.out,
				List.of(0.0, 0.1, 0.3, 0.5, 0.75, 0.95, 1.0),
				LinearRankSelector::new,
				opt,
				npopulation,
				loops
			);
		});
	}
}
