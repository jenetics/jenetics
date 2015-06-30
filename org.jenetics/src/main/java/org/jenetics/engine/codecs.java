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
import static java.util.stream.Stream.concat;

import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.util.IntRange;

/**
 * This class contains factory methods for creating common  problem encodings.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class codecs {

	private codecs() {require.noInstance();}

	/**
	 * Return a scalar {@code Codec} for the given range.
	 *
	 * @param domain the domain of the returned {@code Codec}
	 * @return a new scalar {@code Codec} with the given domain.
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 */
	public static Codec<Integer, IntegerGene> ofScalar(final IntRange domain) {
		requireNonNull(domain);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain)),
			gt -> gt.getChromosome().getGene().getAllele()
		);
	}

	/**
	 * Return a vector {@code Codec} for the given range. All vector values
	 * are restricted by the same domain.
	 *
	 * @param domain the domain of the vector values
	 * @param length the vector length
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if the given {@code domain} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static Codec<int[], IntegerGene> ofVector(
		final IntRange domain,
		final int length
	) {
		requireNonNull(domain);
		require.positive(length);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(domain, length)),
			gt -> ((IntegerChromosome)gt.getChromosome()).toArray()
		);
	}

	/**
	 * Create a vector {@code Codec} for the given ranges. Each vector element
	 * might have a different domain. The vector length is equal to the number
	 * of domains.
	 *
	 * @param first the first domain range
	 * @param rest the rest of the domain ranges
	 * @return a new vector {@code Codec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Codec<int[], IntegerGene> ofVector(
		final IntRange first,
		final IntRange... rest
	) {
		final IntegerGene[] genes = concat(Stream.of(first), Stream.of(rest))
			.map(IntegerGene::of)
			.toArray(IntegerGene[]::new);

		return Codec.of(
			Genotype.of(IntegerChromosome.of(genes)),
			gt -> {
				final int[] args = new int[genes.length];
				for (int i = genes.length; --i >= 0;) {
					args[i] = gt.getChromosome(i).getGene().intValue();
				}
				return args;
			}
		);
	}

}
