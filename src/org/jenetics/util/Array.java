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
import java.util.ListIterator;

import javolution.context.ObjectFactory;

/** 
 * Array class which wraps the the java build in array type T[]. Once the array
 * is created the array length can't be changed (like the build in array).
 * 
 * @param <T> the element type of the arary.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Array.java,v 1.2 2008-08-29 21:18:15 fwilhelm Exp $
 */
public class Array<T> implements Iterable<T> {
	Object[] _array = {};
	boolean _sealed = false;
	
	Array() {
	}
	
	/**
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
	 * @throws UnsupportedOperationException if this array is sealed 
	 *         ({@code isSealed() == true}).
	 */
	public void set(final int index, final T value) {
		if (_sealed) {
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
	 * Returns the index of the first occurrence of the specified element
	 * in this array, or -1 if this array does not contain the element.
	 * 
	 * @param element element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this array, or -1 if this array does not contain the element
	 */
	public int indexOf(final Object element) {
		return ArrayUtils.indexOf(_array, element);
	}
	
	/**
	 * Returns {@code true} if this array contains the specified element.
	 *
	 * @param element element whose presence in this array is to be tested
	 * @return {@code true} if this array contains the specified element
	 */
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}
	
	/**
	 * Making this array immutable. After sealing, calls to the 
	 * {@link #set(int, Object)} will throw an {@link UnsupportedOperationException}.
	 * 
	 * @return {@code this} array.
	 */
	public Array<T> seal() {
		_sealed = true;
		return this;
	}
	
	/**
	 * Return whether this array is sealed (immutable) or not.
	 * 
	 * @return {@code true} if this array can be changed, {@code false} otherwise.
	 */
	public boolean isSealed() {
		return _sealed;
	}
	
	void init() {
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
	public ListIterator<T> iterator() {
		return new ArrayIterator<T>(_array);
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * 
	 * @return a shallow copy of this array.
	 */
	public Array<T> copy() {
		final Array<T> copy = newInstance(length());
		System.arraycopy(_array, 0, copy._array, 0, length());
		return copy;
	}
	
	@Override
	public int hashCode() {
		int code = 17;
		for (int i = 0; i < _array.length; ++i) {
			final Object element = _array[i];
			if (element != null) {
				code += 37*element.hashCode() + 17;
			} else {
				code += 3;
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

	
	/*
	 * Array creation pars.
	 */

	private static final ObjectFactory<Array<Object>> 
	FACTORY = new ObjectFactory<Array<Object>>() {
		@Override protected Array<Object> create() {
			return new Array<Object>();
		}
	};
	
	/**
	 * Create a new array instance with the given {@code length}.
	 * 
	 * @param <A> the element type.
	 * @param length the length of the array.
	 * @return the new created array with the given {@code length}.
	 * @throws NegativeArraySizeException if the given {@code length} is smaller
	 *         than zero.
	 */
	public static <A> Array<A> newInstance(final int length) {
		if (length < 0) {
			throw new NegativeArraySizeException(
				"Negative array size given: " + length
			);
		}
		
		@SuppressWarnings("unchecked")
		final Array<A> a = (Array<A>)FACTORY.object();
		a._sealed = false;
		if (a._array.length != length) {
			a._array = new Object[length];
		} else {
			a.init();
		}
		return a;
	}
	
}








