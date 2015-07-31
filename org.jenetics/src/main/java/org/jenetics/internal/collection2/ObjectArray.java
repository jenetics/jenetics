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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ObjectArray<T> extends Array<T> {

	private final Object[] _array;
	private final int _start;
	private final int _length;

	private ObjectArray(final Object[] array, final int start, final int end) {
		_array = array;
		_start = start;
		_length = end - start;
	}

	@Override
	public void set(int index, T value) {
		_array[index + _start] = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T)_array[index + _start];
	}

	@Override
	public ObjectArray<T> slice(final int from, final int until) {
		return new ObjectArray<>(_array, from + _start, until + _start);
	}

	@Override
	public ObjectArray<T> copy() {
		final Object[] array = new Object[_length];
		System.arraycopy(_array, _start, array, 0, _length);

		return new ObjectArray<>(array, 0, _length);
	}

	public static <T> ObjectArray<T> of(final int length) {
		return new ObjectArray<>(new Object[length], 0, length);
	}

}
