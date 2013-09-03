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

import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public class ArrayProxyIterator<T> implements ListIterator<T> {

	protected final ArrayProxy<T> _proxy;

	private int _pos = 0;

	public ArrayProxyIterator(final ArrayProxy<T> proxy) {
		_proxy = proxy;
	}

	@Override
	public boolean hasNext() {
		return _pos < _proxy._length;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return _proxy.uncheckedGet(_pos++);
	}

	@Override
	public int nextIndex() {
		return _pos;
	}

	@Override
	public boolean hasPrevious() {
		return _pos > 0;
	}

	@Override
	public T previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException();
		}
		return _proxy.uncheckedGet(--_pos);
	}

	@Override
	public int previousIndex() {
		return _pos - 1;
	}

	@Override
	public void set(final T value) {
		throw new UnsupportedOperationException(
			"Iterator is immutable."
		);
	}

	@Override
	public void add(final T value) {
		throw new UnsupportedOperationException(
			"Can't change Iteratior size."
		);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
			"Can't change Iterator size."
		);
	}

}
