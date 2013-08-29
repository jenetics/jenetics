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

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Array;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class CompositeAltererTest {

	public Alterer<Float64Gene> newAlterer(double p) {
		final double p3 = Math.pow(p, 3);
		return new CompositeAlterer<>(
					new Mutator<Float64Gene>(p3),
					new Mutator<Float64Gene>(p3),
					new Mutator<Float64Gene>(p3)
				);
	}

	@Test(dataProvider = "alterCountParameters")
	public void alterCount(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation
	) {
		final Population<Float64Gene, Float64> p1 = population(
					ngenes, nchromosomes, npopulation
				);
		final Population<Float64Gene, Float64> p2 = p1.copy();
		Assert.assertEquals(p2, p1);

		final Alterer<Float64Gene> mutator = newAlterer(0.01);

		Assert.assertEquals(mutator.alter(p1, 1), diff(p1, p2));
	}

	public static Population<Float64Gene, Float64> population(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final Array<Float64Chromosome> chromosomes = new Array<>(nchromosomes);

		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, new Float64Chromosome(0, 10, ngenes));
		}

		final Genotype<Float64Gene> genotype = Genotype.valueOf(chromosomes.toISeq());
		final Population<Float64Gene, Float64> population = new Population<>(npopulation);

		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(genotype.newInstance(), TestUtils.FF, 0));
		}

		return population;
	}

	/*
	 * Count the number of different genes.
	 */
	public int diff(
		final Population<Float64Gene, Float64> p1,
		final Population<Float64Gene, Float64> p2
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
				{ new Integer(1),   new Integer(1),  new Integer(100) },
				{ new Integer(5),   new Integer(1),  new Integer(100) },
				{ new Integer(80),  new Integer(1),  new Integer(100) },
				{ new Integer(1),   new Integer(2),  new Integer(100) },
				{ new Integer(5),   new Integer(2),  new Integer(100) },
				{ new Integer(80),  new Integer(2),  new Integer(100) },
				{ new Integer(1),   new Integer(15), new Integer(100) },
				{ new Integer(5),   new Integer(15), new Integer(100) },
				{ new Integer(80),  new Integer(15), new Integer(100) },

				{ new Integer(1),   new Integer(1),  new Integer(150) },
				{ new Integer(5),   new Integer(1),  new Integer(150) },
				{ new Integer(80),  new Integer(1),  new Integer(150) },
				{ new Integer(1),   new Integer(2),  new Integer(150) },
				{ new Integer(5),   new Integer(2),  new Integer(150) },
				{ new Integer(80),  new Integer(2),  new Integer(150) },
				{ new Integer(1),   new Integer(15), new Integer(150) },
				{ new Integer(5),   new Integer(15), new Integer(150) },
				{ new Integer(80),  new Integer(15), new Integer(150) },

				{ new Integer(1),   new Integer(1),  new Integer(500) },
				{ new Integer(5),   new Integer(1),  new Integer(500) },
				{ new Integer(80),  new Integer(1),  new Integer(500) },
				{ new Integer(1),   new Integer(2),  new Integer(500) },
				{ new Integer(5),   new Integer(2),  new Integer(500) },
				{ new Integer(80),  new Integer(2),  new Integer(500) },
				{ new Integer(1),   new Integer(15), new Integer(500) },
				{ new Integer(5),   new Integer(15), new Integer(500) },
				{ new Integer(80),  new Integer(15), new Integer(500) }
		};
	}
	@Test
	public void join() {
		CompositeAlterer<Float64Gene> alterer = CompositeAlterer.join(
				new Mutator<Float64Gene>(),
				new GaussianMutator<Float64Gene>()
			);

		Assert.assertEquals(alterer.getAlterers().length(), 2);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());

		alterer = CompositeAlterer.join(alterer, new MeanAlterer<Float64Gene>());

		Assert.assertEquals(alterer.getAlterers().length(), 3);
		Assert.assertEquals(alterer.getAlterers().get(0), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new MeanAlterer<Float64Gene>());

		alterer = new CompositeAlterer<>(
				new MeanAlterer<Float64Gene>(),
				new SwapMutator<Float64Gene>(),
				alterer,
				new SwapMutator<Float64Gene>()
			);

		Assert.assertEquals(alterer.getAlterers().length(), 6);
		Assert.assertEquals(alterer.getAlterers().get(0), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(1), new SwapMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(2), new Mutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(3), new GaussianMutator<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(4), new MeanAlterer<Float64Gene>());
		Assert.assertEquals(alterer.getAlterers().get(5), new SwapMutator<Float64Gene>());
	}

}
