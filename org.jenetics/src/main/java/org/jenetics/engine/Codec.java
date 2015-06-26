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
import org.jenetics.util.IntRange;

/**
 * A problem {@code Codec} contains the information about how to encode a given
 * argument type into a {@code Genotype}. It also lets convert the encoded
 * {@code Genotype} back to the argument type.
 *
 * @param <T> the argument type of a given problem
 * @param <G> the {@code Gene} type used for encoding the argument type {@code T}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Codec<T, G extends Gene<?, G>> {

	/**
	 * Return the genotype factory, which represents the encoded problem domain
	 * and argument type, respectively.
	 *
	 * @return the genotype (factory) representation of the problem domain
	 */
	public Factory<Genotype<G>> encoding();

	/**
	 * Return the <em>decoder</em> function which transforms the genotype back
	 * to the original problem domain representation.
	 *
	 * @return genotype decoder
	 */
	public Function<Genotype<G>, T> decoder();





	public static class Real {

		public static Codec<Integer, IntegerGene> of(final IntRange domain) {
			return Codec.of(
				Genotype.of(IntegerChromosome.of(IntegerGene.of(domain.getMin(), domain.getMax()))),
				gt -> gt.getChromosome().getGene().getAllele()
			);
		}

		public static Codec<int[], IntegerGene> of(
			final IntRange domain,
			final int length
		) {
			return Codec.of(
				Genotype.of(IntegerChromosome.of(domain.getMin(), domain.getMax(), length)),
				gt -> ((IntegerChromosome) gt.getChromosome()).toArray()
			);
		}

		public static Codec<int[], IntegerGene> of(
			final IntRange domain1,
			final IntRange domain2,
			final IntRange... domainN
		) {
			return null;
		}

		public static Codec<Long, LongGene> of(
			final long min,
			final long max
		) {
			return Codec.of(
				Genotype.of(LongChromosome.of(LongGene.of(min, max))),
				gt -> gt.getChromosome().getGene().getAllele()
			);
		}

		public static Codec<Double, DoubleGene> of(
			final double min,
			final double max
		) {
			return Codec.of(
				Genotype.of(DoubleChromosome.of(DoubleGene.of(min, max))),
				gt -> gt.getChromosome().getGene().getAllele()
			);
		}

	}

	public static Codec<Integer, IntegerGene> ofInteger(
		final int min,
		final int max
	) {
		return of(
			Genotype.of(IntegerChromosome.of(IntegerGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	public static Codec<Long, LongGene> ofLong(
		final long min,
		final long max
	) {
		return of(
			Genotype.of(LongChromosome.of(LongGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	public static Codec<Double, DoubleGene> ofDouble(
		final double min,
		final double max
	) {
		return of(
			Genotype.of(DoubleChromosome.of(DoubleGene.of(min, max))),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	/**
	 * Create a new {@code Codec} object with the given {@code encoding} and
	 * {@code decoder} function.
	 *
	 * @param encoding
	 * @param decoder
	 * @param <G>
	 * @param <T>
	 * @return
	 */
	public static <G extends Gene<?, G>, T> Codec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder
	) {
		return new Codec<T, G>() {
			@Override
			public Factory<Genotype<G>> encoding() {
				return encoding;
			}

			@Override
			public Function<Genotype<G>, T> decoder() {
				return decoder;
			}
		};
	}

}
