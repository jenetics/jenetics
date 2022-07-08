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
package io.jenetics.ext.internal.util;

import static java.lang.Math.max;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOf;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Resizable-int array implementation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.3
 * @since 4.1
 */
public final class IntList {

	private static final int MAX_SIZE = Integer.MAX_VALUE - 8;
	private static final int DEFAULT_CAPACITY = 10;
	private static final int[] EMPTY_ARRAY = {};
	private static final int[] DEFAULT_EMPTY_ARRAY = {};

	private int[] _data;
	private int _size;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param  capacity  the initial capacity of the list
	 * @throws IllegalArgumentException if the specified initial capacity
	 *         is negative
	 */
	public IntList(final int capacity) {
		if (capacity > 0) {
			_data = new int[capacity];
		} else if (capacity == 0) {
			_data = EMPTY_ARRAY;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: "+ capacity);
		}
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public IntList() {
		_data = DEFAULT_EMPTY_ARRAY;
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param  index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0 || index > size())}
	 */
	public int get(final int index) {
		rangeCheck(index);

		return _data[index];
	}

	/**
	 * Performs the given action for each element of the list.
	 *
	 * @param action the action to be performed for each element
	 * @throws NullPointerException if the specified action is {@code null}
	 */
	public void forEach(final IntConsumer action) {
		requireNonNull(action);

		final int size = _size;
		for (int i = 0; i < size; ++i) {
			action.accept(_data[i]);
		}
	}

	/**
	 * Returns a sequential {@link IntStream} with the specified list as its
	 * source.
	 *
	 * @return a sequential {@link IntStream}
	 */
	public IntStream stream() {
		return Arrays.stream(_data, 0, _size);
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param element element to be appended to this list
	 */
	public void add(final int element) {
		ensureSize(_size + 1);
		_data[_size++] = element;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 *
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0 || index > size())}
	 */
	public void add(final int index, final int element) {
		addRangeCheck(index);

		ensureSize(_size + 1);
		arraycopy(
			_data, index,
			_data, index + 1,
			_size - index
		);
		_data[index] = element;
		_size++;
	}

	/**
	 * Appends all of the elements in the specified array to the end of this
	 * list.
	 *
	 * @param elements array containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws NullPointerException if the specified array is null
	 */
	public boolean addAll(final int[] elements) {
		final int count = elements.length;
		ensureSize(_size + count);
		arraycopy(elements, 0, _data, _size, count);
		_size += count;

		return count != 0;
	}

	/**
	 * Inserts all the elements in the specified array into this list, starting
	 * at the specified position.
	 *
	 * @param index index at which to insert the first element from the
	 *              specified collection
	 * @param elements collection containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0 || index > size())}
	 * @throws NullPointerException if the specified array is null
	 */
	public boolean addAll(final int index, final int[] elements) {
		addRangeCheck(index);

		final int count = elements.length;
		ensureSize(_size + count);

		final int moved = _size - index;
		if (moved > 0) {
			arraycopy(_data, index, _data, index + count, moved);
		}

		arraycopy(elements, 0, _data, index, count);
		_size += count;
		return count != 0;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear() {
		_size = 0;
	}

	/**
	 * Trims the capacity of this {@code ArrayList} instance to be the list's
	 * current size.  An application can use this operation to minimize the
	 * storage of an {@code ArrayList} instance.
	 */
	public void trimToSize() {
		if (_size < _data.length) {
			_data = _size == 0
				? EMPTY_ARRAY
				: copyOf(_data, _size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return _size;
	}

	/**
	 * Return {@code true} if the list is empty.
	 *
	 * @return {@code true} if the list is empty, {@code false} otherwise
	 */
	public boolean isEmpty() {
		return _size == 0;
	}

	/**
	 * Return the current elements as int array.
	 *
	 * @return the current elements as int array
	 */
	public int[] toArray() {
		return copyOf(_data, _size);
	}

	private void ensureSize(int size) {
		ensureExplicitSize(capacity(_data, size));
	}

	private void ensureExplicitSize(int size) {
		if (size - _data.length > 0) {
			grow(size);
		}
	}

	private void rangeCheck(final int index) {
		if (index >= _size)
			throw new IndexOutOfBoundsException(format(
				"Index: %d, Size: %d", index, _size
			));
	}

	private void addRangeCheck(int index) {
		if (index > _size || index < 0)
			throw new IndexOutOfBoundsException(format(
				"Index: %d, Size: %d", index, _size
			));
	}

	private static int capacity(final int[] data, final int capacity) {
		if (data == DEFAULT_EMPTY_ARRAY) {
			return max(DEFAULT_CAPACITY, capacity);
		}
		return capacity;
	}

	private void grow(final int size) {
		final int oldSize = _data.length;

		int newSize = oldSize + (oldSize >> 1);
		if (newSize - size < 0) {
			newSize = size;
		}
		if (newSize - MAX_SIZE > 0) {
			newSize = hugeCapacity(size);
		}

		_data = copyOf(_data, newSize);
	}

	private static int hugeCapacity(final int minCapacity) {
		if (minCapacity < 0) {
			throw new OutOfMemoryError();
		}

		return minCapacity > MAX_SIZE
			? Integer.MAX_VALUE
			: MAX_SIZE;
	}

}
