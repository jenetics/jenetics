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
 * @version 1.2 &mdash; <em>$Date: 2013-05-25 $</em>
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
	public void sort(final int from, final int to) {
		sort(from, to, new Comparator<T>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public int compare(final T o1, final T o2) {
				return ((Comparable)o1).compareTo(o2);
			}
		});
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

				for (int i = (end - start); --i >= 0;) {
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

			for (int i = (end - start); --i >= 0;) {
				final Object temp = _array.data[_start + start + i];
				_array.data[_start + start + i] = (
					other._array.data[other._start + otherStart + i]
				);
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
	public Array<T> setAll(final Iterable<? extends T> values) {
		return setAll(values.iterator());
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
		nonNull(factory);

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


	/* *************************************************************************
	 * Static factory methods.
	 **************************************************************************/

	/**
	 * Create a new array from the given values.
	 *
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	@SafeVarargs
	public static <T> Array<T> valueOf(final T... values) {
		Array<T> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			System.arraycopy(values, 0, array._array.data, 0, values.length);
		}

		return array;
	}

	/**
	 * Create a new Array from the values of the given {@code Collection}. The
	 * order of the elements are determined by the iterator of the Collection.
	 *
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public static <T> Array<T> valueOf(final Collection<? extends T> values) {
		Array<T> array = empty();
		if (values.size() > 0) {
			array = new Array<>(values.size());
			int index = 0;
			for (Iterator<? extends T> it = values.iterator(); it.hasNext(); ++index) {
				array._array.data[index] = it.next();
			}
		}

		return array;
	}

	/**
	 * Create a new Array from the values of the given {@code Seq}.
	 *
	 * @param values the array values.
	 * @throws NullPointerException if the {@code values} array is {@code null}.
	 */
	public static <T> Array<T> valueOf(final Seq<T> values) {
		Array<T> array = empty();
		if (values.length() > 0) {
			if (values instanceof Array<?>) {
				array = ((Array<T>)values).copy();
			} else {
				array = new Array<>(values.length());
				int index = 0;
				for (Iterator<? extends T>
					it = values.iterator(); it.hasNext(); ++index)
				{
					array._array.data[index] = it.next();
				}
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Boolean>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Boolean> box(final boolean... values) {
		Array<Boolean> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}


	/**
	 * Boxes the given native array into an {@code Array<Char>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Character> box(final char... values) {
		Array<Character> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Short>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Short> box(final short... values) {
		Array<Short> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Integer>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Integer> box(final int... values) {
		Array<Integer> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Long>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Long> box(final long... values) {
		Array<Long> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Float>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Float> box(final float... values) {
		Array<Float> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Boxes the given native array into an {@code Array<Double>}.
	 *
	 * @param values the native array to box.
	 * @return the boxed array.
	 */
	public static Array<Double> box(final double... values) {
		Array<Double> array = empty();
		if (values.length > 0) {
			array = new Array<>(values.length);
			for (int i = values.length; --i >= 0;) {
				array._array.data[i] = values[i];
			}
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static boolean[] unboxBoolean(final Array<Boolean> values) {
		final boolean[] array = new boolean[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Boolean)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static char[] unboxChar(final Array<Character> values) {
		final char[] array = new char[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Character)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static short[] unboxShort(final Array<Short> values) {
		final short[] array = new short[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Short)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static int[] unboxInt(final Array<Integer> values) {
		final int[] array = new int[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Integer)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static long[] unboxLong(final Array<Long> values) {
		final long[] array = new long[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Long)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static float[] unboxFloat(final Array<Float> values) {
		final float[] array = new float[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Float)values._array.data[i];
		}

		return array;
	}

	/**
	 * Unboxes the given array to the corresponding native version.
	 *
	 * @param values the {@code Array} to unbox.
	 * @return the unboxed native array.
	 */
	public static double[] unboxDouble(final Array<Double> values) {
		final double[] array = new double[values.length()];
		for (int i = values._start; i < values._end; ++i) {
			array[i - values._start] = (Double)values._array.data[i];
		}

		return array;
	}

}











