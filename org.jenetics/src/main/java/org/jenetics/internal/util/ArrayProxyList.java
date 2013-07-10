/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public class ArrayProxyList<T> extends AbstractList<T>
	implements RandomAccess
{

	private final ArrayProxy<T> _proxy;

	public ArrayProxyList(final ArrayProxy<T> proxy) {
		_proxy = requireNonNull(proxy, "ArrayProxy must not be null");
	}

	@Override
	public T get(final int index) {
		return _proxy.get(index);
	}

	@Override
	public int size() {
		return _proxy._length;
	}

	@Override
	public int indexOf(final Object element) {
		int index = -1;
		if (element == null) {
			for (int i = _proxy._start, n = _proxy._end;
				i < n && index == -1; ++i)
			{
				if (_proxy.uncheckedOffsetGet(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = _proxy._start, n = _proxy._end;
				i < n && index == -1; ++i)
			{
				if (element.equals(_proxy.uncheckedOffsetGet(i))) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public Object[] toArray() {
		final Object[] array = new Object[size()];
		for (int i = size(); --i >= 0;) {
			array[i] = _proxy.uncheckedGet(i);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < size()) {
			final E[] copy = (E[])java.lang.reflect.Array.newInstance(
				array.getClass().getComponentType(), size()
			);
			for (int i = size(); --i >= 0;) {
				copy[i] = (E)_proxy.uncheckedGet(i);
			}

			return copy;
		}

		for (int i = size(); --i >= 0;) {
			array[i] = (E)_proxy.uncheckedGet(i);
		}
		return array;
	}

}
