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
package org.jenetics.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Helper class which iterates over an given array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-12-09 $</em>
 */
class ArraySeqIterator<T> implements ListIterator<T> {
	final ArraySeq<T> _array;

	int _cursor;
	int _lastElement = -1;

	public ArraySeqIterator(final ArraySeq<T> array) {
		_array = array;
		_cursor = array._start;
	}

	@Override
	public boolean hasNext() {
		return _cursor != _array._end;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T next() {
		final int i = _cursor;
		if (_cursor >= _array._end) {
			throw new NoSuchElementException();
		}

		_cursor = i + 1;
		return (T)_array._array.data[_lastElement = i];
	}

	@Override
	public int nextIndex() {
		return _cursor;
	}

	@Override
	public boolean hasPrevious() {
		return _cursor != _array._start;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T previous() {
		final int i = _cursor - 1;
		if (i < _array._start) {
			throw new NoSuchElementException();
		}

		_cursor = i;
		return (T)_array._array.data[_lastElement = i];
	}

	@Override
	public int previousIndex() {
		return _cursor - 1;
	}

	@Override
	public void set(final T value) {
		throw new UnsupportedOperationException("Array is immutable.");
	}

	@Override
	public void add(final T o) {
		throw new UnsupportedOperationException("Can't change array size.");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Can't change array size.");
	}

}
