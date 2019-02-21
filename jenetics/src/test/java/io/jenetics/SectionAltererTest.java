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

import io.jenetics.SectionAlterer.Section;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SectionAltererTest {

	@Test
	public void split() {
		final Phenotype<DoubleGene, Double> pt = Phenotype.of(
			Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(1, 2),
				DoubleChromosome.of(2, 3),
				DoubleChromosome.of(3, 4),
				DoubleChromosome.of(4, 5),
				DoubleChromosome.of(5, 6),
				DoubleChromosome.of(6, 7),
				DoubleChromosome.of(7, 8),
				DoubleChromosome.of(8, 9),
				DoubleChromosome.of(9, 10)
			),
			0
		);

		final Section section = Section.of(1, 3, 5);

		final Phenotype<DoubleGene, Double> split = section.split(pt);
		Assert.assertEquals(split.getGenotype().length(), 3);
		Assert.assertEquals(split.getGenotype().get(0), pt.getGenotype().get(1));
		Assert.assertEquals(split.getGenotype().get(1), pt.getGenotype().get(3));
		Assert.assertEquals(split.getGenotype().get(2), pt.getGenotype().get(5));
	}

	@Test
	public void merge() {
		final Genotype<DoubleGene> gt = Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(1, 2),
				DoubleChromosome.of(2, 3),
				DoubleChromosome.of(3, 4)
			);

		final ISeq<Phenotype<DoubleGene, Double>> population = gt.instances()
			.limit(3)
			.map(g -> Phenotype.<DoubleGene, Double>of(g, 0))
			.collect(ISeq.toISeq());

		final Section section = Section.of(1, 3);

		final Seq<Phenotype<DoubleGene, Double>> split = section.split(population);
		Assert.assertEquals(split.length(), population.length());
		for (int i = 0; i < population.length(); ++i) {
			Assert.assertEquals(split.get(i).getGenotype().length(), 2);
		}

		final Seq<Phenotype<DoubleGene, Double>> merged = section.merge(split, population);
		Assert.assertEquals(merged, population);
	}

	@Test
	public void alterer() {
		final Genotype<DoubleGene> gt = Genotype.of(
			DoubleChromosome.of(0, 1),
			DoubleChromosome.of(1, 2),
			DoubleChromosome.of(2, 3),
			DoubleChromosome.of(3, 4)
		);

		final ISeq<Phenotype<DoubleGene, Double>> population = gt.instances()
			.limit(3)
			.map(g -> Phenotype.<DoubleGene, Double>of(g, 0))
			.collect(ISeq.toISeq());

		final Alterer<DoubleGene, Double> alterer = SectionAlterer.of(
			new ConstAlterer<DoubleGene, Double>(0.5),
			1, 2
		);

		final AltererResult<DoubleGene, Double> result =
			alterer.alter(population, 10);

		for (int i = 0; i < population.length(); ++i) {
			final Phenotype<DoubleGene, Double> pt1 = population.get(0);
			final Phenotype<DoubleGene, Double> pt2 = result.getPopulation().get(0);

			Assert.assertEquals(pt1.getGenotype().get(0), pt2.getGenotype().get(0));
			Assert.assertNotEquals(pt1.getGenotype().get(1), pt2.getGenotype().get(1));
			Assert.assertNotEquals(pt1.getGenotype().get(2), pt2.getGenotype().get(2));
			Assert.assertEquals(pt1.getGenotype().get(3), pt2.getGenotype().get(3));

			Assert.assertEquals(pt2.getGenotype().get(1).getGene().doubleValue(), 0.5);
			Assert.assertEquals(pt2.getGenotype().get(2).getGene().doubleValue(), 0.5);
		}

	}

	private static final class ConstAlterer<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Alterer<G, C>
	{
		private final C _const;

		ConstAlterer(final C value) {
			_const = value;
		}

		@Override
		public AltererResult<G, C> alter(
			final Seq<Phenotype<G, C>> population,
			final long generation
		) {
			final ISeq<Phenotype<G, C>> pop = population.map(this::mapPt).asISeq();
			return AltererResult.of(pop, pop.length());
		}

		private Phenotype<G, C> mapPt(final Phenotype<G, C> phenotype) {
			return Phenotype.of(mapGt(phenotype.getGenotype()), phenotype.getGeneration());
		}

		private Genotype<G> mapGt(final Genotype<G> genotype) {
			return Genotype.of(
				genotype.stream()
					.map(this::mapCh)
					.collect(ISeq.toISeq())
			);
		}

		@SuppressWarnings("unchecked")
		private Chromosome<G> mapCh(final Chromosome chromosome) {
			return chromosome.newInstance(
				chromosome.toSeq().map(g -> mapGene((G)g))
			);
		}

		@SuppressWarnings("unchecked")
		private G mapGene(final G gene) {
			return (G)((Gene)gene).newInstance(_const);
		}

	}

}
