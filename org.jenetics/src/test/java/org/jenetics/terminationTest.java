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

import java.util.Collections;
import java.util.LinkedList;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class terminationTest {

	@Test
	public void generation() {
		final GeneticAlgorithm<Float64Gene, Float64> ga = TestUtils.GA();
		ga.setup();
		ga.evolve(termination.Generation(10));
		Assert.assertEquals(ga.getGeneration(), 10);

		ga.evolve(5);
		ga.evolve(termination.Generation(10));
		Assert.assertEquals(ga.getGeneration(), 15);

		ga.evolve(6);
		ga.evolve(termination.Generation(50));
		Assert.assertEquals(ga.getGeneration(), 50);
	}

	static final Function<Genotype<Float64Gene>, Float64> FF =
		new Function<Genotype<Float64Gene>, Float64>()
	{
		@Override
		public Float64 apply(final Genotype<Float64Gene> genotype) {
			final double value = genotype.getChromosome().getGene().doubleValue();
			return Float64.valueOf(Math.sin(value));
		}
	};

	static GeneticAlgorithm<Float64Gene, Float64> GA() {
		return new GeneticAlgorithm<>(
				Genotype.valueOf(new Float64Chromosome(0, 10)), FF
			);
	}

	@Test
	public void steadyState() {
		final int steadyGenerations = 11;
		final LinkedList<Float64> values = new LinkedList<>();
		values.addFirst(Float64.valueOf(-100));

		final GeneticAlgorithm<Float64Gene, Float64> ga = GA();
		ga.setPopulationSize(20);
		ga.setAlterers(
			ga.getAlterer(),
			new Mutator<Float64Gene>(0.999)
		);
		ga.setup();
		values.addFirst(ga.getBestPhenotype().getFitness());

		final Function<Statistics<?, Float64>, Boolean> until =
			termination.<Float64>SteadyFitness(steadyGenerations);

		while (until.apply(ga.getStatistics())) {
			ga.evolve();
			values.addFirst(ga.getBestPhenotype().getFitness());

			if (values.size() > steadyGenerations) {
				values.removeLast();
			}
		}

		Assert.assertEquals(values.size(), steadyGenerations);
		Assert.assertTrue(ga.getGeneration() > steadyGenerations);

		Collections.sort(values);
		Float64 value = values.removeFirst();
		for (Float64 f : values) {
			Assert.assertEquals(f, value);
		}

	}

}
