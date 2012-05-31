/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Helper class which iterates over an given array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &ndash; <em>$Revision$</em>
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
