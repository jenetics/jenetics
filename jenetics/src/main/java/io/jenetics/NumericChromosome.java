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

/**
 * Numeric chromosome interface.
 *
 * @implSpec
 * Implementations of the {@code NumericChromosome} interface must be
 * <em>immutable</em> and guarantee efficient random access ({@code O(1)}) to
 * the genes. A {@code Chromosome} must contains at least one {@code Gene}.
 *
 * @see NumericGene
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 5.2
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
	default byte byteValue(final int index) {
		return get(index).allele().byteValue();
	}

	/**
	 * Return the byte value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the byte value of the {@link Gene} with {@code index} 0.
	 */
	default byte byteValue() {
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
	default short shortValue(final int index) {
		return get(index).allele().shortValue();
	}

	/**
	 * Return the short value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the short value of the {@link Gene} with {@code index} 0.
	 */
	default short shortValue() {
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
	default int intValue(final int index) {
		return get(index).allele().intValue();
	}

	/**
	 * Return the int value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the int value of the {@link Gene} with {@code index} 0.
	 */
	default int intValue() {
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
	default long longValue(final int index) {
		return get(index).allele().longValue();
	}

	/**
	 * Return the long value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the long value of the {@link Gene} with {@code index} 0.
	 */
	default long longValue() {
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
	default float floatValue(final int index) {
		return get(index).allele().floatValue();
	}

	/**
	 * Return the float value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the float value of the {@link Gene} with {@code index} 0.
	 */
	default float floatValue() {
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
	default double doubleValue(final int index) {
		return get(index).allele().doubleValue();
	}

	/**
	 * Return the double value of this {@code NumericChromosome} at the
	 * {@code index} 0.
	 *
	 * @return the double value of the {@link Gene} with {@code index} 0.
	 */
	default double doubleValue() {
		return doubleValue(0);
	}

}
