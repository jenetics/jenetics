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

import static org.jenetics.util.object.nonNull;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &ndash; <em>$Revision$</em>
 */
class ArraySeqList<T> extends AbstractList<T>
	implements RandomAccess
{
	final ArraySeq<T> _array;

	public ArraySeqList(final ArraySeq<T> array) {
		_array = nonNull(array, "ArrayBase");
	}

	@Override
	public T get(final int index) {
		return _array.get(index);
	}

	@Override
	public int size() {
		return _array.length();
	}

	@Override
	public int indexOf(final Object element) {
		return _array.indexOf(element);
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public Object[] toArray() {
		return _array.toArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < _array.length()) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
					array.getClass().getComponentType(), _array.length()
				);
			for (int i = 0; i < _array.length(); ++i) {
				copy[i] = (E)_array.get(i);
			}

			return copy;
		}

		System.arraycopy(_array._array, _array._start, array, 0, array.length);
		return array;
	}

}









