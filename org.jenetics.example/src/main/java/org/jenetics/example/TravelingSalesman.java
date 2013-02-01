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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.example;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;

import java.io.Serializable;
import java.util.function.Function;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.NumberStatistics.Calculator;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.SwapMutator;
import org.jenetics.util.Factory;

/**
 * The classical <a href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0 &mdash; <em>$Date: 2013-02-01 $</em>
 */
public class TravelingSalesman {

	private static class FF
		implements Function<Genotype<EnumGene<Integer>>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		private final double[][] _adjacence;

		public FF(final double[][] adjacence) {
			_adjacence = adjacence;
		}

		@Override
		public Double apply(final Genotype<EnumGene<Integer>> genotype) {
			final Chromosome<EnumGene<Integer>> path = genotype.getChromosome();

			double length = 0.0;
			for (int i = 0, n = path.length(); i < n; ++i) {
				final int from = path.getGene(i).getAllele();
				final int to = path.getGene((i + 1)%n).getAllele();
				length += _adjacence[from][to];
			}
			return length;
		}

		@Override
		public String toString() {
			return "Point distance";
		}
	}

	public static void main(String[] args) {
		final int stops = 20;

		final Function<Genotype<EnumGene<Integer>>, Double> ff = new FF(adjacencyMatrix(stops));
		final Factory<Genotype<EnumGene<Integer>>> gtf = Genotype.valueOf(
			PermutationChromosome.ofInteger(stops)
		);
		final GeneticAlgorithm<EnumGene<Integer>, Double>
			ga = new GeneticAlgorithm<>(gtf, ff, Optimize.MINIMUM);
		ga.setStatisticsCalculator(
				new Calculator<EnumGene<Integer>, Double>()
			);
		ga.setPopulationSize(500);
		ga.setAlterers(
			new SwapMutator<EnumGene<Integer>>(0.2),
			new PartiallyMatchedCrossover<Integer>(0.3)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}

	/**
	 * All points in the created adjacency matrix lie on a circle. So it is easy
	 * to check the quality of the solution found by the GA.
	 */
	private static double[][] adjacencyMatrix(int stops) {
		double[][] matrix = new double[stops][stops];
		for (int i = 0; i < stops; ++i) {
			for (int j = 0; j < stops; ++j) {
				matrix[i][j] = chord(stops, abs(i - j), RADIUS);
			}
		}
		return matrix;
	}
	private static double chord(int stops, int i, double r) {
		return 2.0*r*abs(sin((PI*i)/stops));
	}
	private static double RADIUS = 10.0;
}





