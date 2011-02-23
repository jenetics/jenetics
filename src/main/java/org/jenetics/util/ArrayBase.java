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

import static java.lang.Math.min;
import static org.jenetics.util.ObjectUtils.hashCodeOf;
import static org.jenetics.util.Validator.nonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
class ArrayBase<T> implements Serializable {
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
	ArrayBase(final Object[] array, final int start, final int end, final boolean sealed) {
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
	ArrayBase(final Object[] array, final boolean sealed) {
		this(array, 0, array.length, sealed);
	}
	
	public void set(final int index, final T value) {
		assertNotSealed();
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
	
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}	
	
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
	public ArrayBase<T> fill(final T value) {
		assertNotSealed();
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
	public ArrayBase<T> fill(final Iterator<? extends T> it) {
		assertNotSealed();
		
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
	public ArrayBase<T> fill(final T[] values) {
		assertNotSealed();
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
	public ArrayBase<T> fill(final Factory<? extends T> factory) {
		Validator.nonNull(factory);
		assertNotSealed();
		for (int i = _start; i < _end; ++i) {
			_array[i] = factory.newInstance();
		}
		return this;
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
	

	@Override
	@SuppressWarnings("unchecked")
	protected ArrayBase<T> clone() {
		try {
			return (ArrayBase<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
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
	
	final void assertNotSealed() {
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
		final ObjectUtils.HashCodeBuilder hash = hashCodeOf(getClass());
		for (int i = _start; i < _end; ++i) {
			hash.and(_array[i]);
		}
		return hash.value();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ArrayBase<?>)) {
			return false;
		}
		
		final ArrayBase<?> array = (ArrayBase<?>)obj;
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
