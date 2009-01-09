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
import java.util.Collection;
import java.util.ListIterator;
import java.util.RandomAccess;


import javolution.context.ObjectFactory;

/** 
 * Array class which wraps the the java build in array type T[]. Once the array
 * is created the array length can't be changed (like the build in array).
 * 
 * @param <T> the element type of the arary.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Array.java,v 1.10 2009-01-09 21:29:40 fwilhelm Exp $
 */
public class Array<T> implements Iterable<T>, Copyable<Array<T>>, RandomAccess {
	Object[] _array = {};
	boolean _sealed = false;
	
	Array() {
	}
	
	/**
	 * @param length the array length.
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
			throw new UnsupportedOperationException("Array is sealed");
		}
		_array[index] = value;
	}
	
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
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 *         this array, or -1 if this array does not contain the element
	 */
	public int indexOf(final Object element) {
		return ArrayUtils.indexOf(_array, element);
	}
	
	/**
	 * Returns {@code true} if this array contains the specified element.
	 *
	 * @param element element whose presence in this array is to be tested. The
	 *        tested element can be {@code null}.
	 * @return {@code true} if this array contains the specified element
	 */
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}
	
	/**
	 * Making this array immutable. After sealing, calls to the 
	 * {@link #set(int, Object)} will throw an {@link UnsupportedOperationException}.
	 * Once an array is seald, it can't be made mutable again.
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
	
	/**
	 * Set all array elements to {@code null}.
	 * 
	 * @throws UnsupportedOperationException if this array is sealed 
	 *         ({@code isSealed() == true}).
	 */
	public void clear() {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed.");
		}
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
		return new ArrayIterator<T>(_array, 0, _array.length, _sealed);
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * 
	 * @return a shallow copy of this array.
	 */
	public Array<T> copy() {
		final Array<T> array = newInstance(length());
		System.arraycopy(_array, 0, array._array, 0, length());
		return array;
	}
	
	/**
	 * Returns a view of the portion of this array between the specified 
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start} 
	 * and {@code end} are equal, the returned array has the length zero.) The 
	 * returned array is backed by this array, so non-structural changes in the 
	 * returned array are reflected in this array, and vice-versa.
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the 
	 * sort that commonly exist for arrays). Any operation that expects an array 
	 * can be used as a range operation by passing an sub array view instead of 
	 * an whole array. E.g.:
	 * [code]
	 *     array.subArray(4, 10).clear();
	 *     Array<?> copy = array.subArray(5, 7).copy();
	 * [/code]
	 * 
	 * @param start low endpoint (inclusive) of the sub array.
	 * @param end high endpoint (exclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value 
	 *         ({@code start < 0 || end > lenght() || start > end}).
	 */
	public Array<T> subArray(final int start, final int end) {
		if (start < 0 || end > length() || start > end) {
			throw new IndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s]", start, end
			));
		}
		
		return new SubArray<T>(_array, start, end, _sealed);
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
	 * Array creation part.
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
			a.clear();
//			a._array = new Object[length];
		}
		return a;
	}
	
	/**
	 * Create a new Array from the given vararg of type T.
	 * 
	 * @param <A> the array type.
	 * @param values the array values.
	 * @return a new Array created from the given values.
	 * @throws NullPointerException if the {@code values} are {@code null}.
	 */
	public static <A> Array<A> valueOf(final A... values) {
		Validator.notNull(values, "Values");
		
		final Array<A> a = newInstance(values.length);
		System.arraycopy(values, 0, a._array, 0, values.length);
		return a;
	}
	
	public static <A> Array<A> valueOf(final Collection<A> values) {
		Validator.notNull(values, "Values");
		
		final Array<A> a = newInstance(values.size());
		int index = 0;
		for (A value : values) {
			a.set(index++, value);
		}
		return a;
	}
	
}













