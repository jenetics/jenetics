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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics.util;

import java.util.ListIterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: SubArray.java,v 1.6 2009-01-09 21:39:09 fwilhelm Exp $
 */
final class SubArray<T> extends Array<T> {
	private int _start;
	private int _end;
	
	SubArray(
		final Object[] array, 
		final int start, 
		final int end, 
		final boolean sealed
	) {
		_start = start;
		_end = end;
		_array = array;
		_sealed = sealed;
	}
	
	@Override
	public void set(int index, T value) {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed");
		}
		if (index >= _end) {
			throw new IndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [%s, %s).", index, 0, (_end - _start)
			));
		}
		
		_array[index + _start] = value;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (index >= _end) {
			throw new IndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [%s, %s).", index, 0, (_end - _start)
			));
		}
		
		return (T)_array[index + _start];
	}
	
	@Override
	public int indexOf(Object element) {
		int index = ArrayUtils.indexOf(_array, _start, _end, element);
		if (index != -1) {
			index -= _start;
		}
		return index;
	}
	
	@Override
	public boolean contains(Object element) {
		return ArrayUtils.indexOf(_array, _start, element) != -1;
	}
	
	@Override
	public Array<T> seal() {
		_sealed = true;
		return this;
	}
	
	@Override
	public boolean isSealed() {
		return _sealed;
	}
	
	@Override
	public void clear() {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed.");
		}
		for (int i = _start; i < _end; ++i) {
			_array[i] = null;
		}
	}
	
	@Override
	public int length() {
		return _end - _start;
	}

	@Override
	public ListIterator<T> iterator() {
		return new ArrayIterator<T>(_array, _start, _end, _sealed);
	}
	
	@Override
	public Array<T> copy() {
		final Array<T> array = Array.newInstance(length());
		System.arraycopy(_array, _start, array._array, 0, length());
		return array;
	}

	public Array<T> subArray(final int start, final int end) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s]", start, end
			));
		}
		
		return new SubArray<T>(_array, start + _start, end + _start, _sealed);
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (int i = 0; i < length(); ++i) {
			final Object element = get(i);
			if (element != null) {
				code += 37*element.hashCode() + 17;
			} else {
				code += 3;
			}
		}
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Array)) {
			return false;
		}
		
		final Array<?> array = (Array<?>)obj;
		boolean equals = (length() == array.length());
		for (int i = 0; equals && i < length(); ++i) {
			if (get(i) != null) {
				equals = get(i).equals(array.get(i));
			} else {
				equals = array.get(i) == null;
			}
		}
		return equals;
	}

	@Override
	public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = _start; ; ++i) {
            b.append(String.valueOf(_array[i]));
            if (i == _end - 1)
                return b.append(']').toString();
            b.append(", ");
        }
	}

}
