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
import static io.jenetics.distassert.Assurance.assertThatObservation;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.distribution.EmpiricalDistribution;
import io.jenetics.distassert.PearsonsChiSquared;
import io.jenetics.internal.util.Named;
import io.jenetics.util.Factory;
import io.jenetics.util.StableRandomExecutor;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TournamentSelectorTest
	extends SelectorTester<TournamentSelector<DoubleGene, Double>>
{

	@Override
	protected Factory<TournamentSelector<DoubleGene, Double>> factory() {
		return () -> new TournamentSelector<>(3);
	}

	@Test(dataProvider = "expectedDistribution")
	public void selectDistribution(
		final Integer tournamentSize,
		final Named<double[]> expected,
		final Optimize opt
	) {
		final var observation = SelectorTester.observation(
			new TournamentSelector<>(tournamentSize),
			opt,
			POPULATION_COUNT,
			50
		);
		new StableRandomExecutor(123451).execute(observation);

		final var distribution = EmpiricalDistribution.of(
			observation.histogram().partition(),
			expected.value
		);

		assertThatObservation(observation)
			.usingHypothesisTester(new PearsonsChiSquared(0.00001))
			.follows(distribution);
	}

	@DataProvider(name = "expectedDistribution")
	public Object[][] expectedDistribution() {
		final String resource =
			"/io/jenetics/selector/distribution/TournamentSelector";

		return Arrays.stream(Optimize.values())
			.flatMap(opt -> {
				final TestData data = TestData.of(resource, opt.toString());
				final double[][] csv = data.stream()
					.map(TestData::toDouble)
					.toArray(double[][]::new);

				final int[] sizes = TestData.toInt(csv[0]);

				return IntStream.range(0, sizes.length)
					.mapToObj(i -> new Object[]{
						sizes[i],
						Named.of(
							format("distribution[%d]", sizes[i]),
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
		using(new Random(), r -> {
			final int npopulation = POPULATION_COUNT;
			//final int loops = 5_000_000;
			final int loops = 100_000;

			printDistributions(
				System.out,
				List.of(2, 3, 4, 5, 6, 7, 13, 23, 37),
				TournamentSelector::new,
				opt,
				npopulation,
				loops
			);
		});
	}


}
