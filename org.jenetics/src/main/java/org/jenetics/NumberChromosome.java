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

import static org.jenetics.internal.util.object.eq;

import org.jscience.mathematics.number.Number;

import org.jenetics.internal.util.HashBuilder;

import org.jenetics.util.ISeq;


/**
 * Abstract number chromosome.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2014-03-05 $</em>
 *
 * @deprecated Use {@link AbstractNumericChromosome} instead. This classes
 *             uses the <i>JScience</i> library, which will be removed in the
 *             next major version.
 */
@Deprecated
public abstract class NumberChromosome<
	N extends Number<N>,
	G extends NumberGene<N, G>
>
	extends AbstractChromosome<G>
	implements NumericChromosome<N, G>
{
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code NumberChromosome}.
	 */
	protected transient N _min;

	/**
	 * The maximum value of this {@code NumberChromosome}.
	 */
	protected transient N _max;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller
	 *          than one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	protected NumberChromosome(final ISeq<? extends G> genes) {
		super(genes);
		_min = genes.get(0)._min;
		_max = genes.get(0)._max;
	}

	/**
	 * Return the minimum value of this {@code NumberChromosome}.
	 *
	 * @return the minimum value of this {@code NumberChromosome}.
	 */
	public N getMin() {
		return _min;
	}

	/**
	 * Return the maximum value of this {@code NumberChromosome}.
	 *
	 * @return the maximum value of this {@code NumberChromosome}.
	 */
	public N getMax() {
		return _max;
	}

	/**
	 * Return the byte value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the byte value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public byte byteValue(final int index) {
		return getGene(index).getAllele().byteValue();
	}

	/**
	 * Return the byte value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the byte value of the {@link Gene} with {@code index} 0.
	 */
	public byte byteValue() {
		return byteValue(0);
	}

	/**
	 * Return the short value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the short value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public short shortValue(final int index) {
		return getGene(index).getAllele().shortValue();
	}

	/**
	 * Return the short value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the short value of the {@link Gene} with {@code index} 0.
	 */
	public short shortValue() {
		return shortValue(0);
	}

	/**
	 * Return the int value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the int value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public int intValue(final int index) {
		return getGene(index).getAllele().intValue();
	}

	/**
	 * Return the int value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the int value of the {@link Gene} with {@code index} 0.
	 */
	public int intValue() {
		return intValue(0);
	}

	/**
	 * Return the long value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the long value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public long longValue(final int index) {
		return getGene(index).getAllele().longValue();
	}

	/**
	 * Return the long value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the long value of the {@link Gene} with {@code index} 0.
	 */
	public long longValue() {
		return longValue(0);
	}

	/**
	 * Return the float value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the float value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public float floatValue(final int index) {
		return getGene(index).getAllele().floatValue();
	}

	/**
	 * Return the float value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the float value of the {@link Gene} with {@code index} 0.
	 */
	public float floatValue() {
		return floatValue(0);
	}

	/**
	 * Return the double value of this {@code NumberChromosome} at the given
	 * {@code index}.
	 *
	 * @param index the index of the {@link NumberGene}.
	 * @return the double value of the {@link Gene} with the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         (index &lt; 0 || index &gt;= length()).
	 */
	public double doubleValue(final int index) {
		return getGene(index).getAllele().doubleValue();
	}

	/**
	 * Return the double value of this {@code NumberChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the double value of the {@link Gene} with {@code index} 0.
	 */
	public double doubleValue() {
		return doubleValue(0);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).
				and(super.hashCode()).
				and(_min).
				and(_max).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof NumberChromosome<?, ?>)) {
			return false;
		}

		final NumberChromosome<?, ?> nc = (NumberChromosome<?, ?>)object;
		return eq(_min, nc._min) && eq(_max, nc._max) && super.equals(object);
	}


}
