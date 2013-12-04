/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.5 &mdash; <em>$Date$</em>
 */
public class ArrayProxyList<T> extends AbstractList<T>
	implements RandomAccess
{

	protected final ArrayProxy<T> _proxy;

	public ArrayProxyList(final ArrayProxy<T> proxy) {
		_proxy = requireNonNull(proxy, "ArrayProxy must not be null.");
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
				if (_proxy.__get(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = _proxy._start, n = _proxy._end;
				i < n && index == -1; ++i)
			{
				if (element.equals(_proxy.__get(i))) {
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
