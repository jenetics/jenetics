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
import static io.jenetics.internal.math.random.nextDouble;
import static io.jenetics.util.RandomRegistry.getRandom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;

import io.jenetics.internal.collection.LazyISeq;
import io.jenetics.internal.math.random;
import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
import io.jenetics.internal.util.reflect;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 64 bit floating point numbers.
 *
 * @see DoubleGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version !__version__!
 */
public class DoubleChromosome
	//extends AbstractBoundedChromosome<Double, DoubleGene>
	implements
		NumericChromosome<Double, DoubleGene>,
		Serializable
{
	private static final long serialVersionUID = 3L;


	private double[] _values;
	private DoubleRange _valueRange;
	private IntRange _lengthRange;

//	/**
//	 * Create a new chromosome from the given {@code genes} and the allowed
//	 * length range of the chromosome.
//	 *
//	 * @since 4.0
//	 *
//	 * @param genes the genes that form the chromosome.
//	 * @param lengthRange the allowed length range of the chromosome
//	 * @throws NullPointerException if one of the arguments is {@code null}.
//	 * @throws IllegalArgumentException if the length of the gene sequence is
//	 *         empty, doesn't match with the allowed length range, the minimum
//	 *         or maximum of the range is smaller or equal zero or the given
//	 *         range size is zero.
//	 */
//	protected DoubleChromosome(
//		final ISeq<DoubleGene> genes,
//		final IntRange lengthRange
//	) {
//		super(genes, lengthRange);
//	}

	protected DoubleChromosome(
		final double[] values,
		final DoubleRange valueRange,
		final IntRange lengthRange
	) {
		_values = requireNonNull(values);
		_valueRange = requireNonNull(valueRange);
		_lengthRange = VariableChromosome.checkLengthRange(lengthRange, values.length);
	}

	public DoubleChromosome(
		final DoubleRange valueRange,
		final IntRange lengthRange
	) {
		_valueRange = requireNonNull(valueRange);
		_lengthRange = VariableChromosome.checkLengthRange(lengthRange, values.length);
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome. The start
	 *        of the range is inclusively and the range end exclusively
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public DoubleChromosome(
		final Double min,
		final Double max,
		final IntRange lengthRange
	) {
		//this(DoubleGene.seq(min, max, lengthRange), lengthRange);
		//_valid = true;
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
		this(min, max, IntRange.of(length));
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

	@Override
	public Double getMin() {
		return _valueRange.getMin();
	}

	@Override
	public Double getMax() {
		return _valueRange.getMax();
	}

	@Override
	public DoubleGene getGene(final int index) {
		return geneAt(index);
	}

	public DoubleGene geneAt(final int index) {
		return DoubleGene.of(doubleValue(index), _valueRange);
	}

	public DoubleGene gene() {
		return geneAt(0);
	}

	public double alleleAt(final int index) {
		return _values[index];
	}

	public double allele() {
		return _values[0];
	}

	@Override
	public int length() {
		return _values.length;
	}

	@Override
	public ISeq<DoubleGene> toSeq() {
		return LazyISeq.of(this::geneAt, length());
	}

	private byte _valid = 0;

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Iterator<DoubleGene> iterator() {
		return toSeq().iterator();
	}

	@Override
	public byte byteValue(final int index) {
		return (byte)_values[index];
	}

	@Override
	public short shortValue(final int index) {
		return (short)_values[index];
	}

	@Override
	public int intValue(final int index) {
		return (int)_values[index];
	}

	@Override
	public long longValue(final int index) {
		return (long)_values[index];
	}

	@Override
	public float floatValue(final int index) {
		return (float)_values[index];
	}

	@Override
	public double doubleValue(final int index) {
		return _values[index];
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
		final double[] a;
		if (array.length >= length()) {
			System.arraycopy(_values, 0, array, 0, length());
			a = array;
		} else {
			a = _values.clone();
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
		return _values.clone();
	}


	/* *************************************************************************
	 *  Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static DoubleChromosome of(final DoubleGene... genes) {
		return null;//new DoubleChromosome(ISeq.of(genes), IntRange.of(genes.length));
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static DoubleChromosome of(
		final double min,
		final double max,
		final IntRange lengthRange
	) {
		return null;//new DoubleChromosome(min, max, lengthRange);
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
		final double max,
		final int length
	) {
		return null;//new DoubleChromosome(min, max, length);
	}

	/**
	 * Create a new random chromosome.
	 *
	 * @since 4.0
	 *
	 * @param range the integer range of the chromosome.
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code DoubleChromosome} with the given parameter
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 * @throws NullPointerException if the given {@code lengthRange} is
	 *         {@code null}
	 */
	public static DoubleChromosome of(
		final DoubleRange range,
		final IntRange lengthRange
	) {
		return null;//new DoubleChromosome(range.getMin(), range.getMax(), lengthRange);
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
		return null;//new DoubleChromosome(range.getMin(), range.getMax(), length);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @return a new {@code DoubleChromosome} with the given parameter
	 */
	public static DoubleChromosome of(final double min, final double max) {
		return null;//new DoubleChromosome(min, max);
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
		return null;//new DoubleChromosome(range.getMin(), range.getMax());
	}

	@Override
	public DoubleChromosome newInstance(final ISeq<DoubleGene> genes) {
		return null;// new DoubleChromosome(genes, lengthRange());
	}

	@Override
	public DoubleChromosome newInstance() {
		return null;//new DoubleChromosome(_min, _max, lengthRange());
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}



	private static double[] values(
		final DoubleRange valueRange,
		final IntRange lengthRange
	) {
		final Random r = getRandom();

		final double min = valueRange.getMin();
		final double max = valueRange.getMax();
		final int length = random.nextInt(lengthRange, r);

		final double[] values = new double[length];
		for (int i = 0; i < length; ++i) {
			values[i] = nextDouble(min, max, r);
		}
		return values;
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

//	private void writeObject(final ObjectOutputStream out)
//		throws IOException
//	{
//		out.defaultWriteObject();
//
//		out.writeInt(length());
//		out.writeObject(lengthRange());
//		out.writeDouble(_min);
//		out.writeDouble(_max);
//
//		for (DoubleGene gene : _genes) {
//			out.writeDouble(gene.getAllele());
//		}
//	}
//
//	private void readObject(final ObjectInputStream in)
//		throws IOException, ClassNotFoundException
//	{
//		in.defaultReadObject();
//
//		final MSeq<DoubleGene> genes = MSeq.ofLength(in.readInt());
//		reflect.setField(this, "_lengthRange", in.readObject());
//		reflect.setField(this, "_min", in.readDouble());
//		reflect.setField(this, "_max", in.readDouble());
//
//		for (int i = 0; i < genes.length(); ++i) {
//			genes.set(i, new DoubleGene(in.readDouble(), _min, _max));
//		}
//
//		reflect.setField(this, "_genes", genes.toISeq());
//	}
}
