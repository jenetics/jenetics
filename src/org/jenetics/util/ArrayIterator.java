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
import java.util.NoSuchElementException;

/**
 * Helper class which iterates over an given array.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayIterator.java,v 1.1 2008-08-25 19:36:07 fwilhelm Exp $
 */
final class ArrayIterator<T> implements ListIterator<T> {
	private final Object[] _array;
	private int _pos = -1;
	
	public ArrayIterator(final Object[] array) {
		this._array = array;
	}
	
	@Override
	public boolean hasNext() {
		return _pos < _array.length - 1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return (T)_array[++_pos];
	}
	
	@Override
	public int nextIndex() {
		return _pos + 1;
	}
	
	@Override
	public boolean hasPrevious() {
		return _pos > 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return (T)_array[--_pos];
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}
	
	@Override
	public void set(final T value) {
		_array[_pos] = value;
	}
	
	@Override
	public void add(final T o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}
