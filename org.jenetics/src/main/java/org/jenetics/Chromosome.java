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
package org.jenetics;

import java.util.stream.Stream;

import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.Verifiable;

/**
 * A chromosome consists of one or more genes. It also provides a factory
 * method for creating new, random chromosome instances of the same type and the
 * same constraint.
 * <p>
 * <span class="simpleTagLabel">API Note: </span>
 * Implementations of the {@code Chromosome} interface must be <em>immutable</em>
 * and guarantee an efficient random access ({@code O(1)}) to the genes. A
 * {@code Chromosome} must contains at least one {@code Gene}.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Chromosome">Wikipdida: Chromosome</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.7
 */
public interface Chromosome<G extends Gene<?, G>>
	extends
		Verifiable,
		Iterable<G>,
		Factory<Chromosome<G>>
{
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
	public Chromosome<G> newInstance(final ISeq<G> genes);

	/**
	 * Return the first gene of this chromosome. Each chromosome must contain
	 * at least one gene.
	 *
	 * @return the first gene of this chromosome.
	 */
	public default G getGene() {
		return getGene(0);
	}

	/**
	 * Return the gene on the specified index.
	 *
	 * @param index The gene index.
	 * @return the wanted gene.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *          (index &lt; 1 || index &gt;= length()).
	 */
	public G getGene(final int index);

	/**
	 * Returns the length of the Chromosome. The minimal length of a
	 * chromosome is one.
	 *
	 * @return Length of the Chromosome
	 */
	public int length();

	/**
	 * Return an unmodifiable sequence of the genes of this chromosome.
	 *
	 * @return an immutable gene sequence.
	 */
	public ISeq<G> toSeq();

	/**
	 * Casts this {@code Chromosome} to an instance of type {@code C}.
	 * This is a convenient method for an ordinary cast and allows seamless
	 * method-chaining. Instead of
	 * <pre>{@code
	 * final Genotype<BitGene> gt = ...
	 * final int count = ((BitChromosome)gt.getChromosome()).bitCount()
	 * }</pre>
	 * you can write
	 * <pre>{@code
	 * final Genotype<BitGene> gt = ...
	 * final int count = gt.getChromosome()
	 *     .as(BitChromosome.class)
	 *     .bitCount()
	 * }</pre>
	 * This may lead to a more elegant programming style in some cases.
	 *
	 * @since 3.7
	 *
	 * @param type the target type class
	 * @param <C> the target chromosome type
	 * @return this chromosome casted as {@code C}
	 * @throws NullPointerException if the target type class is {@code null}
	 * @throws ClassCastException if this chromosome can't be casted to a
	 *         chromosome of type {@code C}
	 */
	public default <C extends Chromosome<G>> C as(final Class<C> type) {
		return type.cast(this);
	}

	/**
	 * Returns a sequential {@code Stream} of genes with this chromosome as
	 * its source.
	 *
	 * @since 3.3
	 *
	 * @return a sequential {@code Stream} of genes
	 */
	public default Stream<G> stream() {
		return toSeq().stream();
	}

}
