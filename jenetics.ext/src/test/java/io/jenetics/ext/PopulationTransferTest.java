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
package io.jenetics.ext;

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.engine.Limits;
import io.jenetics.engine.Problem;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class PopulationTransferTest {


	public static void main(final String[] args) {
		final Problem<Double, DoubleGene, Double> problem = null;
		//final PopulationTransfer<DoubleGene, Double> transfer = new PopulationTransfer<>();

		final SerialPopulationTransfer<DoubleGene, Double>
			serial = new SerialPopulationTransfer<>();

		final Engine.Builder<DoubleGene, Double> builder = Engine.builder(problem)
			.minimizing()
			.selector(new RouletteWheelSelector<>());

		final Engine<DoubleGene, Double> engine1 = builder
			.mapping(serial)
			.build();

		final Engine<DoubleGene, Double> engine2 = builder
			.mapping(serial)
			.build();

		final Engine<DoubleGene, Double> engine3 = builder
			.build();

		final EvolutionStream<DoubleGene, Double> stream1 =
			EngineConcat.<DoubleGene, Double>serial()
				.append(engine1, Limits.bySteadyFitness(10))
				.append(engine2, Limits.bySteadyFitness(10))
				.append(engine3, Limits.bySteadyFitness(10))
				.append(engine3, Limits.infinite())
				.stream();
//
//		serial
//			.using(engine1, Limits.bySteadyFitness(10))
//			.using(engine2, Limits.bySteadyFitness(10))
//			.using(engine3, Limits.bySteadyFitness(10))
//			.stream();

		final EvolutionStream<DoubleGene, Double> stream = EvolutionStream.join(
			builder
				.alterers(new Mutator<>(0.5))
				.mapping(serial).build()
				.stream(serial)
				.limit(Limits.bySteadyFitness(10))
				.peek(serial),

			builder
				.alterers(new Mutator<>(0.2))
				.mapping(serial).build()
				.stream(serial)
				.limit(Limits.bySteadyFitness(10)),

			builder
				.alterers(new Mutator<>(0.1))
				.mapping(serial).build()
				.stream(serial)
				.limit(Limits.bySteadyFitness(10)),

			engine3.stream(serial)
		);

		final Genotype<DoubleGene> best = stream
			.limit(Limits.bySteadyFitness(50))
			.collect(EvolutionResult.toBestGenotype());
	}

}
