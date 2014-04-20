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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public abstract class ArrayProxySeq<T>
	implements
		Seq<T>,
		Serializable
{
	private static final long serialVersionUID = 1L;

	protected final ArrayProxy<T, ?, ?> _proxy;

	public ArrayProxySeq(final ArrayProxy<T, ?, ?> proxy) {
		_proxy = requireNonNull(proxy, "ArrayProxy must not be null.");
	}

	@Override
	public final T get(final int index) {
		return _proxy.get(index);
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayProxyIterator<>(_proxy);
	}

	public ListIterator<T> listIterator() {
		return new ArrayProxyIterator<>(_proxy);
	}

	@Override
	public void forEach(final Consumer<? super T> consumer) {
		requireNonNull(consumer, "The consumer must not be null.");

		for (int i = _proxy._start; i < _proxy._end; ++i) {
			consumer.accept(_proxy.__get(i));
		}
	}

	@Override
	public boolean forAll(final Predicate<? super T> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = _proxy._start; i < _proxy._end && valid; ++i) {
			valid = predicate.test(_proxy.__get(i));
		}
		return valid;
	}

	@Override
	public int indexOf(final Object element, final int start, final int end) {
		_proxy.checkIndex(start, end);

		int index = -1;
		if (element == null) {
			for (int i = start + _proxy._start, n = end + _proxy._start;
					i < n && index == -1; ++i)
			{
				if (_proxy.__get(i) == null) {
					index = i - _proxy._start;
				}
			}
		} else {
			for (int i = start + _proxy._start, n = end + _proxy._start;
					i < n && index == -1; ++i)
			{
				if (element.equals(_proxy.__get(i))) {
					index = i - _proxy._start;
				}
			}
		}

		return index;
	}

	@Override
	public int indexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		_proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start + _proxy._start, n = end + _proxy._start;
				i < n && index == -1; ++i)
		{
			if (predicate.test(_proxy.__get(i))) {
				index = i - _proxy._start;
			}
		}

		return index;
	}

	@Override
	public int lastIndexOf(final Object element, final int start, final int end) {
		_proxy.checkIndex(start, end);
		int index = -1;

		if (element == null) {
			for (int i = end + _proxy._start;
				--i >= start + _proxy._start && index == -1;)
			{
				if (_proxy.__get(i) == null) {
					index = i - _proxy._start;
				}
			}
		} else {
			for (int i = end + _proxy._start;
				--i >= start + _proxy._start && index == -1;)
			{
				if (element.equals(_proxy.__get(i))) {
					index = i - _proxy._start;
				}
			}
		}

		return index;
	}

	@Override
	public int lastIndexWhere(
		final Predicate<? super T> predicate,
		final int start,
		final int end
	) {
		_proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate must not be null.");

		int index = -1;

		for (int i = end + _proxy._start;
			--i >= start + _proxy._start && index == -1;)
		{
			if (predicate.test(_proxy.__get(i))) {
				index = i - _proxy._start;
			}
		}

		return index;
	}

	@Override
	public int length() {
		return _proxy._length;
	}

	@Override
	public List<T> asList() {
		return new ArrayProxyList<>(_proxy);
	}

	@Override
	public Object[] toArray() {
		return asList().toArray();
	}

	@Override
	public T[] toArray(final T[] array) {
		return asList().toArray(array);
	}

	@Override
	public String toString() {
		return toString("[", ",", "]");
	}
}
