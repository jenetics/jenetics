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
import java.util.random.RandomGeneratorFactory;

import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;

/**
 * This GA produces the same results on every run. The {@code RandomRegistry.with}
 * block allows you to <em>inject</em> a random engine with the same start state
 * at every call.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.3
 */
public class ReproducibleGA {

	private static final Codec<Double, DoubleGene>
	CODEC = Codecs.ofScalar(DoubleRange.of(0, 1));

	public static void main(final String[] args) {
		final var random = RandomGeneratorFactory.getDefault();

		final ISeq<Genotype<DoubleGene>> population =
			RandomRegistry.with(random.create(123), r ->
				CODEC.encoding().instances()
					.limit(10)
					.collect(ISeq.toISeq())
			);

		final Engine<DoubleGene, Double> engine =
			Engine.builder(Function.identity(), CODEC)
				.executor(Runnable::run)
				.build();

		final EvolutionResult<DoubleGene, Double> result =
			RandomRegistry.with(random.create(456), r ->
				engine.stream(population)
					.limit(100)
					.collect(EvolutionResult.toBestEvolutionResult())
			);

		System.out.println(result.bestPhenotype());
	}

}
