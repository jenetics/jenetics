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

import static org.jenetics.util.object.hashCodeOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Integer64;

import org.jenetics.util.Array;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;


/**
 * Number chromosome implementation which holds 64 bit integer numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public class Integer64Chromosome
	extends NumberChromosome<Integer64, Integer64Gene>
	implements XMLSerializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws IllegalArgumentException if the {@code genes.length()} is smaller
	 *         than one.
	 */
	protected Integer64Chromosome(final ISeq<Integer64Gene> genes) {
		super(genes);
	}

	/**
	 * Create a new chromosome from the given genes array.
	 *
	 * @param genes the genes of the new chromosome.
	 * @throws NullPointerException if the given genes array is {@code null}.
	 * @throws IllegalArgumentException if the {@code genes.length} is smaller than
	 *         one.
	 */
	public Integer64Chromosome(final Integer64Gene... genes) {
		this(Array.valueOf(genes).toISeq());
	}

	/**
	 * Create a new random {@code Integer64Chromosome} of the given
	 * {@code length}.
	 *
	 * @param min the minimum value of the {@link Integer64Gene}s (inclusively).
	 * @param max the maximum value of the {@link Integer64Gene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if {@code min} or {@code max} is
	 *         {@code null}.
	 */
	public Integer64Chromosome(
		final Integer64 min,
		final Integer64 max,
		final int length
	) {
		this(
			new Array<Integer64Gene>(length).fill(
				Integer64Gene.valueOf(min, max)
			).toISeq()
		);
		_valid = true;
	}

	/**
	 * Create a new random {@code Integer64Chromosome} of the given
	 * {@code length}.
	 *
	 * @param min the minimum value of the {@link Integer64Gene}s (inclusively).
	 * @param max the maximum value of the {@link Integer64Gene}s (inclusively).
	 * @param length the length of the chromosome.
	 */
	public Integer64Chromosome(final long min, final long max, int length) {
		this(Integer64.valueOf(min), Integer64.valueOf(max), length);
	}

	/**
	 * Create a new random {@code Integer64Chromosome} of length one.
	 *
	 * @param min the minimum value of the {@link Integer64Gene}s (inclusively).
	 * @param max the maximum value of the {@link Integer64Gene}s (inclusively).
	 */
	public Integer64Chromosome(final long min, final long max) {
		this(Integer64.valueOf(min), Integer64.valueOf(max));
	}

	/**
	 * Create a new random {@code Integer64Chromosome} of length one.
	 *
	 * @param min the minimum value of the {@link Integer64Gene}s (inclusively).
	 * @param max the maximum value of the {@link Integer64Gene}s (inclusively).
	 * @throws NullPointerException if {@code min} or {@code max} is
	 *         {@code null}.
	 */
	public Integer64Chromosome(final Integer64 min, final Integer64 max) {
		this(min, max, 1);
	}

	@Override
	public Integer64Chromosome newInstance(final ISeq<Integer64Gene> genes) {
		return new Integer64Chromosome(genes);
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public Integer64Chromosome newInstance() {
		return new Integer64Chromosome(_min, _max, length());
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof Integer64Chromosome && super.equals(obj);
	}

	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Return a {@link Function} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	public static final Function<AbstractChromosome<Integer64Gene>, ISeq<Integer64Gene>>
		Genes = AbstractChromosome.genes();

	/**
	 * Return a {@link Function} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	public static final Function<Chromosome<Integer64Gene>, Integer64Gene>
		Gene = AbstractChromosome.gene();

	/**
	 * Return a {@link Function} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 */
	public static final Function<Chromosome<Integer64Gene>, Integer64Gene>
	Gene(final int index)
	{
		return AbstractChromosome.gene(index);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<Integer64Chromosome>
	XML = new XMLFormat<Integer64Chromosome>(Integer64Chromosome.class) {
		private static final String LENGTH = "length";
		private static final String MIN = "min";
		private static final String MAX = "max";

		@Override
		public Integer64Chromosome newInstance(
			final Class<Integer64Chromosome> cls, final InputElement xml
		) throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final long min = xml.getAttribute(MIN, 0L);
			final long max = xml.getAttribute(MAX, 100L);
			final Array<Integer64Gene> genes = new Array<>(length);

			for (int i = 0; i < length; ++i) {
				final Integer64 value = xml.getNext();
				genes.set(i, Integer64Gene.valueOf(value.longValue(), min, max));
			}

			final Integer64Chromosome chromosome = new Integer64Chromosome(genes.toISeq());
			chromosome._min = Integer64.valueOf(min);
			chromosome._max = Integer64.valueOf(max);

			return chromosome;
		}
		@Override
		public void write(final Integer64Chromosome chromosome, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(MIN, chromosome._min.longValue());
			xml.setAttribute(MAX, chromosome._max.longValue());
			for (Integer64Gene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(final InputElement element, final Integer64Chromosome chromosome)
			throws XMLStreamException
		{
		}
	};

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

		for (Integer64Gene gene : _genes) {
			out.writeLong(gene.longValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		Integer64 min = Integer64.valueOf(in.readLong());
		Integer64 max = Integer64.valueOf(in.readLong());

		_min = min;
		_max = max;
		final Array<Integer64Gene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			genes.set(i, Integer64Gene.valueOf(Integer64.valueOf(in.readLong()), min, max));
		}

		_genes = genes.toISeq();
	}

}






