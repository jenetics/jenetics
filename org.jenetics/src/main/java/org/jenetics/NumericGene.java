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
 * @since 1.6
 * @version 3.0
 */
public interface NumericGene<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends
		BoundedGene<N, G>,
		Comparable<G>
{

	/**
	 * Returns the value of the specified gene as an byte. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code byte}.
	 */
	public default byte byteValue() {
		return getAllele().byteValue();
	}

	/**
	 * Returns the value of the specified gene as an short. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code short}.
	 */
	public default short shortValue() {
		return getAllele().shortValue();
	}

	/**
	 * Returns the value of the specified gene as an int. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code int}.
	 */
	public default int intValue() {
		return getAllele().intValue();
	}

	/**
	 * Returns the value of the specified gene as an long. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code long}.
	 */
	public default long longValue() {
		return getAllele().longValue();
	}

	/**
	 * Returns the value of the specified gene as an float. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code float}.
	 */
	public default float floatValue() {
		return getAllele().floatValue();
	}

	/**
	 * Returns the value of the specified gene as an double. This may involve
	 * rounding or truncation.
	 *
	 * @return the numeric value represented by this object after conversion to
	 *         type {@code double}.
	 */
	public default double doubleValue() {
		return getAllele().doubleValue();
	}

	@Override
	public G newInstance(final Number number);

}
