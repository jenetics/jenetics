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

import static io.jenetics.internal.math.Randoms.nextDouble;
import static io.jenetics.util.RandomRegistry.random;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

import io.jenetics.internal.math.Randoms;
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
 * @version !__version__!
 */
public final record DoubleGene(Double allele, Double min, Double max)
	implements
		NumericGene<Double, DoubleGene>,
		Mean<DoubleGene>,
		Comparable<DoubleGene>,
		Serializable
{

	private static final long serialVersionUID = 2L;

	/**
	 * Return the range of {@code this} gene.
	 *
	 * @since 4.4
	 *
	 * @return the range of {@code this} gene
	 */
	public DoubleRange range() {
		return DoubleRange.of(min, max);
	}

	@Override
	public byte byteValue() {
		return allele.byteValue();
	}

	@Override
	public short shortValue() {
		return allele.shortValue();
	}

	@Override
	public int intValue() {
		return allele.intValue();
	}

	@Override
	public long longValue() {
		return allele.longValue();
	}

	@Override
	public float floatValue() {
		return allele.floatValue();
	}

	@Override
	public double doubleValue() {
		 return allele;
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
		return Double.compare(allele, other.allele);
	}

	@Override
	public DoubleGene mean(final DoubleGene that) {
		return of(allele + (that.allele - allele)/2.0, min, max);
	}

	/**
	 * Create a new gene from the given {@code value} and the gene context.
	 *
	 * @since 5.0
	 * @param allele the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public DoubleGene newInstance(final double allele) {
		return DoubleGene.of(allele, min, max);
	}

	@Override
	public DoubleGene newInstance(final Double allele) {
		return of(allele, min, max);
	}

	@Override
	public DoubleGene newInstance(final Number allele) {
		return of(allele.doubleValue(), min, max);
	}

	@Override
	public DoubleGene newInstance() {
		return of(nextDouble(min, max, random()), min, max);
	}

	@Override
	public String toString() {
		return String.format("[%s]", allele);
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
	 */
	public static DoubleGene of(final double min, final double max) {
		return of(nextDouble(min, max, random()), min, max);
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
	 */
	public static DoubleGene of(final DoubleRange range) {
		return of(nextDouble(range.min(), range.max(), random()), range);
	}

	static ISeq<DoubleGene> seq(
		final double min,
		final double max,
		final IntRange lengthRange
	) {
		final Random r = random();
		return MSeq.<DoubleGene>ofLength(Randoms.nextInt(lengthRange, r))
			.fill(() -> new DoubleGene(nextDouble(min, max, r), min, max))
			.toISeq();
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.DOUBLE_GENE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(allele);
		out.writeDouble(min);
		out.writeDouble(max);
	}

	static DoubleGene read(final DataInput in) throws IOException {
		return of(in.readDouble(), in.readDouble(), in.readDouble());
	}

}
