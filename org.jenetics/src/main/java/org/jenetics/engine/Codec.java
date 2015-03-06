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

import java.util.function.Function;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public interface Codec<G extends Gene<?, G>, S> {

	public Function<S, Genotype<G>> encoder();

	public Function<Genotype<G>, S> decoder();

	public static <G extends Gene<?, G>, S> Codec<G, S> of(
		final Function<S, Genotype<G>> encoder,
		final Function<Genotype<G>, S> decoder
	) {
		return new Codec<G, S>() {
			@Override
			public Function<S, Genotype<G>> encoder() {
				return encoder;
			}

			@Override
			public Function<Genotype<G>, S> decoder() {
				return decoder;
			}
		};
	}

	public static Codec<DoubleGene, Double> ofDouble(
		final double min,
		final double max
	) {
		return of(
			value -> Genotype
				.of(DoubleChromosome.of(DoubleGene.of(value, min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

}
