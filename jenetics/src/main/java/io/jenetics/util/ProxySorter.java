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

import static io.jenetics.util.IndexSorters.INSERTION_SORT_THRESHOLD;

import java.util.Comparator;

/**
 * An {@code IndexSorter} doesn't touch the original array type, instead
 * an index lookup array is returned which allows to access the array in
 * an sorted order. The arrays are sorted in ascending order.
 *
 * <pre>{@code
 * final double[] array = new Random().doubles(100).toArray();
 * final int[] indexes = sorter.sort(array);
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface ProxySorter {
	/**
	 * General array sort algorithm.
	 *
	 * @param array the array which is sorted
	 * @param length the array length
	 * @param comparator the array element comparator
	 * @param <T> the array type
	 * @return the sorted index array
	 */
	public <T> int[] sort(
		final T array,
		final int length,
		final ProxyComparator<? super T> comparator
	);


	/* *************************************************************************
	 * Default implementations for common array types.
	 * ************************************************************************/

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @return the index lookup array - &forall; i &isin; [0, N): index[i] = i
	 * @throws NullPointerException if one of the array is {@code null}
	 */
	public default int[] sort(final int[] array) {
		return sort(array, array.length, (a, i, j) -> Integer.compare(a[i], a[j]));
	}

	/**
	 * Sorting the given {@code array} by changing the given {@code indexes}.
	 * The order of the original {@code array} stays unchanged.
	 *
	 * @param array the array to sort
	 * @return the index lookup array - &forall; i &isin; [0, N): index[i] = i
	 * @throws NullPointerException if one of the array is {@code null}
	 */
	public default int[] sort(final double[] array) {
		return sort(array, array.length, (a, i, j) -> Double.compare(a[i], a[j]));
	}


	public default <T> int[] sort(
		final Seq<? extends T> array,
		final Comparator<? super T> comparator
	) {
		return sort(
			array, array.size(),
			(a, i, j) -> comparator.compare(a.get(i), a.get(j))
		);
	}




	/* *************************************************************************
	 * Static helper methods.
	 * ************************************************************************/

	public static ProxySorter heap() {
		return HeapProxySorter.INSTANCE;
	}

	public static ProxySorter insertion() {
		return InsertionIndexSorter.INSTANCE;
	}

	public static ProxySorter sorter() {
		return InsertionIndexSorter.INSTANCE;
	}

	public static ProxySorter sorter(final int size) {
		return size < INSERTION_SORT_THRESHOLD
			? InsertionIndexSorter.INSTANCE
			: HeapProxySorter.INSTANCE;
	}

}
