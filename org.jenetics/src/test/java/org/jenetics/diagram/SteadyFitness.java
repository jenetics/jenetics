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
package org.jenetics.diagram;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.diagram.problem.Knapsack;
import org.jenetics.engine.Engine;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class SteadyFitness {

	private static Engine<BitGene, Double> engine() {
		final Knapsack knapsack = Knapsack.of(500);

		// Configure and build the evolution engine.
		return Engine
			.builder(knapsack.function(), knapsack.genotype())
			.populationSize(150)
			.survivorsSelector(new TournamentSelector<>(3))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.15),
				new SinglePointCrossover<>(0.20))
			.build();
	}

	public static void main(final String[] args) throws IOException {
		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());

		final int samples = 3;
		final SteadyFitnessTermination<BitGene> test =
			new SteadyFitnessTermination<>(engine(), samples);

		final double base = pow(10, log10(100)/20.0);
		IntStream.rangeClosed(1, 30)
			.peek(i -> System.out.print(i + ": "))
			.map(i -> Math.max((int) pow(base, i), i))
			.peek(i -> System.out.println("Generation: " + i))
			.forEach(test::execute);

		test.write(new File(
			"org.jenetics/src/test/scripts/diagram/" +
			"steady_fitness_termination.dat"
		));
		System.out.println("Ready");

	}

}
