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
package org.jenetics.example;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.Serializable;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.functions;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-12 $</em>
 */
public class Performance {

	private static final class Perf
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Double apply(final Genotype<DoubleGene> genotype) {
			final DoubleGene gene = genotype.getChromosome().getGene(0);
			final double radians = toRadians(gene.doubleValue());
			return Math.log(sin(radians)*cos(radians));
		}
	}

	public static void main(String[] args) {
		final Perf ff = new Perf();
		final Factory<Genotype<DoubleGene>> gtf = Genotype.of(DoubleChromosome.of(0, 360));
		final Function<Double, Double> fs = functions.Identity();

		final int size = 1000000;
		final Population<DoubleGene, Double> population = new Population<>(size);
		for (int i = 0; i < size; ++i) {
			final Phenotype<DoubleGene, Double> pt = Phenotype.of(
				gtf.newInstance(), ff, fs, 0
			);
			population.add(pt);
		}

		long start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);

		start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		stop = System.currentTimeMillis();
		System.out.println(stop - start);


	}


}
