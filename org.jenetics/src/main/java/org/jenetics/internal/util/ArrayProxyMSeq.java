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

import static java.lang.Math.min;
import static java.lang.String.format;

import java.util.Iterator;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date: 2013-06-30 $</em>
 */
public class ArrayProxyMSeq<T> extends ArrayProxySeq<T> implements MSeq<T> {

	ArrayProxyMSeq(final ArrayProxy<T> proxy) {
		super(proxy);
	}

	@Override
	public MSeq<T> copy() {
		return null;
	}

	@Override
	public void set(final int index, final T value) {
		_proxy.cloneIfSealed();
		_proxy.set(index, value);
	}

	@Override
	public MSeq<T> setAll(final T value) {
		_proxy.cloneIfSealed();
		for (int i = _proxy._start; i < _proxy._end; ++i) {
			_proxy.uncheckedOffsetSet(i, value);
		}
		return this;
	}

	@Override
	public MSeq<T> setAll(final Iterator<? extends T> it) {
		_proxy.cloneIfSealed();
		for (int i = _proxy._start; i < _proxy._end && it.hasNext(); ++i) {
			_proxy.uncheckedOffsetSet(i, it.next());
		}
		return this;
	}

	@Override
	public MSeq<T> setAll(final Iterable<? extends T> values) {
		return setAll(values.iterator());
	}

	@Override
	public MSeq<T> setAll(final T[] values) {
		_proxy.cloneIfSealed();
		for (int i = 0, n = min(_proxy._length, values.length); i < n; ++i) {
			_proxy.uncheckedSet(i, values[i]);
		}
		return this;
	}

	@Override
	public MSeq<T> fill(Factory<? extends T> factory) {
		_proxy.cloneIfSealed();
		for (int i = _proxy._start; i < _proxy._end; ++i) {
			_proxy.uncheckedOffsetSet(i, factory.newInstance());
		}
		return this;
	}

	@Override
	public void swap(final int i, final int j) {
		final T temp = _proxy.get(i);
		_proxy.uncheckedSet(i, _proxy.get(j));
		_proxy.uncheckedSet(j, temp);
	}

	@Override
	public void swap(int start, int end, MSeq<T> other, int otherStart) {
		_proxy.checkIndex(start, end);
		if (otherStart < 0 || (otherStart + (end - start)) > length()) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %d)",
				otherStart, (otherStart + (end - start))
			));
		}

		if (start < end) {
			_proxy.cloneIfSealed();

			for (int i = (end - start); --i >= 0;) {
				final T temp = _proxy.uncheckedOffsetGet(i + start);
				_proxy.uncheckedOffsetSet(i + start, other.get(otherStart + i));
				other.set(otherStart + i, temp);
			}
		}
	}

	@Override
	public MSeq<T> subSeq(final int start, final int end) {
		return new ArrayProxyMSeq<>(_proxy).subSeq(start, end);
	}

	@Override
	public MSeq<T> subSeq(final int start) {
		return new ArrayProxyMSeq<>(_proxy).subSeq(start);
	}

	@Override
	public <B> MSeq<B> map(Function<? super T, ? extends B> mapper) {
		final ArrayProxyMSeq<B> array = new ArrayProxyMSeq<>(
			new ArrayProxyImpl<B>(length())
		);
		for (int i = 0; i < _proxy._length; ++i) {
			array._proxy.uncheckedSet(i, mapper.apply(_proxy.uncheckedGet(i)));
		}

		return array;
	}

	@Override
	public ISeq<T> toISeq() {
		return new ArrayProxyISeq<>(_proxy.seal());
	}

}




