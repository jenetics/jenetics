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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.problem.Knapsack;
import org.jenetics.trial.Params;
import org.jenetics.trial.Trial;
import org.jenetics.trial.TrialMeter;
import org.jenetics.util.ISeq;
import org.jenetics.util.LCG64ShiftRandom;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KnapsackSteadyFitness {

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final Params<Integer> PARAMS = Params.of(
		"Generations",
		IntStream.rangeClosed(1, 50)
			.map(i -> max((int)pow(GEN_BASE, i), i))
			.mapToObj(Integer::new)
			.collect(ISeq.toISeq())
	);

	private static final Supplier<TrialMeter<Integer>>
	TRIAL_METER = () -> TrialMeter.of(
		"Steady fitness",
		"Create steady fitness performance measures",
		PARAMS,
		"Generation",
		"Fitness",
		"Runtime"
	);

	private static final Engine<BitGene, Double> ENGINE =
		Knapsack.engine(new LCG64ShiftRandom(10101));

	private static double[] function(final int generations) {
		final Predicate<? super EvolutionResult<BitGene, Double>>
			terminator = limit.bySteadyFitness(generations);

		final long start = System.currentTimeMillis();
		final EvolutionResult<BitGene, Double> result = ENGINE.stream()
			.limit(terminator)
			.collect(EvolutionResult.toBestEvolutionResult());
		final long end = System.currentTimeMillis();

		return new double[] {
			result.getTotalGenerations(),
			result.getBestFitness(),
			end - start
		};
	}

	public static void main(final String[] args) throws InterruptedException {
		final Path resultPath = args.length >= 1
			? Paths.get(args[0])
			: Paths.get("trial_meter.xml");

		final Trial<Integer> trial = new Trial<>(
			KnapsackSteadyFitness::function,
			TRIAL_METER,
			resultPath
		);

		final Thread thread = new Thread(trial);
		thread.start();

		String command;
		do {
			command = System.console().readLine();
			Trial.info("Got command '" + command + "'");
		} while (!"exit".equals(command));

		Trial.info("Stopping trial...");
		thread.interrupt();
		thread.join();
		Trial.info("Sopped trial.");
	}

}
