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

import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeLong;
import static io.jenetics.util.RandomRegistry.random;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.LongRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 64-bit integer number.
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

	@Serial
	private static final long serialVersionUID = 2L;

	private final long _allele;
	private final long _min;
	private final long _max;

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
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
		return _allele >= _min && _allele < _max;
	}

	@Override
	public int compareTo(final LongGene other) {
		return Long.compare(_allele, other._allele);
	}

	@Override
	public LongGene mean(final LongGene that) {
		final long x = that._allele;
		final long y = _allele;

		// http://aggregate.org/MAGIC/#Average%20of%20Integers
		return LongGene.of((x&y) + ((x^y) >> 1), _min, _max);
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
		return LongGene.of(random().nextLong(_min, _max), _min, _max);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_min, hash(_max)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof LongGene other &&
			other._allele == _allele &&
			other._min == _min &&
			other._max == _max;
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
	 * @param max the maximal valid value of this gene (exclusively).
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
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code LongGene} with the given parameters.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongGene of(final long min, final long max) {
		return of(random().nextLong(min, max), min, max);
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @since 3.2
	 *
	 * @param range the long range to use
	 * @return a new random {@code LongGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static LongGene of(final LongRange range) {
		return of(random().nextLong(range.min(), range.max()), range);
	}

	static ISeq<LongGene> seq(
		final long min,
		final long max,
		final IntRange lengthRange
	) {
		final var random = random();
		final var length = random.nextInt(lengthRange.min(), lengthRange.max());

		return MSeq.<LongGene>ofLength(length)
			.fill(() -> LongGene.of(random.nextLong(min, max), min, max))
			.toISeq();
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.LONG_GENE, this);
	}

	@Serial
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
