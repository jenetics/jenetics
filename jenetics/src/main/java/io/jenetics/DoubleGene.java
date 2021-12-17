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
import static io.jenetics.util.RandomRegistry.random;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.Mean;

/**
 * Implementation of the NumericGene which holds a 64 bit floating point number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code DoubleGene} may have unpredictable results and should
 * be avoided.
 *
 * @see DoubleChromosome
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 6.0
 */
public final class DoubleGene
	implements
		NumericGene<Double, DoubleGene>,
		Mean<DoubleGene>,
		Comparable<DoubleGene>,
		Serializable
{

	@Serial
	private static final long serialVersionUID = 2L;

	private final double _allele;
	private final double _min;
	private final double _max;

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 */
	private DoubleGene(final double allele, final double min, final double max) {
		_allele = allele;
		_min = min;
		_max = max;
	}

	@Override
	public Double allele() {
		return _allele;
	}

	@Override
	public Double min() {
		return _min;
	}

	@Override
	public Double max() {
		return _max;
	}

	/**
	 * Return the range of {@code this} gene.
	 *
	 * @since 4.4
	 *
	 * @return the range of {@code this} gene
	 */
	public DoubleRange range() {
		return DoubleRange.of(_min, _max);
	}

	@Override
	public byte byteValue() {
		return (byte)_allele;
	}

	@Override
	public short shortValue() {
		return (short)_allele;
	}

	@Override
	public int intValue() {
		return (int)_allele;
	}

	@Override
	public long longValue() {
		return (long)_allele;
	}

	@Override
	public float floatValue() {
		return (float)_allele;
	}

	@Override
	public double doubleValue() {
		 return _allele;
	}

	@Override
	public boolean isValid() {
		return
			Double.isFinite(_allele) &&
			Double.isFinite(_min) &&
			Double.isFinite(_max) &&
			Double.compare(_allele, _min) >= 0 &&
			Double.compare(_allele, _max) < 0;
	}

	@Override
	public int compareTo(final DoubleGene other) {
		return Double.compare(_allele, other._allele);
	}

	@Override
	public DoubleGene mean(final DoubleGene that) {
		// (a - a/2) + b/2
		// https://hal.archives-ouvertes.fr/hal-00576641v1/document
		return of((_allele - _allele/2.0) + that._allele/2.0, _min, _max);
	}

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 5.0
	 * @param allele the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public DoubleGene newInstance(final double allele) {
		return DoubleGene.of(allele, _min, _max);
	}

	@Override
	public DoubleGene newInstance(final Double allele) {
		return of(allele, _min, _max);
	}

	@Override
	public DoubleGene newInstance(final Number allele) {
		return of(allele.doubleValue(), _min, _max);
	}

	@Override
	public DoubleGene newInstance() {
		return of(random().nextDouble(_min, _max), _min, _max);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_min, hash(_max)));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof DoubleGene other &&
			Double.compare(other._allele, _allele) == 0 &&
			Double.compare(other._min, _min) == 0 &&
			Double.compare(other._max, _max) == 0;
	}

	@Override
	public String toString() {
		return String.format("[%s]", _allele);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param allele the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code DoubleGene} with the given parameter
	 */
	public static DoubleGene of(
		final double allele,
		final double min,
		final double max
	) {
		return new DoubleGene(allele, min, max);
	}

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param allele the value of the gene.
	 * @param range the double range to use
	 * @return a new random {@code DoubleGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static DoubleGene of(final double allele, final DoubleRange range) {
		return of(allele, range.min(), range.max());
	}

	/**
	 * Create a new random {@code DoubleGene}. It is guaranteed that the value
	 * of the {@code DoubleGene} lies in the interval [min, max).
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code DoubleGene} with the given parameter
	 * @throws IllegalArgumentException if {@code min} is not finite,
	 *         or {@code max} is not finite, or {@code min}
	 *         is greater than or equal to {@code max}
	 */
	public static DoubleGene of(final double min, final double max) {
		return of(random().nextDouble(min, max), min, max);
	}

	/**
	 * Create a new random {@code DoubleGene}. It is guaranteed that the value
	 * of the {@code DoubleGene} lies in the interval [min, max).
	 *
	 * @since 3.2
	 *
	 * @param range the double range to use
	 * @return a new {@code DoubleGene} with the given parameter
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 * @throws IllegalArgumentException if {@code min} is not finite,
	 *         or {@code max} is not finite, or {@code min}
	 *         is greater than or equal to {@code max}
	 */
	public static DoubleGene of(final DoubleRange range) {
		return of(range.min(), range.max());
	}

	static ISeq<DoubleGene> seq(
		final double min,
		final double max,
		final IntRange lengthRange
	) {
		final var random = random();
		final var length = random.nextInt(lengthRange.min(), lengthRange.max());

		return MSeq.<DoubleGene>ofLength(length)
			.fill(() -> new DoubleGene(random.nextDouble(min, max), min, max))
			.toISeq();
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.DOUBLE_GENE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(_allele);
		out.writeDouble(_min);
		out.writeDouble(_max);
	}

	static DoubleGene read(final DataInput in) throws IOException {
		return of(in.readDouble(), in.readDouble(), in.readDouble());
	}

}
