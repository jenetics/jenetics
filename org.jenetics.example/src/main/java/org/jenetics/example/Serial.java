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

import java.io.File;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Chromosome;
import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.util.Function;
import org.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public class Serial {

	static class Id implements Function<Genotype<Float64Gene>, Float64> {
		@Override
		public Float64 apply(final Genotype<Float64Gene> value) {
			double result = 0;
			for (Chromosome<Float64Gene> c : value) {
				for (Float64Gene g : c) {
					result += Math.sin(g.getAllele().doubleValue());
				}
			}
			return Float64.valueOf(result);
		}
	}

	static class Scaler implements Function<Float64, Float64> {
		@Override
		public Float64 apply(Float64 value) {
			return value.times(Math.PI);
		}
	}

	public static void main(final String[] args) throws Exception {
		final Genotype<Float64Gene> genotype = Genotype.valueOf(
			    new Float64Chromosome(0.0, 1.0, 8),
			    new Float64Chromosome(1.0, 2.0, 10),
			    new Float64Chromosome(0.0, 10.0, 9),
			    new Float64Chromosome(0.1, 0.9, 5)
			);

		final GeneticAlgorithm<Float64Gene, Float64> ga = new GeneticAlgorithm<>(genotype, new Id(), new Scaler());
		ga.setPopulationSize(5);
		ga.setup();
		ga.evolve(5);

		IO.xml.write(ga.getPopulation(), new File("/home/fwilhelm/population.xml"));

	}

}




