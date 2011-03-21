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

import static java.lang.Math.min;
import static org.jenetics.util.ObjectUtils.hashCodeOf;
import static org.jenetics.util.Validator.nonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/** 
 * Array class which wraps the the java build in array type T[]. Once the array
 * is created the array length can't be changed (like the build in array). 
 * <strong>This array is not synchronized.</strong> If multiple threads access
 * an {@code Array} concurrently, and at least one of the threads modifies the
 * array, it <strong>must</strong> be synchronized externally.
 * <br/>
 * Use the {@link #asList()} method to work together with the 
 * <a href="http://download.oracle.com/javase/6/docs/technotes/guides/collections/index.html">
 * Java Collection Framework</a>.
 * 
 * @param <T> the element type of the array.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public final class Array<T> 
	extends ArraySeq<T> 
	implements 
		MSeq<T>, 
		Copyable<Array<T>>, 
		Cloneable, 
		RandomAccess
{
	private static final long serialVersionUID = 2L;
		
	transient boolean _sealed = false;
	
	Array(final Object[] array, final int start, final int end, final boolean sealed) {
		super(array, start, end);
		_sealed = sealed;
	}
	
	/**
	 * Create a new array from the given two arrays.
	 * 
	 * @param a1 array one.
	 * @param a2 array two.
	 * @throws NullPointerException if one of the arrays is {@code null}.
	 */
	Array(final Array<? extends T> a1, final Array<? extends T> a2) {
		this(a1.length() + a2.length());
		System.arraycopy(a1._array, a1._start, _array, 0, a1.length());
		System.arraycopy(a2._array, a2._start, _array, a1.length(), a2.length());
	}
	
	/**
	 * Create a new array with the given length.
	 * 
	 * @param length the array length.
	  * @throws NegativeArraySizeException if the specified {@code length} 
	  *			is negative
	 */
	public Array(final int length) {
		super(new Object[length]);
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
	public Array(
		final T first, 
		final T second
	) {
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
	public Array(
		final T first, 
		final T second, 
		final T third
	) {
		this(3);
		_array[0] = first;
		_array[1] = second;
		_array[2] = third;
	}
	
	/**
	 * Create a new array with length four. The array will be initialized with
	 * the given values.
	 * 
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 */
	public Array(
		final T first, 
		final T second, 
		final T third, 
		final T fourth
	) {
		this(4);
		_array[0] = first;
		_array[1] = second;
		_array[2] = third;
		_array[3] = fourth;
	}
	
	/**
	 * Create a new array with length five. The array will be initialized with
	 * the given values.
	 * 
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 * @param fifth fifth array element.
	 */
	public Array(
		final T first, 
		final T second, 
		final T third, 
		final T fourth,
		final T fifth
	) {
		this(5);
		_array[0] = first;
		_array[1] = second;
		_array[2] = third;
		_array[3] = fourth;
		_array[4] = fifth;
	}
	
	/**
	 * Create a new array from the given values.
	 * 
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 * @param fifth fifth array element.
	 * @param rest the rest of the array element.
	 * @throws NullPointerException if the {@code rest} array is {@code null}.
	 */
	public Array(
		final T first,
		final T second,
		final T third,
		final T fourth,
		final T fifth,
		final T... rest
	) {
		this(5 + rest.length);
		_array[0] = first;
		_array[1] = second;
		_array[2] = third;
		_array[3] = fourth;
		_array[4] = fifth;
		System.arraycopy(rest, 0, _array, 5, rest.length);
	}
	
	/**
	 * Create a new array from the given values.
	 * 
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public Array(final T[] values) {
		this(values.length);
		System.arraycopy(values, 0, _array, 0, values.length);
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
	 * Selects all elements of this list which satisfy a predicate. 
	 * 
	 * @param predicate the predicate used to test elements.
	 * @return a new array consisting of all elements of this list that satisfy 
	 *         the given {@code predicate}. The order of the elements is 
	 *         preserved.
	 * @throws NullPointerException if the given {@code predicate} is 
	 *         {@code null}. 
	 */
	public Array<T> filter(final Predicate<? super T> predicate) {
		final Array<T> copy = new Array<T>(length());
		
		int index = 0;
		for (int i = 0, n = length(); i < n; ++i) {
			final T value = get(i);
			if (predicate.evaluate(value)) {
				copy.set(index++, value);
			}
		}
		
		return copy.subSeq(0, index);
	}
	
	@Override
	public void set(final int index, final T value) {
		cloneIfSealed();
		checkIndex(index);
		_array[index + _start] = value;
	}
	
	@Override
	public Array<T> fill(final T value) {
		cloneIfSealed();
		for (int i = _start; i < _end; ++i) {
			_array[i] = value;
		}
		return this;
	}
	
	@Override
	public Array<T> fill(final Iterator<? extends T> it) {
		cloneIfSealed();
		
		for (int i = _start; i < _end && it.hasNext(); ++i) {
			_array[i] = it.next();
		}
		return this;
	}
	
	@Override
	public Array<T> fill(final T[] values) {
		cloneIfSealed();
		System.arraycopy(values, 0, _array, _start, min(length(), values.length));
		return this;
	}
	
	@Override
	public Array<T> fill(final Factory<? extends T> factory) {
		Validator.nonNull(factory);
		cloneIfSealed();
		for (int i = _start; i < _end; ++i) {
			_array[i] = factory.newInstance();
		}
		return this;
	}
	
	@Override
	public ISeq<T> seal() {
		_sealed = true;
		return new ArrayISeq<T>(_array, _start, _end);
	}
	
	final void cloneIfSealed() {
		if (_sealed) {
			_array = _array.clone();
			_sealed = false;
		}
	}
	
	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code value}. The length of the new array is 
	 * {@code this.length() + 1}. The returned array is not sealed.
	 * 
	 * @param value the value to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 * 		  given {@code value}
	 */
	public Array<T> append(final T value) {
		final Array<T> array = new Array<T>(length() + 1);
		System.arraycopy(_array, _start, array._array, 0, length());
		array._array[array.length() - 1] = value;
		return array;
	}
	
	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code array}. The length of the new array is 
	 * {@code this.length() + array.length()}. The returned array is not sealed.
	 * 
	 * @param array the array to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 * 		  given {@code array}
	 * @throws NullPointerException if the {@code arrays} is {@code null}.
	 */
	public Array<T> append(final Array<? extends T> array) {
		return new Array<T>(this, nonNull(array, "Array"));
	}
	
	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code values}. The length of the new array is 
	 * {@code this.length() + values.size()}. The returned array is not sealed.
	 * 
	 * @param values the array to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 * 		  given {@code array}
	 * @throws NullPointerException if the {@code values} is {@code null}.
	 */
	public Array<T> append(final Collection<? extends T> values) {
		nonNull(values, "Values");
		final Array<T> array = new Array<T>(length() + values.size());
		
		System.arraycopy(_array, _start, array._array, 0, length());
		int index = length();
		for (Iterator<? extends T> it = values.iterator(); it.hasNext(); ++index) {
			array._array[index] = it.next();
		}
		
		return array;
	}
	
	/**
	 * Create a new array with element type {@code B}.
	 * 
	 * @param <B> the element type of the new array.
	 * @param converter the array element converter.
	 * @return a new array with element type {@code B}.
	 * @throws NullPointerException if the element {@code converter} is 
	 *         {@code null}.
	 */
	public <B> Array<B> map(final Converter<? super T, ? extends B> converter) {
		nonNull(converter, "Converter");
		
		final int length = length();
		final Array<B> result = new Array<B>(length);
		assert (result._array.length == length);
		
		for (int i = length; --i <= 0;) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array[i + _start];
			result._array[i] = converter.convert(value);
		}
		return result;
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * The copied array is not sealed. If the array is a sub-array (created
	 * with the {@link #subSeq(int, int)} method, only the sub-array-part is
	 * copied. The {@link #clone()} method <i>copies</i> the whole array.
	 * 
	 * @see #clone()
	 * @return a shallow copy of this array.
	 */
	@Override
	public Array<T> copy() {
		return new Array<T>(toArray(), 0, length(), false);
	}
	
	/**
	 * Create a one to one copy of the given array. If this array is a sub array,
	 * created with {@link #subSeq(int, int)}, the whole underlying data array
	 * with its {@code start} and {@code stop} information is cloned.
	 * 
	 * @see #copy()
	 */
	@Override
	public Array<T> clone() {
		return (Array<T>)super.clone();
	}

	@Override
	public Array<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new Array<T>(_array, start + _start, end + _start, _sealed);
	}
		
	@Override
	public Array<T> subSeq(final int start) {
		return subSeq(start, length());
	}
	
	@Override
	public List<T> asList() {
		return new ArrayMSeqList<T>(this);
	}
	
	@Override
	public ListIterator<T> iterator() {
		return new ArrayMSeqIterator<T>(this);
	}
	
	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
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
