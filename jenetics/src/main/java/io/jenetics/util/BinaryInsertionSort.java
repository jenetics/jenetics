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

/**
 * Sorts a specified portion of the specified array using a binary insertion
 * sort. This is the best method for sorting small numbers of elements. It
 * requires O(n log n) compares, but O(n^2) data movement (the worst case).
 * <p>
 * If the initial part of the specified range is already sorted, this method can
 * take advantage of it: the method assumes that the elements from index
 * {@code lo}, inclusive, to {@code start}, exclusive are already sorted.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.1
 * @since 5.1
 */
final class BinaryInsertionSort {
	private BinaryInsertionSort() {
	}

	// Sorting method used for testing.
	static <T> int[] sort(
		final T array,
		final int length,
		final ProxySorter.Comparator<? super T> cmp
	) {
		final int[] proxy = ProxySorter.indexes(length);
		if (length < 2) {
			return proxy;
		}

		final int begin = countRunAndMakeAscending(array, 0, length, proxy, cmp);
		binarySort(array, 0, length, begin, proxy, cmp);
		return proxy;
	}

	// Sorting method used by Tim sorter.
	static <T> void sort(
		final T array,
		final int low,
		final int high,
		final int[] proxy,
		final ProxySorter.Comparator<? super T> cmp
	) {
		final int begin = countRunAndMakeAscending(array, low, high, proxy, cmp);
		binarySort(array, low, high, low + begin, proxy, cmp);
	}

	@SuppressWarnings("fallthrough")
	private static <T> void binarySort(
		final T array,
		final int low,
		final int high,
		final int begin,
		final int[] proxy,
		final ProxySorter.Comparator<? super T> cmp
	) {
		assert low <= begin;
		assert begin <= high;

		int start = begin;
		if (start == low) {
			++start;
		}

		for (; start < high; ++start) {
			final int pivot = proxy[start];

			int left = low;
			int right = start;
			assert left <= right;

			while (left < right) {
				final int mid = (left + right) >>> 1;
				if (cmp.compare(array, pivot, proxy[mid]) < 0) {
					right = mid;
				} else {
					left = mid + 1;
				}
			}
			assert left == right;

			int n = start - left;
			switch(n) {
				case 2:
					proxy[left + 2] = proxy[left + 1];
				case 1:
					proxy[left + 1] = proxy[left];
					break;
				default:
					System.arraycopy(proxy, left, proxy, left + 1, n);
			}
			proxy[left] = pivot;
		}
	}

	private static <T> int countRunAndMakeAscending(
		final T array,
		final int low,
		final int high,
		final int[] proxy,
		final ProxySorter.Comparator<? super T> cmp
	) {
		assert low < high;

		int runHi = low + 1;
		if (runHi == high) {
			return 1;
		}

		if (cmp.compare(array, proxy[runHi++], proxy[low]) < 0) {
			while (runHi < high &&
				cmp.compare(array, proxy[runHi], proxy[runHi - 1]) < 0)
			{
				++runHi;
			}

			reverse(proxy, low, runHi - 1);
		} else {
			while (runHi < high &&
				cmp.compare(array, proxy[runHi], proxy[runHi - 1]) >= 0)
			{
				++runHi;
			}
		}

		return runHi - low;
	}

	private static void reverse(final int[] proxy, int lo, int hi) {
		while (lo < hi) {
			final int t = proxy[lo];
			proxy[lo++] = proxy[hi];
			proxy[hi--] = t;
		}
	}

}
