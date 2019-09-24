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

import static java.lang.System.arraycopy;

import java.util.function.IntFunction;

import io.jenetics.internal.collection.Array;
import io.jenetics.internal.collection.ArrayMSeq;
import io.jenetics.internal.collection.ObjectStore;

/**
 * Ring buffer implementation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 5.0
 */
final class Buffer<T> {
	private final Object[] _buffer;

	private int _index;
	private int _size;

	/**
	 * Create a new ring buffer with the given {@code capacity}.
	 *
	 * @param capacity the buffer capacity
	 */
	private Buffer(final int capacity) {
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

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @return the buffer snapshot
	 */
	Object[] toArray() {
		return toArray(Object[]::new);
	}

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @param generator a function which produces a new array of the desired
	 *        type and the provided length
	 * @param <A> the element type of the resulting array
	 * @return the buffer snapshot
	 */
	<A> A[] toArray(final IntFunction<A[]> generator) {
		final A[] result = generator.apply(_size);
		if (_size < _buffer.length || _index == 0) {
			arraycopy(_buffer, 0, result, 0, _size);
		} else {
			arraycopy(_buffer, _index, result, 0, _buffer.length - _index);
			arraycopy(_buffer, 0, result, _buffer.length - _index, _index);
		}

		return result;
	}

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @return the buffer snapshot
	 */
	ISeq<T> toSeq() {
		return new ArrayMSeq<T>(Array.of(ObjectStore.of(toArray()))).toISeq();
	}

	/**
	 * Create a new ring buffer with the given {@code capacity}.
	 *
	 * @param capacity the buffer capacity
	 * @return a new ring buffer with the given capacity
	 */
	static <T> Buffer<T> ofCapacity(final int capacity) {
		return new Buffer<T>(capacity);
	}

}
