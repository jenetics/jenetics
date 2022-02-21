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

import static java.lang.Math.min;
import static java.lang.System.arraycopy;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntFunction;

import io.jenetics.internal.collection.Array;
import io.jenetics.internal.collection.ArrayMSeq;
import io.jenetics.internal.collection.ObjectStore;

/**
 * This class is a bounded buffer, which can store only a given amount of
 * elements. If the buffer is full, it starts <em>overwriting</em> previously
 * inserted elements at the beginning of the buffer. A <em>full</em> buffer
 * neither blocks the insertion of new elements, nor does it throw an exception.
 * <p>
 * If you want to access the elements of the buffer, you have to take a
 * <em>snapshot</em> of the current content. This can be done with the
 * {@link #toArray()}, {@link #toArray(IntFunction)} and {@link #toSeq()} methods.
 *
 * @implNote
 * This class is not thread-safe. If two threads accesses the buffer
 * concurrently it must be <em>synchronized</em> externally.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.0
 */
final class Buffer<T> implements Iterable<T> {
	private final Object[] _buffer;

	private int _index;
	private int _size;

	/**
	 * Create a new ring buffer with the given {@code capacity}.
	 *
	 * @param capacity the buffer capacity
	 * @throws NegativeArraySizeException if the given {@code capacity} is
	 *         negative
	 */
	private Buffer(final int capacity) {
		_buffer = new Object[capacity];
	}

	/**
	 * Return the capacity of {@code this} buffer. This is the number of elements
	 * the buffer is able to store.
	 *
	 * @return the capacity of {@code this} buffer
	 */
	public int capacity() {
		return _buffer.length;
	}

	/**
	 * Return the current number of elements {@code this} buffer holds.
	 *
	 * @return the current buffer size
	 */
	public int size() {
		return _size;
	}

	/**
	 * Returns {@code true} if this buffer contains no elements.
	 *
	 * @return {@code true} if this buffer contains no elements
	 */
	public boolean isEmpty() {
		return _size == 0;
	}

	/**
	 * Add a new element to the buffer.
	 *
	 * @param value the value to add
	 */
	public void add(final T value) {
		_buffer[_index] = value;

		if (++_index == _buffer.length) {
			_index = 0;
		}
		if (_size < _buffer.length) {
			++_size;
		}
	}

	/**
	 * Add the given {@code values} to the buffer.
	 *
	 * @param values the values to add
	 * @throws NullPointerException if the given parameter is {@code null}
	 */
	@SafeVarargs
	public final void addAll(final T... values) {
		addAll(values, 0, values.length);
	}

	/**
	 * Add the given values to the ring buffer.
	 *
	 * @param values the values being added to the ring buffer
	 * @throws NullPointerException if the given parameter is {@code null}
	 */
	public void addAll(final Iterable<? extends T> values) {
		if (values instanceof Buffer<?> buff) {
			final Object[] array = buff.toArray();
			addAll(array, 0, array.length);
		} else if (values instanceof Collection<?> coll) {
			final Object[] array = coll.toArray();
			addAll(array, 0, array.length);
		} else {
			for (T value : values) {
				add(value);
			}
		}
	}

	/**
	 * Add the {@code values} of the given array to {@code this} buffer. The
	 * values are added at the given {@code start} index and the given
	 * {@code length}.
	 *
	 * @param values the array which contains the values to add
	 * @param start the start index of the source array
	 * @param length the number of elements to copy
	 * @throws IndexOutOfBoundsException if copying would cause access of data
	 *         outside array bounds
	 * @throws ArrayStoreException if an element in the {@code value} array
	 *         could not be stored into the dest array because of a type mismatch
	 */
	private void addAll(final Object[] values, final int start, final int length) {
		if (length >= _buffer.length) {
			arraycopy(
				values, values.length - _buffer.length + start,
				_buffer, 0, _buffer.length
			);
			_size = _buffer.length;
			_index = 0;
		} else {
			final int remaining = _buffer.length - _index;
			_size = min(_size + length, _buffer.length);

			if (length <= remaining) {
				arraycopy(values, start, _buffer, _index, length);
				_index += length;
			} else {
				arraycopy(values, start, _buffer, _index, remaining);
				arraycopy(
					values, remaining + start,
					_buffer, 0, length - remaining
				);
				_index = length - remaining;
			}
		}
	}

	/**
	 * Return an iterator from an element snapshot, taken with the {@link #toSeq()}
	 * method.
	 *
	 * @return a new iterator from a snapshot of the current elements
	 */
	@Override
	public Iterator<T> iterator() {
		return toSeq().iterator();
	}

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @return the buffer snapshot
	 */
	public Object[] toArray() {
		return toArray(Object[]::new);
	}

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @param generator a function which produces a new array of the desired
	 *        type and the provided length
	 * @param <A> the element type of the resulting array
	 * @return the buffer snapshot
	 * @throws ArrayStoreException if an element in the {@code value} array
	 *         could not be stored into the dest array because of a type mismatch
	 * @throws NullPointerException if the given {@code generator} is {@code null}
	 */
	public <A> A[] toArray(final IntFunction<A[]> generator) {
		final A[] result = generator.apply(_size);
		copyTo(result);
		return result;
	}

	private <A> void copyTo(final A[] array) {
		assert array.length >= _size;

		if (_size < _buffer.length || _index == 0) {
			arraycopy(_buffer, 0, array, 0, _size);
		} else {
			arraycopy(_buffer, _index, array, 0, _buffer.length - _index);
			arraycopy(_buffer, 0, array, _buffer.length - _index, _index);
		}
	}

	/**
	 * Return a snapshot of the current buffer content.
	 *
	 * @return the buffer snapshot
	 */
	public ISeq<T> toSeq() {
		return isEmpty()
			? ISeq.empty()
			: new ArrayMSeq<T>(Array.of(ObjectStore.of(toArray()))).toISeq();
	}

	@Override
	public String toString() {
		return toSeq().toString();
	}

	/**
	 * Create a new ring buffer with the given {@code capacity}.
	 *
	 * @param <T> the element type
	 * @param capacity the buffer capacity
	 * @return a new ring buffer with the given capacity
	 * @throws NegativeArraySizeException if the given {@code capacity} is
	 *         negative
	 */
	public static <T> Buffer<T> ofCapacity(final int capacity) {
		return new Buffer<>(capacity);
	}

}
