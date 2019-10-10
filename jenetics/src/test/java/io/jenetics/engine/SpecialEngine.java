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

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class SpecialEngine {

	// The fitness function.
	private static Double fitness(final Genotype<DoubleGene> gt) {
		return gt.getGene().getAllele();
	}

	// Create new evolution start object.
	private static EvolutionStart<DoubleGene, Double>
	start(final int populationSize, final long generation) {
		final ISeq<Phenotype<DoubleGene, Double>> population =
			Genotype.of(DoubleChromosome.of(0, 1)).instances()
				.map(gt -> Phenotype.<DoubleGene, Double>of(gt, generation))
				.limit(populationSize)
				.collect(ISeq.toISeq());

		return EvolutionStart.of(population, generation);
	}

	// The special evolution function.
	private static EvolutionResult<DoubleGene, Double>
	evolve(final EvolutionStart<DoubleGene, Double> start) {
		// Your special evolution implementation comes here!
		return null;
	}

	public static void main(final String[] args) {
		final Genotype<DoubleGene> best = EvolutionStream
			.ofEvolution(() -> start(50, 0), SpecialEngine::evolve)
			.limit(Limits.bySteadyFitness(10))
			.limit(1000)
			.collect(EvolutionResult.toBestGenotype());

		System.out.println(String.format("Best Genotype: %s", best));
	}
}
