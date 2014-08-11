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

import static org.jenetics.internal.util.arrays.indexes;
import static org.jenetics.internal.util.arrays.swap;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-11 $</em>
 */
public abstract class IndexSorter {
	private static final int INSERTION_SORT_THRESHOLD = 75;

	static final IndexSorter INSERTION_SORTER = new InsertionSorter();
	static final IndexSorter HEAP_SORTER = new HeapSorter();

	abstract int[] sort(final double[] array, final int[] indexes);

	public static int[] sort(final double[] array) {
		final IndexSorter sorter = array.length < INSERTION_SORT_THRESHOLD ?
			INSERTION_SORTER :
			HEAP_SORTER;

		return sorter.sort(array, indexes(array.length));
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}

final class HeapSorter extends IndexSorter {
	@Override
	int[] sort(final double[] array, final int[] indexes) {
		int N = array.length;
		for (int k = N/2; k >= 0; k--) {
			sink(array, indexes, k, N);
		}

		while (N > 0) {
			swap(indexes, 0, --N);
			sink(array, indexes, 0, N);
		}

		return indexes;
	}

	private static void sink(
		final double[] array, final int[] indexes,
		final int k, final int N
	) {
		int m = k;
		while (2*m < N) {
			int j = 2*m;
			if (j < N - 1 && array[indexes[j]] < array[indexes[j + 1]]) j++;
			if (array[indexes[m]] >= array[indexes[j]]) break;
			swap(indexes, m, j);
			m = j;
		}
	}

}

final class InsertionSorter extends IndexSorter {
	@Override
	int[] sort(final double[] array, final int[] indexes) {
		for (int sz = array.length, i = 1; i < sz; ++i) {
			int j = i;
			while (j > 0) {
				if (array[indexes[j - 1]] > array[indexes[j]]) {
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
