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

import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.jenetics.BitGene;
import org.jenetics.engine.limit;
import org.jenetics.tool.trial.Params;
import org.jenetics.tool.trial.TrialMeter;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class KnapsackFitnessConvergence {

	private static final int MAX = 10;
	private static final double FACTOR = 1000000;

	private static final double GEN_BASE = pow(10, log10(100)/30.0);
	private static final Params<Double> PARAMS = Params.of(
		"Convergence epsilon",
		IntStream.rangeClosed(1, MAX)
			.mapToObj(i -> Math.pow(10, -i))
			.collect(ISeq.toISeq())
			//.copy()
			//.sort(Comparator.comparingDouble(Double::doubleValue).reversed())
			//.toISeq()
	);

	private static final Supplier<TrialMeter<Double>>
		TRIAL_METER = () -> TrialMeter.of(
		"Fitness convergence",
		"Create fitness convergence performance measures",
		PARAMS,
		"Generation",
		"Fitness",
		"Runtime"
	);

	public static void main(final String[] args) throws InterruptedException {
		System.out.println(PARAMS);


		final Runner<Double, BitGene, Double> runner = Runner.of(
			fitness -> KNAPSACK,
			epsilon -> limit.byFitnessConvergence(50, 150, epsilon),
			TRIAL_METER,
			args
		);

		runner.start();
		runner.join();

	}

}
