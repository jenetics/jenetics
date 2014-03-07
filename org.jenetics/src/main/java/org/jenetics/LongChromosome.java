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
 * Numeric chromosome implementation which holds 64 bit integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.6 &mdash; <em>$Date: 2014-03-05 $</em>
 * @since 1.6
 */
@XmlJavaTypeAdapter(LongChromosome.Model.Adapter.class)
public class LongChromosome
	extends AbstractNumericChromosome<Long, LongGene>
	implements
		NumericChromosome<Long, LongGene>,
		Serializable
{
	private static final long serialVersionUID = 1L;


	protected LongChromosome(final ISeq<LongGene> genes) {
		super(genes);
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @param min the min value of the {@link LongGene}s (inclusively).
	 * @param max the max value of the {@link LongGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public LongChromosome(final Long min, final Long max, final int length) {
		this(LongGene.seq(min, max, length));
		_valid = true;
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public LongChromosome(final Long min, final Long max) {
		this(min, max, 1);
	}

	/**
	 * Create a new {@code LongChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 */
	public static LongChromosome of(final LongGene... genes) {
		return new LongChromosome(Array.of(genes).toISeq());
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @param min the min value of the {@link LongGene}s (inclusively).
	 * @param max the max value of the {@link LongGene}s (inclusively).
	 * @param length the length of the chromosome.
	 */
	public static LongChromosome of(
		final long min,
		final long max,
		final int length
	) {
		return new LongChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 */
	public static LongChromosome of(final long min, final long max) {
		return new LongChromosome(min, max);
	}

	@Override
	public LongChromosome newInstance(final ISeq<LongGene> genes) {
		return new LongChromosome(genes);
	}

	@Override
	public LongChromosome newInstance() {
		return new LongChromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object o) {
		return o == this || o instanceof LongChromosome && super.equals(o);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeLong(_min.longValue());
		out.writeLong(_max.longValue());

		for (LongGene gene : _genes) {
			out.writeLong(gene.getAllele().longValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final Array<LongGene> genes = new Array<>(in.readInt());
		_min = in.readLong();
		_max = in.readLong();

		for (int i = 0; i < genes.length(); ++i) {
			genes.set(i, new LongGene(in.readLong(), _min, _max));
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.LongChromosome")
	@XmlType(name = "org.jenetics.LongChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public int length;

		@XmlAttribute
		public long min;

		@XmlAttribute
		public long max;

		@XmlElement(name = "allele")
		public List<Long> values;

		@ValueType(LongChromosome.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, LongChromosome>
		{
			@Override
			public Model marshal(final LongChromosome c) {
				final Model m = new Model();
				m.length = c.length();
				m.min = c._min;
				m.max = c._max;
				m.values = c.toSeq().map(Allele).asList();
				return m;
			}

			@Override
			public LongChromosome unmarshal(final Model model) {
				final Long min = model.min;
				final Long max = model.max;
				return new LongChromosome(
					Array.of(model.values).map(Gene(min, max)).toISeq()
				);
			}
		}
	}

	private static final Function<LongGene, Long> Allele =
		new Function<LongGene, Long>() {
			@Override
			public Long apply(LongGene value) {
				return value.getAllele();
			}
		};

	private static Function<Long, LongGene> Gene(final Long min, final Long max) {
		return new Function<Long, LongGene>() {
			@Override
			public LongGene apply(final Long value) {
				return new LongGene(value, min, max);
			}
		};
	}
}
