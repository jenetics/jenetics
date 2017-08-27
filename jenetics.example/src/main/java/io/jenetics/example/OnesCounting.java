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
package io.jenetics.example;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.limit;

public class OnesCounting {

	// This method calculates the fitness for a given genotype.
	private static Integer count(final Genotype<BitGene> gt) {
		return ((BitChromosome)gt.getChromosome()).bitCount();
	}

	public static void main(String[] args) {
		// Configure and build the evolution engine.
		final Engine<BitGene, Integer> engine = Engine
			.builder(
				OnesCounting::count,
				BitChromosome.of(20, 0.15))
			.populationSize(500)
			.selector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.55),
				new SinglePointCrossover<>(0.06))
			.build();

		// Create evolution statistics consumer.
		final EvolutionStatistics<Integer, ?>
			statistics = EvolutionStatistics.ofNumber();

		final Phenotype<BitGene, Integer> best = engine.stream()
			// Truncate the evolution stream after 7 "steady"
			// generations.
			.limit(limit.bySteadyFitness(7))
				// The evolution will stop after maximal 100
				// generations.
			.limit(100)
				// Update the evaluation statistics after
				// each generation
			.peek(statistics)
				// Collect (reduce) the evolution stream to
				// its best phenotype.
			.collect(EvolutionResult.toBestPhenotype());

		System.out.println(statistics);
		System.out.println(best);
	}
}
