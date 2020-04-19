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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.Hashes.hash;

import java.io.Serializable;
import java.util.Objects;

import io.jenetics.internal.math.Randoms;
import io.jenetics.util.CharSeq;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * Character gene implementation.
 *
 * <p>This is a <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html">
 * value-based</a> class; use of identity-sensitive operations (including
 * reference equality ({@code ==}), identity hash code, or synchronization) on
 * instances of {@code CharacterGene} may have unpredictable results and should
 * be avoided.
 *
 * @see CharacterChromosome
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.0
 */
public final class CharacterGene
	implements
		Gene<Character, CharacterGene>,
		Comparable<CharacterGene>,
		Serializable
{
	private static final long serialVersionUID = 3L;

	/**
	 * The default character set used by this gene.
	 */
	public static final CharSeq DEFAULT_CHARACTERS = new CharSeq(
		"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		" !\"$%&/()=?`{[]}\\+~*#';.:,-_<>|@^'"
	);

	private final char _allele;
	private final CharSeq _validCharacters;

	private CharacterGene(final CharSeq chars, final int index) {
		_allele = chars.get(index);
		_validCharacters = chars;
	}

	/**
	 * Create a new character gene from the given {@code character} and the
	 * given set of valid characters.
	 *
	 * @param allele the char this gene represents
	 * @param validChars the set of valid characters.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	private CharacterGene(final char allele, final CharSeq validChars) {
		_allele = allele;
		_validCharacters = requireNonNull(validChars);
	}

	@Override
	public boolean isValid() {
		return _validCharacters.contains(_allele);
	}

	@Override
	public Character allele() {
		return _allele;
	}

	/**
	 * Return the {@code char} value of this character gene.
	 *
	 * @return the {@code char} value.
	 */
	public char charValue() {
		return _allele;
	}

	/**
	 * Test, if the given character is valid.
	 *
	 * @param allele The character to test.
	 * @return true if the character is valid, false otherwise.
	 */
	public boolean isValidCharacter(final Character allele) {
		return _validCharacters.contains(allele);
	}

	/**
	 * Return a (unmodifiable) set of valid characters.
	 *
	 * @return the {@link CharSeq} of valid characters.
	 */
	public CharSeq validChars() {
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
		return Character.compare(_allele, that._allele);
	}

	@Override
	public int hashCode() {
		return hash(_allele, hash(_validCharacters));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof CharacterGene &&
			((CharacterGene)obj)._allele == _allele &&
			Objects.equals(((CharacterGene)obj)._validCharacters, _validCharacters);
	}

	@Override
	public String toString() {
		return Character.toString(_allele);
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
	 * is not within the {@link #validChars()}, an invalid gene will be
	 * created.
	 *
	 * @param allele the character value of the created gene.
	 * @return a new character gene.
	 * @throws NullPointerException if the given {@code character} is
	 *         {@code null}.
	 */
	@Override
	public CharacterGene newInstance(final Character allele) {
		return of(allele, _validCharacters);
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
			RandomRegistry.random().nextInt(validCharacters.length())
		);
	}

	/**
	 * Create a new character gene from the given character. If the character
	 * is not within the {@link #DEFAULT_CHARACTERS}, an invalid gene will be
	 * created.
	 *
	 * @param allele the character value of the created gene.
	 * @return a new character gene.
	 */
	public static CharacterGene of(final char allele) {
		return new CharacterGene(allele, DEFAULT_CHARACTERS);
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
			RandomRegistry.random().nextInt(DEFAULT_CHARACTERS.length())
		);
	}

	/**
	 * Create a new CharacterGene from the give character.
	 *
	 * @param allele The allele.
	 * @param validCharacters the valid characters fo the new gene
	 * @return a new {@code CharacterGene} with the given parameter
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 * @throws IllegalArgumentException if the {@code validCharacters} are empty.
	 */
	public static CharacterGene of(
		final char allele,
		final CharSeq validCharacters
	) {
		return new CharacterGene(allele, validCharacters);
	}

	static ISeq<CharacterGene> seq(
		final CharSeq chars,
		final IntRange lengthRange
	) {
		final var r = RandomRegistry.random();

		return MSeq.<CharacterGene>ofLength(Randoms.nextInt(lengthRange, r))
			.fill(() -> new CharacterGene(chars, r.nextInt(chars.length())))
			.toISeq();
	}

}
