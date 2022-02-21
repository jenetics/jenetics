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

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static io.jenetics.engine.Limits.bySteadyFitness;

import java.util.List;
import java.util.Random;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.prngine.LCG64ShiftRandom;
import io.jenetics.util.RandomRegistry;

public class RngExample {

	private static Integer count(final Genotype<BitGene> gt) {
		return ((BitChromosome)gt.chromosome()).bitCount();
	}

	public static void main(String[] args) {
		// Set the PRNG used by the evolution Engine.
		final LCG64ShiftRandom random = new LCG64ShiftRandom(123);
		RandomRegistry.random(random);

		// Configure and build the evolution Engine.
		final Engine<BitGene, Integer> engine = Engine
			.builder(
				RngExample::count,
				BitChromosome.of(20, 0.15))
			.build();

		// The 'Random(123)' object is used for creating a *reproducible*
		// initial population. The original PRNG is restored after the 'with'
		// block.
		assert RandomRegistry.random() == random;
		final List<Genotype<BitGene>> genotypes =
			RandomRegistry.with(new Random(123), r -> {
				assert RandomRegistry.random() == r;
				return Genotype.of(BitChromosome.of(20, 0.15))
					.instances()
					.limit(50)
					.toList();
			});
		assert RandomRegistry.random() == random;

		// The evolution process uses the global 'random' instance.
		final Phenotype<BitGene, Integer> best = engine.stream(genotypes)
			.limit(bySteadyFitness(20))
			.limit(100)
			.collect(toBestPhenotype());

		System.out.println(best);
	}

}
