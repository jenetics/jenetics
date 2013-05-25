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

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.Function;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * Number chromosome implementation which holds 64 bit floating point numbers.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public class Float64Chromosome
	extends NumberChromosome<Float64, Float64Gene>
	implements XMLSerializable
{
	private static final long serialVersionUID = 1L;


	protected Float64Chromosome(final ISeq<Float64Gene> genes) {
		super(genes);
	}

	/**
	 * Create a new chromosome from the given {@code genes}.
	 *
	 * @param genes the genes this chromosome consists.
	 * @throws IllegalArgumentException if the number of genes is smaller than
	 *         one.
	 * @throws NullPointerException if the {@code genes} are {@code null}.
	 */
	public Float64Chromosome(final Float64Gene... genes) {
		this(Array.valueOf(genes).toISeq());
	}

	/**
	 * Create a new random DoubleChromosome.
	 *
	 * @param min the min value of the {@link Float64Gene}s (inclusively).
	 * @param max the max value of the {@link Float64Gene}s (exclusively).
	 * @param length the length of the chromosome.
	 */
	public Float64Chromosome(
		final Float64 min,
		final Float64 max,
		final int length
	) {
		this(
			new Array<Float64Gene>(length).fill(
				Float64Gene.valueOf(min, max)
			).toISeq()
		);
		_valid = true;
	}

	/**
	 * Create a new random chromosome of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 */
	public Float64Chromosome(final double min, final double max) {
		this(Float64.valueOf(min), Float64.valueOf(max));
	}

	/**
	 * Create a new random chromosome of length one.
	 *
	 * @param min the minimal value of this chromosome (inclusively).
	 * @param max the maximal value of this chromosome (exclusively).
	 * @throws NullPointerException if {@code min} or {@code max} is
	 *         {@code null}.
	 */
	public Float64Chromosome(final Float64 min, final Float64 max) {
		this(min, max, 1);
	}

	/**
	 * Create a new chromosome
	 *
	 * @param min the minimal value of this chromosome.
	 * @param max the maximal value of this chromosome.
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public Float64Chromosome(final double min, final double max, final int length) {
		this(Float64.valueOf(min), Float64.valueOf(max), length);
	}

	@Override
	public Float64Chromosome newInstance(final ISeq<Float64Gene> genes) {
		return new Float64Chromosome(genes);
	}

	/**
	 * Return a more specific view of this chromosome factory.
	 *
	 * @return a more specific view of this chromosome factory.
	 *
	 * @deprecated No longer needed after adding new factory methods to the
	 *             {@link Array} class.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public Factory<Float64Chromosome> asFactory() {
		return (Factory<Float64Chromosome>)(Object)this;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public Float64Chromosome newInstance() {
		return new Float64Chromosome(_min, _max, length());
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
		return obj instanceof Float64Chromosome && super.equals(obj);
	}

	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Return a {@link Function} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	public static final Function<AbstractChromosome<Float64Gene>, ISeq<Float64Gene>>
		Genes = AbstractChromosome.genes();

	/**
	 * Return a {@link Function} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	public static final Function<Chromosome<Float64Gene>, Float64Gene>
		Gene = AbstractChromosome.gene();

	/**
	 * Return a {@link Function} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 */
	public static final Function<Chromosome<Float64Gene>, Float64Gene>
	Gene(final int index)
	{
		return AbstractChromosome.gene(index);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<Float64Chromosome>
	XML = new XMLFormat<Float64Chromosome>(Float64Chromosome.class)
	{
		private static final String LENGTH = "length";
		private static final String MIN = "min";
		private static final String MAX = "max";

		@Override
		public Float64Chromosome newInstance(
			final Class<Float64Chromosome> cls, final InputElement xml
		) throws XMLStreamException {
			final int length = xml.getAttribute(LENGTH, 0);
			final double min = xml.getAttribute(MIN, 0.0);
			final double max = xml.getAttribute(MAX, 1.0);

			final Array<Float64Gene> genes = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				final Float64 value = xml.getNext();
				genes.set(i, Float64Gene.valueOf(value.doubleValue(), min, max));
			}

			final Float64Chromosome chromosome = new Float64Chromosome(genes.toISeq());
			chromosome._min = Float64.valueOf(min);
			chromosome._max = Float64.valueOf(max);

			return chromosome;
		}
		@Override
		public void write(final Float64Chromosome chromosome, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(MIN, chromosome._min.doubleValue());
			xml.setAttribute(MAX, chromosome._max.doubleValue());
			for (Float64Gene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(final InputElement element, final Float64Chromosome chromosome)
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
		out.writeDouble(_min.doubleValue());
		out.writeDouble(_max.doubleValue());

		for (Float64Gene gene : _genes) {
			out.writeDouble(gene.doubleValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		final Float64 min = Float64.valueOf(in.readDouble());
		final Float64 max = Float64.valueOf(in.readDouble());

		_min = min;
		_max = max;
		final Array<Float64Gene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			genes.set(i, Float64Gene.valueOf(Float64.valueOf(in.readDouble()), min, max));
		}

		_genes = genes.toISeq();
	}

}





