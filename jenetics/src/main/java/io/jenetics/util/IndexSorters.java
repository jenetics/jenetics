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
final class IndexSorters {
	private IndexSorters() {
	}

	/**
	 * Create an initial indexes array of the given {@code length}.
	 *
	 * @param length the length of the indexes array
	 * @return the initialized indexes array
	 */
	static int[] indexes(final int length) {
		return init(new int[length]);
	}

	/**
	 * Initializes the given {@code indexes} array.
	 *
	 * @param indexes the indexes array to initialize
	 * @return the initialized indexes array
	 * @throws NullPointerException if the given {@code indexes} array is
	 *         {@code null}
	 */
	static int[] init(final int[] indexes) {
		for (int i = 0; i < indexes.length; ++i) {
			indexes[i] = i;
		}
		return indexes;
	}

}
