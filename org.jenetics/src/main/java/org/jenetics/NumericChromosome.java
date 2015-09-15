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
 * Numeric chromosome interface.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.0
 */
public interface NumericChromosome<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends BoundedChromosome<N, G>
{

	/**
	 * Return the byte value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the byte value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default byte byteValue(int index) {
		return getGene(index).getAllele().byteValue();
	}

	/**
	 * Return the byte value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the byte value of the {@link Gene} with {@code index} 0.
	 */
	public default byte byteValue() {
		return byteValue(0);
	}

	/**
	 * Return the short value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the short value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default short shortValue(int index) {
		return getGene(index).getAllele().shortValue();
	}

	/**
	 * Return the short value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the short value of the {@link Gene} with {@code index} 0.
	 */
	public default short shortValue() {
		return shortValue(0);
	}

	/**
	 * Return the int value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the int value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default int intValue(int index) {
		return getGene(index).getAllele().intValue();
	}

	/**
	 * Return the int value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the int value of the {@link Gene} with {@code index} 0.
	 */
	public default int intValue() {
		return intValue(0);
	}

	/**
	 * Return the long value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the long value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default long longValue(int index) {
		return getGene(index).getAllele().longValue();
	}

	/**
	 * Return the long value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the long value of the {@link Gene} with {@code index} 0.
	 */
	public default long longValue() {
		return longValue(0);
	}

	/**
	 * Return the float value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the float value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default float floatValue(int index) {
		return getGene(index).getAllele().floatValue();
	}

	/**
	 * Return the float value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the float value of the {@link Gene} with {@code index} 0.
	 */
	public default float floatValue() {
		return floatValue(0);
	}

	/**
	 * Return the double value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the double value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public default double doubleValue(int index) {
		return getGene(index).getAllele().doubleValue();
	}

	/**
	 * Return the double value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the double value of the {@link Gene} with {@code index} 0.
	 */
	public default double doubleValue() {
		return doubleValue(0);
	}

}
