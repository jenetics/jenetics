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

import org.jenetics.internal.util.ArrayProxy;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-18 $</em>
 * @since 3.0
 */
final class CharArrayProxy extends ArrayProxy<Character> {
	private static final long serialVersionUID = 1L;

	char[] _characters;
	boolean _sealed = false;

	CharArrayProxy(final char[] characters, final int start, final int end) {
		super(start, end);
		_characters = characters;
	}

	CharArrayProxy(final char[] characters) {
		this(characters, 0, characters.length);
	}

	CharArrayProxy(final int length) {
		this(new char[length], 0, length);
	}

	@Override
	public Character __get(int absoluteIndex) {
		return _characters[absoluteIndex];
	}

	@Override
	public void __set(int absoluteIndex, Character value) {
		_characters[absoluteIndex] = value;
	}

	@Override
	public CharArrayProxy slice(int from, int until) {
		return new CharArrayProxy(_characters, from + _start, until + _start);
	}

	@Override
	public void cloneIfSealed() {
		if (_sealed) {
			_characters = _characters.clone();
			_sealed = false;
		}
	}

	@Override
	public CharArrayProxy seal() {
		_sealed = true;
		return new CharArrayProxy(_characters, _start, _end);
	}

	@Override
	public CharArrayProxy copy() {
		final CharArrayProxy proxy = new CharArrayProxy(_length);
		System.arraycopy(_characters, _start, proxy._characters, 0, _length);
		return proxy;
	}
}
