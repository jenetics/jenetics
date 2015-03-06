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
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public interface Codec<G extends Gene<?, G>, S> {

	public Factory<Genotype<G>> genotype();

	public Function<Genotype<G>, S> decoder();

	public static <G extends Gene<?, G>, S> Codec<G, S> of(
		final Factory<Genotype<G>> genotype,
		final Function<Genotype<G>, S> decoder
	) {
		return new Codec<G, S>() {
			@Override
			public Factory<Genotype<G>> genotype() {
				return genotype;
			}

			@Override
			public Function<Genotype<G>, S> decoder() {
				return decoder;
			}
		};
	}

	public static Codec<IntegerGene, Integer> ofInteger(
		final int min,
		final int max
	) {
		return of(
			Genotype.of(IntegerChromosome.of(IntegerGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	public static Codec<LongGene, Long> ofLong(
		final long min,
		final long max
	) {
		return of(
			Genotype.of(LongChromosome.of(LongGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	public static Codec<DoubleGene, Double> ofDouble(
		final double min,
		final double max
	) {
		return of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

}
