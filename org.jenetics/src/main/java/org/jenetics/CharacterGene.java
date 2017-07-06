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
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.Hash;

import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * Character gene implementation.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code CharacterGene} may have unpredictable results and should
 * be avoided.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
@XmlJavaTypeAdapter(CharacterGene.Model.Adapter.class)
public final class CharacterGene
	implements
		Gene<Character, CharacterGene>,
		Comparable<CharacterGene>,
		Serializable
{
	private static final long serialVersionUID = 2L;

	/**
	 * The default character set used by this gene.
	 */
	public static final CharSeq DEFAULT_CHARACTERS = new CharSeq(
		"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
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
	CharacterGene(final Character character, final CharSeq validChars) {
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
	 * Return a (unmodifiable) set of valid characters.
	 *
	 * @return the {@link CharSeq} of valid characters.
	 */
	public CharSeq getValidCharacters() {
		return _validCharacters;
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

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_character)
			.and(_validCharacters).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof CharacterGene &&
			eq(((CharacterGene)obj)._character, _character) &&
			eq(((CharacterGene)obj)._validCharacters, _validCharacters);
	}

	@Override
	public String toString() {
		return _character.toString();
	}


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
	 * Create a new CharacterGene from the give character.
	 *
	 * @param character The allele.
	 * @param validCharacters the valid characters fo the new gene
	 * @return a new {@code CharacterGene} with the given parameter
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code validCharacters} are empty.
	 */
	public static CharacterGene of(
		final char character,
		final CharSeq validCharacters
	) {
		return new CharacterGene(character, validCharacters);
	}

	static ISeq<CharacterGene> seq(final CharSeq chars, final int length) {
		final Random r = RandomRegistry.getRandom();

		return MSeq.<CharacterGene>ofLength(length)
			.fill(() -> new CharacterGene(chars, r.nextInt(chars.length())))
			.toISeq();
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "character-gene")
	@XmlType(name = "org.jenetics.CharacterGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute(name = "valid-alleles", required = true)
		public String validCharacters;

		@XmlValue
		public String value;

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
