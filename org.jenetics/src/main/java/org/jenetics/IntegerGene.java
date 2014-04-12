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

import static org.jenetics.util.math.random.nextInt;

import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;

/**
 * NumericGene implementation which holds a 32 bit integer number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-04-10 $</em>
 * @since 2.0
 */
@XmlJavaTypeAdapter(IntegerGene.Model.Adapter.class)
public final class IntegerGene
	extends AbstractNumericGene<Integer, IntegerGene>
	implements
			NumericGene<Integer, IntegerGene>,
			Mean<IntegerGene>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public IntegerGene(final Integer value, final Integer min, final Integer max) {
		super(value, min, max);
	}

	/**
	 * Create a new random {@code IntegerGene} with the given value and the
	 * given range. If the {@code value} isn't within the interval [min, max],
	 * no exception is thrown. In this case the method
	 * {@link IntegerGene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	public static IntegerGene of(final int value, final int min, final int max) {
		return new IntegerGene(value, min, max);
	}

	/**
	 * Create a new random {@code IntegerGene}. It is guaranteed that the value of
	 * the {@code IntegerGene} lies in the interval [min, max].
	 *
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 */
	public static IntegerGene of(final int min, final int max) {
		return of(nextInt(RandomRegistry.getRandom(), min, max), min, max);
	}

	static ISeq<IntegerGene> seq(
		final Integer minimum,
		final Integer maximum,
		final int length
	) {
		final int min = minimum;
		final int max = maximum;
		final Random r = RandomRegistry.getRandom();

		final Array<IntegerGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			genes.set(i, new IntegerGene(nextInt(r, min, max), minimum, maximum));
		}
		return genes.toISeq();
	}

	@Override
	public IntegerGene newInstance(final Number number) {
		return new IntegerGene(number.intValue(), _min, _max);
	}

	@Override
	public IntegerGene newInstance() {
		return new IntegerGene(
			nextInt(RandomRegistry.getRandom(), _min, _max), _min, _max
		);
	}

	@Override
	public IntegerGene mean(final IntegerGene that) {
		return new IntegerGene(_value + (that._value - _value) / 2, _min, _max);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "int-gene")
	@XmlType(name = "org.jenetics.IntegerGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "min", required = true)
		public int min;

		@XmlAttribute(name = "max", required = true)
		public int max;

		@XmlValue
		public int value;

		public final static class Adapter
			extends XmlAdapter<Model, IntegerGene>
		{
			@Override
			public Model marshal(final IntegerGene value) {
				final Model m = new Model();
				m.min = value.getMin();
				m.max = value.getMax();
				m.value = value.getAllele();
				return m;
			}

			@Override
			public IntegerGene unmarshal(final Model m) {
				return IntegerGene.of(m.value, m.min, m.max);
			}
		}
	}

}
