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

import org.jenetics.util.Array;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;

/**
 * Numeric chromosome implementation which holds 32 bit integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 * @version 2.0 &mdash; <em>$Date: 2014-04-10 $</em>
 * @since 2.0
 */
@XmlJavaTypeAdapter(IntegerChromosome.Model.Adapter.class)
public class IntegerChromosome
	extends AbstractNumericChromosome<Integer, IntegerGene>
	implements
			NumericChromosome<Integer, IntegerGene>,
			Serializable
{
	private static final long serialVersionUID = 1L;


	protected IntegerChromosome(final ISeq<IntegerGene> genes) {
		super(genes);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public IntegerChromosome(final Integer min, final Integer max, final int length) {
		this(IntegerGene.seq(min, max, length));
		_valid = true;
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public IntegerChromosome(final Integer min, final Integer max) {
		this(min, max, 1);
	}

	/**
	 * Create a new {@code IntegerChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static IntegerChromosome of(final IntegerGene... genes) {
		return new IntegerChromosome(Array.of(genes).toISeq());
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final int length
	) {
		return new IntegerChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 */
	public static IntegerChromosome of(final int min, final int max) {
		return new IntegerChromosome(min, max);
	}

	@Override
	public IntegerChromosome newInstance(final ISeq<IntegerGene> genes) {
		return new IntegerChromosome(genes);
	}

	@Override
	public IntegerChromosome newInstance() {
		return new IntegerChromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object o) {
		return o == this || o instanceof IntegerChromosome && super.equals(o);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeInt(_min.intValue());
		out.writeInt(_max.intValue());

		for (IntegerGene gene : _genes) {
			out.writeInt(gene.getAllele().intValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final Array<IntegerGene> genes = new Array<>(in.readInt());
		_min = in.readInt();
		_max = in.readInt();

		for (int i = 0; i < genes.length(); ++i) {
			genes.set(i, new IntegerGene(in.readInt(), _min, _max));
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "int-chromosome")
	@XmlType(name = "org.jenetics.IntegerChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlAttribute(name = "min", required = true)
		public int min;

		@XmlAttribute(name = "max", required = true)
		public int max;

		@XmlElement(name = "allele", required = true, nillable = false)
		public List<Integer> values;

		public final static class Adapter
			extends XmlAdapter<Model, IntegerChromosome>
		{
			@Override
			public Model marshal(final IntegerChromosome c) {
				final Model m = new Model();
				m.length = c.length();
				m.min = c._min;
				m.max = c._max;
				m.values = c.toSeq().map(Allele).asList();
				return m;
			}

			@Override
			public IntegerChromosome unmarshal(final Model model) {
				final Integer min = model.min;
				final Integer max = model.max;
				return new IntegerChromosome(
					Array.of(model.values).map(Gene(min, max)).toISeq()
				);
			}
		}

		private static final Function<IntegerGene, Integer> Allele =
			new Function<IntegerGene, Integer>() {
				@Override
				public Integer apply(IntegerGene value) {
					return value.getAllele();
				}
			};

		private static Function<Integer, IntegerGene>
		Gene(final Integer min, final Integer max) {
			return new Function<Integer, IntegerGene>() {
				@Override
				public IntegerGene apply(final Integer value) {
					return new IntegerGene(value, min, max);
				}
			};
		}

	}
}
