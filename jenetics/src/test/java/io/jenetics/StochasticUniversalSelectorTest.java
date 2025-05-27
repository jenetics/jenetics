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

import static io.jenetics.distassert.assertion.Assurance.assertThatObservation;
import static io.jenetics.util.RandomRegistry.using;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.distribution.EmpiricalDistribution;
import io.jenetics.internal.util.Named;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.StableRandomExecutor;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StochasticUniversalSelectorTest
	extends ProbabilitySelectorTester<StochasticUniversalSelector<DoubleGene,Double>>
{

	@Override
	protected boolean isSorted() {
		return false;
	}

	@Override
	protected Factory<StochasticUniversalSelector<DoubleGene, Double>> factory() {
		return StochasticUniversalSelector::new;
	}

	@Test
	public void selectMinimum() {
		final Function<Genotype<IntegerGene>, Integer> ff = gt ->
			gt.chromosome().stream()
				.mapToInt(IntegerGene::intValue)
				.sum();

		Factory<Genotype<IntegerGene>> gtf =
			Genotype.of(IntegerChromosome.of(0, 100, 10));

		final ISeq<Phenotype<IntegerGene, Integer>> population =
			IntStream.range(0, 50)
				.mapToObj(i -> {
					final Genotype<IntegerGene> gt = gtf.newInstance();
					return Phenotype.of(gt, 50, gt.gene().intValue());
				})
				.collect(ISeq.toISeq());

		final StochasticUniversalSelector<IntegerGene, Integer> selector =
			new StochasticUniversalSelector<>();

		final ISeq<Phenotype<IntegerGene, Integer>> selection =
			selector.select(population, 50, Optimize.MINIMUM);
	}

	@Test
	public void selectExpectedPopulation() {
		final ISeq<Phenotype<DoubleGene, Double>> population =
			ISeq.of(0.7, 0.2, 0.1)
				.map(value ->
					Phenotype.of(
						Genotype.of(
							DoubleChromosome.of(new DoubleRange(0, 1), 1)
						),
						50,
						value
					)
				);

		// gives double equal to 1 on the first resolve
		final Random random = new Random(43328395);
		using(random, r -> {
			final StochasticUniversalSelector<DoubleGene, Double> selector = factory().newInstance();
			final ISeq<Phenotype<DoubleGene, Double>> selectedPop =
				selector.select(population, population.size(), Optimize.MAXIMUM);
			Assert.assertEquals(selectedPop.get(0).fitness(), 0.7);
			Assert.assertEquals(selectedPop.get(1).fitness(), 0.7);
			Assert.assertEquals(selectedPop.get(2).fitness(), 0.1);
		});
	}

	@Test(dataProvider = "expectedDistribution")
	public void selectDistribution(final Named<double[]> expected, final Optimize opt) {
		final var observation = SelectorTester.observation(
			new StochasticUniversalSelector<>(),
			opt,
			POPULATION_COUNT,
			50
		);
		new StableRandomExecutor(1).execute(observation);

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
			"/io/jenetics/selector/distribution/StochasticUniversalSelector";

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
		final RandomGenerator random = RandomRegistry.random();
		using(random, r -> {
			final int npopulation = POPULATION_COUNT;
			//final int loops = 2_500_000;
			final int loops = 1_000_000;

			printDistributions(
				System.out,
				List.of(""),
				value -> new StochasticUniversalSelector<>(),
				opt,
				npopulation,
				loops
			);
		});
	}

}
