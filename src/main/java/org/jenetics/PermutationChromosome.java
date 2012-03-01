/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.EnumGene.Gene;
import static org.jenetics.util.factories.Int;
import static org.jenetics.util.functions.StringToInteger;
import static org.jenetics.util.object.hashCodeOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.bit;


/**
 * The mutable methods of the {@link AbstractChromosome} has been overridden so
 * that no invalid permutation will be created.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class PermutationChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private ISeq<T> _validAlleles;

	PermutationChromosome(final int length, final ISeq<EnumGene<T>> genes) {
		super(genes);
		_validAlleles = genes.get(0).getValidAlleles();
		_valid = true;
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @param validAlleles the valid alleles used for this permutation arrays.
	 */
	public PermutationChromosome(final ISeq<T> validAlleles) {
		super(
			new Array<EnumGene<T>>(
				validAlleles.length()
			).fill(Gene(validAlleles)).shuffle(RandomRegistry.getRandom()).toISeq()
		);
		_validAlleles = validAlleles;
	}

	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Check if this chromosome represents still a valid permutation.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			byte[] check = new byte[length()/8 + 1];
			Arrays.fill(check, (byte)0);

			boolean valid = super.isValid();
			for (int i = 0; i < length() && valid; ++i) {
				final int value = _genes.get(i).getAlleleIndex();
				if (value >= 0 && value < length()) {
					if (bit.get(check, value)) {
						valid = false;
					} else {
						bit.set(check, value, true);
					}
				} else {
					valid = false;
				}
			}

			_valid = valid;
		}

		return _valid;
	}

	/**
	 * Return a more specific view of this chromosome factory.
	 *
	 * @return a more specific view of thiw chromosome factory.
	 */
	@SuppressWarnings("unchecked")
	public Factory<PermutationChromosome<T>> asFactory() {
		return (Factory<PermutationChromosome<T>>)(Object)this;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public PermutationChromosome<T> newInstance() {
		return new PermutationChromosome<>(_validAlleles);
	}

	@Override
	public PermutationChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes.length(), genes);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass())
				.and(super.hashCode())
				.value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(_genes.get(0).getAllele());
		for (int i = 1; i < length(); ++i) {
			out.append("|").append(_genes.get(i).getAllele());
		}
		return out.toString();
	}

	/**
	 * Create a new PermutationChromosome from the given genes.
	 *
	 * @param genes the genes of this chromosome.
	 * @return a new PermutationChromosome from the given genes.
	 */
	public static <T> PermutationChromosome<T> valueOf(
		final ISeq<EnumGene<T>> genes
	) {
		return new PermutationChromosome<>(genes.length(), genes);
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @param length the chromosome length.
	 * @return a integer permutation chromosome with the given length.
	 */
	public static PermutationChromosome<Integer> ofInteger(final int length) {
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		return new PermutationChromosome<>(alleles);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings("rawtypes")
	static final XMLFormat<PermutationChromosome>
	XML = new XMLFormat<PermutationChromosome>(PermutationChromosome.class) {

		private static final String LENGTH = "length";
		private static final String ALLELE_INDEXES = "allele-indexes";

		@SuppressWarnings("unchecked")
		@Override
		public PermutationChromosome newInstance(
			final Class<PermutationChromosome> cls, final InputElement xml
		) throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final Array<Object> alleles = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				alleles.set(i, xml.getNext());
			}

			final ISeq<Object> ialleles = alleles.toISeq();

			final Array<Integer> indexes = new Array<>(
				xml.get(ALLELE_INDEXES, String.class
			).split(",")).map(StringToInteger);

			final Array<Object> genes = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				genes.set(i, EnumGene.valueOf(ialleles, indexes.get(i)));
			}

			return new PermutationChromosome(genes.length(), genes.toISeq());
		}

		@Override
		public void write(final PermutationChromosome chromosome, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			for (Object allele : chromosome.getValidAlleles()) {
				xml.add(allele);
			}

			final PermutationChromosome<?> pc = chromosome;
			final String indexes = pc.toSeq().map(new Function<Object, Integer>() {
				@Override public Integer apply(final Object value) {
					return ((EnumGene<?>)value).getAlleleIndex();
				}
			}).toString(",");
			xml.add(indexes, ALLELE_INDEXES);
		}
		@Override
		public void read(
			final InputElement element, final PermutationChromosome chromosome
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

		out.writeObject(_validAlleles);
		for (EnumGene<?> gene : _genes) {
			out.writeInt(gene.getAlleleIndex());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_validAlleles = (ISeq<T>)in.readObject();

		final Array<EnumGene<T>> genes = new Array<>(_validAlleles.length());
		for (int i = 0; i < _validAlleles.length(); ++i) {
			genes.set(i, EnumGene.valueOf(_validAlleles, in.readInt()));
		}

		_genes = genes.toISeq();
	}

}




