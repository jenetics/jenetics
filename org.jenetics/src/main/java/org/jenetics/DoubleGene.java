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
package org.jenetics;

import static org.jenetics.internal.math.random.nextDouble;
import static org.jenetics.util.RandomRegistry.getRandom;

import java.io.Serializable;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.require;

import org.jenetics.util.DoubleRange;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Mean;

/**
 * Implementation of the NumericGene which holds a 64 bit floating point number.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code DoubleGene} may have unpredictable results and should
 * be avoided.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.2
 */
@XmlJavaTypeAdapter(DoubleGene.Model.Adapter.class)
public final class DoubleGene
	extends AbstractNumericGene<Double, DoubleGene>
	implements
		NumericGene<Double, DoubleGene>,
		Mean<DoubleGene>,
		Comparable<DoubleGene>,
		Serializable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	DoubleGene(final Double value, final Double min, final Double max) {
		super(value, min, max);
	}

	@Override
	public int compareTo(final DoubleGene other) {
		return _value.compareTo(other._value);
	}

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (exclusively).
	 * @return a new {@code DoubleGene} with the given parameter
	 */
	public static DoubleGene of(
		final double value,
		final double min,
		final double max
	) {
		return new DoubleGene(value, min, max);
	}

	/**
	 * Create a new random {@code DoubleGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max),
	 * no exception is thrown. In this case the method
	 * {@link DoubleGene#isValid()} returns {@code false}.
	 *
	 * @since 3.2
	 *
	 * @param value the value of the gene.
	 * @param range the double range to use
	 * @return a new random {@code DoubleGene}
	 * @throws NullPointerException if the given {@code range} is {@code null}.
	 */
	public static DoubleGene of(final double value, final DoubleRange range) {
		return new DoubleGene(value, range.getMin(), range.getMax());
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
		return of(nextDouble(getRandom(), min, max), min, max);
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
		return of(nextDouble(getRandom(), range.getMin(), range.getMax()), range);
	}

	static ISeq<DoubleGene> seq(
		final Double minimum,
		final Double maximum,
		final int length
	) {
		require.positive(length);

		final double min = minimum;
		final double max = maximum;
		final Random r = getRandom();

		return MSeq.<DoubleGene>ofLength(length)
			.fill(() -> new DoubleGene(nextDouble(r, min, max), minimum, maximum))
			.toISeq();
	}

	@Override
	public DoubleGene newInstance(final Number number) {
		return new DoubleGene(number.doubleValue(), _min, _max);
	}

	@Override
	public DoubleGene newInstance() {
		return new DoubleGene(
			nextDouble(getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public DoubleGene mean(final DoubleGene that) {
		return new DoubleGene(_value + (that._value - _value)/2.0, _min, _max);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "double-gene")
	@XmlType(name = "org.jenetics.DoubleGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "min", required = true)
		public double min;

		@XmlAttribute(name = "max", required = true)
		public double max;

		@XmlValue
		public double value;

		public final static class Adapter
			extends XmlAdapter<Model, DoubleGene>
		{
			@Override
			public Model marshal(final DoubleGene value) {
				final Model m = new Model();
				m.min = value.getMin();
				m.max = value.getMax();
				m.value = value.getAllele();
				return m;
			}

			@Override
			public DoubleGene unmarshal(final Model m) {
				return DoubleGene.of(m.value, m.min, m.max);
			}
		}
	}

}
