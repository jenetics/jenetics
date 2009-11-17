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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javolution.lang.Immutable;


/**
 * Helper class holding the valid characters.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: CharSet.java,v 1.3 2009-11-17 20:14:28 fwilhelm Exp $
 */
public class CharSet 
	implements CharSequence, Iterable<Character>, Immutable, Serializable 
{
	private static final long serialVersionUID = -1170134682688418212L;
	
	private final String _characters;
	private final Set<Character> _characterSet = new LinkedHashSet<Character>();
	
	/**
	 * Create a new CharSet from the given {@code characters}.
	 * 
	 * @param characters the characters.
	 * @throws NullPointerException if the {@code characters} are null.
	 */
	public CharSet(final CharSequence characters) {
		Validator.notNull(characters, "Characters");
		
		for (int i = 0; i < characters.length(); ++i) {
			_characterSet.add(characters.charAt(i));
		}
		
		final StringBuilder builder = new StringBuilder();
		for (Character c : _characterSet) {
			builder.append(c);
		}
		_characters = builder.toString();
	}
	
	public boolean contains(final Character c) {
		return _characterSet.contains(c);
	}

	@Override
	public char charAt(int index) {
		return _characters.charAt(index);
	}

	@Override
	public int length() {
		return _characters.length();
	}

	@Override
	public CharSet subSequence(int start, int end) {
		return new CharSet(_characters.substring(start, end));
	}
	
	public boolean isEmpty() {
		return _characterSet.isEmpty();
	}
	
	@Override
	public Iterator<Character> iterator() {
		return _characterSet.iterator();
	}
	
	@Override
	public int hashCode() {
		return _characters.hashCode()*17 + 31;
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
		return ch._characters.equals(_characters);
	}
	
	@Override
	public String toString() {
		return _characters;
	}
	
	/**
	 * Expands the character range for the given {@code pattern}. E.g 
	 * {@code a-zA-Z0-1} will return a string containing all upper and lower
	 * case characters (from a to z) and all digits form 0 to 9.
	 * 
	 * @param pattern the {@code pattern} to expand.
	 * @return the expanded pattern.
	 * @throws PatternSyntaxException if the pattern could not be expanded.
	 * @throws NullPointerException if the patten is {@code null}.
	 */
	public static String expand(final CharSequence pattern)
		throws PatternSyntaxException 
	{
		Validator.notNull(pattern, "Pattern");
		final StringBuilder out = new StringBuilder();
	
		for (int i = 0, n = pattern.length(); i < n; ++i) {
			if (pattern.charAt(i) == '\\') {
				++i;
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

}
