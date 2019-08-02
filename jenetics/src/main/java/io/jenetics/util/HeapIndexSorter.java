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

import static io.jenetics.internal.util.array.swap;

import java.util.function.ToIntFunction;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class HeapIndexSorter<T> implements IndexSorter<T> {

	private final ToIntFunction<T> _length;
	private final IndexComparator<T> _comparator;

	HeapIndexSorter(
		final ToIntFunction<T> length,
		final IndexComparator<T> comparator
	) {
		_length = length;
		_comparator = comparator;
	}

	public void sort(
		final T array,
		final int[] indexes
	) {
		final int length = _length.applyAsInt(array);

		// Heapify
		for (int k = length/2; k >= 0; --k) {
			sink(array, indexes, _comparator, k, length);
		}

		// Sort down.
		for (int i = length; --i >= 1;) {
			swap(indexes, 0, i);
			sink(array, indexes, _comparator, 0, i);
		}
	}

	private static <T> void sink(
		final T array,
		final int[] indexes,
		final IndexComparator<T> comparator,
		final int start,
		final int end
	) {
		int m = start;
		while (2*m < end) {
			int j = 2*m;
			if (j < end - 1 && comparator.compare(array, indexes[j], indexes[j + 1]) > 0) ++j;
			if (comparator.compare(array, indexes[m], indexes[j]) <= 0) break;
			swap(indexes, m, j);
			m = j;
		}
	}

}
