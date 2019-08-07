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

import java.util.Comparator;
import java.util.List;

/**
 * An {@code ProxySort} doesn't sort a given array directly, instead
 * an index lookup array is returned which allows to access the array in
 * an sorted order.
 *
 * <pre>{@code
 * final double[] array = new Random().doubles(100).toArray();
 * final int[] indexes = ProxySorter.sort(array);
 *
 * // 'Classical' array sort.
 * final double[] sorted = array.clone();
 * Arrays.sort(sorted);
 *
 * // Iterating the array in ascending order.
 * for (int i = 0; i < array.length; ++i) {
 *     assert sorted[i] == array[indexes[i]];
 * }
 * }</pre>
 *
 * @see ProxyComparator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class ProxySorter {

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
		final ProxyComparator<? super T> comparator
	) {
		return TimProxySorter.sort(array, length, comparator);
	}


	/* *************************************************************************
	 * Derived sorting methods.
	 * ************************************************************************/

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static int[] sort(final int[] array) {
		return sort(array, array.length, ProxySorters::compare);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static int[] sort(final double[] array) {
		return sort(array, array.length, ProxySorters::compare);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final T[] array,
		final Comparator<? super T> comparator
	) {
		return sort(
			array, array.length,
			(a, i, j) -> comparator.compare(a[i], a[j])
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
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
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final Seq<? extends T> array,
		final Comparator<? super T> comparator
	) {
		return sort(
			array, array.size(),
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if the array is {@code null}
	 */
	public static <T extends Comparable<? super T>>
	int[] sort(final Seq<? extends T> array) {
		return sort(
			array, array.size(),
			(a, i, j) -> a.get(i).compareTo(a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
	 *
	 * @param <T> the array element type
	 * @param array the array to sort
	 * @param comparator the array element comparator
	 * @return the <em>sorted</em> index lookup array
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <T> int[] sort(
		final List<? extends T> array,
		final Comparator<? super T> comparator
	) {
		return sort(
			array, array.size(),
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}

	/**
	 * Sorting the given array by creating an index lookup array.
	 *
	 * @see #sort(Object, int, ProxyComparator)
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

}
