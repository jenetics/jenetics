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
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.io.Serializable;
import java.util.Comparator;

import org.jenetics.util.Mean;

/**
 * Abstract base class for implementing concrete NumericGenes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-06 $</em>
 * @since @__version__@
 */
public abstract class NumericGene<
	N extends Number,
	G extends NumericGene<N, G>
>
	extends Number
	implements
		Gene<N, G>,
		Mean<G>,
		Comparable<G>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code NumericGene}.
	 */
	protected N _min;

	/**
	 * The maximum value of this {@code NumericGene}.
	 */
	protected N _max;

	/**
	 * The value of this {@code NumericGene}.
	 */
	protected N _value;

	protected Comparator<N> _comparator;

	private boolean _valid = true;

	/**
	 * Set the {@code NumericGene}.
	 *
	 * @param value The value of the number gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	protected NumericGene(
		final N value,
		final N min,
		final N max,
		final Comparator<N> comparator
	) {
		_min = requireNonNull(min, "Min value not be null.");
		_max = requireNonNull(max, "Max value must not be null.");
		_value = requireNonNull(value, "Gene value must not be null.");
		_comparator = requireNonNull(comparator, "Comparator must not be null.");
		_valid = comparator.compare(_value, _min) >= 0 &&
				comparator.compare(_value, _max) <= 0;
	}

	/**
	 * Return the number value of this gene.
	 *
	 * @return the number value of this gene.
	 */
	public N getNumber() {
		return _value;
	}

	@Override
	public N getAllele() {
		return _value;
	}

	/**
	 * Return the allowed min value.
	 *
	 * @return The allowed min value.
	 */
	public N getMin() {
		return _min;
	}

	/**
	 * Return the allowed max value.
	 *
	 * @return The allowed max value.
	 */
	public N getMax() {
		return _max;
	}

	@Override
	public int intValue() {
		return _value.intValue();
	}

	@Override
	public long longValue() {
		return _value.longValue();
	}

	@Override
	public float floatValue() {
		return _value.floatValue();
	}

	@Override
	public double doubleValue() {
		return _value.doubleValue();
	}

	@Override
	public Object copy() {
		return this;
	}

	public abstract G newInstance(final N value);

	@Override
	public boolean isValid() {
		return _valid;
	}

	@Override
	public int compareTo(final G other) {
		return _comparator.compare(_value, other._value);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_value).and(_min).and(_max).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final NumericGene<?, ?> gene = (NumericGene<?, ?>)obj;
		return eq(_value, gene._value) &&
			eq(_min, gene._min) &&
			eq(_max, gene._max);
	}

	@Override
	public String toString() {
		return String.format("[%s]", _value);
	}

}
