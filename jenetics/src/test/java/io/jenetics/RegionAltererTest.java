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

import io.jenetics.RegionAlterer.Section;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RegionAltererTest {

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

}
