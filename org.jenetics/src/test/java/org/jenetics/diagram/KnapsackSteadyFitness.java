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
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.Mutator;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.diagram.problem.Knapsack;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KnapsackSteadyFitness {

	private static Engine<BitGene, Double> engine() {
		// Search space fo 2²⁵⁰.
		final Knapsack knapsack =
			RandomRegistry.with(new LCG64ShiftRandom(10101), r ->
				Knapsack.of(250)
			);

		// Configure and build the evolution engine.
		return Engine
			.builder(knapsack.function(), knapsack.genotype())
			.populationSize(150)
			.survivorsSelector(new TournamentSelector<>(5))
			.offspringSelector(new RouletteWheelSelector<>())
			.alterers(
				new Mutator<>(0.03),
				new SinglePointCrossover<>(0.125))
			.build();
	}

	public static void main(final String[] args) throws IOException {
		final double base = pow(10, log10(100)/20.0);

		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());
		final int samples = 10;

		final Function<Integer, Predicate<? super EvolutionResult<BitGene, Double>>>
			terminator = limit::bySteadyFitness;

		final TerminationStatistics<BitGene, Integer> statistics =
			new TerminationStatistics<>(samples, engine(), terminator);

		final long start = System.nanoTime();
		final int generations = IntStream.rangeClosed(1, 20)
			.peek(i -> System.out.print(i + ": "))
			.map(i -> max((int) pow(base, i), i))
			.peek(i -> System.out.println("Generation: " + i))
			.peek(statistics::accept)
			.sum();
		final long end = System.nanoTime();

		System.out.println(format(
			"Executed %d generations in %s",
			generations,
			DurationFormat.format(Duration.ofNanos(end - start))
		));
		System.out.println(format(
			"%s sec per generation.",
			(end - start)/(1_000_000_000.0*generations)
		));

		statistics.write(new File(
			"org.jenetics/src/test/scripts/diagram/" +
				"steady_fitness_termination.dat"
		));
		System.out.println("Ready");

	}

}
