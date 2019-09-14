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

import java.util.stream.Stream;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.EvolutionDurations;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStream;
import io.jenetics.internal.engine.EvolutionStreamImpl;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class EvolutionStreams {

	public static EvolutionStream<IntegerGene, Integer>
	stream(final Stream<Integer> stream) {
		return new EvolutionStreamImpl<>(
			stream
				.map(EvolutionStreams::result)
				.spliterator(),
			false
		);
	}

	public static EvolutionResult<IntegerGene, Integer> result(final int value) {
		final Genotype<IntegerGene> genotype = Genotype.of(
			IntegerChromosome.of(
				IntegerGene.of(value, 0, Integer.MAX_VALUE)
			)
		);

		return EvolutionResult.<IntegerGene, Integer>of(
			Optimize.MINIMUM,
			ISeq.of(Phenotype.of(genotype, 1)),
			1,
			1,
			EvolutionDurations.ZERO,
			1,
			1,
			1
		);
	}

}
