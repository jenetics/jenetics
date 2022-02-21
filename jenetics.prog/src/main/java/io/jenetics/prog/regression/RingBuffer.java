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
package io.jenetics.prog.regression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 6.0
 */
final class RingBuffer {

	private final Object[] _buffer;

	private int _cursor = -1;
	private int _size = 0;

	private Object[] _snapshot = null;

	RingBuffer(final int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException(
				"Buffer size must be a positive: " + capacity
			);
		}

		_buffer = new Object[capacity];
	}

	synchronized void add(final Object element) {
		_buffer[next()] = element;
		_snapshot = null;
	}

	private int next() {
		if (_size < _buffer.length) {
			++_size;
		}
		return _cursor = (_cursor + 1 < _buffer.length) ? _cursor + 1 : 0;
	}

	synchronized void addAll(final Collection<?> elements) {
		final Iterator<?> it = elements.iterator();
		if (elements.size() > capacity()) {
			for (int i = capacity(); i < elements.size(); ++i) {
				it.next();
			}
		}

		while (it.hasNext()) {
			_buffer[next()] = it.next();
		}
		_snapshot = null;
	}

	synchronized Object[] snapshot() {
		if (_snapshot != null) {
			return _snapshot;
		}

		final Object[] result = new Object[_size];
		if (_size < _buffer.length) {
			System.arraycopy(_buffer, 0, result, 0, _size);
		} else {
			System.arraycopy(
				_buffer, _cursor + 1,
				result, 0, _buffer.length - _cursor - 1
			);
			System.arraycopy(
				_buffer, 0,
				result, _buffer.length - _cursor - 1, _cursor + 1
			);
		}

		return _snapshot = result;
	}

	/**
	 * Removes all the elements from {@code this} ring-buffer. The buffer
	 * will be empty after this method returns.
	 */
	void clear() {
		Arrays.fill(_buffer, null);
		_cursor = -1;
		_size = 0;
		_snapshot = null;
	}

	/**
	 * Return the capacity of {@code this} ring-buffer.
	 *
	 * @return the capacity of {@code this} ring-buffers
	 */
	int capacity() {
		return _buffer.length;
	}

	/**
	 * Return the size of {@code this} ring-buffer, where {@code size <= capacity}
	 * is always true.
	 *
	 * @return the capacity of {@code this} ring-buffers
	 */
	int size() {
		return _size;
	}
}
