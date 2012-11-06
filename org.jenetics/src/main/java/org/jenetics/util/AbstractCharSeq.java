/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
abstract class AbstractCharSeq extends ArrayISeq<Character> {
	private static final long serialVersionUID = 1L;

	final char[] _characters;

	AbstractCharSeq(final char[] characters) {
		super(toArrayRef(characters), 0, characters.length);
		_characters = characters;
	}

	private static ArrayRef toArrayRef(final char[] characters) {
		final Object[] values = new Object[characters.length];
		for (int i = 0; i < characters.length; ++i) {
			values[i] = characters[i];
		}
		final ArrayRef ref = new ArrayRef(values);
		ref._sealed = true;

		return ref;
	}


}
