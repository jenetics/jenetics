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
package org.jenetics.example;

import org.jenetics.DoubleGene;
import org.jenetics.Mutator;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;

/**
 * The (μ + λ) evolution strategy.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class MpLStrategy {

	static double fitness(final double x) {
		return x;
	}

	public static void main(final String[] args) {
		final int μ = 5;
		final int λ = 20;
		final double p = 0.2;

		final Codec<Double, DoubleGene> codec = codecs
			.ofScalar(DoubleRange.of(0, 1));

		final Engine<DoubleGene, Double> engine = Engine
			.builder(MpLStrategy::fitness, codec)
			.populationSize(λ)
			.survivorsSize(μ)
			.selector(new TruncationSelector<>(μ))
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
