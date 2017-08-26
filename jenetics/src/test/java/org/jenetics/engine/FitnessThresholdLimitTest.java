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
package org.jenetics.engine;

import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class FitnessThresholdLimitTest {

	@Test(dataProvider = "testData")
	public void testMaximum(
		final Double threshold,
		final Integer min,
		final Integer max,
		final Optimize opt,
		final Boolean result
	) {
		final FitnessThresholdLimit<Double> limit =
			new FitnessThresholdLimit<>(threshold);

		Assert.assertEquals(
			limit.test(result(min, max, opt)),
			result.booleanValue()
		);
	}

	@DataProvider(name = "testData")
	public Object[][] testData() {
		return new Object[][] {
			{990.0, 0, 900, Optimize.MAXIMUM, true},
			{990.0, 0, 990, Optimize.MAXIMUM, true},
			{990.0, 0, 991, Optimize.MAXIMUM, false},
			{990.0, 800, 991, Optimize.MAXIMUM, false},

			{300.0, 800, 1000, Optimize.MINIMUM, true},
			{300.0, 300, 1000, Optimize.MINIMUM, true},
			{300.0, 299, 1000, Optimize.MINIMUM, false},
			{300.0, 0, 1000, Optimize.MINIMUM, false},
		};
	}

	private static EvolutionResult<DoubleGene, Double> result(
		final int min,
		final int max,
		final Optimize opt
	) {
		return EvolutionResult.of(
			opt,
			population(min, max),
			1L,
			EvolutionDurations.ZERO,
			1,
			1,
			1
		);
	}

	private static Population<DoubleGene, Double> population(
		final int min,
		final int max
	) {
		return IntStream.rangeClosed(min, max)
			.mapToDouble(i -> (double)i)
			.mapToObj(FitnessThresholdLimitTest::phenotype)
			.collect(Population.toPopulation());
	}

	private static Phenotype<DoubleGene, Double> phenotype(final double value) {
		return Phenotype.of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0.0, 1000.0))),
			1,
			a -> a.getGene().getAllele()
		);
	}

}
