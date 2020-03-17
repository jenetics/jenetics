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

import java.util.Collection;

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

	RingBuffer(final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Buffer size must be a positive: " + size
			);
		}

		_buffer = new Object[size];
	}

	synchronized void add(final Object element) {
		_buffer[next()] = element;
		_snapshot = null;
	}

	private int next() {
		if (_size < _buffer.length) ++_size;
		return _cursor = (_cursor + 1 < _buffer.length) ? _cursor + 1 : 0;
	}

	synchronized void addAll(final Collection<?> elements) {
		for (Object element : elements) {
			_buffer[next()] = element;
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

	int size () {
		return _buffer.length;
	}
}
