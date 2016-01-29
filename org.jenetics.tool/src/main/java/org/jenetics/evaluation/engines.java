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
package org.jenetics.evaluation;

import java.util.Random;

import org.jenetics.internal.util.require;

import org.jenetics.BitGene;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.Engine;
import org.jenetics.problem.Knapsack;
import org.jenetics.util.LCG64ShiftRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class engines {
	private engines() {require.noInstance();}

	public static Engine<BitGene, Double>
		KNAPSACK = knapsack(new LCG64ShiftRandom(10101));

	public static Engine<BitGene, Double> knapsack(final Random random) {
		// Search space fo 2²⁵⁰ ~ 10⁷⁵.
		final Knapsack knapsack = Knapsack.of(250, random);

		// Configure and build the evolution engine.
		return Engine
			.builder(knapsack.fitness(), knapsack.codec())
			.populationSize(150)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.03),
				new SinglePointCrossover<>(0.125))
			.build();
	}

}
