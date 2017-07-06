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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * Base class for genes where the alleles are bound by a minimum and a maximum
 * value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 1.6
 */
abstract class AbstractBoundedGene<
	A extends Comparable<? super A>,
	G extends AbstractBoundedGene<A, G>
>
	implements BoundedGene<A, G>, Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code BoundedGene}.
	 */
	final A _min;

	/**
	 * The maximum value of this {@code BoundedGene}.
	 */
	final A _max;

	/**
	 * The value of this {@code BoundedGene}.
	 */
	final A _value;

	// Holds the valid state of the gene.
	private final boolean _valid;

	/**
	 * Create new {@code BoundedGene}.
	 *
	 * @param value The value of the gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	protected AbstractBoundedGene(
		final A value,
		final A min,
		final A max
	) {
		_min = requireNonNull(min, "Min value not be null.");
		_max = requireNonNull(max, "Max value must not be null.");
		_value = requireNonNull(value, "Gene value must not be null.");
		_valid = _value.compareTo(min) >= 0 && _value.compareTo(max) <= 0;
	}

	@Override
	public A getAllele() {
		return _value;
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
	public boolean isValid() {
		return _valid;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_value)
			.and(_min)
			.and(_max).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(gene ->
			eq(_value, gene._value) &&
			eq(_min, gene._min) &&
			eq(_max, gene._max)
		);
	}

	@Override
	public String toString() {
		return String.format("[%s]", _value);
	}
}
