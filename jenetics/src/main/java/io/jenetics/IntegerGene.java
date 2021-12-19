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
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;
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
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * NumericGene implementation which holds a 32-bit integer number.
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
 * @version 7.0
 */
public final class IntegerGene
	implements
		NumericGene<Integer, IntegerGene>,
		Mean<IntegerGene>,
		Comparable<IntegerGene>,
		Serializable
{

	@Serial
	private static final long serialVersionUID = 2L;

	private final int _allele;
	private final int _min;
	private final int _max;

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
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
		return _allele >= _min && _allele < _max;
	}

	@Override
	public int compareTo(final IntegerGene other) {
		return Integer.compare(_allele, other._allele);
	}

	@Override
	public IntegerGene mean(final IntegerGene that) {
		final int x = that._allele;
		final int y = _allele;

		// http://aggregate.org/MAGIC/#Average%20of%20Integers
		return IntegerGene.of((x&y) + ((x^y) >> 1), _min, _max);
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
		return IntegerGene.of(random().nextInt(_min, _max), _min, _max);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_min, hash(_max)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IntegerGene other &&
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
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
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
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new random {@code IntegerGene}
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerGene of(final int min, final int max) {
		return of(random().nextInt(min, max), min, max);
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max).
	 *
	 * @since 3.2
	 *
	 * @param range the integer range to use
	 * @return a new random {@code IntegerGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 * @throws IllegalArgumentException if {@code max} is greater than
	 *         or equal to {@code min}
	 */
	public static IntegerGene of(final IntRange range) {
		return of(random().nextInt(range.min(), range.max()), range);
	}

	static ISeq<IntegerGene> seq(
		final int min,
		final int max,
		final IntRange lengthRange
	) {
		final var random = random();
		final var length = random.nextInt(lengthRange.min(), lengthRange.max());

		return MSeq.<IntegerGene>ofLength(length)
			.fill(() -> new IntegerGene(random.nextInt(min, max), min, max))
			.toISeq();
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.INTEGER_GENE, this);
	}

	@Serial
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
