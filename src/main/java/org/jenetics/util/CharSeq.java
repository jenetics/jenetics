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
package org.jenetics.util;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.PatternSyntaxException;

import javolution.lang.Immutable;

/**
 * This class is used for holding the valid characters of an
 * {@link org.jenetics.CharacterGene}. It is not a character sequence in the
 * classical sense. The characters of this sequence are sorted and doesn't
 * contain duplicate values, like a set.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class CharSeq
	extends AbstractCharSeq
	implements
		CharSequence,
		ISeq<Character>,
		Comparable<CharSeq>,
		Immutable,
		Serializable
{
	private static final long serialVersionUID = 2L;

	/**
	 * Create a new (distinct) CharSeq from the given {@code characters}. The
	 * given {@link CharSequence} is sorted and duplicate values are removed
	 *
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSeq(final CharSequence characters) {
		super(toCharArray(characters));
	}

	private static char[] toCharArray(final CharSequence characters) {
		nonNull(characters, "Characters");

		final char[] chars = new char[characters.length()];
		for (int i = 0; i < characters.length(); ++i) {
			chars[i] = characters.charAt(i);
		}

		return distinct(chars);
	}

	/**
	 * Create a new (distinct) CharSeq from the given {@code characters}. The
	 * given {@link CharSequence} is sorted and duplicate values are removed
	 *
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSeq(final char[] characters) {
		super(distinct(characters.clone()));
	}

	private static char[] distinct(final char[] chars) {
		char[] result = chars;

		if (chars.length > 0) {
			Arrays.sort(result);

			int nextIndex = 0;
			int count = 1;
			char last = result[0];

			for (int i = 1; i < result.length; ++i) {
				while (nextIndex < result.length && result[nextIndex] == last) {
					++nextIndex;
				}
				if (nextIndex < result.length) {
					last = result[nextIndex];
					result[i] = last;
					++count;
				}
			}

			char[] array = new char[count];
			System.arraycopy(result, 0, array, 0, count);
			result = array;
		}

		return result;
	}

	@Override
	public boolean contains(final Object object) {
		if (object instanceof Character) {
			return contains((Character)object);
		} else {
			return false;
		}
	}

	/**
	 * Test whether this character set contains the given character {@code c}.
	 *
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 * 		  {@code false} otherwise.
	 * @throws NullPointerException if the given character {@code c} is
	 * 		  {@code null}.
	 */
	public boolean contains(final Character c) {
		return contains(c.charValue());
	}

	/**
	 * Test whether this character set contains the given character {@code c}.
	 *
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 * 		  {@code false} otherwise.
	 */
	public boolean contains(final char c) {
		return Arrays.binarySearch(_characters, c) >= 0;
	}

	@Override
	public char charAt(int index) {
		return _characters[index];
	}

	@Override
	public int length() {
		return _characters.length;
	}

	@Override
	public CharSeq subSequence(int start, int end) {
		return new CharSeq(new String(_characters, start, end - start));
	}

	/**
	 * Test whether this character set is empty.
	 *
	 * @return {@code true} if this character set is empty, {@code false}
	 * 		  otherwise.
	 */
	public boolean isEmpty() {
		return _characters.length == 0;
	}

	@Override
	public Iterator<Character> iterator() {
		return new Iterator<Character>() {
			private int _pos = 0;
			@Override public boolean hasNext() {
				return _pos < _characters.length;
			}
			@Override public Character next() {
				if (!hasNext()) {
					throw new NoSuchElementException(String.format(
							"Index %s is out of range [0, %s)",
							_pos, _characters.length
						));
				}
				return _characters[_pos++];
			}
			@Override public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_characters).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof CharSeq)) {
			return false;
		}

		final CharSeq ch = (CharSeq)object;
		return eq(_characters, ch._characters);
	}

	@Override
	public int compareTo(final CharSeq set) {
		int result = 0;

		final int n = Math.min(_characters.length, set._characters.length);
		for (int i = 0; i < n && result == 0; ++i) {
			result = _characters[i] - set._characters[i];
		}
		if (result == 0) {
			result = _characters.length - set._characters.length;
		}

		return result;
	}

	@Override
	public String toString() {
		return new String(_characters);
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
		nonNull(pattern, "Pattern");
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
				c = (char) (c - 1);
			}
		}

		return out.toString();
	}

	/**
	 * Expands the character range for the given {@code pattern}. E.g
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
	public static CharSeq valueOf(final CharSequence pattern) {
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
	public static CharSeq valueOf(final char a, final char b) {
		return new CharSeq(expand(a, b));
	}

}




