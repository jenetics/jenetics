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

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-16 $</em>
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
	protected Distribution<Double> getDistribution() {
		return new LinearDistribution<>(getDomain(), 0);
	}

	@Override
	protected LinearRankSelector<DoubleGene, Double> selector() {
		return new LinearRankSelector<>(0.0);
	}

	public static void main(final String[] args) {
		final Range<Double> domain = new Range<>(0.0, 100.0);
		final int npopulation = 101;
		final int loops = 100_000;

		final Double min = domain.getMin();
		final Double max = domain.getMax();
		final Histogram<Double> histogram = Histogram.of(min, max, 37);

		final Function<Genotype<DoubleGene>, Double> ff = gt -> gt.getGene().getAllele();
		final Factory<Phenotype<DoubleGene, Double>> ptf = () ->
			Phenotype.of(Genotype.of(DoubleChromosome.of(min, max)), ff, 12);

		final Selector<DoubleGene, Double> selector = new LinearRankSelector<>();

		for (int j = 0; j < loops; ++j) {
			final Population<DoubleGene, Double> population = IntStream.range(0, npopulation)
				.mapToObj(i -> ptf.newInstance())
				.collect(Population.toPopulation());

			selector.select(population, npopulation/2, Optimize.MINIMUM).stream()
				.map(pt -> pt.getGenotype().getGene().getAllele())
				.forEach(histogram);
		}

		System.out.println(histogram);
	}
}
