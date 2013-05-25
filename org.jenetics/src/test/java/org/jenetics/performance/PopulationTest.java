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
package org.jenetics.performance;

import java.io.Serializable;
import java.util.Iterator;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Function;
import org.jenetics.util.arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
@Suite("Population")
public class PopulationTest {

	private int SIZE = 1000000;
	private final int LOOPS = 20;

	private final Population<Float64Gene, Float64> _population = newFloat64GenePopulation(
			1, 1, SIZE
		);

	@Test(1)
	public TestCase iterator = new TestCase("iterator()", LOOPS, SIZE) {
		@Override
		protected void test() {
			for (Iterator<?> it = _population.iterator(); it.hasNext();) {
				it.next();
			}
		}
	};

	@Test(2)
	public TestCase iterable = new TestCase("iterable()", LOOPS, SIZE) {
		@Override
		protected void test() {
			for (@SuppressWarnings("unused") Object value : _population);
		}
	};

	@Test(3)
	public TestCase sort = new TestCase("sort()", LOOPS, SIZE) {

		@Override
		protected void test() {
			_population.sort();
		}

		@Override
		protected void afterTest() {
			arrays.shuffle(_population);
		}
	};


	private static final class Continous
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele();
		}
	}

	private static final Function<Genotype<Float64Gene>, Float64> FF = new Continous();

	private static final Population<Float64Gene, Float64> newFloat64GenePopulation(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final GenotypeBuilder gtb = new GenotypeBuilder();
		gtb.ngenes(ngenes);
		gtb.nchromosomes(nchromosomes);
		gtb.min(0);
		gtb.max(10);

		final Population<Float64Gene, Float64>
		population = new Population<>(npopulation);
		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(gtb.build(), FF, 0));
		}

		return population;
	}

}
