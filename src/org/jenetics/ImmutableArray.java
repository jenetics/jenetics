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
 */
package org.jenetics;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: ImmutableArray.java,v 1.1 2008-07-05 20:28:13 fwilhelm Exp $
 */
final class ImmutableArray<T> extends Array<T> {
	private Array<T> _array;

	public ImmutableArray(final Array<T> array) {
		_array = array;
	}
	
	@Override
	public Array<T> copy() {
		return _array.copy();
	}

	@Override
	public boolean equals(Object obj) {
		return _array.equals(obj);
	}

	@Override
	public T get(int index) {
		return _array.get(index);
	}

	@Override
	public int hashCode() {
		return _array.hashCode();
	}

	@Override
	public Iterator<T> iterator() {
		return _array.iterator();
	}

	@Override
	public int length() {
		return _array.length();
	}

	@Override
	public ListIterator<T> listIterator() {
		return _array.listIterator();
	}

	@Override
	public void set(int index, T value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return _array.toString();
	}
	
	
}
