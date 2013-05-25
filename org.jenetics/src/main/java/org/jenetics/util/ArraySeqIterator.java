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
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
class ArraySeqIterator<T> implements ListIterator<T> {
	final ArraySeq<T> _array;

	int _pos;

	public ArraySeqIterator(final ArraySeq<T> array) {
		_array = array;
		_pos = array._start - 1;
	}

	@Override
	public boolean hasNext() {
		return _pos < _array._end - 1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return (T)_array._array.data[++_pos];
	}

	@Override
	public int nextIndex() {
		return _pos + 1;
	}

	@Override
	public boolean hasPrevious() {
		return _pos > _array._start;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return (T)_array._array.data[--_pos];
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}

	@Override
	public void set(final T value) {
		throw new UnsupportedOperationException("Array is sealed.");
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
