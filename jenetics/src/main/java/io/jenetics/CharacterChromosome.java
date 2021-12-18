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
import static io.jenetics.CharacterGene.DEFAULT_CHARACTERS;
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.readString;
import static io.jenetics.internal.util.SerialIO.writeInt;
import static io.jenetics.internal.util.SerialIO.writeString;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import io.jenetics.util.CharSeq;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;
import io.jenetics.util.MSeq;

/**
 * CharacterChromosome which represents character sequences.
 *
 * @see CharacterGene
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.1
 */
public class CharacterChromosome
	extends VariableChromosome<CharacterGene>
	implements
		CharSequence,
		Serializable
{
	@Serial
	private static final long serialVersionUID = 3L;

	private transient final CharSeq _validCharacters;

	/**
	 * Create a new chromosome from the given {@code genes} array. The genes
	 * array is copied, so changes to the given genes array doesn't effect the
	 * genes of this chromosome.
	 *
	 * @since 4.0
	 *
	 * @param genes the genes that form the chromosome.
	 * @param lengthRange the allowed length range of the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	protected CharacterChromosome(
		final ISeq<CharacterGene> genes,
		final IntRange lengthRange
	) {
		super(genes, lengthRange);
		_validCharacters = genes.get(0).validChars();
	}

	@Override
	public char charAt(final int index) {
		return get(index).charValue();
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public CharacterChromosome subSequence(final int start, final int end) {
		return new CharacterChromosome(_genes.subSeq(start, end), lengthRange());
	}

	/**
	 * @throws NullPointerException if the given gene array is {@code null}.
	 */
	@Override
	public CharacterChromosome newInstance(final ISeq<CharacterGene> genes) {
		return new CharacterChromosome(genes, lengthRange());
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public CharacterChromosome newInstance() {
		return of(_validCharacters, lengthRange());
	}

	/**
	 * Maps the gene alleles of this chromosome, given as {@code char[]} array,
	 * by applying the given mapper function {@code f}. The mapped gene values
	 * are then wrapped into a newly created chromosome.
	 *
	 * <pre>{@code
	 * final CharacterChromosome chromosome = ...;
	 * final CharacterChromosome uppercase = chromosome.map(Main::uppercase);
	 *
	 * static int[] uppercase(final int[] values) {
	 *     for (int i = 0; i < values.length; ++i) {
	 *         values[i] = Character.toUpperCase(values[i]);
	 *     }
	 *     return values;
	 * }
	 * }</pre>
	 *
	 * @since 6.1
	 *
	 * @param f the mapper function
	 * @return a newly created chromosome with the mapped gene values
	 * @throws NullPointerException if the mapper function is {@code null}.
	 * @throws IllegalArgumentException if the length of the mapped
	 *         {@code char[]} array is empty or doesn't match with the allowed
	 *         length range
	 */
	public CharacterChromosome map(final Function<? super char[], char[]> f) {
		requireNonNull(f);

		final char[] chars = f.apply(toArray());
		final var genes = IntStream.range(0, chars.length)
			.mapToObj(i -> CharacterGene.of(chars[i], _validCharacters))
			.collect(ISeq.toISeq());

		return newInstance(genes);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), hash(_validCharacters));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj != null &&
			getClass() == obj.getClass() &&
			Objects.equals(_validCharacters, ((CharacterChromosome)obj)._validCharacters) &&
			super.equals(obj);
	}

	@Override
	public String toString() {
		return new String(toArray());
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
		final char[] a = array.length >= length()
			? array
			: new char[length()];

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


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new chromosome with the {@code validCharacters} char set as
	 * valid characters.
	 *
	 * @since 4.3
	 *
	 * @param validCharacters the valid characters for this chromosome.
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws NullPointerException if the {@code validCharacters} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 */
	public static CharacterChromosome of(
		final CharSeq validCharacters,
		final IntRange lengthRange
	) {
		return new CharacterChromosome(
			CharacterGene.seq(validCharacters, lengthRange),
			lengthRange
		);
	}

	/**
	 * Create a new chromosome with the {@link CharacterGene#DEFAULT_CHARACTERS}
	 * char set as valid characters.
	 *
	 * @param lengthRange the allowed length range of the chromosome.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public static CharacterChromosome of(final IntRange lengthRange) {
		return of(DEFAULT_CHARACTERS, lengthRange);
	}

	/**
	 * Create a new chromosome with the {@code validCharacters} char set as
	 * valid characters.
	 *
	 * @since 4.3
	 *
	 * @param validCharacters the valid characters for this chromosome.
	 * @param length the {@code length} of the new chromosome.
	 * @return a new {@code CharacterChromosome} with the given parameter
	 * @throws NullPointerException if the {@code validCharacters} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the length of the gene sequence is
	 *         empty, doesn't match with the allowed length range, the minimum
	 *         or maximum of the range is smaller or equal zero or the given
	 *         range size is zero.
	 */
	public static CharacterChromosome of(
		final CharSeq validCharacters,
		final int length
	) {
		return of(validCharacters, IntRange.of(length));
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
		return of(DEFAULT_CHARACTERS, length);
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
		final MSeq<CharacterGene> genes = MSeq.ofLength(alleles.length());
		for (int i = 0; i < alleles.length(); ++i) {
			genes.set(i, CharacterGene.of(alleles.charAt(i), validChars));
		}

		return new CharacterChromosome(genes.toISeq(), IntRange.of(alleles.length()));
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

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.CHARACTER_CHROMOSOME, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(lengthRange().min(), out);
		writeInt(lengthRange().max(), out);
		writeString(_validCharacters.toString(), out);
		writeString(toString(), out);
	}

	static CharacterChromosome read(final DataInput in) throws IOException {
		final var lengthRange = IntRange.of(readInt(in), readInt(in));
		final var validCharacters = new CharSeq(readString(in));
		final var chars = readString(in);

		final MSeq<CharacterGene> values = MSeq.ofLength(chars.length());
		for (int i = 0, n = chars.length(); i <  n; ++i) {
			values.set(i, CharacterGene.of(chars.charAt(i), validCharacters));
		}

		return new CharacterChromosome(values.toISeq(), lengthRange);
	}

}
