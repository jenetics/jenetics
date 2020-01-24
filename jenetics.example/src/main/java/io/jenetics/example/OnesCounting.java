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

import java.util.function.Function;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;

/**
 * Full Ones-Counting example.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.6
 * @since 3.5
 */
public class OnesCounting implements Problem<ISeq<BitGene>, BitGene, Integer> {

	private final int _length;
	private final double _onesProbability;

	/**
	 * Create a new Ones-Counting example with the given parameters.
	 *
	 * @param length the length of the ones-vector
	 * @param onesProbability the probability of ones in the created vector
	 */
	public OnesCounting(final int length, final double onesProbability) {
		_length = length;
		_onesProbability = onesProbability;
	}

	@Override
	public Function<ISeq<BitGene>, Integer> fitness() {
		return genes -> (int)genes.stream().filter(BitGene::bit).count();
	}

	@Override
	public Codec<ISeq<BitGene>, BitGene> codec() {
		return Codec.of(
			Genotype.of(BitChromosome.of(_length, _onesProbability)),
			gt -> ISeq.of(gt.chromosome())
		);
	}

	public static void main(final String[] args) {
		final OnesCounting problem = new OnesCounting(15, 0.13);
		final Engine<BitGene, Integer> engine = Engine.builder(problem).build();

		final ISeq<BitGene> result = problem.codec().decoder().apply(
			engine.stream()
				.limit(10)
				.collect(EvolutionResult.toBestGenotype())
		);

		System.out.println(result);
	}

}
