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

import static java.lang.Math.log10;
import static java.lang.Math.pow;
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
 * @since 3.9
 */
public class KnapsackPopulationConvergence {

	private static final double GEN_BASE = pow(10, log10(1000)/10.0);
	private static final Params<Double> PARAMS = Params.of(
		"Convergence epsilon",
		IntStream.rangeClosed(0, 10)
			.mapToObj(i -> 0.01 - i*(0.01 - 0.001)/10)
			.collect(ISeq.toISeq())
	);

	static {
		System.out.println(PARAMS);
	}

	private static final Supplier<TrialMeter<Double>>
		TRIAL_METER = () -> TrialMeter.of(
		"Population convergence",
		"Create population convergence performance measures",
		PARAMS,
		"Generation",
		"Fitness",
		"Runtime"
	);

	public static void main(final String[] args) throws InterruptedException {
		final Args arguments = Args.of(args);

		final Runner<Double, BitGene, Double> runner = Runner.of(
			fitness -> KNAPSACK,
			Limits::byPopulationConvergence,
			TRIAL_METER,
			Writer.text().map(Object::toString),
			Reader.text().map(Double::parseDouble),
			args
		);

		runner.start();
		runner.join();
	}

}
