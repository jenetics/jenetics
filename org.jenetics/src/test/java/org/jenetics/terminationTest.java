/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
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
 * @version <em>$Date$</em>
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
