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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
abstract class ArraySeq<T> implements Seq<T>, Serializable {
	private static final long serialVersionUID = 1L;
	
	transient ArrayData _data = null;
	
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
	ArraySeq(final Object[] array, final int start, final int end, final boolean sealed) {
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
	ArraySeq(final Object[] array, final boolean sealed) {
		this(array, 0, array.length, sealed);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		checkIndex(index);
		return (T)_array[index + _start];
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
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
	protected ArraySeq<T> clone() {
		try {
			return (ArraySeq<T>)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public Object[] toArray() {
		final Object[] array = new Object[length()];
		System.arraycopy(_array, _start, array, 0, length());
		return array;
	}
	
	@Override
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
	
	@Override
	public List<T> asList() {
		return new ArraySeqList<T>(this);
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
		if (!(obj instanceof ArraySeq<?>)) {
			return false;
		}
		
		final ArraySeq<?> array = (ArraySeq<?>)obj;
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
	
	@Override
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
