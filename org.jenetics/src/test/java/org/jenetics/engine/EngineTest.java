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
package org.jenetics.engine;

import java.util.function.Consumer;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Phenotype;
import org.jenetics.stat.DoubleMomentStatistics;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class EngineTest {

	@Test
	public void generationCount() {
		final Engine<DoubleGene, Double> engine = Engine
			.builder(a -> a.getGene().getAllele(), DoubleChromosome.of(0, 1))
			.build();

		final EvolutionResult<DoubleGene, Double> result = engine.stream()
			.limit(123)
			//.peek(new FitnessStatistics())
			.collect(EvolutionResult.toBestEvolutionResult());

		Assert.assertEquals(123L, result.getTotalGenerations());
	}

	static class FitnessStatistics implements Consumer<EvolutionResult<DoubleGene, Double>> {
		@Override
		public void accept(EvolutionResult<DoubleGene, Double> result) {
			final DoubleMomentStatistics stat = new DoubleMomentStatistics();
			result.getPopulation().stream()
				.mapToDouble(Phenotype::getFitness)
				.forEach(stat);

			System.out.println(stat);
		}
	}

}
