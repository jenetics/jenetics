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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.PatternSyntaxException;

import javolution.lang.Immutable;


/**
 * Helper class holding the valid characters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharSet.java 333 2010-02-16 20:42:29Z fwilhelm $
 */
public class CharSet 
	implements CharSequence, Iterable<Character>, Immutable, Serializable 
{
	private static final long serialVersionUID = -1170134682688418212L;
	
	private final char[] _characters;
	
	/**
	 * Create a new CharSet from the given {@code characters}.
	 * 
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSet(final CharSequence characters) {
		Validator.nonNull(characters, "Characters");
		
		_characters = new char[characters.length()];
		for (int i = 0; i < characters.length(); ++i) {
			_characters[i] = characters.charAt(i);
		}
		Arrays.sort(_characters);
	}
	
	/**
	 * Create a new CharSet from the given {@code characters}.
	 * 
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are {@code null}.
	 */
	public CharSet(final char[] characters) {
		_characters = characters.clone();
		Arrays.sort(_characters);
	}
	
	/**
	 * Test whether this character set contains the given character {@code c}.
	 * 
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 *         {@code false} otherwise.
	 * @throws NullPointerException if the given character {@code c} is 
	 *         {@code null}.
	 */
	public boolean contains(final Character c) {
		return contains(c.charValue());
	}
	
	/**
	 * Test whether this character set contains the given character {@code c}.
	 * 
	 * @param c the character to test.
	 * @return {@code true} if this character set contains the given character,
	 *         {@code false} otherwise.
	 */
	public boolean contains(final char c) {
		return Arrays.binarySearch(_characters, c) != -1;
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
	public CharSet subSequence(int start, int end) {
		return new CharSet(new String(_characters, start, end - start));
	}
	
	/**
	 * Test whether this character set is empty.
	 * 
	 * @return {@code true} if this character set is empty, {@code false} 
	 *         otherwise.
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
		return Arrays.hashCode(_characters)*17 + 31;
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof CharSet)) {
			return false;
		}
		
		final CharSet ch = (CharSet)object;
		return Arrays.equals(_characters, ch._characters);
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
		Validator.nonNull(pattern, "Pattern");
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
	
				final String range = expand(pattern.charAt(i - 1), pattern.charAt(i + 1));
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
	public static CharSet valueOf(final CharSequence pattern) {
		return new CharSet(expand(pattern));
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
	public static CharSet valueOf(final char a, final char b) {
		return new CharSet(expand(a, b));
	}

}




