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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import static java.util.Objects.checkFromToIndex;

import java.util.List;

/**
 * This sorting methods doesn't sort a given array directly, instead
 * an index lookup array is returned which allows to access the array in
 * an sorted order.
 *
 * <pre>{@code
 * final double[] array = new Random().doubles(100).toArray();
 * final int[] proxy = ProxySorter.sort(array);
 *
 * // 'Classical' array sort.
 * final double[] sorted = array.clone();
 * Arrays.sort(sorted);
 *
 * // Iterating the array in ascending order.
 * for (int i = 0; i < array.length; ++i) {
 *     assert sorted[i] == array[proxy[i]];
 * }
 * }</pre>
 *
 * The minimal requirement of the proxy-sorter will be an access function and
 * the number of elements you want to sort.
 * <pre>{@code
 * final IntFunction<String> access = ...;
 * final int length = 100;
 * final int[] proxy = ProxySorter.sort(
 *     access, length,
 *     (a, i, j) -> a.apply(i).compareTo(a.apply(j))
 * );
 * }</pre>
 * @apiNote
 * The most general sorting method is {@link #sort(Object, int, Comparator)}.
 * All other sorting methods can be created with this method.
 *
 * @see #sort(Object, int, Comparator)
 * @see Comparator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.3
 * @since 5.1
 */
public final class ProxySorter {

	/**
	 * The comparator used for comparing two array elements at the specified
	 * indexes.
	 * <pre>{@code
	 * final ProxySorter.Comparator<double[]> comparator =
	 *     (a, i, j) -> Double.compare(a[i], a[j]);
	 * }</pre>
	 * The example above shows how to create a comparator for {@code double[]}
	 * arrays.
	 *
	 * @see ProxySorter#sort(Object, int, Comparator)
	 * @see ProxySorter
	 *
	 * @param <T> the array type, e.g. {@code int[]}, {@code double[]} or
	 *            {@code Seq<String>}
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 5.1
	 * @since 5.1
	 */
	@FunctionalInterface
	public interface Comparator<T> {

		/**
		 * Compares the two array elements, specified by its indices, for order.
		 * Returns a negative integer, zero, or a positive integer as the first
		 * argument is less than, equal to, or greater than the second.
		 *
		 * @see java.util.Comparator#compare(Object, Object)
		 *
		 * @param array the array where the two comparing elements are fetched
		 * @param i the index of the first array element
		 * @param j the index of the second array element
		 * @return a negative integer, zero, or a positive integer as the first
		 *         argument is less than, equal to, or greater than the second.
		 * @throws NullPointerException if an argument is null and this
		 *         comparator does not permit null arguments
		 */
		int compare(final T array, final int i, final int j);

		/**
		 * Returns a comparator that imposes the reverse ordering of this
		 * comparator.
		 *
		 * @return a comparator that imposes the reverse ordering of this
		 *         comparator.
		 */
		default Comparator<T> reversed() {
			return (a, i, j) -> compare(a, j, i);
		}

	}

	private ProxySorter() {
	}

	/**
	 * Sorting the given array by creating an index lookup array. The original
	 * array is not touched and the returned array can then be used for
	 * iterating the array in ascending order.
	 *
	 * <pre>{@code
	 * final double[] array = ...;
	 * final int[] sorted = ProxySorter.sort(
	 *     array, array.length,
	 *     (a, i, j) -> Doubler.compare(a[i], a[j])
	 * );
	 * for (int i : sorted) {
	 *     System.out.println(array[i]);
	 * }
	 * }</pre>
	 *
	 * @param array the array which is sorted
	 * @param length the array length
	 * @param comparator the array element comparator
	 * @param <T> the array type
	 * @return the sorted index array
	 * @throws NullPointerException if one of the array is {@code null}
	 */
	public static <T> int[] sort(
		final T array,
		final int length,
		final Comparator<? super T> comparator
	) {
		return TimProxySorter.sort(array, length, comparator);
	}

	/**
	 * Sorting the given array by creating an index lookup array. The original
	 * array is not touched and the returned array can then be used for
	 * iterating the array in ascending order.
	 *
	 * <pre>{@code
	 * final double[] array = ...;
	 * final int[] sorted = ProxySorter.sort(
	 *     array, 5, array.length,
	 *     (a, i, j) -> Doubler.compare(a[i], a[j])
	 * );
	 * for (int i : sorted) {
	 *     System.out.println(array[i]);
	 * }
	 * }</pre>
	 *
	 * @since 6.3
	 *
	 * @param array the array which is sorted
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param comparator the array element comparator
	 * @param <T> the array type
	 * @return the sorted index array
	 * @throws NullPointerException if one of the array or comparator is
	 *         {@code null}
	 * @throws IllegalArgumentException if {@code from > to}
	 */
	public static <T> int[] sort(
		final T array,
		final int from,
		final int to,
		final Comparator<? super T> comparator
	) {
		if (from > to) {
			throw new IllegalArgumentException(from + " > " + to);
		}

		final int[] indexes = TimProxySorter.sort(
			array,
			to - from,
			(a, i, j) -> comparator.compare(a, i + from, j + from)
		);

		return add(indexes, from);
	}


	/* *************************************************************************
	 * Derived sorting methods.
	 * ************************************************************************/

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static int[] sort(final int[] array) {
		return sort(array, array.length, ProxySorter::compare);
	}

	private static int compare(final int[] a, final int i, final int j) {
		return Integer.compare(a[i], a[j]);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static int[] sort(final int[] array, final int from, final int to) {
		checkFromToIndex(from, to, array.length);
		return sort(array, from, to, ProxySorter::compare);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static int[] sort(final long[] array) {
		return sort(array, array.length, ProxySorter::compare);
	}

	private static int compare(final long[] a, final int i, final int j) {
		return Long.compare(a[i], a[j]);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static int[] sort(final long[] array, final int from, final int to) {
		checkFromToIndex(from, to, array.length);
		return sort(array, from, to, ProxySorter::compare);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static int[] sort(final double[] array) {
		return sort(array, array.length, ProxySorter::compare);
	}

	private static int compare(final double[] a, final int i, final int j) {
		return Double.compare(a[i], a[j]);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static int[] sort(final double[] array, final int from, final int to) {
		checkFromToIndex(from, to, array.length);
		return sort(array, from, to, ProxySorter::compare);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final T[] array,
		final java.util.Comparator<? super T> comparator
	) {
		return sort(
			array, array.length,
			(a, i, j) -> comparator.compare(a[i], a[j])
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T> int[] sort(
		final T[] array,
		final int from,
		final int to,
		final java.util.Comparator<? super T> comparator
	) {
		checkFromToIndex(from, to, array.length);
		return sort(
			array, from, to,
			(a, i, j) -> comparator.compare(a[i], a[j])
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static <T extends Comparable<? super T>> int[] sort(final T[] array) {
		return sort(
			array, array.length,
			(a, i, j) -> a[i].compareTo(a[j])
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T extends Comparable<? super T>> int[] sort(
		final T[] array,
		final int from,
		final int to
	) {
		checkFromToIndex(from, to, array.length);
		return sort(
			array, from, to,
			(a, i, j) -> a[i].compareTo(a[j])
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final BaseSeq<? extends T> array,
		final java.util.Comparator<? super T> comparator
	) {
		return sort(
			array, array.length(),
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T> int[] sort(
		final BaseSeq<? extends T> array,
		final int from,
		final int to,
		final java.util.Comparator<? super T> comparator
	) {
		checkFromToIndex(from, to, array.length());
		return sort(
			array, from, to,
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static <T extends Comparable<? super T>>
	int[] sort(final BaseSeq<? extends T> array) {
		return sort(
			array, array.length(),
			(a, i, j) -> a.get(i).compareTo(a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T extends Comparable<? super T>>
	int[] sort(final BaseSeq<? extends T> array, final int from, final int to) {
		checkFromToIndex(from, to, array.length());
		return sort(
			array, from, to,
			(a, i, j) -> a.get(i).compareTo(a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final List<? extends T> array,
		final java.util.Comparator<? super T> comparator
	) {
		return sort(
			array, array.size(),
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T> int[] sort(
		final List<? extends T> array,
		final int from,
		final int to,
		final java.util.Comparator<? super T> comparator
	) {
		checkFromToIndex(from, to, array.size());
		return sort(
			array, from, to,
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, Comparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static <T extends Comparable<? super T>>
	int[] sort(final List<? extends T> array) {
		return sort(
			array, array.size(),
			(a, i, j) -> a.get(i).compareTo(a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, int, Comparator)
	 *
	 * @since 6.3
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param from the index of the first element (inclusive) to be sorted
	 * @param to the index of the last element (exclusive) to be sorted
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 * @throws IndexOutOfBoundsException if the sub-range is out of bounds
	 */
	public static <T extends Comparable<? super T>>
	int[] sort(final List<? extends T> array, final int from, final int to) {
		checkFromToIndex(from, to, array.size());
		return sort(
			array, from, to,
			(a, i, j) -> a.get(i).compareTo(a.get(j))
		);
	}

	/* *************************************************************************
	 * Some helper methods.
	 * ************************************************************************/

	/**
	 * Create an initial indexes array of the given {@code length}.
	 *
	 * @param length the length of the indexes array
	 * @return the initialized indexes array
	 */
	static int[] indexes(final int length) {
		return init(new int[length]);
	}

	/**
	 * Initializes the given {@code indexes} array.
	 *
	 * @param indexes the indexes array to initialize
	 * @return the initialized indexes array
	 * @throws NullPointerException if the given {@code indexes} array is
	 *         {@code null}
	 */
	private static int[] init(final int[] indexes) {
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		return indexes;
	}

	private static int[] add(final int[] array, final int b) {
		for (int i = 0; i < array.length; ++i) {
			array[i] += b;
		}
		return array;
	}

}
