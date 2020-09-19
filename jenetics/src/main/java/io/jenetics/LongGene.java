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
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeLong;
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
import io.jenetics.util.LongRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 64 bit integer number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code LongGene} may have unpredictable results and should
 * be avoided.
 *
 * @see LongChromosome
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 6.1
 */
public final class LongGene
	implements
		NumericGene<Long, LongGene>,
		Mean<LongGene>,
		Comparable<LongGene>,
		Serializable
{

	private static final long serialVersionUID = 2L;

	private final long _allele;
	private final long _min;
	private final long _max;

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	private LongGene(final long allele, final long min, final long max) {
		_allele = allele;
		_min = min;
		_max = max;
	}

	@Override
	public Long allele() {
		return _allele;
	}

	@Override
	public Long min() {
		return _min;
	}

	@Override
	public Long max() {
		return _max;
	}

	/**
	 * Return the range of {@code this} gene.
	 *
	 * @since 4.4
	 *
	 * @return the range of {@code this} gene
	 */
	public LongRange range() {
		return LongRange.of(_min, _max);
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
		return (int) _allele;
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
	public int compareTo(final LongGene other) {
		return Long.compare(_allele, other._allele);
	}

	@Override
	public LongGene mean(final LongGene that) {
		return LongGene.of(_allele + (that._allele - _allele)/2, _min, _max);
	}

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 5.0
	 * @param allele the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public LongGene newInstance(final long allele) {
		return LongGene.of(allele, _min, _max);
	}

	@Override
	public LongGene newInstance(final Long allele) {
		return LongGene.of(allele, _min, _max);
	}

	@Override
	public LongGene newInstance(final Number allele) {
		final long value = allele instanceof Double || allele instanceof Float
			? Math.round(allele.doubleValue())
			: allele.longValue();

		return LongGene.of(value, _min, _max);
	}

	@Override
	public LongGene newInstance() {
		return LongGene.of(nextLong(random(), _min, _max), _min, _max);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_min, hash(_max, hash(getClass()))));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof LongGene &&
			((LongGene)obj)._allele == _allele &&
			((LongGene)obj)._min == _min &&
			((LongGene)obj)._max == _max;
	}

	@Override
	public String toString() {
		return String.format("[%s]", _allele);
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code LongGene} with the given parameters.
	 */
	public static LongGene of(final long allele, final long min, final long max) {
		return new LongGene(allele, min, max);
	}

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param allele the value of the gene.
	 * @param range the long range to use
	 * @return a new random {@code LongGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static LongGene of(final long allele, final LongRange range) {
		return LongGene.of(allele, range.min(), range.max());
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new {@code LongGene} with the given parameters.
	 */
	public static LongGene of(final long min, final long max) {
		return of(nextLong(random(), min, max), min, max);
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @since 3.2
	 *
	 * @param range the long range to use
	 * @return a new random {@code LongGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static LongGene of(final LongRange range) {
		return of(nextLong(random(), range.min(), range.max()), range);
	}

	static ISeq<LongGene> seq(
		final long min,
		final long max,
		final IntRange lengthRange
	) {
		final Random r = random();
		return MSeq.<LongGene>ofLength(Randoms.nextInt(lengthRange, r))
			.fill(() -> LongGene.of(nextLong(r, min, max), min, max))
			.toISeq();
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between min
	 * and max (min and max included).
	 *
	 * @param random the random engine to use for calculating the random
	 *        long value
	 * @param min lower bound for generated long integer
	 * @param max upper bound for generated long integer
	 * @return a random long integer greater than or equal to {@code min}
	 *         and less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code min > max}
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	static long nextLong(
		final Random random,
		final long min, final long max
	) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"min >= max: %d >= %d.", min, max
			));
		}

		final long diff = (max - min) + 1;
		long result = 0;

		if (diff <= 0) {
			do {
				result = random.nextLong();
			} while (result < min || result > max);
		} else if (diff < Integer.MAX_VALUE) {
			result = random.nextInt((int)diff) + min;
		} else {
			result = nextLong(random, diff) + min;
		}

		return result;
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between 0
	 * (inclusive) and the specified value (exclusive), drawn from the given
	 * random number generator's sequence.
	 *
	 * @param random the random engine used for creating the random number.
	 * @param n the bound on the random number to be returned. Must be
	 *        positive.
	 * @return the next pseudo-random, uniformly distributed int value
	 *         between 0 (inclusive) and n (exclusive) from the given random
	 *         number generator's sequence
	 * @throws IllegalArgumentException if n is smaller than 1.
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	static long nextLong(final Random random, final long n) {
		if (n <= 0) {
			throw new IllegalArgumentException(format(
				"n is smaller than one: %d", n
			));
		}

		long bits;
		long result;
		do {
			bits = random.nextLong() & 0x7fffffffffffffffL;
			result = bits%n;
		} while (bits - result + (n - 1) < 0);

		return result;
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.LONG_GENE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeLong(_allele, out);
		writeLong(_min, out);
		writeLong(_max, out);
	}

	static LongGene read(final DataInput in) throws IOException {
		return of(readLong(in), readLong(in), readLong(in));
	}

}
