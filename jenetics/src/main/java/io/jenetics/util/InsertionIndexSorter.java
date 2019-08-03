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

import static java.util.Objects.requireNonNull;
import static io.jenetics.internal.util.array.swap;

import java.util.function.ToIntFunction;

/**
 * Implementing the index sorter with the insertion sort algorithm.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class InsertionIndexSorter<T> implements IndexSorter<T> {

	private final ToIntFunction<? super T> _length;
	private final Comp<? super T> _comparator;

	InsertionIndexSorter(
		final ToIntFunction<? super T> length,
		final Comp<? super T> comparator
	) {
		_length = requireNonNull(length);
		_comparator = requireNonNull(comparator);
	}

	@Override
	public int[] sort(final T array) {
		return sort(array, _length, _comparator);
	}

	/**
	 * Implementation of the insertion index sort algorithm.
	 *
	 * @param array the array which is sorted
	 * @param length the array length
	 * @param comp the array element comparator
	 * @param <T> the array type
	 * @return the sorted index array
	 */
	static <T> int[] sort(
		final T array,
		final ToIntFunction<? super T> length,
		final Comp<? super T> comp
	) {
		final int n = length.applyAsInt(array);
		final int[] indexes = IndexSorters.indexes(n);

		for (int i = 1; i < n; ++i) {
			int j = i;
			while (j > 0) {
				if (comp.compare(array, indexes[j - 1], indexes[j]) > 0) {
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
