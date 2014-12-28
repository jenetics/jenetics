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
package org.jenetics.internal.collection;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date: 2014-04-21 $</em>
 */
public class ArrayProxyList<T, P extends ArrayProxy<T, ?, ?>>
	extends AbstractList<T>
	implements
		RandomAccess,
		Serializable
{
	private static final long serialVersionUID = 1L;

	public final P proxy;

	public ArrayProxyList(final P proxy) {
		this.proxy = requireNonNull(proxy, "ArrayProxy must not be null.");
	}

	@Override
	public T get(final int index) {
		return proxy.get(index);
	}

	@Override
	public int size() {
		return proxy.length;
	}

	@Override
	public int indexOf(final Object element) {
		int index = -1;
		if (element == null) {
			for (int i = proxy.start; i < proxy.end && index == -1; ++i) {
				if (proxy.__get__(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = proxy.start; i < proxy.end && index == -1; ++i) {
				if (element.equals(proxy.__get__(i))) {
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
			array[i] = proxy.__get(i);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(final E[] array) {
		if (array.length < size()) {
			final E[] copy = (E[])Array.newInstance(
				array.getClass().getComponentType(), size()
			);
			for (int i = size(); --i >= 0;) {
				copy[i] = (E) proxy.__get(i);
			}

			return copy;
		}

		for (int i = size(); --i >= 0;) {
			array[i] = (E) proxy.__get(i);
		}
		return array;
	}

}
