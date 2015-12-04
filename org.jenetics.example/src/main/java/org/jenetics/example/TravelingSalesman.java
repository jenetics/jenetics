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
import static org.jenetics.engine.limit.bySteadyFitness;

import java.util.stream.IntStream;

import org.jenetics.EnumGene;
import org.jenetics.Optimize;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.Phenotype;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.codecs;

public class TravelingSalesman {

	// Problem initialization:
	// Calculating the adjacence matrix of the "city" distances.

	private static final int STOPS = 20;
	private static final double[][] ADJACENCE = matrix(STOPS);

	private static double[][] matrix(int stops) {
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
		return 2.0*r*abs(sin(PI*i/stops));
	}

	// Calculate the path length of the current genotype.
	private static double dist(final int[] path) {
		// Calculate the path distance.
		return IntStream.range(0, STOPS)
			.mapToDouble(i ->
				ADJACENCE[path[i]][path[(i + 1)%STOPS]])
			.sum();
	}

	public static void main(String[] args) {
		final Engine<EnumGene<Integer>, Double> engine = Engine
			.builder(
				TravelingSalesman::dist,
				codecs.ofPermutation(STOPS))
			.optimize(Optimize.MINIMUM)
			.maximalPhenotypeAge(11)
			.populationSize(500)
			.alterers(
				new SwapMutator<>(0.2),
				new PartiallyMatchedCrossover<>(0.35))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Double, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<EnumGene<Integer>, Double> best =
			engine.stream()
			// Truncate the evolution stream after 7 "steady"
			// generations.
			.limit(bySteadyFitness(15))
			// The evolution will stop after maximal 100
			// generations.
			.limit(250)
			// Update the evaluation statistics after
			// each generation
			.peek(statistics)
			// Collect (reduce) the evolution stream to
			// its best phenotype.
			.collect(toBestPhenotype());

		System.out.println(statistics);
		System.out.println(best);
	}

}
