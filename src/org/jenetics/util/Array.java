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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/** 
 * Array class which wraps the the java build in array type T[]. Once the array
 * is created the array length can't be changed (like the build in array).
 * 
 * @param <T> the element type of the array.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Array.java,v 1.35 2010-01-27 20:35:44 fwilhelm Exp $
 */
public class Array<T> implements 
	Iterable<T>, Copyable<Array<T>>, Cloneable, RandomAccess, Serializable 
{
	private static final long serialVersionUID = -5271247554278598795L;
	
	transient Object[] _array;
	transient int _start;
	transient int _end;
	transient boolean _sealed = false;
	
	/**
	 * <i>Universal</i> array constructor.
	 * 
	 * @param array the array which holds the elements. The array will not be 
	 *        copied.
	 * @param start the start index of the given array (exclusively).
	 * @param end the end index of the given array (exclusively)
	 * @param sealed the seal status. If {@code true} calls to 
	 *        {@link #set(int, Object)} will throw an 
	 *        {@link UnsupportedOperationException}.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 * @throws ArrayIndexOutOfBoundsException for an illegal start/end point index 
	 *         value ({@code start < 0 || end > array.lenght || start > end}).
	 */
	Array(final Object[] array, final int start, final int end, final boolean sealed) {
		Validator.notNull(array, "Array");
		if (start < 0 || end > array.length || start > end) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
		
		_array = array;
		_start = start;
		_end = end;
		_sealed = sealed;
	}
	
	/**
	 * @param array the array which holds the elements. The array will not be 
	 *        copied.
	 * @param sealed the seal status. If {@code true} calls to 
	 *        {@link #set(int, Object)} will throw an 
	 *        {@link UnsupportedOperationException}.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */
	Array(final Object[] array, final boolean sealed) {
		this(array, 0, array.length, sealed);
	}
	
	/**
	 * Create a new array with the given length.
	 * 
	 * @param length the array length.
     * @throws NegativeArraySizeException if the specified {@code length} 
     *         is negative
	 */
	public Array(final int length) {
		this(new Object[length], false);
	}
	
	/**
	 * Create a new array with length one. The array will be initialized with 
	 * the given value.
	 * 
	 * @param first the only element of the array.
	 */
	public Array(final T first) {
		this(1);
		_array[0] = first;
	}
	
	/**
	 * Create a new array with length two. The array will be initialized with
	 * the given values.
	 * 
	 * @param first first array element.
	 * @param second second array element.
	 */
	public Array(final T first, final T second) {
		this(2);
		_array[0] = first;
		_array[1] = second;
	}
	
	/**
	 * Create a new array with length three. The array will be initialized with
	 * the given values.
	 * 
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 */
	public Array(final T first, final T second, final T third) {
		this(3);
		_array[0] = first;
		_array[1] = second;
		_array[2] = third;
	}
	
	/**
	 * Create a new array from the given values.
	 * 
	 * @param values the values of the new array.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public Array(final T... values) {
		this(values.length);
		System.arraycopy(values, 0, _array, 0, values.length);
	}
	
	public Array(final Array<T> a1, final Array<T> a2) {
		this(a1.length() + a2.length());
		
		int index = 0;
		for (int i = 0, n = a1.length(); i < n; ++i) {
			_array[index++] = a1.get(i);
		}
		for (int i = 0, n = a2.length(); i < n; ++i) {
			_array[index++] = a2.get(i);
		}
	}
	
	/**
	 * Create a new Array from the values of the given Collection. The order of
	 * the elements are determined by the iterator of the Collection.
	 * 
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public Array(final Collection<? extends T> values) {
		this(values.size());
		
		int index = 0;
		for (Iterator<? extends T> it = values.iterator(); it.hasNext(); ++index) {
			_array[index] = it.next();
		}
	}
	
	/**
	 * Set the {@code value} at the given {@code index}.
	 * 
	 * @param index the index of the new value.
	 * @param value the new value.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 *         {@code (index < 0 || index >= size())}.
	 * @throws UnsupportedOperationException if this array is sealed 
	 *         ({@code isSealed() == true}).
	 */
	public void set(final int index, final T value) {
		checkSeal();
		checkIndex(index);
		_array[index + _start] = value;
	}
	
	/**
	 * Return the value at the given {@code index}.
	 * 
	 * @param index index of the element to return.
	 * @return the value at the given {@code index}.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 *         {@code (index < 0 || index >= size())}.
	 */
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		checkIndex(index);
		return (T)_array[index + _start];
	}
	
	/**
	 * Applies the given predicate to every element in the array.
	 * 
	 * @param predicate the predicate to apply.
	 * @throws NullPointerException if the given {@code predicate} is 
	 *         {@code null}.
	 */
	public void foreach(final Predicate<T> predicate) {
		Validator.notNull(predicate, "Predicate");
	
		for (int i = _start; i < _end; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array[i];
			predicate.evaluate(element);
		}
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
		int index = -1;
		
		if (element == null) {
			index = indexOf(new Predicate<T>() {
				@Override public boolean evaluate(final T object) {
					return object == null;
				}
			});
		} else {
			index = indexOf(new Predicate<T>() {
				@Override public boolean evaluate(final T object) {
					return element.equals(object);
				}
			});
		}
		
		return index;
	}
	
	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this array, or -1 if this array does not contain the element.
	 * 
	 * @param element element to search for, can be {@code null}
	 * @return the index of the last occurrence of the specified element in
	 *         this array, or -1 if this array does not contain the element
	 */
	public int lastIndexOf(final Object element) {
		int index = -1;
		
		if (element == null) {
			index = lastIndexOf(new Predicate<T>() {
				@Override public boolean evaluate(final T object) {
					return object == null;
				}
			});
		} else {
			index = lastIndexOf(new Predicate<T>() {
				@Override public boolean evaluate(final T object) {
					return element.equals(object);
				}
			});
		}
		
		return index;
	}
	
	/**
	 * Returns the index of the first element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * array element.
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate 
	 *         returns {@code true}, or -1 if the predicate returns false for 
	 *         every array element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int indexOf(final Predicate<? super T> predicate) {
		Validator.notNull(predicate, "Predicate");
		
		int index = -1;
		
		for (int i = _start; i < _end && index == -1; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array[i];
			if (predicate.evaluate(element)) {
				index = i - _start;
			}
		}
		
		return index;
	}
	
	/**
	 * Returns the index of the last element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * array element.
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate 
	 *         returns {@code true}, or -1 if the predicate returns false for 
	 *         every array element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int lastIndexOf(final Predicate<? super T> predicate) {
		Validator.notNull(predicate, "Predicate");
		
		int index = -1;
		
		for (int i = _end - 1; i >= _start && index == -1; --i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array[i];
			if (predicate.evaluate(element)) {
				index = i - _start;
			}
		}
		
		return index;
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
	 * Once an array is sealed, it can't be made mutable again.
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
	 * @return {@code false} if this array can be changed, {@code true} otherwise.
	 */
	public boolean isSealed() {
		return _sealed;
	}
	
	/**
	 * Set all array elements to the given {@code value}.
	 *
	 * @param value {@code value} to fill this array with.
	 * @throws UnsupportedOperationException if this array is sealed 
	 *         ({@code isSealed() == true}).
	 */
	public void fill(final T value) {
		checkSeal();
		for (int i = _start; i < _end; ++i) {
			_array[i] = value;
		}
	}
	
	/**
	 * Return the length of this array. Once the array is created, the length
	 * can't be changed.
	 * 
	 * @return the length of this array.
	 */
	public int length() {
		return _end - _start;
	}

	@Override
	public ListIterator<T> iterator() {
		return new ArrayIterator<T>(_array, _start, _end, _sealed);
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * The copied array is not sealed.
	 * 
	 * @return a shallow copy of this array.
	 */
	@Override
	public Array<T> copy() {
		final Array<T> array = new Array<T>(length());
		System.arraycopy(_array, _start, array._array, 0, length());
		return array;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Array<T> clone() {
		try {
			return (Array<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	/**
	 * Returns a view of the portion of this array between the specified 
	 * {@code start}, inclusive, and {@code end}, exclusive. (If {@code start} 
	 * and {@code end} are equal, the returned array has the length zero.) The 
	 * returned array is backed by this array, so non-structural changes in the 
	 * returned array are reflected in this array, and vice-versa. The sealing
	 * state is copied from this array 
	 * ({@code this.isSealed() == this.subArray(start, end).isSealed()}).
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
	 * @param start low end point (inclusive) of the sub array.
	 * @param end high end point (exclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws ArrayIndexOutOfBoundsException for an illegal end point index value 
	 *         ({@code start < 0 || end > lenght() || start > end}).
	 */
	public Array<T> subArray(final int start, final int end) {
		if (start < 0 || end > length() || start > end) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
		
		return new Array<T>(_array, start + _start, end + _start, _sealed);
	}
	
	/**
	 * Returns a fixed-size list backed by the specified array. (Changes to
	 * the returned list "write through" to the array.) The returned list is
	 * fixed size, serializable and implements {@link RandomAccess}.
	 *
	 * @return a list view of this array
	 */	
	public List<T> asList() {
		return new org.jenetics.util.ArrayList<T>(this);
	}
	
	void checkSeal() {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed");
		}
	}
	
	void checkIndex(final int index) {
		if (index < 0 || index >= (_end - _start)) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [0, %s)", index, (_end - _start)
			));
		}
	}
	
	void checkIndex(final int from, final int to) {
		if (from < 0 || to > length() || from > to) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s]", from, to
			));
		}
	}
	
	
	@Override
	public int hashCode() {
		int hash = 17;
		for (int i = _start; i < _end; ++i) {
			final Object element = _array[i];
			if (element != null) {
				hash += 37*element.hashCode() + 17;
			} else {
				hash += 3;
			}
		}
		return hash;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Array<?>)) {
			return false;
		}
		
		final Array<?> array = (Array<?>)obj;
		boolean equals = (length() == array.length());
		for (int i = _start; equals && i < _end; ++i) {
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
        final StringBuilder out = new StringBuilder();
        
        out.append("[");
        if (length() > 0) {
        	out.append(_array[_start]);
        }
        for (int i = _start + 1; i < _end; ++i) {
        	out.append(",");
        	out.append(_array[i]);
        }
        out.append("]");
        
        return out.toString();
	}
	
	
	private void writeObject(final ObjectOutputStream out)
		throws IOException 
	{
		out.defaultWriteObject();

		out.writeInt(length());
		for (int i = _start; i < _end; ++i) {
			out.writeObject(_array[i]);
		}		
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException 
	{
		in.defaultReadObject();

		final int length = in.readInt();
		_array = new Object[length];
		_start = 0;
		_end = length;
		_sealed = false;		
		for (int i = 0; i < length; ++i) {
			_array[i] = in.readObject();
		}
	}
}













