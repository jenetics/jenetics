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

import static org.jenetics.util.ISeq.toISeq;

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

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.ISeq;
import org.jenetics.util.LongRange;
import org.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 64 bit integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.6
 * @version 3.2
 */
@XmlJavaTypeAdapter(LongChromosome.Model.Adapter.class)
public class LongChromosome
	extends AbstractBoundedChromosome<Long, LongGene>
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
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
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
	 * Returns an long array containing all of the elements in this chromosome
	 * in proper sequence.  If the chromosome fits in the specified array, it is
	 * returned therein. Otherwise, a new array is allocated with the length of
	 * this chromosome.
	 *
	 * @since 3.0
	 *
	 * @param array the array into which the elements of this chromosomes are to
	 *        be stored, if it is big enough; otherwise, a new array is
	 *        allocated for this purpose.
	 * @return an array containing the elements of this chromosome
	 * @throws NullPointerException if the given {@code array} is {@code null}
	 */
	public long[] toArray(final long[] array) {
		final long[] a = array.length >= length() ?
			array : new long[length()];

		for (int i = length(); --i >= 0;) {
			a[i] = longValue(i);
		}

		return a;
	}

	/**
	 * Returns an long array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public long[] toArray() {
		return toArray(new long[length()]);
	}

	/**
	 * Create a new {@code LongChromosome} with the given genes.
	 *
	 * @param genes the genes of the chromosome.
	 * @return a new chromosome with the given genes.
	 * @throws IllegalArgumentException if the length of the genes array is
	 *         empty.
	 * @throws NullPointerException if the given {@code genes} are {@code null}
	 */
	public static LongChromosome of(final LongGene... genes) {
		return new LongChromosome(ISeq.of(genes));
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @param min the min value of the {@link LongGene}s (inclusively).
	 * @param max the max value of the {@link LongGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @return a new {@code LongChromosome} with the given gene parameters.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static LongChromosome of(
		final long min,
		final long max,
		final int length
	) {
		return new LongChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code LongChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the long range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code LongChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static LongChromosome of(final LongRange range, final int length) {
		return new LongChromosome(range.getMin(), range.getMax(), length);
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @return a new {@code LongChromosome} with the given gene parameters.
	 */
	public static LongChromosome of(final long min, final long max) {
		return new LongChromosome(min, max);
	}

	/**
	 * Create a new random {@code LongChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the long range of the chromosome.
	 * @return a new random {@code LongChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 */
	public static LongChromosome of(final LongRange range) {
		return new LongChromosome(range.getMin(), range.getMax());
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
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeLong(_min);
		out.writeLong(_max);

		for (LongGene gene : _genes) {
			out.writeLong(gene.getAllele());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final MSeq<LongGene> genes = MSeq.ofLength(in.readInt());
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

	@XmlRootElement(name = "long-chromosome")
	@XmlType(name = "org.jenetics.LongChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlAttribute(name = "min", required = true)
		public long min;

		@XmlAttribute(name = "max", required = true)
		public long max;

		@XmlElement(name = "allele", required = true, nillable = false)
		public List<Long> values;

		public final static class Adapter
			extends XmlAdapter<Model, LongChromosome>
		{
			@Override
			public Model marshal(final LongChromosome c) {
				final Model m = new Model();
				m.length = c.length();
				m.min = c._min;
				m.max = c._max;
				m.values = c.toSeq().map(LongGene::getAllele).asList();
				return m;
			}

			@Override
			public LongChromosome unmarshal(final Model model) {
				final Long min = model.min;
				final Long max = model.max;
				return new LongChromosome(
					model.values.stream()
						.map(value -> new LongGene(value, min, max))
						.collect(toISeq())
				);
			}
		}
	}
}
