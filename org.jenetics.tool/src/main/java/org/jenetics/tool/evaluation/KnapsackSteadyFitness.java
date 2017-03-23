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
import static org.jenetics.tool.evaluation.engines.KNAPSACK;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.limit;
import org.jenetics.tool.trial.Params;
import org.jenetics.tool.trial.TrialMeter;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.4
 * @since 3.4
 */
public class KnapsackSteadyFitness {

	private static final double GEN_BASE = pow(10, log10(100)/20.0);
	private static final Params<Integer> PARAMS = Params.of(
		"Generations",
		IntStream.rangeClosed(1, 50)
			.map(i -> max((int)pow(GEN_BASE, i), i))
			.mapToObj(Integer::valueOf)
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

	public static void main(final String[] args) throws InterruptedException {
		final Runner<Integer, BitGene, Double> runner = Runner.of(
			fitness -> KNAPSACK,
			limit::bySteadyFitness,
			TRIAL_METER,
			args
		);

		runner.start();
		runner.join();
	}

}
