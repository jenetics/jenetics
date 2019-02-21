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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CompositeAltererTest {

	public Alterer<DoubleGene, Double> newAlterer(double p) {
		final double p3 = Math.pow(p, 3);
		return CompositeAlterer.of(
			new Mutator<DoubleGene, Double>(p3),
			new Mutator<DoubleGene, Double>(p3),
			new Mutator<DoubleGene, Double>(p3)
		);
	}

	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final ISeq<Phenotype<DoubleGene, Double>> p1 =
			population(ngenes, nchromosomes, npopulation);

		final Alterer<DoubleGene, Double> mutator = newAlterer(0.01);
		final AltererResult<DoubleGene, Double> result = mutator.alter(p1, 1);

		Assert.assertEquals(
			result.getAlterations(),
			diff(p1, result.getPopulation())
		);
	}

	public static ISeq<Phenotype<DoubleGene, Double>> population(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final MSeq<DoubleChromosome> chromosomes = MSeq.ofLength(nchromosomes);

		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, DoubleChromosome.of(0, 10, ngenes));
		}

		final Genotype<DoubleGene> genotype = new Genotype<>(chromosomes.toISeq());
		final MSeq<Phenotype<DoubleGene, Double>> population = MSeq.ofLength(npopulation);

		for (int i = 0; i < npopulation; ++i) {
			population.set(i, Phenotype.of(genotype.newInstance(), 0));
		}

		return population.toISeq();
	}

	/*
	 * Count the number of different genes.
	 */
	public int diff(
		final Seq<Phenotype<DoubleGene, Double>> p1,
		final Seq<Phenotype<DoubleGene, Double>> p2
	) {
		int count = 0;
		for (int i = 0; i < p1.size(); ++i) {
			final Genotype<?> gt1 = p1.get(i).getGenotype();
			final Genotype<?> gt2 = p2.get(i).getGenotype();

			for (int j = 0; j < gt1.length(); ++j) {
				final Chromosome<?> c1 = gt1.getChromosome(j);
				final Chromosome<?> c2 = gt2.getChromosome(j);

				for (int k = 0; k < c1.length(); ++k) {
					if (!c1.getGene(k).equals(c2.getGene(k))) {
						++count;
					}
				}
			}
		}
		return count;
	}

	@DataProvider(name = "alterCountParameters")
	public Object[][] alterCountParameters() {
		return new Object[][] {
				//    ngenes,       nchromosomes     npopulation
				{ 1,   1,  100 },
				{ 5,   1,  100 },
				{ 80,  1,  100 },
				{ 1,   2,  100 },
				{ 5,   2,  100 },
				{ 80,  2,  100 },
				{ 1,   15, 100 },
				{ 5,   15, 100 },
				{ 80,  15, 100 },

				{ 1,   1,  150 },
				{ 5,   1,  150 },
				{ 80,  1,  150 },
				{ 1,   2,  150 },
				{ 5,   2,  150 },
				{ 80,  2,  150 },
				{ 1,   15, 150 },
				{ 5,   15, 150 },
				{ 80,  15, 150 },

				{ 1,   1,  500 },
				{ 5,   1,  500 },
				{ 80,  1,  500 },
				{ 1,   2,  500 },
				{ 5,   2,  500 },
				{ 80,  2,  500 },
				{ 1,   15, 500 },
				{ 5,   15, 500 },
				{ 80,  15, 500 }
		};
	}

	@Test
	public void join() {
		CompositeAlterer<DoubleGene, Double> alterer = CompositeAlterer.join(
				new Mutator<DoubleGene, Double>(),
				new SwapMutator<DoubleGene, Double>()
			);

		Assert.assertEquals(alterer.getAlterers().length(), 2);
		Assert.assertTrue(alterer.getAlterers().get(0) instanceof Mutator);
		Assert.assertTrue(alterer.getAlterers().get(1) instanceof SwapMutator);

		alterer = CompositeAlterer.join(alterer, new MeanAlterer<>());

		Assert.assertEquals(alterer.getAlterers().length(), 3);
		Assert.assertTrue(alterer.getAlterers().get(0) instanceof Mutator);
		Assert.assertTrue(alterer.getAlterers().get(1) instanceof SwapMutator);
		Assert.assertTrue(alterer.getAlterers().get(2) instanceof MeanAlterer);

		alterer = CompositeAlterer.of(
			new MeanAlterer<>(),
			new SwapMutator<>(),
			alterer,
			new SwapMutator<>()
		);

		Assert.assertEquals(alterer.getAlterers().length(), 6);
	}

}
