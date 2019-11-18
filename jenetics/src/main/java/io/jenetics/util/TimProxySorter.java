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
 * Implementing the Tim sort algorithm.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.1
 */
final class TimProxySorter {

	private TimProxySorter() {
	}

	private final static int RUN = 32;

	// Main sort method.
	static <T> int[] sort(
		final T array,
		final int length,
		final ProxySorter.Comparator<? super T> comparator
	) {
		final int[] proxy = ProxySorter.indexes(length);
		if (length < 2) {
			return proxy;
		}

		// Sorting the sub-arrays with binary insertion sort.
		for (int i = 0; i < length; i += RUN) {
			BinaryInsertionSort.sort(
				array, i,
				min(i + RUN, length),
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

	// Merges the sorted runs.
	private static <T> void merge(
		final T array,
		final int[] proxy,
		final int begin,
		final int mid,
		final int end,
		final ProxySorter.Comparator<? super T> cmp
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
			proxy[k++] = cmp.compare(array, left[i], right[j]) <= 0
				? left[i++]
				: right[j++];
		}

		// Copy remaining elements from the left.
		while (i < left.length) {
			proxy[k++] = left[i++];
		}

		// Copy remaining elements from the right.
		while (j < right.length) {
			proxy[k++] = right[j++];
		}
	}

}
