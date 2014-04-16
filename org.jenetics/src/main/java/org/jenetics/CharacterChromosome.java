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

import static org.jenetics.CharacterGene.DEFAULT_CHARACTERS;
import static org.jenetics.internal.util.object.eq;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;

/**
 * CharacterChromosome which represents character sequences.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-03-30 $</em>
 */
@XmlJavaTypeAdapter(CharacterChromosome.Model.Adapter.class)
public class CharacterChromosome
	extends
		AbstractChromosome<CharacterGene>
	implements
		CharSequence,
		Serializable
{
	private static final long serialVersionUID = 2L;

	private transient CharSeq _validCharacters;

	/**
	 * Create a new chromosome from the given {@code genes} array. The genes
	 * array is copied, so changes to the given genes array doesn't effect the
	 * genes of this chromosome.
	 *
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	protected CharacterChromosome(final ISeq<CharacterGene> genes) {
		super(genes);
		_validCharacters = genes.get(0).getValidCharacters();
	}

	/**
	 * Create a new chromosome with the {@code validCharacters} char set as
	 * valid characters.
	 *
	 * @param validCharacters the valid characters for this chromosome.
	 * @param length the length of the new chromosome.
	 * @throws NullPointerException if the {@code validCharacters} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public CharacterChromosome(final CharSeq validCharacters, final int length) {
		this(CharacterGene.seq(validCharacters, length));
		_valid = true;
	}

	@Override
	public char charAt(final int index) {
		return getGene(index).getAllele();
	}

	@Override
	public CharacterChromosome subSequence(final int start, final int end) {
		return new CharacterChromosome(_genes.subSeq(start, end));
	}

	/**
	 * @throws NullPointerException if the given gene array is {@code null}.
	 */
	@Override
	public CharacterChromosome newInstance(final ISeq<CharacterGene> genes) {
		return new CharacterChromosome(genes);
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public CharacterChromosome newInstance() {
		return new CharacterChromosome(_validCharacters, length());
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).
				and(super.hashCode()).
				and(_validCharacters).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final CharacterChromosome cc = (CharacterChromosome)obj;
		return super.equals(obj) && eq(_validCharacters, cc._validCharacters);
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		for (CharacterGene gene : this) {
			out.append(gene);
		}
		return out.toString();
	}


	/**
	 * Create a new chromosome with the {@link CharacterGene#DEFAULT_CHARACTERS}
	 * char set as valid characters.
	 *
	 * @param length the {@code length} of the new chromosome.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static CharacterChromosome of(final int length) {
		return new CharacterChromosome(
			CharacterGene.seq(DEFAULT_CHARACTERS, length)
		);
	}

	/**
	 * Create a new chromosome from the given genes (given as string).
	 *
	 * @param alleles the character genes.
	 * @param validChars the valid characters.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws IllegalArgumentException if the genes string is empty.
	 */
	public static CharacterChromosome of(
		final String alleles,
		final CharSeq validChars
	) {
		final Array<CharacterGene> genes = new Array<>(alleles.length());
		genes.fill(GeneFactory(alleles, validChars));
		return new CharacterChromosome(genes.toISeq());
	}

	private static Factory<CharacterGene>
	GeneFactory(final String alleles, final CharSeq validChars) {
		return new Factory<CharacterGene>() {
			private int _index = 0;
			@Override
			public CharacterGene newInstance() {
				return CharacterGene.of(
					alleles.charAt(_index++), validChars
				);
			}
		};
	}

	/**
	 * Create a new chromosome from the given genes (given as string).
	 *
	 * @param alleles the character genes.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws IllegalArgumentException if the genes string is empty.
	 */
	public static CharacterChromosome of(final String alleles) {
		return of(alleles, DEFAULT_CHARACTERS);
	}

	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Return a {@link Function} which returns the gene array from this
	 * {@link Chromosome}.
	 */
	public static final Function<Chromosome<CharacterGene>, ISeq<CharacterGene>>
		Genes = AbstractChromosome.genes();

	/**
	 * Return a {@link Function} which returns the first {@link Gene} from this
	 * {@link Chromosome}.
	 */
	public static final Function<Chromosome<CharacterGene>, CharacterGene>
		Gene = AbstractChromosome.gene();

	/**
	 * Return a {@link Function} which returns the {@link Gene} with the given
	 * {@code index} from this {@link Chromosome}.
	 *
	 * @param index the gene index within the chromosome
	 * @return a function witch returns the gene at the given index
	 */
	public static Function<Chromosome<CharacterGene>, CharacterGene>
	Gene(final int index)
	{
		return AbstractChromosome.gene(index);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeObject(_validCharacters);

		for (CharacterGene gene : _genes) {
			out.writeChar(gene.getAllele().charValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		_validCharacters = (CharSeq)in.readObject();

		final Array<CharacterGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			final CharacterGene gene = CharacterGene.of(
				in.readChar(),
				_validCharacters
			);
			genes.set(i, gene);
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "character-chromosome")
	@XmlType(name = "org.jenetics.CharacterChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlElement(name = "valid-alleles", required = true, nillable = false)
		public String validCharacters;

		@XmlElement(name = "alleles", required = true, nillable = false)
		public String genes;

		public final static class Adapter
			extends XmlAdapter<Model, CharacterChromosome>
		{
			@Override
			public Model marshal(final CharacterChromosome value) {
				final Model m = new Model();
				m.length = value.length();
				m.validCharacters = value._validCharacters.toString();
				m.genes = value.toString();
				return m;
			}

			@Override
			public CharacterChromosome unmarshal(final Model m) {
				return CharacterChromosome.of(
					m.genes,
					new CharSeq(m.validCharacters)
				);
			}
		}
	}
}
