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
import static org.jenetics.evaluation.engines.KNAPSACK;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.trial.Params;
import org.jenetics.trial.Trial;
import org.jenetics.trial.TrialMeter;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class KnapsackExecutionTime {

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final Params<Long> PARAMS = Params.of(
		"Execution time",
		IntStream.rangeClosed(1, 50)
			.mapToLong(i -> max((long)pow(GEN_BASE, i), i))
			.mapToObj(Long::new)
			.collect(ISeq.toISeq())
	);

	private static final Supplier<TrialMeter<Long>>
		TRIAL_METER = () -> TrialMeter.of(
		"Execution time",
		"Create execution time performance measures",
		PARAMS,
		"Generation",
		"Fitness",
		"Runtime"
	);

	private static double[] function(final long duration) {
		final Predicate<? super EvolutionResult<BitGene, Double>>
			terminator = limit.byExecutionTime(Duration.ofMillis(duration));

		final long start = System.currentTimeMillis();
		final EvolutionResult<BitGene, Double> result = KNAPSACK.stream()
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

		final Trial<Long> trial = new Trial<>(
			KnapsackExecutionTime::function,
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
