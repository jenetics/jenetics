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
package org.jenetics;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.jenetics.util.Function;
import org.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
abstract class ArrayProxy<T> {

	final int _start;
	final int _end;
	final int _length;

	ArrayProxy(final int start, final int end) {
		_start = start;
		_end = end;
		_length = _end - _start;
	}

	final void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, _length
			));
		}
	}

	final void checkIndex(final int from, final int to) {
		if (from > to) {
			throw new ArrayIndexOutOfBoundsException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0 || to > _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", from, to
			));
		}
	}

	T get(final int index) {
		checkIndex(index);
		return uncheckedOffsetGet(index + _start);
	}

	T uncheckedGet(final int index) {
		return uncheckedOffsetGet(index + _start);
	}

	abstract T uncheckedOffsetGet(final int absoluteIndex);

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
abstract class ArrayProxySeq<T> implements Seq<T> {

	final ArrayProxy<T> _proxy;

	ArrayProxySeq(final ArrayProxy<T> proxy) {
		_proxy = proxy;
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
		requireNonNull(mapper, "Mapper must not be null");

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
	@Deprecated
	public <R> void foreach(final Function<? super T, ? extends R> function) {
		forEach(function);
	}

	@Override
	public <R> void forEach(final Function<? super T, ? extends R> function) {
		requireNonNull(function, "Function");

		for (int i = _proxy._start; i < _proxy._end; ++i) {
			function.apply(_proxy.uncheckedGet(i));
		}
	}

	@Override
	@Deprecated
	public boolean forall(final Function<? super T, Boolean> predicate) {
		return forAll(predicate);
	}

	@Override
	public boolean forAll(final Function<? super T, Boolean> predicate) {
		requireNonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = _proxy._start; i < _proxy._end && valid; ++i) {
			valid = predicate.apply(_proxy.uncheckedGet(i));
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
				if (_proxy.uncheckedOffsetGet(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = start + _proxy._start, n = end + _proxy._start;
				i < n && index == -1; ++i)
				{
					if (element.equals(_proxy.uncheckedOffsetGet(i))) {
						index = i;
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
			if (predicate.apply(_proxy.uncheckedOffsetGet(i))) {
				index = i;
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

		if (element instanceof BitGene) {
			for (int i = end + _proxy._start;
				--i >= start + _proxy._start && index == -1;)
			{
				if (_proxy.uncheckedOffsetGet(i) == element) {
					index = i;
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
		requireNonNull(predicate, "Predicate");

		int index = -1;

		for (int i = end + _proxy._start;
			--i >= start + _proxy._start && index == -1;)
		{
			if (predicate.apply(_proxy.uncheckedOffsetGet(i))) {
				index = i;
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
		final Object[] array = new Object[length()];
		for (int i = length(); --i >= 0;) {
			array[i] = _proxy.uncheckedGet(i);
		}
		return array;
	}

	@Override
	public T[] toArray(final T[] array) {
		return new ArrayProxyList<>(_proxy).toArray(array);
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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since @__new_version__@
 * @version @__new_version__@ &mdash; <em>$Date$</em>
 */
final class ArrayProxyIterator<T> implements ListIterator<T> {

	final ArrayProxy<T> _proxy;

	private int _pos = -1;

	ArrayProxyIterator(final ArrayProxy<T> proxy) {
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

class ArrayProxyList<T> extends AbstractList<T>
	implements RandomAccess
	{
	private final ArrayProxy<T> _proxy;

	ArrayProxyList(final ArrayProxy<T> proxy) {
		_proxy = proxy;
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
				if (_proxy.uncheckedOffsetGet(i) == null) {
					index = i;
				}
			}
		} else {
			for (int i = _proxy._start, n = _proxy._end;
				i < n && index == -1; ++i)
			{
				if (element.equals(_proxy.uncheckedOffsetGet(i))) {
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





