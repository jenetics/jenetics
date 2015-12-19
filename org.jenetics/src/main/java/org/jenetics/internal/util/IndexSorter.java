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

import static org.jenetics.internal.util.array.swap;

/**
 * Implementations of this class doesn't sort the given array directly, instead
 * an index lookup array is returned which allows to access the array in
 * an sorted order. The arrays are sorted in descending order.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public abstract class IndexSorter {

	// This value has been chosen after JMH benchmarking.
//	Benchmark                                   Mode  Samples      Score  Score error  Units
//	o.j.i.u.IndexSorterPerf.heapSort160         avgt       14   5560.895       80.158  ns/op
//	o.j.i.u.IndexSorterPerf.heapSort250         avgt       14   9516.441      119.648  ns/op
//	o.j.i.u.IndexSorterPerf.heapSort320         avgt       14  12722.461      103.487  ns/op
//	o.j.i.u.IndexSorterPerf.heapSort80          avgt       14   2473.058       27.884  ns/op
//	o.j.i.u.IndexSorterPerf.insertionSort160    avgt       14  10877.158      550.338  ns/op
//	o.j.i.u.IndexSorterPerf.insertionSort250    avgt       14  25731.100      925.196  ns/op
//	o.j.i.u.IndexSorterPerf.insertionSort320    avgt       14  41864.108     1801.247  ns/op
//	o.j.i.u.IndexSorterPerf.insertionSort80     avgt       14   2643.726      165.315  ns/op
	//private static final int INSERTION_SORT_THRESHOLD = 80;
	private static final int INSERTION_SORT_THRESHOLD = 80;

	static final IndexSorter INSERTION_SORTER = new InsertionSorter();
	static final IndexSorter HEAP_SORTER = new HeapSorter();

	/**
	 * This method must be implemented by the different sorting algorithms.
	 *
	 * @param array the array to sort
	 * @param indexes the index lookup array -
	 *        &forall; i &isin; [0, N): index[i] = i
	 * @return the given {@code indexes} which is now "sorted"
	 */
	abstract int[] sort(final double[] array, final int[] indexes);

	/**
	 * Return an new sorted index lookup array. The given array is not touched.
	 *
	 * @param array the array to sort.
	 * @return the index lookup array
	 */
	public static int[] sort(final double[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD
			? INSERTION_SORTER
			: HEAP_SORTER;

		return sorter.sort(array, indexes(array.length));
	}

	static int[] indexes(final int length) {
		final int[] indexes = new int[length];
		for (int i = length; --i >= 0;) {
			indexes[i] = i;
		}
		return indexes;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

/**
 * Heap sort implementation.
 */
final class HeapSorter extends IndexSorter {

	@Override
	int[] sort(final double[] array, final int[] indexes) {
		// Heapify
		for (int k = array.length/2; k >= 0; --k) {
			sink(array, indexes, k, array.length);
		}

		// Sort down.
		for (int i = array.length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, 0, i);
		}

		return indexes;
	}

	private static void sink(
		final double[] array,
		final int[] indexes,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && array[indexes[j]] > array[indexes[j + 1]]) ++j;
			if (array[indexes[m]] <= array[indexes[j]]) break;
			swap(indexes, m, j);
			m = j;
		}
	}

}

/**
 * Insertion sort implementation.
 */
final class InsertionSorter extends IndexSorter {

	@Override
	int[] sort(final double[] array, final int[] indexes) {
		for (int i = 1, n = array.length; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (array[indexes[j - 1]] < array[indexes[j]]) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}

		return indexes;
	}

}
