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

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.functions;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public class Performance {

	private static final class Perf
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			final double radians = toRadians(gene.doubleValue());
			return Float64.valueOf(Math.log(sin(radians)*cos(radians)));
		}
	}

	public static void main(String[] args) {
		final Perf ff = new Perf();
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(new Float64Chromosome(0, 360));
		final Function<Float64, Float64> fs = functions.Identity();

		final int size = 1000000;
		final Population<Float64Gene, Float64> population = new Population<>(size);
		for (int i = 0; i < size; ++i) {
			final Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(
				gtf.newInstance(), ff, fs, 0
			);
			population.add(pt);
		}

		long start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		long stop = System.currentTimeMillis();
		System.out.println(Measure.valueOf(stop - start, SI.MILLI(SI.SECOND)));

		start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		stop = System.currentTimeMillis();
		System.out.println(Measure.valueOf(stop - start, SI.MILLI(SI.SECOND)));


	}


}





