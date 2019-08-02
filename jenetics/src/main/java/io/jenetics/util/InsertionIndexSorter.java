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
final class InsertionIndexSorter<T> implements IndexSorter<T> {

	private final ToIntFunction<T> _length;
	private final IndexComparator<T> _comparator;

	InsertionIndexSorter(
		final ToIntFunction<T> length,
		final IndexComparator<T> comparator
	) {
		_length = length;
		_comparator = comparator;
	}

	@Override
	public void sort(
		final T array,
		final int[] indexes
	) {
		final int length = _length.applyAsInt(array);

		for (int i = 1; i < length; ++i) {
			int j = i;

			while (j > 0) {
				if (_comparator.compare(array, indexes[j - 1], indexes[j]) < 0) {
					swap(indexes, j - 1, j);
				} else {
					break;
				}
				--j;
			}
		}
	}

}
