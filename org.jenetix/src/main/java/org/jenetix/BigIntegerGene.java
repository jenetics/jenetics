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

import static java.util.Objects.requireNonNull;
import static org.jenetics.util.RandomRegistry.getRandom;
import static org.jenetix.internal.random.nextBigInteger;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.require;

import org.jenetics.NumericGene;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;

/**
 * Numeric chromosome implementation which holds an arbitrary sized integer
 * number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code IntegerGene} may have unpredictable results and should
 * be avoided.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.5
 * @version 3.5
 */
@XmlJavaTypeAdapter(BigIntegerGene.Model.Adapter.class)
public final class BigIntegerGene
	implements
		NumericGene<BigInteger, BigIntegerGene>,
		Mean<BigIntegerGene>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	private static final BigInteger TWO = BigInteger.valueOf(2);

	private final BigInteger _value;
	private final BigInteger _min;
	private final BigInteger _max;

	private BigIntegerGene(
		final BigInteger value,
		final BigInteger min,
		final BigInteger max
	) {
		_value = requireNonNull(value);
		_min = requireNonNull(min);
		_max = requireNonNull(max);
	}

	@Override
	public BigInteger getAllele() {
		return _value;
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
	public BigIntegerGene mean(final BigIntegerGene that) {
		final BigInteger value = _value.add(that._value).divide(TWO);
		return of(value, _min, _max);
	}

	@Override
	public BigIntegerGene newInstance(final Number number) {
		return of(BigInteger.valueOf(number.longValue()), _min, _max);
	}

	@Override
	public BigIntegerGene newInstance(final BigInteger value) {
		return of(value, _min, _max);
	}

	@Override
	public BigIntegerGene newInstance() {
		return of(_min, _max);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_value)
			.and(_min)
			.and(_max).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BigIntegerGene &&
			((BigIntegerGene)obj)._value.equals(_value) &&
			((BigIntegerGene)obj)._min.equals(_min) &&
			((BigIntegerGene)obj)._max.equals(_max);
	}

	@Override
	public String toString() {
		return String.format("[%s]", _value);
	}

	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	static ISeq<BigIntegerGene> seq(
		final BigInteger minimum,
		final BigInteger maximum,
		final int length
	) {
		require.positive(length);

		final Random r = getRandom();

		return MSeq.<BigIntegerGene>ofLength(length)
			.fill(() -> new BigIntegerGene(
				nextBigInteger(r, minimum, maximum), minimum, maximum))
			.toISeq();
	}

	/**
	 * Create a new random {@code BigIntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link BigIntegerGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new random {@code BigIntegerGene}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static BigIntegerGene of(
		final BigInteger value,
		final BigInteger min,
		final BigInteger max
	) {
		return new BigIntegerGene(value, min, max);
	}

	/**
	 * Create a new random {@code BigIntegerGene}. It is guaranteed that the
	 * value of the {@code BigIntegerGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return a new random {@code BigIntegerGene}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static BigIntegerGene of(final BigInteger min, final BigInteger max) {
		return of(
			nextBigInteger(RandomRegistry.getRandom(), min, max),
			min,
			max
		);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "big-integer-gene")
	@XmlType(name = "org.jenetix.BigIntegerGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "min", required = true)
		public BigInteger min;

		@XmlAttribute(name = "max", required = true)
		public BigInteger max;

		@XmlValue
		public BigInteger value;

		public final static class Adapter
			extends XmlAdapter<Model, BigIntegerGene>
		{
			@Override
			public Model marshal(final BigIntegerGene value) {
				final Model m = new Model();
				m.min = value.getMin();
				m.max = value.getMax();
				m.value = value.getAllele();
				return m;
			}

			@Override
			public BigIntegerGene unmarshal(final Model m) {
				return BigIntegerGene.of(m.value, m.min, m.max);
			}
		}
	}

}
