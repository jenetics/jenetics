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
package org.jenetix;

import static org.jenetics.util.object.hashCodeOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.LargeInteger;

import org.jenetics.NumberChromosome;
import org.jenetics.util.Array;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-05-23 $</em>
 */
public class LargeIntegerChromosome
	extends NumberChromosome<LargeInteger, LargeIntegerGene>
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
	protected LargeIntegerChromosome(final ISeq<LargeIntegerGene> genes) {
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
	public LargeIntegerChromosome(final LargeIntegerGene... genes) {
		this(Array.valueOf(genes).toISeq());
	}

	/**
	 * Create a new random LargeIntegerChromosome.
	 *
	 * @param min the minimum value of the {@link LargeIntegerGene}s (inclusively).
	 * @param max the maximum value of the {@link LargeIntegerGene}s (inclusively).
	 * @param length the length of the chromosome.
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 */
	public LargeIntegerChromosome(
		final LargeInteger min,
		final LargeInteger max,
		final int length
	) {
		this(
			new Array<LargeIntegerGene>(length).fill(
				LargeIntegerGene.valueOf(min, max)
			).toISeq()
		);
		_valid = true;
	}

	/**
	 * Create a new random IntegerChromosome with length one.
	 *
	 * @param min the min value of the {@link LargeIntegerGene}s (inclusively).
	 * @param max the max value of the {@link LargeIntegerGene}s (inclusively).
	 * @throws NullPointerException if {@code min} or {@code max} is {@code null}.
	 */
	public LargeIntegerChromosome(final LargeInteger min, final LargeInteger max) {
		this(min, max, 1);
	}

	@Override
	public LargeIntegerChromosome newInstance(final ISeq<LargeIntegerGene> genes) {
		return new LargeIntegerChromosome(genes);
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public LargeIntegerChromosome newInstance() {
		return new LargeIntegerChromosome(_min, _max, length());
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
		return obj instanceof LargeIntegerChromosome && super.equals(obj);
	}



	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<LargeIntegerChromosome>
	XML = new XMLFormat<LargeIntegerChromosome>(LargeIntegerChromosome.class) {
		private static final String LENGTH = "length";
		private static final String MIN = "min";
		private static final String MAX = "max";

		private final static String MIN_VALUE = "0";
		private final static String MAX_VALUE = "1000000";

		@Override
		public LargeIntegerChromosome newInstance(
			final Class<LargeIntegerChromosome> cls, final InputElement xml
		) throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final LargeInteger min = LargeInteger.valueOf(
				xml.getAttribute(MIN, MIN_VALUE)
			);
			final LargeInteger max = LargeInteger.valueOf(
				xml.getAttribute(MAX, MAX_VALUE)
			);
			final Array<LargeIntegerGene> genes = new Array<>(length);

			for (int i = 0; i < length; ++i) {
				final LargeInteger value = xml.getNext();
				genes.set(i, LargeIntegerGene.valueOf(value, min, max));
			}

			final LargeIntegerChromosome
			chromosome = new LargeIntegerChromosome(genes.toISeq());

			chromosome._min = min;
			chromosome._max = max;

			return chromosome;
		}
		@Override
		public void write(
			final LargeIntegerChromosome chromosome,
			final OutputElement xml
		)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(MIN, chromosome._min.toString());
			xml.setAttribute(MAX, chromosome._max.toString());
			for (LargeIntegerGene gene : chromosome) {
				xml.add(gene.getAllele());
			}
		}
		@Override
		public void read(
			final InputElement element,
			final LargeIntegerChromosome chromosome
		)
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
		out.writeObject(_min);
		out.writeObject(_max);

		for (LargeIntegerGene gene : _genes) {
			out.writeObject(gene.getAllele());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		LargeInteger min = (LargeInteger)in.readObject();
		LargeInteger max = (LargeInteger)in.readObject();

		_min = min;
		_max = max;
		final Array<LargeIntegerGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			final LargeInteger value = (LargeInteger)in.readObject();
			genes.set(i, LargeIntegerGene.valueOf(value, min, max));
		}

		_genes = genes.toISeq();
	}

}





