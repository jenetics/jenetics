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

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;


import javolution.context.ObjectFactory;

/** 
 * Array class which wraps the the java build in array type T[].
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Array.java,v 1.1 2008-08-25 19:36:05 fwilhelm Exp $
 */
public class Array<T> implements Iterable<T> {
	protected Object[] _array;
	protected boolean _immutable = false;
	
	Array() {
		_array = new Object[0];
	}
	
	/**
	 * 
	 * @param type
	 * @param length
	 * 
     * @throws NegativeArraySizeException if the specified {@code length} 
     *         is negative
	 */
	Array(final int length) {
		_array = new Object[length];
	}
	
	/**
	 * Set the {@code value} at the given {@code index}.
	 * 
	 * @param index the index of the new value.
	 * @param value the new value.
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 *         {@code (index < 0 || index >= size())}.
	 */
	public void set(final int index, final T value) {
		if (_immutable) {
			throw new UnsupportedOperationException();
		}
		_array[index] = value;
	}
	
//	public void set(final int index, final Array<? extends T> values) {
//		System.arraycopy(values._array, 0, _array, index, values.length());
//	}
	
	/**
	 * Return the value at the given {@code index}.
	 * 
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws IndexOutOfBoundsException if the index is out of range 
	 *         {@code (index < 0 || index >= size())}.
	 */
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_array[index];
	}
	
	/**
	 * Making this array immutable.
	 */
	public Array<T> seal() {
		_immutable = true;
		return this;
	}
	
	void setAllNull() {
		for (int i = 0; i < _array.length; ++i) {
			_array[i] = null;
		}
	}
	
	/**
	 * Return the length of this array. Once the array is created, the length
	 * can't be changed.
	 * 
	 * @return the length of this array.
	 */
	public int length() {
		return _array.length;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<T>(_array);
	}
	
	public ListIterator<T> listIterator() {
		return new ArrayIterator<T>(_array);
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not copied.
	 * 
	 * @return a shallow copy of this array.
	 */
	public Array<T> copy() {
		Array<T> copy = newInstance(length());
		System.arraycopy(_array, 0, copy._array, 0, length());
		return copy;
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (Object element : _array) {
			if (element != null) {
				code += 37*element.hashCode() + 17;
			}
		}
		return code;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Array)) {
			return false;
		}
		
		final Array<?> array = (Array<?>)obj;
		boolean equals = (length() == array.length());
		for (int i = 0; equals && i < length(); ++i) {
			if (_array[i] != null) {
				equals = _array[i].equals(array._array[i]);
			} else {
				equals = array._array[i] == null;
			}
		}
		return equals;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(_array);
	}
	
	
	private static final ObjectFactory<Array<Object>> 
	FACTORY = new ObjectFactory<Array<Object>>() {
		@Override protected Array<Object> create() {
			return new Array<Object>();
		}
	};
	
	public static <A> Array<A> newInstance(final int length) {
		@SuppressWarnings("unchecked")
		final Array<A> a = (Array<A>)FACTORY.object();
		a._immutable = false;
		if (a._array == null || a._array.length != length) {
			a._array = new Object[length];
		} else {
			a.setAllNull();
		}
		return a;
	}
	
}








