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

import static org.jenetics.util.math.random.nextLong;

import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;

/**
 * NumericGene implementation which holds a 64 bit integer number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-03-05 $</em>
 * @since 1.6
 */
@XmlJavaTypeAdapter(LongGene.Model.Adapter.class)
public final class LongGene
	extends AbstractNumericGene<Long, LongGene>
	implements
		NumericGene<Long, LongGene>,
		Mean<LongGene>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public LongGene(final Long value, final Long min, final Long max) {
		super(value, min, max);
	}

	/**
	 * Create a new random {@code LongGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link LongGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	public static LongGene of(final long value, final long min, final long max) {
		return new LongGene(value, min, max);
	}

	/**
	 * Create a new random {@code LongGene}. It is guaranteed that the value of
	 * the {@code LongGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	public static LongGene of(final long min, final long max) {
		return of(nextLong(RandomRegistry.getRandom(), min, max), min, max);
	}

	static ISeq<LongGene> seq(
		final Long minimum,
		final Long maximum,
		final int length
	) {
		final long min = minimum;
		final long max = maximum;
		final Random r = RandomRegistry.getRandom();

		final Array<LongGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			genes.set(i, new LongGene(nextLong(r, min, max), minimum, maximum));
		}
		return genes.toISeq();
	}

	@Override
	public LongGene newInstance(final Number number) {
		return new LongGene(number.longValue(), _min, _max);
	}

	@Override
	public LongGene newInstance() {
		return new LongGene(
			nextLong(RandomRegistry.getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public LongGene mean(final LongGene that) {
		return new LongGene(_value + (that._value - _value) / 2, _min, _max);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.LongGene")
	@XmlType(name = "org.jenetics.LongGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public long min;

		@XmlAttribute
		public long max;

		@XmlValue
		public long value;

		@ValueType(LongGene.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, LongGene>
		{
			@Override
			public Model marshal(final LongGene value) {
				final Model m = new Model();
				m.min = value.getMin();
				m.max = value.getMax();
				m.value = value.getAllele();
				return m;
			}

			@Override
			public LongGene unmarshal(final Model m) {
				return LongGene.of(m.value, m.min, m.max);
			}
		}
	}

}
