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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayList.java,v 1.1 2008-08-29 21:18:15 fwilhelm Exp $
 */
final class ArrayList<T> extends AbstractList<T> 
	implements RandomAccess, java.io.Serializable 
{
	private static final long serialVersionUID = -3687635182118067928L;

	private final Object[] _array;
	
	public ArrayList(final Object[] array) {
		Validator.notNull(array, "Array");
		_array = array;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		return (T)_array[index];
	}
	
	@Override
	public T set(int index, T element) {
		@SuppressWarnings("unchecked")
		final T old = (T)_array[index];
		_array[index] = element;
		return old;
	}

	@Override
	public int size() {
		return _array.length;
	}

	@Override
	public int indexOf(final Object element) {
		return ArrayUtils.indexOf(_array, element);
	}
	
	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}
	
	@Override
	public Object[] toArray() {
		return _array.clone();
	}
	
	@Override
	public <E> E[] toArray(final E[] array) {
		final int size = size();
		
		if (array.length < size) {
			@SuppressWarnings("unchecked")
			final E[] copy = (E[])Arrays.copyOf(
				_array, size, (Class<? extends T[]>) array.getClass()
			);
			return copy;
		}
		
		System.arraycopy(_array, 0, array, 0, size);
		if (array.length > size) {
			array[size] = null;
		}
		return array;
	}
    
}









