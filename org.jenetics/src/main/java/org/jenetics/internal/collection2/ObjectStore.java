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
package org.jenetics.internal.collection2;

import static java.lang.String.format;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObjectStore<T> extends Array.Store<T> {

	private final Object[] _array;
	private final int _start;
	private final int _length;

	private ObjectStore(final Object[] array, final int start, final int end) {
		if (start < 0 || end > array.length || start > end) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Start index %s and/or end index %s are out of bounds [0, %s)",
				start, end, array.length
			));
		}

		_array = array;
		_start = start;
		_length = end - start;
	}

	@Override
	public void set(final int i, T value) {
		_array[i + _start] = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(final int i) {
		return (T)_array[i + _start];
	}

	@Override
	public ObjectStore<T> slice(final int from, final int until) {
		return new ObjectStore<>(_array, from + _start, until + _start);
	}

	@Override
	public ObjectStore<T> copy() {
		final Object[] array = new Object[_length];
		System.arraycopy(_array, _start, array, 0, _length);

		return new ObjectStore<>(array, 0, _length);
	}

	@Override
	public int length() {
		return _length;
	}

	public static <T> ObjectStore<T> of(final int length) {
		return new ObjectStore<>(new Object[length], 0, length);
	}

}
