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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collector;

import org.jenetics.internal.collection.Array;
import org.jenetics.internal.collection.ArrayISeq;
import org.jenetics.internal.collection.CharStore;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * This class is used for holding the valid characters of an
 * {@link org.jenetics.CharacterGene}. It is not a character sequence in the
 * classical sense. The characters of this sequence are sorted and doesn't
 * contain duplicate values, like a set.
 *
 * <pre>{@code
 * final CharSeq cs1 = new CharSeq("abcdeaafg");
 * final CharSeq cs2 = new CharSeq("gfedcbabb");
 * assert(cs1.equals(cs2));
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
public final class CharSeq
	extends CharSeqBase
	implements
		CharSequence,
		ISeq<Character>,
		Comparable<CharSeq>,
		Serializable
{
	private static final long serialVersionUID = 2L;

	/**
	 * Create a new (distinct) CharSeq from the given {@code characters}. The
	 * given {@link CharSequence} is sorted and duplicate values are removed
	 *
	 * @see #CharSeq(CharSequence)
	 *
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSeq(final char[] characters) {
		super(distinct(characters.clone()));
	}

	/**
	 * Create a new (distinct) CharSeq from the given {@code characters}. The
	 * given {@link CharSequence} is sorted and duplicate values are removed.
	 *
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSeq(final CharSequence characters) {
		super(distinct(toCharArray(characters)));
	}

	private static char[] toCharArray(final CharSequence characters) {
		requireNonNull(characters, "Characters");

		final char[] chars = new char[characters.length()];
		for (int i = chars.length; --i >= 0;) {
			chars[i] = characters.charAt(i);
		}

		return chars;
	}

	private static char[] distinct(final char[] chars) {
		Arrays.sort(chars);

		int j = 0;
		for (int i = 1; i < chars.length; ++i) {
			if (chars[j] != chars[i]) {
				chars[++j] = chars[i];
			}
		}

		final int size = Math.min(chars.length, j + 1);
		final char[] array = new char[size];
		System.arraycopy(chars, 0, array, 0, size);
		return array;
	}

	@Override
	public boolean contains(final Object object) {
		return object instanceof Character && contains((Character)object);
	}

	/**
	 * Test whether this character set contains the given character {@code c}.
	 *
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 *          {@code false} otherwise.
	 * @throws NullPointerException if the given character {@code c} is
	 *          {@code null}.
	 */
	public boolean contains(final Character c) {
		return contains(c.charValue());
	}

	/**
	 * Test whether this character set contains the given character {@code c}.
	 *
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 *          {@code false} otherwise.
	 */
	public boolean contains(final char c) {
		return Arrays.binarySearch(array, c) >= 0;
	}

	@Override
	public char charAt(final int index) {
		return array[index];
	}

	@Override
	public int length() {
		return array.length;
	}

	@Override
	public CharSeq subSequence(final int start, final int end) {
		return new CharSeq(new String(array, start, end - start));
	}

	/**
	 * Test whether this character set is empty.
	 *
	 * @return {@code true} if this character set is empty, {@code false}
	 *          otherwise.
	 */
	public boolean isEmpty() {
		return array.length == 0;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(array).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(ch ->
			eq(array, ch.array)
		);
	}

	@Override
	public int compareTo(final CharSeq set) {
		int result = 0;

		final int n = Math.min(array.length, set.array.length);
		for (int i = 0; i < n && result == 0; ++i) {
			result = array[i] - set.array[i];
		}
		if (result == 0) {
			result = array.length - set.array.length;
		}

		return result;
	}

	@Override
	public String toString() {
		return new String(array);
	}

	/**
	 * Expands the character range for the given {@code pattern}. E.g
	 * {@code a-zA-Z0-1} will return a string containing all upper and lower
	 * case characters (from a to z) and all digits form 0 to 9.
	 *
	 * @param pattern the {@code pattern} to expand.
	 * @return the expanded pattern.
	 * @throws PatternSyntaxException if the pattern could not be expanded.
	 * @throws NullPointerException if the pattern is {@code null}.
	 */
	public static String expand(final CharSequence pattern) {
		requireNonNull(pattern, "Pattern");
		final StringBuilder out = new StringBuilder();

		for (int i = 0, n = pattern.length(); i < n; ++i) {
			if (pattern.charAt(i) == '\\') {
				++i;
				if (i < pattern.length()) {
					out.append(pattern.charAt(i));
				}
			} else if (pattern.charAt(i) == '-') {
				if (i <= 0 || i >= (pattern.length() - 1)) {
					throw new PatternSyntaxException(
						"Dangling range operator '-'", pattern.toString(),
						pattern.length() - 1
					);
				}

				final String range = expand(
					pattern.charAt(i - 1),
					pattern.charAt(i + 1)
				);
				out.append(range);

				++i;
			} else if (i + 1 == n || pattern.charAt(i + 1) != '-') {
				out.append(pattern.charAt(i));
			}
		}

		return out.toString();
	}

	/**
	 * Expands the characters between {@code a} and {@code b}.
	 *
	 * @param a the start character.
	 * @param b the stop character.
	 * @return the expanded characters.
	 */
	public static String expand(final char a, final char b) {
		final StringBuilder out = new StringBuilder();

		if (a < b) {
			char c = a;
			while (c <= b) {
				out.append(c);
				c = (char) (c + 1);
			}
		} else if (a > b) {
			char c = a;
			while (c >= b) {
				out.append(c);
				c = (char)(c - 1);
			}
		}

		return out.toString();
	}

	/**
	 * Expands the character range for the given {@code pattern}. E.g.
	 * {@code a-zA-Z0-1} will return a string containing all upper and lower
	 * case characters (from a to z) and all digits form 0 to 9.
	 *
	 * @see #expand(CharSequence)
	 *
	 * @param pattern the {@code pattern} to expand.
	 * @return the expanded pattern.
	 * @throws PatternSyntaxException if the pattern could not be expanded.
	 * @throws NullPointerException if the pattern is {@code null}.
	 */
	public static CharSeq of(final CharSequence pattern) {
		return new CharSeq(expand(pattern));
	}

	/**
	 * Expands the characters between {@code a} and {@code b}.
	 *
	 * @see #expand(char, char)
	 *
	 * @param a the start character.
	 * @param b the stop character.
	 * @return the expanded characters.
	 */
	public static CharSeq of(final char a, final char b) {
		return new CharSeq(expand(a, b));
	}

	/**
	 * Helper method for creating a sequence of characters from the given
	 * {@code CharSequence}. The returned sequence will contain all characters
	 * in the original order.
	 *
	 * @param chars the char sequence to convert.
	 * @return a sequence which will contain all given chars in the original
	 *         order.
	 */
	public static ISeq<Character> toISeq(final CharSequence chars) {
		final MSeq<Character> seq = MSeq.ofLength(chars.length());
		for (int i = 0; i < chars.length(); ++i) {
			seq.set(i, chars.charAt(i));
		}

		return seq.toISeq();
	}

	public static Collector<Character, ?, CharSeq> toCharSeq() {
		return Collector.of(
			StringBuilder::new,
			StringBuilder::append,
			(a, b) -> {a.append(b); return a;},
			CharSeq::new
		);
	}

}

abstract class CharSeqBase extends ArrayISeq<Character> {
	private static final long serialVersionUID = 1L;

	final char[] array;

	protected CharSeqBase(final char[] characters) {
		super(Array.of(CharStore.of(characters)).seal());
		array = characters;
	}
}
