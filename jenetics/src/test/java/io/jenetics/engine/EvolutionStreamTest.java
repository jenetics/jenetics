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
package io.jenetics.engine;

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.MeanAlterer;
import io.jenetics.MonteCarloSelector;
import io.jenetics.Mutator;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.stat.DoubleMoments;
import io.jenetics.util.DoubleRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionStreamTest {

	public static void main(final String[] args) {
		final Problem<double[], DoubleGene, Double> problem = Problem.of(
			v -> Math.sin(v[0])*Math.cos(v[1]),
			Codecs.ofVector(DoubleRange.of(0, 2*Math.PI), 2)
		);

		// Engine builder template.
		final Engine.Builder<DoubleGene, Double> builder = Engine
			.builder(problem)
			.minimizing();

		// Evolution used for low population variance.
		final Evolution<DoubleGene, Double> lowVar = builder.copy()
			.alterers(new Mutator<>(0.5))
			.selector(new MonteCarloSelector<>())
			.build();

		// Evolution used for high population variance.
		final Evolution<DoubleGene, Double> highVar = builder.copy()
			.alterers(
				new Mutator<>(0.05),
				new MeanAlterer<>())
			.selector(new RouletteWheelSelector<>())
			.build();

		final EvolutionStream<DoubleGene, Double> stream = EvolutionStream
			.ofAdjustableEvolution(
				EvolutionStart::empty,
				er -> engine(er, lowVar, highVar)
			);

		final Genotype<DoubleGene> result = stream
			.limit(Limits.bySteadyFitness(50))
			.collect(EvolutionResult.toBestGenotype());

		System.out.println(result + ": " +
			problem.fitness().apply(problem.codec().decode(result)));
	}

	private static Evolution<DoubleGene, Double> engine(
		final EvolutionStart<DoubleGene, Double> result,
		final Evolution<DoubleGene, Double> lowVarEngine,
		final Evolution<DoubleGene, Double> highVarEngine
	) {
		return var(result) < 0.2
			? lowVarEngine
			: highVarEngine;
	}

	private static double var(final EvolutionStart<DoubleGene, Double> result) {
		return result != null
			? result.getPopulation().stream()
				.map(Phenotype::getFitness)
				.collect(DoubleMoments.toDoubleMoments())
				.getVariance()
			: 0.0;
	}

}
