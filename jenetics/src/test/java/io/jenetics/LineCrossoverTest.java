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

import static io.jenetics.TestUtils.newDoubleGenePopulation;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class LineCrossoverTest {

	@Test
	public void recombine() {
		final Factory<DoubleGene> factory = DoubleGene.of(0, 100);

		final MSeq<DoubleGene> v = MSeq.of(factory::newInstance, 10);
		final MSeq<DoubleGene> w = MSeq.of(factory::newInstance, 10);

		final LineCrossover<DoubleGene, Double> recombinator =
			new LineCrossover<>();
		recombinator.crossover(v, w);

		Assert.assertTrue(v.forAll(DoubleGene::isValid));
		Assert.assertTrue(w.forAll(DoubleGene::isValid));
	}

	@Test
	public void populationRecombine() {
		RandomRegistry.using(new Random(123), r -> {
			ISeq<Phenotype<DoubleGene, Double>> pop =
				newDoubleGenePopulation(5, 1, 2);
			final MSeq<Phenotype<DoubleGene, Double>> copy = pop.copy();

			final LineCrossover<DoubleGene, Double> recombinator =
				new LineCrossover<>(1);

			pop = recombinator.alter(pop, 10).getPopulation();

			for (int i = 0; i < pop.size(); ++i) {
				final Seq<DoubleGene> genes = pop.get(i)
					.getGenotype()
					.getChromosome()
					.toSeq();

				final Seq<DoubleGene> genesCopy = copy.get(i)
					.getGenotype()
					.getChromosome()
					.toSeq();

				for (int j = 0; j < genes.length(); ++j) {
					Assert.assertNotEquals(genes.get(j), genesCopy.get(i));
				}
			}
		});
	}

}
