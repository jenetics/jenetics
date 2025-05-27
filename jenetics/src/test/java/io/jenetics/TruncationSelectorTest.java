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

import static io.jenetics.distassert.assertion.Assertions.assertThatObservation;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.distribution.EmpiricalDistribution;
import io.jenetics.internal.util.Named;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.StableRandomExecutor;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TruncationSelectorTest
	extends SelectorTester<TruncationSelector<DoubleGene, Double>>
{

	@Override
	protected Factory<TruncationSelector<DoubleGene, Double>> factory() {
		return TruncationSelector::new;
	}

	@Test
	public void worstIndividual() {
		final int size = 20;
		final MSeq<Phenotype<DoubleGene, Integer>> population = MSeq.ofLength(size);
		for (int i = 0; i < size; ++i) {
			final DoubleGene gene = DoubleGene.of(i, 0, size + 10);
			final DoubleChromosome ch = DoubleChromosome.of(gene);
			final Genotype<DoubleGene> gt = Genotype.of(ch);
			final Phenotype<DoubleGene, Integer> pt = Phenotype.of(
				gt, 1, gt.gene().intValue()
			);

			population.set(i, pt);
		}

		final TruncationSelector<DoubleGene, Integer> selector =
			new TruncationSelector<>(5);
		final ISeq<Phenotype<DoubleGene, Integer>> selected =
			selector.select(population.toISeq(), 10, Optimize.MINIMUM);

		for (Phenotype<DoubleGene, Integer> pt : selected) {
			Assert.assertTrue(pt.fitness() < 5);
		}
	}

	@Test(dataProvider = "expectedDistribution")
	public void selectDistribution(final Named<double[]> expected, final Optimize opt) {
		final var observation = SelectorTester.observation(
			new TruncationSelector<>(),
			opt,
			POPULATION_COUNT,
			50
		);
		new StableRandomExecutor(123456).execute(observation);

		final var distribution = EmpiricalDistribution.of(
			observation.histogram().partition(),
			expected.value
		);

		assertThatObservation(observation)
			.follows(distribution);
	}

	@DataProvider(name = "expectedDistribution")
	public Object[][] expectedDistribution() {
		final String resource =
			"/io/jenetics/selector/distribution/TruncationSelector";

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
			final int loops = 100_000;

			printDistributions(
				System.out,
				List.of(""),
				value -> new TruncationSelector<>(),
				opt,
				npopulation,
				loops
			);
		});
	}

}

