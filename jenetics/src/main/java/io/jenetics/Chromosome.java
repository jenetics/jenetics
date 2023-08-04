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
package io.jenetics;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.Verifiable;

/**
 * A chromosome consists of one or more genes. It also provides a factory
 * method for creating new, random chromosome instances of the same type and the
 * same constraint.
 *
 * @implSpec
 * Implementations of the {@code Chromosome} interface must be <em>immutable</em>
 * and guarantee efficient random access ({@code O(1)}) to the genes. A
 * {@code Chromosome} must contains at least one {@code Gene}.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Chromosome">Wikipedia: Chromosome</a>
 * @see Genotype
 * @see Gene
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.0
 */
public interface Chromosome<G extends Gene<?, G>>
	extends
		BaseSeq<G>,
		Factory<Chromosome<G>>,
		Verifiable
{

	/**
	 * Return the first gene of this chromosome. Each chromosome must contain
	 * at least one gene.
	 *
	 * @since 5.2
	 *
	 * @return the first gene of this chromosome.
	 */
	default G gene() {
		return get(0);
	}

	@Override
	default boolean isValid() {
		return stream().allMatch(Gene::isValid);
	}

	/**
	 * A factory method which creates a new {@link Chromosome} of specific type
	 * and the given {@code genes}.
	 *
	 * @param genes the genes of the new chromosome. The given genes array is
	 *         not copied.
	 * @return A new {@link Chromosome} of the same type with the given genes.
	 * @throws NullPointerException if the given {@code gene}s are {@code null}.
	 * @throws IllegalArgumentException if the length of the given gene sequence
	 *        is smaller than one.
	 */
	Chromosome<G> newInstance(final ISeq<G> genes);

	/**
	 * Casts this {@code Chromosome} to an instance of type {@code C}.
	 * This is a convenient method for an ordinary cast and allows seamless
	 * method-chaining. Instead of
	 * <pre>{@code
	 * final Genotype<BitGene> gt = ...
	 * final int count = ((BitChromosome)gt.chromosome()).bitCount()
	 * }</pre>
	 * you can write
	 * <pre>{@code
	 * final Genotype<BitGene> gt = ...
	 * final int count = gt.chromosome()
	 *     .as(BitChromosome.class)
	 *     .bitCount()
	 * }</pre>
	 * This may lead to a more elegant programming style in some cases.
	 *
	 * @since 3.7
	 *
	 * @param type the target type class
	 * @param <C> the target chromosome type
	 * @return this chromosome cast as {@code C}
	 * @throws NullPointerException if the target type class is {@code null}
	 * @throws ClassCastException if this chromosome can't be cast to a
	 *         chromosome of type {@code C}
	 */
	default <C extends Chromosome<G>> C as(final Class<C> type) {
		return type.cast(this);
	}

}
