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

import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public class ArrayProxyIterator<T> implements ListIterator<T> {

	final ArrayProxy<T> _proxy;

	private int _pos = -1;

	public ArrayProxyIterator(final ArrayProxy<T> proxy) {
		_proxy = proxy;
	}

	@Override
	public boolean hasNext() {
		return _pos < _proxy._length - 1;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return _proxy.uncheckedGet(++_pos);
	}

	@Override
	public int nextIndex() {
		return _pos + 1;
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
			"Can't change Iteration size."
		);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
			"Can't change Iterattion size."
		);
	}

}
