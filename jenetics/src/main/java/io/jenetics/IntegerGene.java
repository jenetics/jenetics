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

import static java.lang.String.format;
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.util.RandomRegistry.random;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

import io.jenetics.internal.math.Randoms;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 32 bit integer number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code IntegerGene} may have unpredictable results and should
 * be avoided.
 *
 * @see IntegerChromosome
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 2.0
 * @version 6.1
 */
public final class IntegerGene
	implements
		NumericGene<Integer, IntegerGene>,
		Mean<IntegerGene>,
		Comparable<IntegerGene>,
		Serializable
{

	private static final long serialVersionUID = 2L;

	private final int _allele;
	private final int _min;
	private final int _max;

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	private IntegerGene(final int allele, final int min, final int max) {
		_allele = allele;
		_min = min;
		_max = max;
	}

	@Override
	public Integer allele() {
		return _allele;
	}

	@Override
	public Integer min() {
		return _min;
	}

	@Override
	public Integer max() {
		return _max;
	}

	/**
	 * Return the range of {@code this} gene.
	 *
	 * @since 4.4
	 *
	 * @return the range of {@code this} gene
	 */
	public IntRange range() {
		return IntRange.of(_min, _max);
	}

	@Override
	public byte byteValue() {
		return (byte) _allele;
	}

	@Override
	public short shortValue() {
		return (short) _allele;
	}

	@Override
	public int intValue() {
		return _allele;
	}

	@Override
	public long longValue() {
		return _allele;
	}

	@Override
	public float floatValue() {
		return (float) _allele;
	}

	@Override
	public double doubleValue() {
		return _allele;
	}

	@Override
	public boolean isValid() {
		return _allele >= _min && _allele <= _max;
	}

	@Override
	public int compareTo(final IntegerGene other) {
		return Integer.compare(_allele, other._allele);
	}

	@Override
	public IntegerGene mean(final IntegerGene that) {
		return IntegerGene.of(_allele + (that._allele - _allele)/2, _min, _max);
	}

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 5.0
	 * @param allele the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public IntegerGene newInstance(final int allele) {
		return IntegerGene.of(allele, _min, _max);
	}

	@Override
	public IntegerGene newInstance(final Integer allele) {
		return IntegerGene.of(allele, _min, _max);
	}

	@Override
	public IntegerGene newInstance(final Number allele) {
		final int value = allele instanceof Double || allele instanceof Float
			? (int)Math.round(allele.doubleValue())
			: allele.intValue();

		return IntegerGene.of(value, _min, _max);
	}

	@Override
	public IntegerGene newInstance() {
		return IntegerGene.of(nextInt(random(), _min, _max), _min, _max);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_min, hash(_max)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IntegerGene &&
			((IntegerGene)obj)._allele == _allele &&
			((IntegerGene)obj)._min == _min &&
			((IntegerGene)obj)._max == _max;
	}

	@Override
	public String toString() {
		return String.format("[%s]", _allele);
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code IntegerGene} with the given {@code value}
	 */
	public static IntegerGene of(final int allele, final int min, final int max) {
		return new IntegerGene(allele, min, max);
	}

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param allele the value of the gene.
	 * @param range the integer range to use
	 * @return a new {@code IntegerGene} with the give {@code value}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static IntegerGene of(final int allele, final IntRange range) {
		return IntegerGene.of(allele, range.min(), range.max());
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new random {@code IntegerGene}
	 */
	public static IntegerGene of(final int min, final int max) {
		return of(nextInt(random(), min, max), min, max);
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max].
	 *
	 * @since 3.2
	 *
	 * @param range the integer range to use
	 * @return a new random {@code IntegerGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static IntegerGene of(final IntRange range) {
		return of(nextInt(random(), range.min(), range.max()), range);
	}

	static ISeq<IntegerGene> seq(
		final int min,
		final int max,
		final IntRange lengthRange
	) {
		final Random r = random();
		return MSeq.<IntegerGene>ofLength(Randoms.nextInt(lengthRange, r))
			.fill(() -> new IntegerGene(nextInt(r, min, max), min, max))
			.toISeq();
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between min and
	 * max (min and max included).
	 *
	 * @param random the random engine to use for calculating the random int
	 *        value
	 * @param min lower bound for generated integer
	 * @param max upper bound for generated integer
	 * @return a random integer greater than or equal to {@code min} and
	 *         less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min > max}
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	static int nextInt(
		final Random random,
		final int min, final int max
	) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"Min >= max: %d >= %d", min, max
			));
		}

		final int diff = max - min + 1;
		int result = 0;

		if (diff <= 0) {
			do {
				result = random.nextInt();
			} while (result < min || result > max);
		} else {
			result = random.nextInt(diff) + min;
		}

		return result;
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.INTEGER_GENE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(_allele, out);
		writeInt(_min, out);
		writeInt(_max, out);
	}

	static IntegerGene read(final DataInput in) throws IOException {
		return of(readInt(in), readInt(in), readInt(in));
	}

}
