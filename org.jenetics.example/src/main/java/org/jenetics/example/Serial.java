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

import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.util.Function;
import org.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-07 $</em>
 */
public class Serial {

	static class Id implements Function<Genotype<DoubleGene>, Double> {
		@Override
		public Double apply(final Genotype<DoubleGene> value) {
			double result = 0;
			for (Chromosome<DoubleGene> c : value) {
				for (DoubleGene g : c) {
					result += Math.sin(g.getAllele().doubleValue());
				}
			}
			return result;
		}
	}

	static class Scaler implements Function<Double, Double> {
		@Override
		public Double apply(Double value) {
			return value*Math.PI;
		}
	}

	public static void main(final String[] args) throws Exception {
		final Genotype<DoubleGene> genotype = Genotype.of(
			new DoubleChromosome(0.0, 1.0, 8),
			new DoubleChromosome(1.0, 2.0, 10),
			new DoubleChromosome(0.0, 10.0, 9),
			new DoubleChromosome(0.1, 0.9, 5)
		);

		final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(genotype, new Id(), new Scaler());
		ga.setPopulationSize(5);
		ga.setup();
		ga.evolve(5);

		IO.jaxb.write(ga.getPopulation(), new File("/home/fwilhelm/population.xml"));

	}

}
