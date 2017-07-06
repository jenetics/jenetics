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

import java.io.Serializable;
import java.util.Random;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.lists;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class PopulationTest
	extends ObjectTester<Population<DoubleGene, Double>>
{

	private static final Function<Genotype<DoubleGene>, Double> FF =
		(Function<Genotype<DoubleGene>, Double> & Serializable)
			gt -> gt.getGene().getAllele();

	private static final Function<Double, Double> FS =
		(Function<Double, Double> & Serializable)d -> d;

	private static Phenotype<DoubleGene, Double> pt(double value) {
		return Phenotype.of(Genotype.of(DoubleChromosome.of(DoubleGene.of(value, 0, 10))), 0, FF, FS);
	}

	@Override
	protected Factory<Population<DoubleGene, Double>> factory() {
		return () -> {
			final Random random = RandomRegistry.getRandom();
			final Genotype<DoubleGene> gt = Genotype.of(DoubleChromosome.of(0, 1));

			return new Population<DoubleGene, Double>(100)
				.fill(() -> Phenotype.of(gt.newInstance(), 1, FF, FS), 100);
		};
	}

	@Test
	public void sort() {
		final Population<DoubleGene, Double> population = new Population<>();
		for (int i = 0; i < 100; ++i) {
			population.add(pt(Math.random()*9.0));
		}

		population.populationSort();
		for (int i = 0; i < population.size() - 1; ++i) {
			Double first = FF.apply(population.get(i).getGenotype());
			Double second = FF.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0);
		}

		lists.shuffle(population);
		population.sortWith(Optimize.MAXIMUM.<Double>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Double first = FF.apply(population.get(i).getGenotype());
			Double second = FF.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) >= 0, first + "<" + second);
		}

		lists.shuffle(population);
		population.sortWith(Optimize.MINIMUM.<Double>descending());
		for (int i = 0; i < population.size() - 1; ++i) {
			Double first = FF.apply(population.get(i).getGenotype());
			Double second = FF.apply(population.get(i + 1).getGenotype());
			Assert.assertTrue(first.compareTo(second) <= 0, first + ">" + second);
		}
	}

}
