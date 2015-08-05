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
package org.jenetics.internal.collection;

import org.jenetics.internal.collection2.Array;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class CharArrayProxy implements Array.Store<Character> {
	private static final long serialVersionUID = 1L;

	public final char[] _array;

	public CharArrayProxy(final char[] chars) {
		_array = chars;
	}

	public CharArrayProxy(final int length) {
		this(new char[length]);
	}

	@Override
	public Character get(final int index) {
		return _array[index];
	}

	@Override
	public void set(final int index, final Character value) {
		_array[index] = value;
	}

	@Override
	public CharArrayProxy copy(final int from, final int until) {
		final char[] array = new char[until - from];
		System.arraycopy(_array, from, array, 0, until - from);
		return new CharArrayProxy(array);
	}

	@Override
	public int length() {
		return _array.length;
	}

}
