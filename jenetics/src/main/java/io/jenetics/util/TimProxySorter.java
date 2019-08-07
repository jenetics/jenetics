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

import static java.lang.Math.min;

/**
 * Implementing the index sorter with the (quasi) Tim sort algorithm.
 *
 * @implNote
 * To be precise, this is not the exact Timsort algorithm. The scan for the
 * longest sorted run is not implemented. It does an insertion sort for small
 * sub-arrays and merges them back into the bigger array.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class TimProxySorter {

	private TimProxySorter() {
	}

	private final static int RUN = 32;

	static <T> int[] sort(
		final T array,
		final int length,
		final ProxyComparator<? super T> comparator
	) {
		final int[] proxy = ProxySorters.indexes(length);

		// Sorting the sub-arrays with insertion-sort.
		for (int i = 0; i < length; i += RUN) {
			sort(
				array, i,
				min(i + RUN - 1, length - 1),
				proxy,
				comparator
			);
		}

		// Merging sub-arrays.
		for (int size = RUN; size < length; size = 2*size) {
			for (int left = 0; left < length; left += 2*size) {
				final int mid = min(left + size - 1, length - 1);
				final int right = min(left + 2*size - 1, length - 1);

				merge(array, proxy, left, mid, right, comparator);
			}
		}

		return proxy;
	}

	// Insertion sort for sub-arrays.
	private static <T> void sort(
		final T array,
		final int begin,
		final int end,
		final int[] proxy,
		final ProxyComparator<? super T> cmp
	) {
		for (int i = begin + 1; i <= end; ++i) {
			final int temp = proxy[i];

			int j = i - 1;
			while (j >= begin && cmp.compare(array, proxy[j], temp) > 0) {
				proxy[j + 1] = proxy[j];
				--j;
			}

			proxy[j + 1] = temp;
		}
	}

	// Merges the sorted runs.
	private static <T> void merge(
		final T array,
		final int[] proxy,
		final int begin,
		final int mid,
		final int end,
		final ProxyComparator<? super T> cmp
	) {
		final int[] left = new int[mid - begin + 1];
		System.arraycopy(proxy, begin, left, 0, left.length);

		final int[] right = new int[end - mid];
		System.arraycopy(proxy, mid + 1, right, 0, right.length);

		int i = 0;
		int j = 0;
		int k = begin;

		// After comparing, merge the two arrays in larger sub-array.
		while (i < left.length && j < right.length) {
			if (cmp.compare(array, left[i], right[j]) <= 0) {
				proxy[k] = left[i];
				++i;
			} else {
				proxy[k] = right[j];
				++j;
			}
			++k;
		}

		// Copy remaining elements from the left.
		while (i < left.length) {
			proxy[k] = left[i];
			++k;
			++i;
		}

		// Copy remaining elements from the right.
		while (j < right.length) {
			proxy[k] = right[j];
			++k;
			++j;
		}
	}

}
