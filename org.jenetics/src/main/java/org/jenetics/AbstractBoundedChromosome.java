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

import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.ISeq;

/**
 * Abstract chromosome for {@code BoundedGene}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 1.6
 */
abstract class AbstractBoundedChromosome<
	A extends Comparable<? super A>,
	G extends AbstractBoundedGene<A, G>
>
	extends AbstractChromosome<G>
	implements BoundedChromosome<A, G>, Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code BoundedChromosome}.
	 */
	A _min;

	/**
	 * The maximum value of this {@code BoundedChromosome}.
	 */
	A _max;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the gene sequence is empty
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	protected AbstractBoundedChromosome(final ISeq<? extends G> genes) {
		super(genes);
		_min = genes.get(0)._min;
		_max = genes.get(0)._max;
	}

	@Override
	public A getMin() {
		return _min;
	}

	@Override
	public A getMax() {
		return _max;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(super.hashCode())
			.and(_min)
			.and(_max).value();
	}

	@Override
	public boolean equals(final Object object) {
		return Equality.of(this, object).test(nc ->
			eq(_min, nc._min) &&
			eq(_max, nc._max) &&
			super.equals(object)
		);
	}

}
