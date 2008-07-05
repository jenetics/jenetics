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
package org.jenetics;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

import javolution.context.ObjectFactory;

/** 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: Array.java,v 1.1 2008-07-05 20:28:13 fwilhelm Exp $
 */
public class Array<T> implements Iterable<T> {
	private Object[] _array;
	
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
	
	public void set(final int index, final T value) {
		_array[index] = value;
	}
	
	void setAllNull() {
		for (int i = 0; i < _array.length; ++i) {
			_array[i] = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_array[index];
	}
	
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
		if (a._array == null || a._array.length != length) {
			a._array = new Object[length];
		} else {
			a.setAllNull();
		}
		return a;
	}
	
	public Array<T> copy() {
		Array<T> copy = newInstance(length());
		System.arraycopy(_array, 0, copy._array, 0, length());
		return copy;
	}
	
}


