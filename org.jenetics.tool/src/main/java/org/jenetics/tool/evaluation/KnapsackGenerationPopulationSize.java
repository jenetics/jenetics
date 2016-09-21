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
package org.jenetics.tool.evaluation;

import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static org.jenetics.tool.evaluation.engines.KNAPSACK;

import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.limit;
import org.jenetics.tool.trial.Params;
import org.jenetics.tool.trial.TrialMeter;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.6
 * @since 3.6
 */
public class KnapsackGenerationPopulationSize {

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final ISeq<String> GENERATIONS = IntStream.rangeClosed(1, 40)
		.mapToLong(i -> max((long)pow(GEN_BASE, i), i))
		.mapToObj(String::valueOf)
		.collect(ISeq.toISeq());

	private static final ISeq<String> POPULATION_SIZES = IntStream.rangeClosed(1, 30)
		.mapToLong(i -> max((long)pow(GEN_BASE, i), i))
		.mapToObj(String::valueOf)
		.collect(ISeq.toISeq());

	private static final ISeq<String> GEN_POP = GENERATIONS.stream()
		.flatMap(g -> POPULATION_SIZES.stream()
			.map(ps -> format("%s:%s", g, ps)))
		.collect(ISeq.toISeq());

	private static final Params<String> PARAMS = Params.of(
		"Generation/Population size",
		GEN_POP
	);

	private static long toGeneration(final String param) {
		return Long.parseLong(param.split(Pattern.quote(":"))[0]);
	}

	private static int toPopulationSize(final String param) {
		return Integer.parseInt(param.split(Pattern.quote(":"))[1]);
	}

	private static final Supplier<TrialMeter<String>>
		TRIAL_METER = () -> TrialMeter.of(
		"Execution time",
		"Create execution time performance measures",
		PARAMS,
		"Generation",
		"Fitness",
		"Runtime"
	);

	public static void main(final String[] args) throws InterruptedException {
		final Runner<String, BitGene, Double> runner = Runner.of(
			param -> KNAPSACK(toPopulationSize(param)),
			param -> limit.byFixedGeneration(toGeneration(param)),
			TRIAL_METER,
			args
		);

		runner.start();
		runner.join();
	}

}
