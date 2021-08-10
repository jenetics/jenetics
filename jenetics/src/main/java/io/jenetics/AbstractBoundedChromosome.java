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

import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

/**
 * Abstract chromosome for {@code BoundedGene}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 1.6
 */
abstract class AbstractBoundedChromosome<
	A extends Comparable<? super A>,
	G extends BoundedGene<A, G>
>
	extends VariableChromosome<G>
	implements BoundedChromosome<A, G>, Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code BoundedChromosome}.
	 */
	final A _min;

	/**
	 * The maximum value of this {@code BoundedChromosome}.
	 */
	final A _max;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty or doesn't match with the allowed length range.
	 * @throws IllegalArgumentException if the minimum or maximum of the range
	 *         is smaller or equal zero
	 * @throws IllegalArgumentException if the given range size is zero
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	AbstractBoundedChromosome(
		final ISeq<? extends G> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
		_min = genes.get(0).min();
		_max = genes.get(0).max();
	}

	@Override
	public A min() {
		return _min;
	}

	@Override
	public A max() {
		return _max;
	}

	@Override
	public int hashCode() {
		return
			hash(super.hashCode(),
			hash(_min,
			hash(_max)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj != null &&
			getClass() == obj.getClass() &&
			Objects.equals(_min, ((AbstractBoundedChromosome<?, ?>)obj)._min) &&
			Objects.equals(_max, ((AbstractBoundedChromosome<?, ?>)obj)._max) &&
			super.equals(obj);
	}

	static void checkGeneRange(final Stream<?> ranges) {
		if (ranges.distinct().count() > 1) {
			throw new IllegalArgumentException(
				"All genes must have the same range."
			);
		}
	}

}
