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
package io.jenetics.example;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.jenetics.DoubleGene;
import io.jenetics.Optimize;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class ResultRescue {

	private static final Problem<Double, DoubleGene, Double>
	PROBLEM = Problem.of(
		x -> cos(0.5 + sin(x))*cos(x),
		Codecs.ofScalar(new DoubleRange(0.0, 2.0*PI))
	);

	private static final Engine<DoubleGene, Double>
	ENGINE = Engine.builder(PROBLEM)
		.optimize(Optimize.MINIMUM)
		.offspringSelector(new RouletteWheelSelector<>())
		.build();

	public static void main(final String[] args) throws IOException {
		final EvolutionResult<DoubleGene, Double> rescue = ENGINE.stream()
			.limit(Limits.bySteadyFitness(10))
			.collect(EvolutionResult.toBestEvolutionResult());

		final Path path = Paths.get("result.bin");
		IO.object.write(rescue, path);

		@SuppressWarnings("unchecked")
		final EvolutionResult<DoubleGene, Double> result = ENGINE
			.stream((EvolutionResult<DoubleGene, Double>)IO.object.read(path))
			.limit(Limits.bySteadyFitness(20))
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(result);
	}

}
