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

import static java.util.Objects.requireNonNull;
import static io.jenetics.util.RandomRegistry.getRandom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.jenetics.internal.math.random;
import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
import io.jenetics.internal.util.reflect;
import io.jenetics.internal.util.require;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 64 bit floating point numbers.
 *
 * @see DoubleGene
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 4.0
 */
public class DoubleChromosome
	extends AbstractBoundedChromosome<Double, DoubleGene>
	implements
		NumericChromosome<Double, DoubleGene>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	private final IntRange _lengthRange;

	protected DoubleChromosome(
		final ISeq<DoubleGene> genes,
		final IntRange lengthRange
	) {
		super(genes);
		_lengthRange = checkLengthRange(lengthRange);
	}

	private static IntRange checkLengthRange(final IntRange lengthRange) {
		requireNonNull(lengthRange);
		require.positive(lengthRange.getMin());
		require.positive(lengthRange.getMax());
		require.positive(lengthRange.size());
		return lengthRange;
	}

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the gene sequence is empty
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	protected DoubleChromosome(final ISeq<DoubleGene> genes) {
		this(genes, IntRange.of(genes.size(), genes.size() + 1));
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public DoubleChromosome(
		final Double min,
		final Double max,
		final IntRange lengthRange
	) {
		this(DoubleGene.seq(min, max, random.nextInt(lengthRange, getRandom())));
		_valid = true;
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public DoubleChromosome(final Double min,final Double max,final int length) {
		this(DoubleGene.seq(min, max, length));
		_valid = true;
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public DoubleChromosome(final Double min, final Double max) {
		this(min, max, 1);
	}

	/**
	 * Returns an double array containing all of the elements in this chromosome
	 * in proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of this chromosomes are to
	 *        be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public double[] toArray(final double[] array) {
		final double[] a = array.length >= length() ?
			array : new double[length()];

		for (int i = length(); --i >= 0;) {
			a[i] = doubleValue(i);
		}

		return a;
	}

	/**
	 * Returns an double array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public double[] toArray() {
		return toArray(new double[length()]);
	}

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static DoubleChromosome of(final DoubleGene... genes) {
		return new DoubleChromosome(ISeq.of(genes));
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static DoubleChromosome of(
		final double min,
		double max,
		final int length
	) {
		return new DoubleChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code DoubleChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static DoubleChromosome of(final DoubleRange range, final int length) {
		return new DoubleChromosome(range.getMin(), range.getMax(), length);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new {@code DoubleChromosome} with the given parameter
	 */
	public static DoubleChromosome of(final double min, final double max) {
		return new DoubleChromosome(min, max);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the double range of the chromosome.
	 * @return a new random {@code DoubleChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 */
	public static DoubleChromosome of(final DoubleRange range) {
		return new DoubleChromosome(range.getMin(), range.getMax());
	}

	@Override
	public DoubleChromosome newInstance(final ISeq<DoubleGene> genes) {
		return new DoubleChromosome(genes);
	}

	@Override
	public DoubleChromosome newInstance() {
		return new DoubleChromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeDouble(_min);
		out.writeDouble(_max);

		for (DoubleGene gene : _genes) {
			out.writeDouble(gene.getAllele());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final MSeq<DoubleGene> genes = MSeq.ofLength(in.readInt());
		reflect.setField(this, "_min", in.readDouble());
		reflect.setField(this, "_max", in.readDouble());

		for (int i = 0; i < genes.length(); ++i) {
			genes.set(i, new DoubleGene(in.readDouble(), _min, _max));
		}

		reflect.setField(this, "_genes", genes.toISeq());
	}

}
