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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.eq;

import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Array;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * Character gene implementation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date: 2014-03-03 $</em>
 */
@XmlJavaTypeAdapter(CharacterGene.Model.Adapter.class)
public final class CharacterGene
	implements
		Gene<Character, CharacterGene>,
		Comparable<CharacterGene>,
		Realtime,
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The default character set used by this gene.
	 */
	public static final CharSeq DEFAULT_CHARACTERS = new CharSeq(
		CharSeq.expand("0-9a-zA-Z") +
		" !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'"
	);

	private final Character _character;
	private final CharSeq _validCharacters;
	private final Boolean _valid;

	private CharacterGene(final CharSeq chars, final int index) {
		_character = chars.get(index);
		_validCharacters = chars;
		_valid = true;
	}

	/**
	 * Create a new character gene from the given {@code character} and the
	 * given set of valid characters.
	 *
	 * @param character the char this gene represents
	 * @param validChars the set of valid characters.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public CharacterGene(final Character character, final CharSeq validChars) {
		_character = requireNonNull(character);
		_validCharacters = requireNonNull(validChars);
		_valid = _validCharacters.contains(_character);
	}

	@Override
	public boolean isValid() {
		return _valid;
	}

	@Override
	public Character getAllele() {
		return _character;
	}

	/**
	 * Return the {@code char} value of this character gene.
	 *
	 * @return the {@code char} value.
	 */
	public char charValue() {
		return _character;
	}

	/**
	 * Test, if the given character is valid.
	 *
	 * @param character The character to test.
	 * @return true if the character is valid, false otherwise.
	 */
	public boolean isValidCharacter(final Character character) {
		return _validCharacters.contains(character);
	}

	/**
	 * Retunr a (unmodifiable) set of valid characters.
	 *
	 * @return the {@link CharSeq} of valid characters.
	 */
	public CharSeq getValidCharacters() {
		return _validCharacters;
	}

	@Deprecated
	@Override
	public CharacterGene copy() {
		return of(_character, _validCharacters);
	}

	/**
	 * @see java.lang.Character#compareTo(java.lang.Character)
	 * @param that The other gene to compare.
	 * @return the value 0 if the argument Character is equal to this Character;
	 *         a value less than 0 if this Character is numerically less than
	 *         the Character argument; and a value greater than 0 if this
	 *         Character is numerically greater than the Character argument
	 *         (unsigned comparison). Note that this is strictly a numerical
	 *         comparison; it is not local-dependent.
	 */
	@Override
	public int compareTo(final CharacterGene that) {
		return getAllele().compareTo(that.getAllele());
	}

	/**
	 * Return the {@link Factory} view of this gene.
	 *
	 * @return the {@link Factory} view of this gene.
	 *
	 * @deprecated No longer needed after adding new factory methods to the
	 *             {@link Array} class.
	 */
	@Deprecated
	Factory<CharacterGene> asFactory() {
		return this;
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_character).and(_validCharacters).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CharacterGene)) {
			return false;
		}
		CharacterGene gene = (CharacterGene)obj;
		return eq(_character, gene._character) &&
				eq(_validCharacters, gene._validCharacters);
	}

	@Override
	public String toString() {
		return _character.toString();
	}

	@Override
	public Text toText() {
		return Text.valueOf(_character);
	}


	/* *************************************************************************
	 *  Property access methods.
	 * ************************************************************************/

	/**
	 * Converter for accessing the allele from a given gene.
	 */
	public static final Function<CharacterGene, Character> Allele =
		new Function<CharacterGene, Character>() {
			@Override public Character apply(final CharacterGene value) {
				return value._character;
			}
		};

	/**
	 * Converter for accessing the valid characters from a given gene.
	 */
	public static final Function<CharacterGene, CharSeq> ValidCharacters =
		new Function<CharacterGene, CharSeq>() {
			@Override public CharSeq apply(final CharacterGene value) {
				return value._validCharacters;
			}
		};


	/* *************************************************************************
	 *  Factory methods
	 * ************************************************************************/

	@Override
	public CharacterGene newInstance() {
		return of(_validCharacters);
	}

	/**
	 * Create a new character gene from the given character. If the character
	 * is not within the {@link #getValidCharacters()}, an invalid gene will be
	 * created.
	 *
	 * @param character the character value of the created gene.
	 * @return a new character gene.
	 * @throws NullPointerException if the given {@code character} is
	 *         {@code null}.
	 */
	public CharacterGene newInstance(final Character character) {
		return of(character, _validCharacters);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new CharacterGene with a randomly chosen character from the
	 * set of valid characters.
	 *
	 * @param validCharacters the valid characters for this gene.
	 * @return a new valid, <em>random</em> gene,
	 * @throws NullPointerException if the {@code validCharacters} are
	 *         {@code null}.
	 */
	public static CharacterGene of(final CharSeq validCharacters) {
		return new CharacterGene(
			validCharacters,
			RandomRegistry.getRandom().nextInt(validCharacters.length())
		);
	}

	/**
	 * @deprecated Use {@link #of(org.jenetics.util.CharSeq)} instead.
	 */
	@Deprecated
	public static CharacterGene valueOf(final CharSeq validCharacters) {
		return of(validCharacters);
	}

	/**
	 * Create a new character gene from the given character. If the character
	 * is not within the {@link #DEFAULT_CHARACTERS}, an invalid gene will be
	 * created.
	 *
	 * @param character the character value of the created gene.
	 * @return a new character gene.
	 * @throws NullPointerException if the given {@code character} is
	 *         {@code null}.
	 */
	public static CharacterGene of(final Character character) {
		return new CharacterGene(character, DEFAULT_CHARACTERS);
	}

	/**
	 * @deprecated Use {@link #of(Character)} instead.
	 */
	@Deprecated
	public static CharacterGene valueOf(final Character character) {
		return of(character);
	}

	/**
	 * Create a new random character gene, chosen from the
	 * {@link #DEFAULT_CHARACTERS}.
	 *
	 * @return a new random character gene.
	 */
	public static CharacterGene of() {
		return new CharacterGene(
			DEFAULT_CHARACTERS,
			RandomRegistry.getRandom().nextInt(DEFAULT_CHARACTERS.length())
		);
	}

	/**
	 * @deprecated Use {@link #of()} instead.
	 */
	@Deprecated
	public static CharacterGene valueOf() {
		return of();
	}

	/**
	 * Create a new CharacterGene from the give character.
	 *
	 * @param character The allele.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code validCharacters} are empty.
	 */
	public static CharacterGene of(
		final char character,
		final CharSeq validCharacters
	) {
		return new CharacterGene(character, validCharacters);
	}

	/**
	 * @deprecated Use {@link #of(char, org.jenetics.util.CharSeq)} instead.
	 */
	@Deprecated
	public static CharacterGene valueOf(
		final Character character,
		final CharSeq validCharacters
	) {
		return of(character, validCharacters);
	}

	static ISeq<CharacterGene> seq(final CharSeq characters, final int length) {
		final Random random = RandomRegistry.getRandom();
		final int charsLength = characters.length();

		final Array<CharacterGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			final CharacterGene gene = new CharacterGene(
				characters, random.nextInt(charsLength)
			);
			genes.set(i, gene);
		}
		return genes.toISeq();
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<CharacterGene>
	XML = new XMLFormat<CharacterGene>(CharacterGene.class)
	{
		private static final String VALID_CHARS = "valid-characters";

		@Override
		public CharacterGene newInstance(
			final Class<CharacterGene> cls, final InputElement xml
		)
			throws XMLStreamException
		{
			final String validCharacters = xml.getAttribute(
				VALID_CHARS,
				DEFAULT_CHARACTERS.toString()
			);
			final String character = xml.getText().toString();

			return CharacterGene.of(
				character.charAt(0),
				new CharSeq(validCharacters)
			);
		}
		@Override
		public void write(final CharacterGene gene, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(VALID_CHARS, gene.getValidCharacters().toString());
			xml.addText(gene._character.toString());
		}
		@Override
		public void read(final InputElement element, final CharacterGene gene) {
		}
	};


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.CharacterGene")
	@XmlType(name = "org.jenetics.CharacterGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "valid-characters")
		public String validCharacters;

		@XmlValue
		public String value;

		@ValueType(CharacterGene.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, CharacterGene>
		{
			@Override
			public Model marshal(final CharacterGene value) {
				final Model m = new Model();
				m.validCharacters = value.getValidCharacters().toString();
				m.value = value.getAllele().toString();
				return m;
			}

			@Override
			public CharacterGene unmarshal(final Model m) {
				return CharacterGene.of(
					m.value.charAt(0),
					new CharSeq(m.validCharacters)
				);
			}
		}
	}

}
