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

import static java.lang.Math.min;
import static java.lang.String.format;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.5 &mdash; <em>$Date: 2014-02-15 $</em>
 */
public class ArrayProxyMSeq<T> extends ArrayProxySeq<T> implements MSeq<T> {

	public ArrayProxyMSeq(final ArrayProxy<T> proxy) {
		super(proxy);
	}

	@Override
	public MSeq<T> copy() {
		return new ArrayProxyMSeq<>(_proxy.copy());
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayProxyMIterator<>(_proxy);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayProxyMIterator<>(_proxy);
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
			_proxy.__set(i, value);
		}
		return this;
	}

	@Override
	public MSeq<T> setAll(final Iterator<? extends T> it) {
		_proxy.cloneIfSealed();
		for (int i = _proxy._start; i < _proxy._end && it.hasNext(); ++i) {
			_proxy.__set(i, it.next());
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
			_proxy.__set(i, factory.newInstance());
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
		checkIndex(start, end, otherStart, other.length());

		if (start < end) {
			if (other instanceof ArrayProxyMSeq<?>) {
				swap(start, end, (ArrayProxyMSeq<T>)other, otherStart);
			} else {
				_proxy.cloneIfSealed();

				for (int i = (end - start); --i >= 0;) {
					final T temp = _proxy.uncheckedGet(i + start);
					_proxy.uncheckedSet(i + start, other.get(otherStart + i));
					other.set(otherStart + i, temp);
				}
			}
		}
	}

	private void swap(int start, int end, ArrayProxyMSeq<T> other, int otherStart) {
		_proxy.swap(start, end, other._proxy, otherStart);
	}

	private void checkIndex(
		final int start, final int end,
		final int otherStart, final int otherLength
	) {
		_proxy.checkIndex(start, end);
		if (otherStart < 0 || (otherStart + (end - start)) > otherLength) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %d)",
				otherStart, (otherStart + (end - start))
			));
		}
	}

	@Override
	public MSeq<T> subSeq(final int start, final int end) {
		return new ArrayProxyMSeq<>(_proxy.slice(start, end));
	}

	@Override
	public MSeq<T> subSeq(final int start) {
		return new ArrayProxyMSeq<>(_proxy.slice(start));
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

	@Override
	public List<T> asList() {
		return new ArrayProxyMList<>(_proxy);
	}

}
