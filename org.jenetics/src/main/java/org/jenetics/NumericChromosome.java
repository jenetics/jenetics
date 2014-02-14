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

import org.jenetics.util.ISeq;

/**
 * Abstract numeric chromosome.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-13 $</em>
 * @since @__version__@
 */
public abstract class NumericChromosome<
	N extends Number & Comparable<? super N>,
	G extends NumericGene<N, G>
>
	extends BoundedChromosome<N, G>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller
	 *          than one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	protected NumericChromosome(final ISeq<? extends G> genes) {
		super(genes);
	}

	/**
	 * Return the byte value of this {@code NumericChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumericGene}.
	 * @return the byte value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public byte byteValue(final int index) {
		return getGene(index).getAllele().byteValue();
	}

	/**
	 * Return the byte value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the byte value of the {@link Gene} with {@code index} 0.
	 */
	public byte byteValue() {
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
	public short shortValue(final int index) {
		return getGene(index).getAllele().shortValue();
	}

	/**
	 * Return the short value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the short value of the {@link Gene} with {@code index} 0.
	 */
	public short shortValue() {
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
	public int intValue(final int index) {
		return getGene(index).getAllele().intValue();
	}

	/**
	 * Return the int value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the int value of the {@link Gene} with {@code index} 0.
	 */
	public int intValue() {
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
	public long longValue(final int index) {
		return getGene(index).getAllele().longValue();
	}

	/**
	 * Return the long value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the long value of the {@link Gene} with {@code index} 0.
	 */
	public long longValue() {
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
	public float floatValue(final int index) {
		return getGene(index).getAllele().floatValue();
	}

	/**
	 * Return the float value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the float value of the {@link Gene} with {@code index} 0.
	 */
	public float floatValue() {
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
	public double doubleValue(final int index) {
		return getGene(index).getAllele().doubleValue();
	}

	/**
	 * Return the double value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the double value of the {@link Gene} with {@code index} 0.
	 */
	public double doubleValue() {
		return doubleValue(0);
	}

}
