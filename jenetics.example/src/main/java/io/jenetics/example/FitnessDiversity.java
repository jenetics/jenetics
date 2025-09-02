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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.example;

import static io.jenetics.engine.EvolutionResult.toBestEvolutionResult;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.Random;

import io.jenetics.BitGene;
import io.jenetics.Mutator;
import io.jenetics.SinglePointCrossover;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStreamable;
import io.jenetics.engine.Limits;

import io.jenetics.ext.engine.CyclicEngine;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 4.2
 */
public class FitnessDiversity {

	public static void main(final String[] args) {
		final Knapsack knapsack = Knapsack.of(15, new Random(123));

		// The base engine tries to approximate to a good solution in the current
		// environment.
		final Engine<BitGene, Double> baseEngine = Engine.builder(knapsack)
			.populationSize(500)
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		// The 'diversity' engine tries to broaden the search space again.
		final Engine<BitGene, Double> diversityEngine = baseEngine.toBuilder()
			.alterers(new Mutator<>(0.5))
			.build();

		// Concatenates the two engines into one cyclic engine.
		final EvolutionStreamable<BitGene, Double> engine = CyclicEngine.of(
			// This engine stops the evolution after 10 non-improving
			// generations and hands over to the diversity engine.
			baseEngine.limit(() -> Limits.bySteadyFitness(10)),

			// The higher mutation rate of this engine broadens the search
			// space for 15 generations and hands over to the base engine.
			diversityEngine.limit(15)
		);

		final EvolutionResult<BitGene, Double> best = engine.stream()
			// The evolution is stopped after 50 non-improving generations.
			.limit(bySteadyFitness(50))
			.collect(toBestEvolutionResult());

		System.out.println(best.totalGenerations());
		System.out.println(best.bestPhenotype());
	}

}
