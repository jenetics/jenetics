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
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-27 $</em>
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









