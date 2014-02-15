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

import java.util.Comparator;

/**
 * Abstract base class for implementing concrete NumericGenes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-02-15 $</em>
 * @since 1.6
 */
public abstract class NumericGene<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends BoundedGene<N, G>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create new {@code NumericGene}.
	 *
	 * @param value The value of the gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given arguments is
	 *         {@code null}.
	 */
	protected NumericGene(final N value, final N min, final N max) {
		super(value, min, max);
	}

	@Override
	public abstract G newInstance(final Number value);

	/**
	 * Returns the value of the specified gene as an byte. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code byte}.
	 */
	public byte byteValue() {
		return _value.byteValue();
	}

	/**
	 * Returns the value of the specified gene as an short. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code short}.
	 */
	public short shortValue() {
		return _value.shortValue();
	}

	/**
	 * Returns the value of the specified gene as an int. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code int}.
	 */
	public int intValue() {
		return _value.intValue();
	}

	/**
	 * Returns the value of the specified gene as an long. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code long}.
	 */
	public long longValue() {
		return _value.longValue();
	}

	/**
	 * Returns the value of the specified gene as an float. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code float}.
	 */
	public float floatValue() {
		return _value.floatValue();
	}

	/**
	 * Returns the value of the specified gene as an double. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code double}.
	 */
	public double doubleValue() {
		return _value.doubleValue();
	}

}
