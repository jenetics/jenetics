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
package io.jenetics.ext;

import static io.jenetics.internal.util.SerialIO.readBytes;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeBytes;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;

import io.jenetics.AbstractChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.NumericChromosome;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds arbitrary sized integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.2
 * @since 3.5
 */
public class BigIntegerChromosome
	extends AbstractChromosome<BigIntegerGene>
	implements
		NumericChromosome<BigInteger, BigIntegerGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	private final BigInteger _min;
	private final BigInteger _max;

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
		_min = genes.get(0).min();
		_max = genes.get(0).max();
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
	public BigInteger min() {
		return _min;
	}

	@Override
	public BigInteger max() {
		return _max;
	}

	@Override
	public BigIntegerChromosome newInstance(final ISeq<BigIntegerGene> genes) {
		return new BigIntegerChromosome(genes);
	}

	@Override
	public BigIntegerChromosome newInstance() {
		return new BigIntegerChromosome(_min, _max, length());
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

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.BIG_INTEGER_CHROMOSOME, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(length(), out);
		writeBytes(_min.toByteArray(), out);
		writeBytes(_max.toByteArray(), out);

		for (BigIntegerGene gene : _genes) {
			writeBytes(gene.allele().toByteArray(), out);
		}
	}

	static BigIntegerChromosome read(final DataInput in) throws IOException {
		final var length = readInt(in);
		final var min = new BigInteger(readBytes(in));
		final var max = new BigInteger(readBytes(in));

		final MSeq<BigIntegerGene> genes = MSeq.ofLength(length);
		for (int i = 0; i < length; ++i) {
			final BigInteger value = new BigInteger(readBytes(in));
			genes.set(i, BigIntegerGene.of(value, min, max));
		}

		return new BigIntegerChromosome(genes.toISeq());
	}

}
