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

/**
 * Base interface for numeric genes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-03-05 $</em>
 * @since 1.6
 */
public interface NumericGene<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends BoundedGene<N, G>
{

	/**
	 * Returns the value of the specified gene as an byte. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code byte}.
	 */
	public byte byteValue();

	/**
	 * Returns the value of the specified gene as an short. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code short}.
	 */
	public short shortValue();

	/**
	 * Returns the value of the specified gene as an int. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code int}.
	 */
	public int intValue();

	/**
	 * Returns the value of the specified gene as an long. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code long}.
	 */
	public long longValue();

	/**
	 * Returns the value of the specified gene as an float. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code float}.
	 */
	public float floatValue();

	/**
	 * Returns the value of the specified gene as an double. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code double}.
	 */
	public double doubleValue();

	@Override
	public G newInstance(final Number number);

}
