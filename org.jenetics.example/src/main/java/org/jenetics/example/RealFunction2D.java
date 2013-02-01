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

import java.util.function.Function;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.GaussianMutator;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.TournamentSelector;
import org.jenetics.util.Factory;

final class Real2D implements Function<Genotype<Float64Gene>, Float64> {
	@Override
	public Float64 apply(final Genotype<Float64Gene> gt) {
		final Float64 x1 = gt.getChromosome(0).getGene().getAllele();
		final Float64 x2 = gt.getChromosome(1).getGene().getAllele();

		return Float64.valueOf(f(x1.doubleValue(), x2.doubleValue()));
	}

	private static double f(final double x1, final double x2) {
		return x1+x2;
	}
}

public class RealFunction2D {

	private static class FF implements Function<Genotype<Float64Gene>, Double> {
		@Override
		public Double apply(final Genotype<Float64Gene> gt) {
			final Float64 x1 = gt.getChromosome(0).getGene().getAllele();
			final Float64 x2 = gt.getChromosome(1).getGene().getAllele();

			return f(x1.doubleValue(), x2.doubleValue());
		}

		// Function to be optimized.
		private static double f(final double x1, final double x2) {
			return x1+x2;
		}
	}

	public static void main(final String[] args) {
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(
			new Float64Chromosome(0.0, 10.0), // x1 in [0, 10]
			new Float64Chromosome(5.0, 33.0)  // x2 in [5, 33]
		);

		final Function<Genotype<Float64Gene>, Double> ff = new FF();
		final GeneticAlgorithm<Float64Gene, Double> ga = new GeneticAlgorithm<>(gtf, ff);

		ga.setSelectors(new TournamentSelector<Float64Gene, Double>(3));
		// This are the alterers you can use for this example, in any combination.
		ga.setAlterers(
			new MeanAlterer<Float64Gene>(),
			new GaussianMutator<Float64Gene>()
		);

		ga.setup();
		ga.evolve(100);

		System.out.println(ga.getBestPhenotype());
	}

}
