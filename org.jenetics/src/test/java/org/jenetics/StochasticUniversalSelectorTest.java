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

import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-12 $</em>
 */
public class StochasticUniversalSelectorTest
	extends ProbabilitySelectorTester<StochasticUniversalSelector<DoubleGene,Double>>
{

	@Override
	protected boolean isSorted() {
		return true;
	}

	@Override
	protected Factory<StochasticUniversalSelector<DoubleGene, Double>> factory() {
		return StochasticUniversalSelector::new;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	@Test
	public void selectMinimum() {
		final Function<Genotype<IntegerGene>, Integer> ff = gt ->
			gt.getChromosome().toSeq().stream()
				.mapToInt(IntegerGene::intValue)
				.sum();

		Factory<Genotype<IntegerGene>> gtf =
			Genotype.of(IntegerChromosome.of(0, 100, 10));

		final Population<IntegerGene, Integer> population = IntStream.range(0, 50)
			.mapToObj(i -> Phenotype.of(gtf.newInstance(), ff, 50))
			.collect(Population.toPopulation());

		final StochasticUniversalSelector<IntegerGene, Integer> selector =
			new StochasticUniversalSelector<>();

		final Population<IntegerGene, Integer> selection = selector.select(
			population, 50, Optimize.MINIMUM
		);
	}

	// TODO: implement select-distribution test.
	@Override
	@Test
	public void selectDistribution() {
		//super.selectDistribution();
	}

}
