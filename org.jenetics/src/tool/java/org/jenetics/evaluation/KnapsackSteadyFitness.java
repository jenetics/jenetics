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

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.pow;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.problem.Knapsack;
import org.jenetics.trial.Measurement;
import org.jenetics.trial.Params;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KnapsackSteadyFitness {

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final File BASE_OUTPUT_DIR =
		new File("org.jenetics/src/test/scripts/diagram");


	private static final Params<Integer> GENERATIONS = Params.of(
		"Generations",
		IntStream.rangeClosed(1, 50)
			.map(i -> max((int)pow(GEN_BASE, i), i))
			.mapToObj(Integer::new)
			.collect(ISeq.toISeq())
	);


	public static void main(final String[] args) throws IOException {
		final Measurement<Integer> measurement = Measurement.of(
			"Steady fitness", null,
			GENERATIONS, "Generation", "Fitness"
		);

		measurement.write(System.out);

		final Engine<BitGene, Double> engine = Knapsack.engine(new LCG64ShiftRandom(10101));

		for (int i = 0; i < 5; ++i) {
			measurement.sample(generations -> {
				Predicate<? super EvolutionResult<BitGene, Double>> terminator =
					limit.bySteadyFitness(generations);
				try {
					measurement.write(System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
				final EvolutionResult<BitGene, Double> result = engine.stream()
					.limit(terminator)
					.collect(EvolutionResult.toBestEvolutionResult());

				return new double[]{
					result.getTotalGenerations(),
					result.getBestFitness()
				};
			});

			measurement.write(System.out);
		}


		/*
		final GenerationParam param = GenerationParam.of(
			args,
			250,
			50,
			new File(BASE_OUTPUT_DIR, "SteadyFitnessTermination.dat"));

		RandomRegistry.setRandom(new LCG64ShiftRandom.ThreadLocal());

		final Function<Integer, Predicate<? super EvolutionResult<BitGene, Double>>>
			terminator = limit::bySteadyFitness;

		final TerminationStatistics<BitGene, Integer> statistics =
			new TerminationStatistics<>(
				param.getSamples(),
				Knapsack.engine(new LCG64ShiftRandom(10101)),
				terminator);

		final long start = System.nanoTime();
		final int generations = IntStream.rangeClosed(1, param.getGenerations())
			.peek(i -> System.out.print(i + ": "))
			.map(i -> max((int)pow(GEN_BASE, i), i))
			.peek(i -> System.out.println("Generation: " + i))
			.peek(statistics::accept)
			.sum();
		final long end = System.nanoTime();

		System.out.println(String.format(
			"Executed %d generations in %s",
			generations,
			DurationFormat.format(Duration.ofNanos(end - start))
		));
		System.out.println(String.format(
			"%s sec per generation.",
			(end - start)/(1_000_000_000.0*generations*param.getSamples())
		));

		statistics.write(param.getOutputFile());
		System.out.println("Ready");
		*/
	}

}
