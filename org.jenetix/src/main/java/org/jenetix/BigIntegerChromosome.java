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
package org.jenetix;

import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.math.BigInteger;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.AbstractChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.NumericChromosome;
import org.jenetics.util.ISeq;

/**
 * Numeric chromosome implementation which holds arbitrary sized integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class BigIntegerChromosome
	extends AbstractChromosome<BigIntegerGene>
	implements
		NumericChromosome<BigInteger, BigIntegerGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	private BigInteger _min;
	private BigInteger _max;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller
	 *         than one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 * @throws IllegalArgumentException if the gene sequence is empty
	 */
	protected BigIntegerChromosome(final ISeq<BigIntegerGene> genes) {
		super(genes);
		_min = genes.get(0).getMin();
		_max = genes.get(0).getMax();
	}

	/**
	 * Create a new random {@code BigIntegerChromosome} with the given
	 * {@code length}.
	 *
	 * @param min the min value of the {@link BigIntegerGene}s (inclusively).
	 * @param max the max value of the {@link BigIntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public BigIntegerChromosome(
		final BigInteger min,
		final BigInteger max,
		final int length
	) {
		this(BigIntegerGene.seq(min, max, length));
		_valid = true;
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public BigIntegerChromosome(final BigInteger min, final BigInteger max) {
		this(min, max, 1);
	}

	@Override
	public BigInteger getMin() {
		return _min;
	}

	@Override
	public BigInteger getMax() {
		return _max;
	}

	@Override
	public BigIntegerChromosome newInstance(final ISeq<BigIntegerGene> genes) {
		return null;
	}

	@Override
	public BigIntegerChromosome newInstance() {
		return null;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(super.hashCode())
			.and(_min)
			.and(_max).value();
	}

	@Override
	public boolean equals(final Object object) {
		return Equality.of(this, object).test(nc ->
			eq(_min, nc._min) &&
				eq(_max, nc._max) &&
				super.equals(object)
		);
	}

	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 * @throws NullPointerException if the given {@code genes} array is
	 *         {@code null}
	 */
	public static BigIntegerChromosome of(final BigIntegerGene... genes) {
		return new BigIntegerChromosome(ISeq.of(genes));
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 */
	public static BigIntegerChromosome of(final BigInteger min, BigInteger max, final int length) {
		return new BigIntegerChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new {@code DoubleChromosome} with the given parameter
	 */
	public static BigIntegerChromosome of(final BigInteger min, final BigInteger max) {
		return new BigIntegerChromosome(min, max);
	}

}
