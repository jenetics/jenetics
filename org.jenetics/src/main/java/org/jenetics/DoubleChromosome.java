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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Array;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;

/**
 * Numeric chromosome implementation which holds 64 bit floating point numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-03-05 $</em>
 * @since 1.6
 */
@XmlJavaTypeAdapter(DoubleChromosome.Model.Adapter.class)
public class DoubleChromosome
	extends AbstractNumericChromosome<Double, DoubleGene>
	implements
		NumericChromosome<Double, DoubleGene>,
		Serializable
{
	private static final long serialVersionUID = 1L;


	protected DoubleChromosome(final ISeq<DoubleGene> genes) {
		super(genes);
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public DoubleChromosome(final Double min,final Double max,final int length) {
		this(DoubleGene.seq(min, max, length));
		_valid = true;
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public DoubleChromosome(final Double min, final Double max) {
		this(min, max, 1);
	}

	/**
	 * Create a new {@code DoubleChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static DoubleChromosome of(final DoubleGene... genes) {
		return new DoubleChromosome(Array.of(genes).toISeq());
	}

	/**
	 * Create a new random {@code DoubleChromosome}.
	 *
	 * @param min the min value of the {@link DoubleGene}s (inclusively).
	 * @param max the max value of the {@link DoubleGene}s (exclusively).
	 * @param length the length of the chromosome.
	 */
	public static DoubleChromosome of(final double min, double max, final int length) {
		return new DoubleChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code DoubleChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 */
	public static DoubleChromosome of(final double min, final double max) {
		return new DoubleChromosome(min, max);
	}

	@Override
	public DoubleChromosome newInstance(final ISeq<DoubleGene> genes) {
		return new DoubleChromosome(genes);
	}

	@Override
	public DoubleChromosome newInstance() {
		return new DoubleChromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object o) {
		return o == this || o instanceof DoubleChromosome && super.equals(o);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeDouble(_min.doubleValue());
		out.writeDouble(_max.doubleValue());

		for (DoubleGene gene : _genes) {
			out.writeDouble(gene.getAllele().doubleValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final Array<DoubleGene> genes = new Array<>(in.readInt());
		_min = in.readDouble();
		_max = in.readDouble();

		for (int i = 0; i < genes.length(); ++i) {
			genes.set(i, new DoubleGene(in.readDouble(), _min, _max));
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.DoubleChromosome")
	@XmlType(name = "org.jenetics.DoubleChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public int length;

		@XmlAttribute
		public double min;

		@XmlAttribute
		public double max;

		@XmlElement(name = "allele")
		public List<Double> values;

		@ValueType(DoubleChromosome.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, DoubleChromosome>
		{
			@Override
			public Model marshal(final DoubleChromosome c) {
				final Model m = new Model();
				m.length = c.length();
				m.min = c._min;
				m.max = c._max;
				m.values = c.toSeq().map(Allele).asList();
				return m;
			}

			@Override
			public DoubleChromosome unmarshal(final Model model) {
				final Double min = model.min;
				final Double max = model.max;
				return new DoubleChromosome(
					Array.of(model.values).map(Gene(min, max)).toISeq()
				);
			}
		}
	}

	private static final Function<DoubleGene, Double> Allele =
		new Function<DoubleGene, Double>() {
			@Override
			public Double apply(final DoubleGene value) {
				return value.getAllele();
			}
		};

	private static Function<Double, DoubleGene> Gene(final Double min, final Double max) {
		return new Function<Double, DoubleGene>() {
			@Override
			public DoubleGene apply(final Double value) {
				return new DoubleGene(value, min, max);
			}
		};
	}
}
