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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jenetics.util.Function;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.5 &mdash; <em>$Date: 2014-03-10 $</em>
 */
public abstract class ArrayProxySeq<T> implements Seq<T> {

	protected final ArrayProxy<T> _proxy;

	public ArrayProxySeq(final ArrayProxy<T> proxy) {
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
	public <B> Iterator<B> iterator(
		final Function<? super T, ? extends B> mapper
	) {
		requireNonNull(mapper, "Mapper must not be null.");

		return new Iterator<B>() {
			private final Iterator<T> _iterator = iterator();
			@Override public boolean hasNext() {
				return _iterator.hasNext();
			}
			@Override public B next() {
				return mapper.apply(_iterator.next());
			}
			@Override public void remove() {
				_iterator.remove();
			}
		};
	}

	@Override
	public <R> void forEach(final Function<? super T, ? extends R> function) {
		requireNonNull(function, "Function");

		for (int i = _proxy._start; i < _proxy._end; ++i) {
			function.apply(_proxy.__get(i));
		}
	}

	@Override
	public boolean forAll(final Function<? super T, Boolean> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = _proxy._start; i < _proxy._end && valid; ++i) {
			valid = predicate.apply(_proxy.__get(i));
		}
		return valid;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public int indexOf(final Object element) {
		return indexOf(element, 0, length());
	}

	@Override
	public int indexOf(final Object element, final int start) {
		return indexOf(element, start, _proxy._length);
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
	public int indexWhere(final Function<? super T, Boolean> predicate) {
		return indexWhere(predicate, 0, _proxy._length);
	}

	@Override
	public int indexWhere(
		final Function<? super T, Boolean> predicate,
		final int start
	) {
		return indexWhere(predicate, start, _proxy._length);
	}

	@Override
	public int indexWhere(
		final Function<? super T, Boolean> predicate,
		final int start,
		final int end
	) {
		_proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = start + _proxy._start, n = end + _proxy._start;
				i < n && index == -1; ++i)
		{
			if (predicate.apply(_proxy.__get(i))) {
				index = i - _proxy._start;
			}
		}

		return index;
	}

	@Override
	public int lastIndexOf(final Object element) {
		return lastIndexOf(element, 0, _proxy._length);
	}

	@Override
	public int lastIndexOf(final Object element, final int end) {
		return lastIndexOf(element, 0, end);
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
	public int lastIndexWhere(final Function<? super T, Boolean> predicate) {
		return lastIndexWhere(predicate, 0, _proxy._length);
	}

	@Override
	public int lastIndexWhere(
		final Function<? super T, Boolean> predicate,
		final int end
	) {
		return lastIndexWhere(predicate, 0, end);
	}

	@Override
	public int lastIndexWhere(
		final Function<? super T, Boolean> predicate,
		final int start,
		final int end
	) {
		_proxy.checkIndex(start, end);
		requireNonNull(predicate, "Predicate must not be null.");

		int index = -1;

		for (int i = end + _proxy._start;
			--i >= start + _proxy._start && index == -1;)
		{
			if (predicate.apply(_proxy.__get(i))) {
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
	public String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		final StringBuilder out = new StringBuilder();

		out.append(prefix);
		if (_proxy._length > 0) {
			out.append(_proxy.get(0));
		}
		for (int i = 1; i < _proxy._length; ++i) {
			out.append(separator);
			out.append(_proxy.get(i));
		}
		out.append(suffix);

		return out.toString();
	}

	@Override
	public String toString(String separator) {
		return toString("", separator, "");
	}

	@Override
	public String toString() {
		  return toString("[", ",", "]");
	}

}
