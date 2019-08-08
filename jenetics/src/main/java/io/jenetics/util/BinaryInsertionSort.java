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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class BinaryInsertionSort {
	private BinaryInsertionSort() {
	}

	static <T> int[] sort(T a, int length, ProxySorter.Comparator<? super T> c) {
		final int[] proxy = ProxySorter.indexes(length);
		binarySort(a, 0, length, 0, proxy, c);
		return proxy;
	}

	@SuppressWarnings("fallthrough")
	static <T> void binarySort(
		final T array,
		final int low,
		final int high,
		final int begin,
		final int[] proxy,
		final ProxySorter.Comparator<? super T> cmp
	) {
		assert low <= begin && begin <= high;

		int start = begin;
		if (start == low) {
			start++;
		}

		for ( ; start < high; start++) {
			int pivot = proxy[start];

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

			int n = start - left;  // The number of elements to move
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

}
