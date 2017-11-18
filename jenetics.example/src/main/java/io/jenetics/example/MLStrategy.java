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

import io.jenetics.DoubleGene;
import io.jenetics.Mutator;
import io.jenetics.TruncationSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.DoubleRange;

/**
 * The (μ, λ) evolution strategy.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class MLStrategy {

	static double fitness(final double x) {
		return x;
	}

	public static void main(final String[] args) {
		final int μ = 5;
		final int λ = 20;
		final double p = 0.2;

		final Codec<Double, DoubleGene> codec = Codecs
			.ofScalar(DoubleRange.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(MLStrategy::fitness, codec)
			.populationSize(λ)
			.survivorsSize(0)
			.offspringSelector(new TruncationSelector<>(μ))
			.alterers(new Mutator<>(p))
			.build();

		System.out.println(
			codec.decode(
				engine.stream()
					.limit(100)
					.collect(EvolutionResult.toBestGenotype())
			)
		);
	}

}
