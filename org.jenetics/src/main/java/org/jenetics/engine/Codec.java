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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
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
	Factory<Genotype<G>> encoding();

	/**
	 * Return the <em>decoder</em> function which transforms the genotype back
	 * to the original problem domain representation.
	 *
	 * @return genotype decoder
	 */
	Function<Genotype<G>, T> decoder();


	/**
	 * Create a new {@code Codec} object with the given {@code encoding} and
	 * {@code decoder} function.
	 *
	 * @param encoding the genotype factory used for creating new
	 *        {@code Genotypes}.
	 * @param decoder decoder function, which converts a {@code Genotype} to a
	 *        value in the problem domain.
	 * @param <G> the {@code Gene} type
	 * @param <T> the fitness function argument type in the problem domain
	 * @return a new {@code Codec} object with the given parameters.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	static <G extends Gene<?, G>, T> Codec<T, G> of(
		final Factory<Genotype<G>> encoding,
		final Function<Genotype<G>, T> decoder
	) {
		requireNonNull(encoding);
		requireNonNull(decoder);

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


	/* *************************************************************************
	 * Factory methods for commonly usable Codecs.
	 **************************************************************************/


	/**
	 * Return a scalar {@code Codec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	static Codec<Integer, IntegerGene> of(final IntRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain.getMin(), domain.getMax())),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	/**
	 * Return an vector {@code Codec} for the given range. All vector values
	 * are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	static Codec<int[], IntegerGene> of(
		final IntRange domain,
		final int length
	) {
		requireNonNull(domain);
		require.positive(length);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(
				domain.getMin(), domain.getMax(), length
			)),
			gt -> ((IntegerChromosome)gt.getChromosome()).toArray()
		);
	}

	/**
	 *
	 * @param domain1
	 * @param domain2
	 * @param domainN
	 * @return
	 */
	static Codec<int[], IntegerGene> of(
		final IntRange domain1,
		final IntRange domain2,
		final IntRange... domainN
	) {
		final IntegerGene[] genes = Stream
			.concat(Stream.of(domain1, domain2), Stream.of(domainN))
			.map(d -> IntegerGene.of(d.getMin(), d.getMin()))
			.toArray(IntegerGene[]::new);

		final int length = 2 + domainN.length;
		return Codec.of(
			Genotype.of(IntegerChromosome.of(genes)),
			gt -> {
				final int[] args = new int[length];
				for (int i = 2 + domainN.length; --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().intValue();
				}
				return args;
			}
		);
	}


}
