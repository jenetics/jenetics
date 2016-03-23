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

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.MSeq;

/**
 * Numeric chromosome implementation which holds 32 bit integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz  Wilhelmstötter</a>
 * @since 2.0
 * @version 3.2
 */
@XmlJavaTypeAdapter(IntegerChromosome.Model.Adapter.class)
public class IntegerChromosome
	extends AbstractBoundedChromosome<Integer, IntegerGene>
	implements
			NumericChromosome<Integer, IntegerGene>,
			Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the gene sequence is empty
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
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
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public IntegerChromosome(
		final Integer min,
		final Integer max,
		final int length
	) {
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
	 * Returns an int array containing all of the elements in this chromosome
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
	public int[] toArray(final int[] array) {
		final int[] a = array.length >= length() ?
			array : new int[length()];

		for (int i = length(); --i >= 0;) {
			a[i] = intValue(i);
		}

		return a;
	}

	/**
	 * Returns an int array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public int[] toArray() {
		return toArray(new int[length()]);
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
		return new IntegerChromosome(ISeq.of(genes));
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @param min the min value of the {@link IntegerGene}s (inclusively).
	 * @param max the max value of the {@link IntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public static IntegerChromosome of(
		final int min,
		final int max,
		final int length
	) {
		return new IntegerChromosome(min, max, length);
	}

	/**
	 * Create a new random {@code IntegerChromosome}.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @param length the length of the chromosome.
	 * @return a new random {@code IntegerChromosome}
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 * @throws IllegalArgumentException if the length is smaller than one
	 */
	public static IntegerChromosome of(final IntRange range, final int length) {
		return new IntegerChromosome(range.getMin(), range.getMax(), length);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (inclusively).
	 * @return a new random {@code IntegerChromosome} of length one
	 */
	public static IntegerChromosome of(final int min, final int max) {
		return new IntegerChromosome(min, max);
	}

	/**
	 * Create a new random {@code IntegerChromosome} of length one.
	 *
	 * @since 3.2
	 *
	 * @param range the integer range of the chromosome.
	 * @return a new random {@code IntegerChromosome} of length one
	 * @throws NullPointerException if the given {@code range} is {@code null}
	 */
	public static IntegerChromosome of(final IntRange range) {
		return new IntegerChromosome(range.getMin(), range.getMax());
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
		out.writeInt(_min);
		out.writeInt(_max);

		for (IntegerGene gene : _genes) {
			out.writeInt(gene.getAllele());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final MSeq<IntegerGene> genes = MSeq.ofLength(in.readInt());
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
				m.values = c.toSeq().map(IntegerGene::getAllele).asList();
				return m;
			}

			@Override
			public IntegerChromosome unmarshal(final Model model) {
				final Integer min = model.min;
				final Integer max = model.max;
				return new IntegerChromosome(
					ISeq.of(model.values)
						.map(a -> new IntegerGene(a, min, max))
				);
			}
		}

	}
}
