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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static java.lang.Math.min;
import static org.jenetics.util.object.nonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Function;

import javolution.context.StackContext;
import javolution.util.FastList;

/**
 * Array class which wraps the the java build in array type T[]. Once the array
 * is created the array length can't be changed (like the build in array).
 * <strong>This array is not synchronized.</strong> If multiple threads access
 * an {@code Array} concurrently, and at least one of the threads modifies the
 * array, it <strong>must</strong> be synchronized externally.
 *
 * @param <T> the element type of the array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date: 2013-02-02 $</em>
 */
public final class Array<T>
	extends ArraySeq<T>
	implements
		MSeq<T>,
		RandomAccess
{
	private static final long serialVersionUID = 2L;

	@SuppressWarnings("rawtypes")
	private static final Array EMPTY = new Array(0);

	/**
	 * Return the empty array.
	 *
	 * @param <T> the element type.
	 * @return empty array.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Array<T> empty() {
		return EMPTY;
	}

	Array(final ArrayRef array, final int start, final int end) {
		super(array, start, end);
	}

	/**
	 * Create a new array with the given length.
	 *
	 * @param length the array length.
	 * @throws NegativeArraySizeException if the specified {@code length}
	 *          is negative
	 */
	public Array(final int length) {
		super(length);
	}

	/**
	 * Create a new array with length one. The array will be initialized with
	 * the given value.
	 *
	 * @param first the only element of the array.
	 */
	public Array(final T first) {
		this(1);
		_array.data[0] = first;
	}

	/**
	 * Create a new array with length two. The array will be initialized with
	 * the given values.
	 *
	 * @param first first array element.
	 * @param second second array element.
	 */
	public Array(
		final T first,
		final T second
	) {
		this(2);
		_array.data[0] = first;
		_array.data[1] = second;
	}

	/**
	 * Create a new array with length three. The array will be initialized with
	 * the given values.
	 *
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 */
	public Array(
		final T first,
		final T second,
		final T third
	) {
		this(3);
		_array.data[0] = first;
		_array.data[1] = second;
		_array.data[2] = third;
	}

	/**
	 * Create a new array with length four. The array will be initialized with
	 * the given values.
	 *
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 */
	public Array(
		final T first,
		final T second,
		final T third,
		final T fourth
	) {
		this(4);
		_array.data[0] = first;
		_array.data[1] = second;
		_array.data[2] = third;
		_array.data[3] = fourth;
	}

	/**
	 * Create a new array with length five. The array will be initialized with
	 * the given values.
	 *
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 * @param fifth fifth array element.
	 */
	public Array(
		final T first,
		final T second,
		final T third,
		final T fourth,
		final T fifth
	) {
		this(5);
		_array.data[0] = first;
		_array.data[1] = second;
		_array.data[2] = third;
		_array.data[3] = fourth;
		_array.data[4] = fifth;
	}

	/**
	 * Create a new array from the given values.
	 *
	 * @param first first array element.
	 * @param second second array element.
	 * @param third third array element.
	 * @param fourth fourth array element.
	 * @param fifth fifth array element.
	 * @param rest the rest of the array element.
	 * @throws NullPointerException if the {@code rest} array is {@code null}.
	 */
	@SafeVarargs
	public Array(
		final T first,
		final T second,
		final T third,
		final T fourth,
		final T fifth,
		final T... rest
	) {
		this(5 + rest.length);
		_array.data[0] = first;
		_array.data[1] = second;
		_array.data[2] = third;
		_array.data[3] = fourth;
		_array.data[4] = fifth;
		System.arraycopy(rest, 0, _array.data, 5, rest.length);
	}

	/**
	 * Create a new array from the given values.
	 *
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public Array(final T[] values) {
		this(values.length);
		System.arraycopy(values, 0, _array.data, 0, values.length);
	}

	/**
	 * Create a new Array from the values of the given Collection. The order of
	 * the elements are determined by the iterator of the Collection.
	 *
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public Array(final Collection<? extends T> values) {
		this(values.size());

		int index = 0;
		for (Iterator<? extends T> it = values.iterator(); it.hasNext(); ++index) {
			_array.data[index] = it.next();
		}
	}

	/**
	 * Selects all elements of this list which satisfy a predicate.
	 *
	 * @param predicate the predicate used to test elements.
	 * @return a new array consisting of all elements of this list that satisfy
	 *         the given {@code predicate}. The order of the elements is
	 *         preserved.
	 * @throws NullPointerException if the given {@code predicate} is
	 *         {@code null}.
	 */
	public Array<T> filter(final Function<? super T, Boolean> predicate) {
		StackContext.enter();
		try {
			final FastList<T> filtered = FastList.newInstance();
			for (int i = 0, n = length(); i < n; ++i) {
				@SuppressWarnings("unchecked")
				final T value = (T)_array.data[i + _start];

				if (predicate.apply(value) == Boolean.TRUE) {
					filtered.add(value);
				}
			}

			final Array<T> copy = new Array<>(filtered.size());
			int index = 0;
			for (FastList.Node<T> n = filtered.head(), end = filtered.tail();
				(n = n.getNext()) != end;)
			{
				copy.set(index++, n.getValue());
			}

			return copy;
		} finally {
			StackContext.exit();
		}
	}

	@Override
	public void set(final int index, final T value) {
		checkIndex(index);

		_array.cloneIfSealed();
		_array.data[index + _start] = value;
	}

	/**
	 * <p>
	 * Sorts the array of objects into ascending order, according to the natural
	 * ordering of its elements. All elements in the array <b>must</b> implement
	 * the Comparable interface. Furthermore, all elements in the array must be
	 * mutually comparable.
	 * </p>
	 * The sorting algorithm is the Quicksort.
	 *
	 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Quicksort">
	 *          Wikipedia: Quicksort
	 *      </a>
	 *
	 * @throws ClassCastException if the array contains elements that are not
	 *          <i>mutually comparable</i> (for example, strings and integers).
	 */
	public void sort() {
		sort(0, length());
	}

	/**
	 * <p>
	 * Sorts the array of objects into ascending order, according to the natural
	 * ordering of its elements. All elements in the array <b>must</b> implement
	 * the Comparable interface. Furthermore, all elements in the array must be
	 * mutually comparable.
	 * </p>
	 * The sorting algorithm is the Quicksort.
	 *
	 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Quicksort">
	 *          Wikipedia: Quicksort
	 *      </a>
	 *
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @throws IndexOutOfBoundsException if {@code from < 0 or to > length()}
	 * @throws IllegalArgumentException if {@code from > to}
	 * @throws ClassCastException if the array contains elements that are not
	 *        <i>mutually comparable</i> (for example, strings and integers).
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sort(final int from, final int to) {
		sort(from, to, (a, b) -> ((Comparable)a).compareTo(b));
	}

	/**
	 * <p>
	 * Sorts the array of objects according to the order induced by the specified
	 * comparator. All elements in the array must be mutually comparable by the
	 * specified comparator.
	 * </p>
	 * The sorting algorithm is the Quicksort.
	 *
	 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Quicksort">
	 *          Wikipedia: Quicksort
	 *      </a>
	 *
	 * @throws NullPointerException if the given {@code comparator} is
	 *          {@code null}.
	 * @throws ClassCastException if the array contains elements that are not
	 *          <i>mutually comparable</i> (for example, strings and integers).
	 */
	public void sort(final Comparator<? super T> comparator) {
		sort(0, length(), comparator);
	}

	/**
	 * <p>
	 * Sorts the array of objects according to the order induced by the specified
	 * comparator. All elements in the array must be mutually comparable by the
	 * specified comparator.
	 * </p>
	 * The sorting algorithm is the <i>Timsort</i>.
	 *
	 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Timsort">
	 *          Wikipedia: Timsort
	 *      </a>
	 * @see Arrays#sort(Object[], int, int, Comparator)
	 *
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @throws NullPointerException if the given {@code comparator} is
	 *          {@code null}.
	 * @throws IndexOutOfBoundsException if {@code from < 0 or to > length()}
	 * @throws IllegalArgumentException if {@code from > to}
	 * @throws ClassCastException if the array contains elements that are not
	 *          <i>mutually comparable</i> (for example, strings and integers).
	 */
	public void sort(
		final int from, final int to,
		final Comparator<? super T> comparator
	) {
		checkIndex(from, to);
		if (from > to) {
			throw new IllegalArgumentException(String.format(
					"From index > to index: %d > %d.", from, to
				));
		}
		nonNull(comparator, "Comparator");

		_array.cloneIfSealed();

		@SuppressWarnings("unchecked")
		final T[] data = (T[])_array.data;
		Arrays.sort(data, from + _start, to + _start, comparator);
	}

	private void uncheckedSwap(final int i, final int j) {
		final Object temp = _array.data[i + _start];
		_array.data[i + _start] = _array.data[j + _start];
		_array.data[j + _start] = temp;
	}

	/**
	 * Reverses the given array in place.
	 *
	 * @return this (reversed) array.
	 */
	public Array<T> reverse() {
		return reverse(0, length());
	}

	/**
	 * Reverses the part of the array determined by the to indexes. The reverse
	 * method is performed in place.
	 *
	 * @param from the first index (inclusive)
	 * @param to the second index (exclusive)
	 * @return this (reversed) array.
	 * @throws IllegalArgumentException if <tt>from &gt; to</tt>
	 * @throws IndexOutOfBoundsException if <tt>from &lt; 0</tt> or
	 *	        <tt>to &gt; a.length</tt>
	 */
	public Array<T> reverse(final int from, final int to) {
		checkIndex(from, to);
		_array.cloneIfSealed();

		int i = from;
		int j = to;
		while (i < j) {
			uncheckedSwap(i++, --j);
		}

		return this;
	}

	@Override
	public void swap(final int i, final int j) {
		checkIndex(i);
		checkIndex(j);

		_array.cloneIfSealed();
		uncheckedSwap(i, j);
	}

	@Override
	public void swap(
		final int start, final int end,
		final MSeq<T> other, final int otherStart
	) {
		if (other instanceof Array<?>) {
			swap(start, end, (Array<T>)other, otherStart);
		} else {
			checkIndex(start, end);
			if (otherStart < 0 || (otherStart + (end - start)) > _length) {
				throw new ArrayIndexOutOfBoundsException(String.format(
					"Invalid index range: [%d, %d)",
					otherStart, (otherStart + (end - start))
				));
			}

			if (start < end) {
				_array.cloneIfSealed();

				for (int i = 0; i < (end - start); ++i) {
					@SuppressWarnings("unchecked")
					final T temp = (T)_array.data[_start + start + i];
					_array.data[_start + start + i] = other.get(otherStart + i);
					other.set(otherStart + i, temp);
				}
			}
		}
	}

	/**
	 * @see MSeq#swap(int, int, MSeq, int)
	 */
	public void swap(
		final int start, final int end,
		final Array<T> other, final int otherStart
	) {
		checkIndex(start, end);
		other.checkIndex(otherStart, otherStart + (end - start));

		if (start < end) {
			_array.cloneIfSealed();
			other._array.cloneIfSealed();

			for (int i = 0; i < (end - start); ++i) {
				final Object temp = _array.data[_start + start + i];
				_array.data[_start + start + i] =
					other._array.data[other._start + otherStart + i];
				other._array.data[other._start + otherStart + i] = temp;
			}
		}
	}

	public Array<T> shuffle(final Random random) {
		_array.cloneIfSealed();

		for (int j = length() - 1; j > 0; --j) {
			uncheckedSwap(j, random.nextInt(j + 1));
		}

		return this;
	}

	@Override
	public Array<T> setAll(final T value) {
		_array.cloneIfSealed();
		for (int i = _start; i < _end; ++i) {
			_array.data[i] = value;
		}
		return this;
	}

	@Override
	public Array<T> setAll(final Iterator<? extends T> it) {
		_array.cloneIfSealed();
		for (int i = _start; i < _end && it.hasNext(); ++i) {
			_array.data[i] = it.next();
		}
		return this;
	}

	@Override
	public Array<T> setAll(final T[] values) {
		_array.cloneIfSealed();
		System.arraycopy(
			values, 0, _array.data, _start, min(length(), values.length)
		);
		return this;
	}

	@Override
	public Array<T> fill(final Factory<? extends T> factory) {
		_array.cloneIfSealed();
		for (int i = _start; i < _end; ++i) {
			_array.data[i] = factory.newInstance();
		}
		return this;
	}

	@Override
	public ISeq<T> toISeq() {
		_array._sealed = true;
		return new ArrayISeq<>(new ArrayRef(_array.data), _start, _end);
	}

	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code value}. The length of the new array is
	 * {@code this.length() + 1}. The returned array is not sealed.
	 *
	 * @param value the value to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 *          given {@code value}
	 */
	public Array<T> add(final T value) {
		final Array<T> array = new Array<>(length() + 1);
		System.arraycopy(_array.data, _start, array._array.data, 0, length());
		array._array.data[array.length() - 1] = value;
		return array;
	}

	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code array}. The length of the new array is
	 * {@code this.length() + array.length()}. The returned array is not sealed.
	 *
	 * @param array the array to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 *          given {@code array}
	 * @throws NullPointerException if the {@code arrays} is {@code null}.
	 */
	public Array<T> add(final Array<? extends T> array) {
		final Array<T> appended = new Array<>(length() + array.length());

		System.arraycopy(
			_array.data, _start,
			appended._array.data, 0, length()
		);
		System.arraycopy(
			array._array.data, array._start,
			appended._array.data, length(), array.length()
		);

		return appended;
	}

	/**
	 * Create a new array which contains the values of {@code this} and the
	 * given {@code values}. The length of the new array is
	 * {@code this.length() + values.size()}. The returned array is not sealed.
	 *
	 * @param values the array to append to this array.
	 * @return a new array which contains the values of {@code this} and the
	 *          given {@code array}
	 * @throws NullPointerException if the {@code values} is {@code null}.
	 */
	public Array<T> add(final Collection<? extends T> values) {
		nonNull(values, "Values");
		final Array<T> array = new Array<>(length() + values.size());

		System.arraycopy(_array.data, _start, array._array.data, 0, length());
		int index = length();
		for (Iterator<? extends T> it = values.iterator(); it.hasNext(); ++index) {
			array._array.data[index] = it.next();
		}

		return array;
	}

	@Override
	public <B> Array<B> map(final Function<? super T, ? extends B> mapper) {
		nonNull(mapper, "Converter");

		final int length = length();
		final Array<B> result = new Array<>(length);
		assert (result._array.data.length == length);

		for (int i = length; --i >= 0;) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array.data[i + _start];
			result._array.data[i] = mapper.apply(value);
		}
		return result;
	}

	@Override
	public Array<T> copy() {
		return new Array<>(new ArrayRef(toArray()), 0, length());
	}

	@Override
	public Array<T> subSeq(final int start, final int end) {
		checkIndex(start, end);
		return new Array<>(_array, start + _start, end + _start);
	}

	@Override
	public Array<T> subSeq(final int start) {
		return subSeq(start, length());
	}

	@Override
	public List<T> asList() {
		return new ArrayMSeqList<>(this);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayMSeqIterator<>(this);
	}

}
