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

import org.jenetics.util.Mean;
import org.jenetics.util.Numeric;

/**
 * Abstract base class for implementing concrete NumericGenes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-12 $</em>
 * @since @__version__@
 */
public abstract class NumericGene<N, G extends NumericGene<N, G>>
	extends BoundedGene<N, G>
	implements Mean<G>
{
	private static final long serialVersionUID = 1L;

	protected final Numeric<N> _numeric;

	/**
	 * Create new {@code NumericGene}.
	 *
	 * @param value The value of the gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @param numeric the comparator used for comparing the alleles.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	protected NumericGene(
		final N value,
		final N min,
		final N max,
		final Numeric<N> numeric
	) {
		super(value, min, max, numeric);
		_numeric = numeric;
	}

	public Numeric<N> numeric() {
		return _numeric;
	}

	/**
	 * Return the number value of this gene.
	 *
	 * @return the number value of this gene.
	 */
	public N getNumber() {
		return _value;
	}

	/**
	 * Returns the value of the specified gene as an byte. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code byte}.
	 */
	public byte byteValue() {
		return _numeric.toByteValue(_value);
	}

	/**
	 * Returns the value of the specified gene as an short. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code short}.
	 */
	public short shortValue() {
		return _numeric.toShortValue(_value);
	}

	/**
	 * Returns the value of the specified gene as an int. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code int}.
	 */
	public int intValue() {
		return _numeric.toIntValue(_value);
	}

	/**
	 * Returns the value of the specified gene as an long. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code long}.
	 */
	public long longValue() {
		return _numeric.toLongValue(_value);
	}

	/**
	 * Returns the value of the specified gene as an float. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code float}.
	 */
	public float floatValue() {
		return _numeric.toFloatValue(_value);
	}

	/**
	 * Returns the value of the specified gene as an double. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code double}.
	 */
	public double doubleValue() {
		return _numeric.toDoubleValue(_value);
	}

	@Override
	public G mean(final G that) {
		return newInstance(_numeric.mean(_value, that._value));
	}

}
