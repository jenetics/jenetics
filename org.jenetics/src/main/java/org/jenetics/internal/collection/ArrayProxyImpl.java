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

/**
 * {@code ArrayProxy} implementation which stores {@code Object}s.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class ArrayProxyImpl<T>
	extends ArrayProxy<T, Object[], ArrayProxyImpl<T>>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new array proxy implementation.
	 *
	 * @param array the array where the elements are stored.
	 * @param start the start index of the array proxy, inclusively.
	 * @param end the end index of the array proxy, exclusively.
	 */
	public ArrayProxyImpl(final Object[] array, final int start, final int end) {
		super(array, start, end, ArrayProxyImpl<T>::new, o -> o.clone());
	}

	/**
	 * Create a new array proxy implementation.
	 *
	 * @param length the length of the array proxy.
	 */
	public ArrayProxyImpl(final int length) {
		this(new Object[length], 0, length);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T __get(final int absoluteIndex) {
		return (T)_array[absoluteIndex];
	}

	@Override
	public void __set(final int absoluteIndex, final T value) {
		_array[absoluteIndex] = value;
	}

	@Override
	public ArrayProxyImpl<T> copy() {
		final ArrayProxyImpl<T> proxy = new ArrayProxyImpl<>(_length);
		System.arraycopy(_array, _start, proxy._array, 0, _length);
		return proxy;
	}

}
