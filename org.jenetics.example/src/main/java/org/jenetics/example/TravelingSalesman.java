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

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sin;
import static org.jenetics.engine.EvolutionResult.toBestPhenotype;

import org.jenetics.Chromosome;
import org.jenetics.EnumGene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.PermutationChromosome;
import org.jenetics.Phenotype;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Engine;

/**
 * The classical <a href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-22 $</em>
 */
public class TravelingSalesman {

	private static final int STOPS = 20;
	private static final double[][] ADJACENCE = adjacencyMatrix(STOPS);

	private static double[][] adjacencyMatrix(int stops) {
		final double radius = 10.0;
		double[][] matrix = new double[stops][stops];

		for (int i = 0; i < stops; ++i) {
			for (int j = 0; j < stops; ++j) {
				matrix[i][j] = chord(stops, abs(i - j), radius);
			}
		}
		return matrix;
	}

	private static double chord(int stops, int i, double r) {
		return 2.0*r*abs(sin((PI*i)/stops));
	}

	private static Double distance(final Genotype<EnumGene<Integer>> gt) {
		final Chromosome<EnumGene<Integer>> path = gt.getChromosome();

		double length = 0.0;
		for (int i = 0, n = path.length(); i < n; ++i) {
			final int from = path.getGene(i).getAllele();
			final int to = path.getGene((i + 1)%n).getAllele();
			length += ADJACENCE[from][to];
		}
		return length;
	}

	public static void main(String[] args) {
		final Engine<EnumGene<Integer>, Double> engine = Engine
			.newBuilder(
				TravelingSalesman::distance,
				PermutationChromosome.ofInteger(STOPS))
			.optimize(Optimize.MINIMUM)
			.populationSize(500)
			.alterers(
				new SwapMutator<>(0.2),
				new PartiallyMatchedCrossover<>(0.3))
			.build();

		final Phenotype<EnumGene<Integer>, Double> result = engine.stream()
			.limit(100)
			.collect(toBestPhenotype());

		System.out.println(result);
	}

}
