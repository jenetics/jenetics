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

import static org.jenetics.engine.EvolutionResult.toBestEvolutionResult;
import static org.jenetics.engine.limit.byPopulationConvergence;
import static org.jenetics.engine.limit.bySteadyFitness;

import java.util.Random;

import org.jenetics.BitGene;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public class ParallelEvolution {

	public static void main(final String[] args) {
		final Knapsack knapsack = Knapsack.of(15, new Random(123));

		final Engine<BitGene, Double> engine = Engine.builder(knapsack)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.115),
				new SinglePointCrossover<>(0.16))
			.build();

		final EvolutionResult<BitGene, Double> best = engine.stream()
			.limit(byPopulationConvergence(0.0075))
			.parallel()
			.collect(toBestEvolutionResult());

		System.out.println(best.getTotalGenerations());
		System.out.println(best.getBestPhenotype());
	}

}
