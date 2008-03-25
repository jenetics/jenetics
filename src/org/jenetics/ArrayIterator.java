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

import java.util.Iterator;

/**
 * Helper class which iterates over an given array.
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ArrayIterator.java,v 1.1 2008-03-25 18:31:55 fwilhelm Exp $
 */
final class ArrayIterator<T> implements Iterator<T> {
	private final T[] _array;
	private int _pos = 0;
	
	public ArrayIterator(final T[] array) {
		Checker.checkNull(array, "Array");
		this._array = array;
	}
	
	@Override
	public boolean hasNext() {
		return _pos < _array.length;
	}
	
	@Override
	public T next()  {
		T value = null;
		if (hasNext()) {
			value = _array[_pos++];
		}
		return value;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
