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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IntermediateCrossoverTest {

	@Test
	public void recombine() {
		final Factory<DoubleGene> factory = DoubleGene.of(0, 100);

		final MSeq<DoubleGene> v = MSeq.of(factory::newInstance, 10);
		final MSeq<DoubleGene> w = MSeq.of(factory::newInstance, 10);
		Assert.assertTrue(v.forAll(DoubleGene::isValid));
		Assert.assertTrue(w.forAll(DoubleGene::isValid));

		final var co = new IntermediateCrossover<DoubleGene, Double>(0.1, 0);
		co.crossover(v, w);

		Assert.assertTrue(v.forAll(DoubleGene::isValid));
		Assert.assertTrue(w.forAll(DoubleGene::isValid));
	}

	@Test
	public void populationRecombine() {
		RandomRegistry.with(new Random(123)).run(() -> {
			ISeq<Phenotype<DoubleGene, Double>> pop =
				newDoubleGenePopulation(5, 1, 2);
			final MSeq<Phenotype<DoubleGene, Double>> copy = pop.copy();

			final IntermediateCrossover<DoubleGene, Double> recombinator =
				new IntermediateCrossover<>(1);

			pop = recombinator.alter(pop, 10).population();

			for (int i = 0; i < pop.size(); ++i) {
				final BaseSeq<DoubleGene> genes = pop.get(i)
					.genotype()
					.chromosome();

				final BaseSeq<DoubleGene> genesCopy = copy.get(i)
					.genotype()
					.chromosome();

				for (int j = 0; j < genes.length(); ++j) {
					Assert.assertNotEquals(genes.get(j), genesCopy.get(i));
				}
			}
		});
	}

	// https://github.com/jenetics/jenetics/issues/718
	@Test(timeOut = 1000, dataProvider = "recombinations")
	public void recombineEqualIndividuals(
		final double min,
		final double max,
		final double v1,
		final double v2
	) {
		final MSeq<DoubleGene> u = MSeq.of(
			DoubleGene.of(v1, min, max),
			DoubleGene.of(v1, min, max),
			DoubleGene.of(v1, min, max),
			DoubleGene.of(v1, min, max),
			DoubleGene.of(v1, min, max)
		);
		final MSeq<DoubleGene> v = MSeq.of(
			DoubleGene.of(v2, min, max),
			DoubleGene.of(v2, min, max),
			DoubleGene.of(v2, min, max),
			DoubleGene.of(v2, min, max),
			DoubleGene.of(v2, min, max)
		);

		final var recombinator = new IntermediateCrossover<DoubleGene, Double>();
		recombinator.crossover(v, u);
	}

	@DataProvider
	public Object[][] recombinations() {
		return new Object[][] {
			{0.0, 100.0, 100.0, 100.0},
			{-0.0, 100.0, 100.0, 100.0},
			{0.0, 0.0, 0.0, 0.0},
			{100.0, 100.0, 0.0, 0.0},
			{50.0, 100.0, 10.0, 0.0},
			{50.0, 100.0, 10.0, 70.0},
			{50.0, 100.0, Math.nextDown(100.0), 100.0},
			{50.0, 100.0, Math.nextDown(100.0), Math.nextDown(100.0)},
			{50.0, 100.0, -100000.0, Math.nextUp(50.0)},
			{50.0, -50.0, 50.0, 50.0}
		};
	}

}
