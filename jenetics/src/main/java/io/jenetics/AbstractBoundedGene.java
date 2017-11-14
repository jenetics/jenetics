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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for genes where the alleles are bound by a minimum and a maximum
 * value.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
		return _value.compareTo(_min) >= 0 && _value.compareTo(_max) <= 0;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*Objects.hashCode(_value) +37;
		hash += 31*Objects.hashCode(_min) +37;
		hash += 31*Objects.hashCode(_max) +37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof AbstractBoundedGene<?, ?> &&
			Objects.equals(((AbstractBoundedGene)obj)._value, _value) &&
			Objects.equals(((AbstractBoundedGene)obj)._min, _min) &&
			Objects.equals(((AbstractBoundedGene)obj)._max, _max);
	}

	@Override
	public String toString() {
		return String.format("[%s]", _value);
	}
}
