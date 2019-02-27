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

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EliteSelectorTest
	extends SelectorTester<EliteSelector<DoubleGene, Double>>
{

	@Override
	protected Factory<EliteSelector<DoubleGene, Double>> factory() {
		return EliteSelector::new;
	}

	@Test
	public void selectMaximum() {
		final MSeq<Phenotype<DoubleGene, Double>> population = MSeq.ofLength(100);
		for (int i = 0, n = population.size(); i < n; ++i) {
			final Genotype<DoubleGene> gt =
				Genotype.of(DoubleChromosome.of(DoubleGene.of(i, 0, n)));

			population.set(i, Phenotype.of(gt, 12, gt.getGene().doubleValue()));
		}

		final EliteSelector<DoubleGene, Double> selector = new EliteSelector<>();
		final ISeq<Phenotype<DoubleGene, Double>> selected =
			selector.select(population, 10, Optimize.MAXIMUM);

		Assert.assertEquals(
			population.get(population.length() - 1).getGenotype().getGene().doubleValue(),
			selected.get(0).getGenotype().getGene().doubleValue()
		);
	}

	@Test
	public void selectMinimum() {
		final MSeq<Phenotype<DoubleGene, Double>> population = MSeq.ofLength(100);
		for (int i = 0, n = population.size(); i < n; ++i) {
			final Genotype<DoubleGene> gt =
				Genotype.of(DoubleChromosome.of(DoubleGene.of(i, 0, n)));

			population.set(i, Phenotype.of(gt, 12, gt.getGene().doubleValue()));
		}

		final EliteSelector<DoubleGene, Double> selector = new EliteSelector<>();
		final ISeq<Phenotype<DoubleGene, Double>> selected =
			selector.select(population, 10, Optimize.MINIMUM);

		Assert.assertEquals(
			population.get(0).getGenotype().getGene().doubleValue(),
			selected.get(0).getGenotype().getGene().doubleValue()
		);
	}

}
