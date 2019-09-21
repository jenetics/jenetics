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
package io.jenetics.util;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 5.0
 */
final class Buffer<T> {
	private final Object[] _buffer;

	private int _index;
	private int _size;

	Buffer(final int capacity) {
		_buffer = new Object[capacity];
	}

	void add(final T value) {
		_buffer[_index] = value;

		if (++_index == _buffer.length) {
			_index = 0;
		}
		if (_size < _buffer.length) {
			++_size;
		}
	}

	int index() {
		return _index;
	}

	void addAll(final Iterable<? extends T> values) {
		for (T value : values) {
			add(value);
		}
	}

	@SuppressWarnings("unchecked")
	ISeq<T> toSeq() {
		final MSeq<T> seq = MSeq.ofLength(_size);
		final int start = (_buffer.length + _index - _size)%_buffer.length;

		for (int i = 0; i < _size; ++i) {
			seq.set(i, (T)_buffer[(start + i)%_buffer.length]);
		}

		return seq.toISeq();
	}

}
