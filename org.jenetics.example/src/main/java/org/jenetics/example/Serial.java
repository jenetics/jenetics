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
 * @version 1.0 &mdash; <em>$Date$</em>
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




