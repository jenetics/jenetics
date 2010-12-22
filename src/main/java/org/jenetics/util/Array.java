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
import static org.jenetics.util.Validator.nonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
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
<math xmlns="http://www.w3.org/1998/Math/MathML">
<matrix>
  <matrixrow>
    <cn> 0 </cn> <cn> 1 </cn> <cn> 0 </cn>

  </matrixrow>
  <matrixrow>
    <cn> 0 </cn> <cn> 0 </cn> <cn> 1 </cn>
  </matrixrow>
  <matrixrow>

    <cn> 1 </cn> <cn> 0 </cn> <cn> 0 </cn>
  </matrixrow>
</matrix>
</math>


 * @param <T> the element type of the array.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class Array<T> implements 
	Iterable<T>, Copyable<Array<T>>, Cloneable, RandomAccess, Serializable 
{
	private static final long serialVersionUID = 1L;
	
	transient Object[] _array;
	transient int _start;
	transient int _end;
	transient boolean _sealed = false;
	
	/**
	 * <i>Universal</i> array constructor.
	 * 
	 * @param array the array which holds the elements. The array will not be 
	 * 		 copied.
	 * @param start the start index of the given array (exclusively).
	 * @param end the end index of the given array (exclusively)
	 * @param sealed the seal status. If {@code true} calls to 
	 * 		 {@link #set(int, Object)} will throw an 
	 * 		 {@link UnsupportedOperationException}.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 * @throws ArrayIndexOutOfBoundsException for an illegal start/end point index 
	 * 		  value ({@code start < 0 || end > array.lenght || start > end}).
	 */
	Array(final Object[] array, final int start, final int end, final boolean sealed) {
		nonNull(array, "Array");
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
	 * 		 copied.
	 * @param sealed the seal status. If {@code true} calls to 
	 * 		 {@link #set(int, Object)} will throw an 
	 * 		 {@link UnsupportedOperationException}.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 */
	Array(final Object[] array, final boolean sealed) {
		this(array, 0, array.length, sealed);
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
	 * Set the {@code value} at the given {@code index}.
	 * 
	 * @param index the index of the new value.
	 * @param value the new value.
	 * @throws ArrayIndexOutOfBoundsException if the index is out of range 
	 * 		  {@code (index < 0 || index >= size())}.
	 * @throws UnsupportedOperationException if this array is sealed 
	 * 		  ({@code isSealed() == true}).
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
	 * 		  {@code (index < 0 || index >= size())}.
	 */
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		checkIndex(index);
		return (T)_array[index + _start];
	}
	
	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this array, or -1 if this array does not contain the element.
	 * 
	 * @param element element to search for, can be {@code null}
	 * @return the index of the first occurrence of the specified element in
	 * 		  this array, or -1 if this array does not contain the element
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
	 * 		  this array, or -1 if this array does not contain the element
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
	 * <p>
	 * Returns the index of the first element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * array element.
	 * </p>
	 * [code]
	 * 	 // Finding index of first null value.
	 * 	 final int index = array.indexOf(new Predicates.Nil());
	 * 	 
	 * 	 // Assert of no null values.
	 * 	 assert (array.indexOf(new Predicates.Nil()) == -1);
	 * [/code]
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the first element on which the given predicate 
	 * 		  returns {@code true}, or -1 if the predicate returns {@code false}
	 * 		  for every array element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int indexOf(final Predicate<? super T> predicate) {
		nonNull(predicate, "Predicate");
		
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
	 * Iterates over this array as long as the given predicate returns 
	 * {@code true}. This method is more or less an  <i>alias</i> of the 
	 * {@link #indexOf(Predicate)} method. In some cases a call to a 
	 * {@code array.foreach()} method can express your intention much better 
	 * than a {@code array.indexOf()} call.
	 * 
	 * [code]
	 * 	 final Array<Integer> values = new Array<Integer>(Arrays.asList(1, 2, 3, 4, 5));
	 * 	 final AtomicInteger sum = new AtomicInteger(0);
	 * 	 values.foreach(new Predicate<Integer>() {
	 * 		  public boolean evaluate(final Integer value) {
	 * 				sum.addAndGet(value);
	 * 				return true;
	 * 		  }
	 * 	 });
	 * 	 System.out.println("Sum: " + sum);
	 * [/code]
	 * 
	 * @param predicate the predicate to apply.
	 * @return the index of the first element on which the given predicate 
	 * 		  returns {@code false}, or -1 if the predicate returns {@code true}
	 * 		  for every array element.
	 * @throws NullPointerException if the given {@code predicate} is 
	 * 		  {@code null}.
	 */
	public int foreach(final Predicate<? super T> predicate) {
		nonNull(predicate, "Predicate");
		
		int index = -1;
		
		for (int i = _start; i < _end && index == -1; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array[i];
			
			if (!predicate.evaluate(element)) {
				index = i - _start;
			}
		}
		
		return index;
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
		
		return copy.subArray(0, index);
	}
	
	/**
	 * Returns the index of the last element on which the given predicate 
	 * returns {@code true}, or -1 if the predicate returns false for every
	 * array element.
	 * 
	 * @param predicate the search predicate.
	 * @return the index of the last element on which the given predicate 
	 * 		  returns {@code true}, or -1 if the predicate returns false for 
	 * 		  every array element.
	 * @throws NullPointerException if the given {@code predicate} is {@code null}.
	 */
	public int lastIndexOf(final Predicate<? super T> predicate) {
		nonNull(predicate, "Predicate");
		
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
	 * 		 tested element can be {@code null}.
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
	 * @return {@code this} array.
	 * @throws UnsupportedOperationException if this array is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public Array<T> fill(final T value) {
		checkSeal();
		for (int i = _start; i < _end; ++i) {
			_array[i] = value;
		}
		return this;
	}
	
	/**
	 * Fills the array with values of the given iterator.
	 * 
	 * @param it the iterator of the values to fill this array.
	 * @return {@code this} array.
	 * @throws UnsupportedOperationException if this array is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public Array<T> fill(final Iterator<? extends T> it) {
		checkSeal();
		
		for (int i = _start; i < _end && it.hasNext(); ++i) {
			_array[i] = it.next();
		}
		return this;
	}
	
	/**
	 * Fill the array with the given values.
	 * 
	 * @param values the first initial values of this array
	 * @return {@code this} array.
	 * @throws UnsupportedOperationException if this array is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public Array<T> fill(final T[] values) {
		checkSeal();
		System.arraycopy(values, 0, _array, _start, min(length(), values.length));
		return this;
	}
	
	/**
	 * Fill the array with values generated by the given factory.
	 * 
	 * @param factory the value factory.
	 * @return {@code this} array.
	 * @throws NullPointerException if the given {@code factory} is {@code null}.
	 * @throws UnsupportedOperationException if this array is sealed 
	 * 		  ({@code isSealed() == true}).
	 */
	public Array<T> fill(final Factory<? extends T> factory) {
		Validator.nonNull(factory);
		checkSeal();
		for (int i = _start; i < _end; ++i) {
			_array[i] = factory.newInstance();
		}
		return this;
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
	 * Return an iterator with the new type {@code B}.
	 * 
	 * @param <B> the component type of the returned type.
	 * @param converter the converter for converting from {@code T} to {@code B}.
	 * @return the iterator of the converted type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public <B> Iterator<B> iterator(
		final Converter<? super T, ? extends B> converter
	) {
		nonNull(converter, "Converter");
		
		return new Iterator<B>() {
			private final Iterator<T> _iterator = iterator();
			@Override public boolean hasNext() {
				return _iterator.hasNext();
			}
			@Override public B next() {
				return converter.convert(_iterator.next());
			}
			@Override public void remove() {
				_iterator.remove();
			}
		};
	}
	
	/**
	 * Return a shallow copy of this array. The array elements are not cloned.
	 * The copied array is not sealed. If the array is a sub-array (created
	 * with the {@link #subArray(int, int)} method, only the sub-array-part is
	 * copied. The {@link #clone()} method <i>copies</i> the whole array.
	 * 
	 * @see #clone()
	 * @return a shallow copy of this array.
	 */
	@Override
	public Array<T> copy() {
		final Array<T> array = new Array<T>(length());
		System.arraycopy(_array, _start, array._array, 0, length());
		return array;
	}
	
	/**
	 * Create a one to one copy of the given array. If this array is a sub array,
	 * created with {@link #subArray(int, int)}, the whole underlying data array
	 * with its {@code start} and {@code stop} information is cloned.
	 * 
	 * @see #copy()
	 */
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
	 * 	 array.subArray(4, 10).clear();
	 * 	 Array<?> copy = array.subArray(5, 7).copy();
	 * [/code]
	 * 
	 * @param start low end point (inclusive) of the sub array.
	 * @param end high end point (exclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws ArrayIndexOutOfBoundsException for an illegal end point index value 
	 * 		  ({@code start < 0 || end > lenght() || start > end}).
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
	 * Returns a view of the portion of this array between the specified 
	 * {@code start}, inclusive, till the end of the array. The 
	 * returned array is backed by this array, so non-structural changes in the 
	 * returned array are reflected in this array, and vice-versa. The sealing
	 * state is copied from this array 
	 * ({@code this.isSealed() == this.subArray(start).isSealed()}).
	 * <p/>
	 * This method eliminates the need for explicit range operations (of the 
	 * sort that commonly exist for arrays). Any operation that expects an array 
	 * can be used as a range operation by passing an sub array view instead of 
	 * an whole array. E.g.:
	 * [code]
	 * 	 array.subArray(4).clear();
	 * 	 Array<?> copy = array.subArray(5).copy();
	 * [/code]
	 * 
	 * @param start low end point (inclusive) of the sub array.
	 * @return a view of the specified range within this array.
	 * @throws ArrayIndexOutOfBoundsException for an illegal end point index value 
	 * 		  ({@code start < 0 || start > lenght()}).
	 */	
	public Array<T> subArray(final int start) {
		return subArray(start, length());
	}
	
	/**
	 * Return an array containing all of the elements in this array in right 
	 * order. The returned array will be "safe" in that no references to it 
	 * are maintained by this array. (In other words, this method must allocate 
	 * a new array.) The caller is thus free to modify the returned array. 
	 * 
	 * @see java.util.Collection#toArray()
	 * 
	 * @return an array containing all of the elements in this list in right 
	 * 		  order
	 */
	public Object[] toArray() {
		final Object[] array = new Object[length()];
		System.arraycopy(_array, _start, array, 0, length());
		return array;
	}
	
	/**
	 * Return an array containing all of the elements in this array in right
	 * order; the runtime type of the returned array is that of the specified 
	 * array. If this array fits in the specified array, it is returned therein. 
	 * Otherwise, a new array is allocated with the runtime type of the specified 
	 * array and the length of this array.
	 * <p/>
	 * If this array fits in the specified array with room to spare (i.e., the 
	 * array has more elements than this array), the element in the array 
	 * immediately following the end of this array is set to null. (This is 
	 * useful in determining the length of the array only if the caller knows 
	 * that the list does not contain any null elements.) 
	 * 
	 * @see java.util.Collection#toArray(Object[])
	 * 
	 * @param array the array into which the elements of this array are to be 
	 * 		 stored, if it is big enough; otherwise, a new array of the same 
	 * 		 runtime type is allocated for this purpose. 
	 * @return an array containing the elements of this array
	 * @throws ArrayStoreException if the runtime type of the specified array is 
	 * 		  not a super type of the runtime type of every element in this array
	 * @throws NullPointerException if the given array is {@code null}.	
	 */
	@SuppressWarnings("unchecked")
	public T[] toArray(final T[] array) {
		T[] result = null;
		if (array.length < length()) {
			result = (T[])Arrays.copyOfRange(_array, _start, _end, array.getClass());
		} else {
			System.arraycopy(_array, _start, array, 0, length());
			if (array.length > length()) {
				array[length()] = null;
			}
			result = array;
		}

		return result;
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
	
	final void checkSeal() {
		if (_sealed) {
			throw new UnsupportedOperationException("Array is sealed");
		}
	}
	
	final void checkIndex(final int index) {
		if (index < 0 || index >= (_end - _start)) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [0, %s)", index, (_end - _start)
			));
		}
	}
	
	final void checkIndex(final int from, final int to) {
		if (from < 0 || to > length() || from > to) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", from, to
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
		final int difference = _start - array._start;
		for (int i = _start; equals && i < _end; ++i) {
			if (_array[i] != null) {
				equals = _array[i].equals(array._array[i - difference]);
			} else {
				equals = array._array[i] == null;
			}
		}
		return equals;
	}
	
	/**
	 * Create a string representation of the given array.
	 * 
	 * @param prefix the prefix of the string representation; e.g {@code '['}.
	 * @param separator the separator of the array elements; e.g. {@code ','}.
	 * @param suffix the suffix of the string representation; e.g. {@code ']'}.
	 * @return the string representation of this array.
	 */
	public String toString(
		final String prefix, 
		final String separator,
		final String suffix
	) {
		  final StringBuilder out = new StringBuilder();
		  
		  out.append(prefix);
		  if (length() > 0) {
			out.append(_array[_start]);
		  }
		  for (int i = _start + 1; i < _end; ++i) {
			out.append(separator);
			out.append(_array[i]);
		  }
		  out.append(suffix);
		  
		  return out.toString();		
	}
	
	@Override
	public String toString() {
		  return toString("[", ",", "]");
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

