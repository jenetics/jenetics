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
package org.jenetics.example;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jenetics.DoubleGene;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.Problem;
import org.jenetics.engine.codecs;
import org.jenetics.engine.limit;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.IO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public final class ResultRescue {

	private static final Problem<Double, DoubleGene, Double>
	PROBLEM = Problem.of(
		x -> cos(0.5 + sin(x))*cos(x),
		codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
	);

	private static final Engine<DoubleGene, Double>
	ENGINE = Engine.builder(PROBLEM)
		.optimize(Optimize.MINIMUM)
		.offspringSelector(new RouletteWheelSelector<>())
		.build();

	public static void main(final String[] args) throws IOException {
		final EvolutionResult<DoubleGene, Double> rescue = ENGINE.stream()
			.limit(limit.bySteadyFitness(10))
			.collect(EvolutionResult.toBestEvolutionResult());

		final Path path = Paths.get("result.bin");
		IO.object.write(rescue, path);

		@SuppressWarnings("unchecked")
		final EvolutionResult<DoubleGene, Double> result = ENGINE
			.stream((EvolutionResult<DoubleGene, Double>)IO.object.read(path))
			.limit(limit.bySteadyFitness(20))
			.collect(EvolutionResult.toBestEvolutionResult());

		System.out.println(result);
	}

}
