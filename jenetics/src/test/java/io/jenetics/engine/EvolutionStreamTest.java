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

import org.testng.Assert;
import org.testng.annotations.Test;

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
 */
public class EvolutionStreamTest {

	@Test
	public void ofAdjustableEvolution() {
		final Problem<double[], DoubleGene, Double> problem = Problem.of(
			v -> Math.sin(v[0])*Math.cos(v[1]),
			Codecs.ofVector(new DoubleRange(0, 2*Math.PI), 2)
		);

		// Engine builder template.
		final Engine.Builder<DoubleGene, Double> builder = Engine
			.builder(problem)
			.minimizing();

		// Evolution used for low fitness variance.
		final Evolution<DoubleGene, Double> lowVar = builder.copy()
			.alterers(new Mutator<>(0.5))
			.selector(new MonteCarloSelector<>())
			.build();

		// Evolution used for high fitness variance.
		final Evolution<DoubleGene, Double> highVar = builder.copy()
			.alterers(
				new Mutator<>(0.05),
				new MeanAlterer<>())
			.selector(new RouletteWheelSelector<>())
			.build();

		final EvolutionStream<DoubleGene, Double> stream =
			EvolutionStream.ofAdjustableEvolution(
				EvolutionStart::empty,
				er -> var(er) < 0.2 ? lowVar : highVar
			);

		final Genotype<DoubleGene> result = stream
			.limit(Limits.bySteadyFitness(50))
			.collect(EvolutionResult.toBestGenotype());

		Assert.assertTrue(
			problem.fitness(result) < -0.99,
			"Fitness: " + problem.fitness(result)
		);

		//System.out.println(result + ": " +
		//	problem.fitness().apply(problem.codec().decode(result)));
	}

	private static double var(final EvolutionStart<DoubleGene, Double> result) {
		return result != null
			? result.population().stream()
				.map(Phenotype::fitness)
				.collect(DoubleMoments.toDoubleMoments())
				.variance()
			: 0.0;
	}

}
