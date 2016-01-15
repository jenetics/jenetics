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
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.limit;
import org.jenetics.problem.Knapsack;
import org.jenetics.trial.Params;
import org.jenetics.trial.TrialMeter;
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
		final Path outputPath;
		if (args.length >= 1) {
			outputPath = Paths.get(args[0]);
		} else {
			outputPath = Paths.get("trial_meter.xml");
		}

		final TrialMeter<Integer> trialMeter;
		if (Files.exists(outputPath)) {
			trialMeter = TrialMeter.read(outputPath);

			info("Continue existing trial: '%s'.", outputPath.toAbsolutePath());
			info("    " + trialMeter);
		} else {
			trialMeter = TrialMeter.of(
				"Steady fitness", "Create steady fitness performance measures",
				GENERATIONS, "Generation", "Fitness"
			);

			info("Writing results to '%s'.", outputPath.toAbsolutePath());
		}

		final Engine<BitGene, Double> engine = Knapsack.engine(new LCG64ShiftRandom(10101));

		for (int i = 0; i < 500; ++i) {
			trialMeter.sample(generations -> {
				final Predicate<? super EvolutionResult<BitGene, Double>>
					terminator = limit.bySteadyFitness(generations);

				try {
					trialMeter.write(outputPath);
					info(trialMeter.toString());
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				final EvolutionResult<BitGene, Double> result = engine.stream()
					.limit(terminator)
					.collect(EvolutionResult.toBestEvolutionResult());

				return new double[]{
					result.getTotalGenerations(),
					result.getBestFitness()
				};
			});

			trialMeter.write(outputPath);
		}
	}

	private static final DateTimeFormatter FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private static void info(final String pattern, final Object... params) {
		final LocalDateTime time = LocalDateTime.now();
		System.out.println(
			"" + FORMATTER.format(time) + " - " + format(pattern, params)
		);
	}

}
