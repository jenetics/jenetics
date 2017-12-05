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
package io.jenetics.ext.internal;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Resizable-int array implementation
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class IntList {

	private static final int MAX_SIZE = Integer.MAX_VALUE - 8;
	private static final int DEFAULT_CAPACITY = 10;
	private static final int[] EMPTY_DATA = {};
	private static final int[] DEFAULT_EMPTY_DATA = {};

	private int[] _data;
	private int _size;
	private int _modCount = 0;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param  initialCapacity  the initial capacity of the list
	 * @throws IllegalArgumentException if the specified initial capacity
	 *         is negative
	 */
	public IntList(int initialCapacity) {
		if (initialCapacity > 0) {
			_data = new int[initialCapacity];
		} else if (initialCapacity == 0) {
			_data = EMPTY_DATA;
		} else {
			throw new IllegalArgumentException(
				"Illegal Capacity: "+ initialCapacity
			);
		}
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public IntList() {
		_data = DEFAULT_EMPTY_DATA;
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

		final int expectedModCount = _modCount;
		final int size = _size;
		for (int i = 0; _modCount == expectedModCount && i < size; i++) {
			action.accept(_data[i]);
		}
		if (_modCount != expectedModCount) {
			throw new ConcurrentModificationException();
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
		ensureCapacity(_size + 1);
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
		rangeCheckForAdd(index);

		ensureCapacity(_size + 1);
		System.arraycopy(
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
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws NullPointerException if the specified array is null
	 */
	public boolean addAll(final int[] elements) {
		final int count = elements.length;
		ensureCapacity(_size + count);
		System.arraycopy(elements, 0, _data, _size, count);
		_size += count;

		return count != 0;
	}

	/**
	 * Inserts all of the elements in the specified array into this list,
	 * starting at the specified position.
	 *
	 * @param index index at which to insert the first element from the
	 *              specified collection
	 * @param elements collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *         {@code (index < 0 || index > size())}
	 * @throws NullPointerException if the specified array is null
	 */
	public boolean addAll(final int index, final int[] elements) {
		rangeCheckForAdd(index);

		final int count = elements.length;
		ensureCapacity(_size + count);

		final int moved = _size - index;
		if (moved > 0) {
			System.arraycopy(_data, index, _data, index + count, moved);
		}

		System.arraycopy(elements, 0, _data, index, count);
		_size += count;
		return count != 0;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear() {
		_modCount++;
		_size = 0;
	}

	/**
	 * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's
	 * current size.  An application can use this operation to minimize the
	 * storage of an <tt>ArrayList</tt> instance.
	 */
	public void trimToSize() {
		_modCount++;
		if (_size < _data.length) {
			_data = _size == 0
				? EMPTY_DATA
				: Arrays.copyOf(_data, _size);
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

	public int[] toArray() {
		return Arrays.copyOf(_data, _size);
	}

	private void ensureCapacity(int minCapacity) {
		ensureExplicitCapacity(calculateCapacity(_data, minCapacity));
	}

	private void ensureExplicitCapacity(int minCapacity) {
		_modCount++;
		if (minCapacity - _data.length > 0) {
			grow(minCapacity);
		}
	}

	private void rangeCheck(final int index) {
		if (index >= _size)
			throw new IndexOutOfBoundsException(format(
				"Index: %d, Size: %d", index, _size
			));
	}

	private void rangeCheckForAdd(int index) {
		if (index > _size || index < 0)
			throw new IndexOutOfBoundsException(format(
				"Index: %d, Size: %d", index, _size
			));
	}

	private static int calculateCapacity(int[] elementData, int minCapacity) {
		if (elementData == DEFAULT_EMPTY_DATA) {
			return Math.max(DEFAULT_CAPACITY, minCapacity);
		}
		return minCapacity;
	}

	private void grow(final int minCapacity) {
		final int oldCapacity = _data.length;

		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0) {
			newCapacity = minCapacity;
		}
		if (newCapacity - MAX_SIZE > 0) {
			newCapacity = hugeCapacity(minCapacity);
		}

		_data = Arrays.copyOf(_data, newCapacity);
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
