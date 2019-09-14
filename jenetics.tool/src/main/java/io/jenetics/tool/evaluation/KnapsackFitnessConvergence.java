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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.tool.evaluation;

import static io.jenetics.tool.evaluation.engines.KNAPSACK;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import io.jenetics.BitGene;
import io.jenetics.engine.Limits;
import io.jenetics.internal.util.Args;
import io.jenetics.tool.trial.Params;
import io.jenetics.tool.trial.TrialMeter;
import io.jenetics.util.ISeq;

import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.7
 */
public class KnapsackFitnessConvergence {

	private static final Params<Double> PARAMS = Params.of(
		"Convergence epsilon",
		IntStream.rangeClosed(1, 10)
			.mapToObj(i -> Math.pow(10, -i))
			.collect(ISeq.toISeq())
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
		final Args arguments = Args.of(args);
		final ISeq<Integer> sizes = arguments.intArgs("params");

		final Runner<Double, BitGene, Double> runner = Runner.of(
			fitness -> KNAPSACK,
			epsilon -> Limits.byFitnessConvergence(sizes.get(0), sizes.get(1), epsilon),
			TRIAL_METER,
			Writer.text().map(Object::toString),
			Reader.text().map(Double::parseDouble),
			args
		);

		runner.start();
		runner.join();
	}

}
