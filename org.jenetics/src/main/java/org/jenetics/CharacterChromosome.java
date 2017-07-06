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
import static org.jenetics.internal.util.Equality.eq;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
import org.jenetics.internal.util.IntRef;

import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * CharacterChromosome which represents character sequences.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
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
		return getGene(index).charValue();
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
		return Hash.of(getClass())
				.and(super.hashCode())
				.and(_validCharacters).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(cc ->
			super.equals(obj) &&
			eq(_validCharacters, cc._validCharacters)
		);
	}

	@Override
	public String toString() {
		return toSeq().stream()
			.map(Object::toString)
			.collect(Collectors.joining());
	}

	/**
	 * Returns an char array containing all of the elements in this chromosome
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
	public char[] toArray(final char[] array) {
		final char[] a = array.length >= length() ?
			array : new char[length()];

		for (int i = length(); --i >= 0;) {
			a[i] = charAt(i);
		}

		return a;
	}

	/**
	 * Returns an char array containing all of the elements in this chromosome
	 * in proper sequence.
	 *
	 * @since 3.0
	 *
	 * @return an array containing the elements of this chromosome
	 */
	public char[] toArray() {
		return toArray(new char[length()]);
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
		final IntRef index = new IntRef();
		final Supplier<CharacterGene> geneFactory = () -> CharacterGene.of(
			alleles.charAt(index.value++), validChars
		);

		final ISeq<CharacterGene> genes =
			MSeq.<CharacterGene>ofLength(alleles.length())
				.fill(geneFactory)
				.toISeq();

		return new CharacterChromosome(genes);
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
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeObject(_validCharacters);

		for (CharacterGene gene : _genes) {
			out.writeChar(gene.getAllele());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		_validCharacters = (CharSeq)in.readObject();

		final MSeq<CharacterGene> genes = MSeq.ofLength(length);
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
