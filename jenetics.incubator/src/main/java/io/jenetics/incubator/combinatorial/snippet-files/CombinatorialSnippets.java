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

import java.util.Arrays;
import java.util.random.RandomGenerator;

import io.jenetics.incubator.combinatorial.Cursor;
import io.jenetics.incubator.combinatorial.KSubset;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
final class CombinatorialSnippets {
	private CombinatorialSnippets() {
	}

	static final class CursorSnippets {

		void loop() {
			// @start region="Cursor.loop"
			final Cursor cursor = null; // @replace regex="null" replacement="..."
			final int[] index = new int[cursor.size()];
			while (cursor.next(index)) {
				System.out.println(Arrays.toString(index));
			}
			// @end
		}

		void loop2() {
			// @start region="Cursor.loop2"
			final Cursor cursor = null; // @replace regex="null" replacement="..."
			int[] index = null;
			while ((index = cursor.next()) != null) {
				System.out.println(Arrays.toString(index));
			}
			// @end
		}

	}

	static final class KSubsetSnippets {

		void iteration() {
			// @start region="KSubset.iteration"
			final var ksubset = new KSubset(5, 3);
			ksubset.forEach(s -> System.out.println(Arrays.toString(s)));
			// [0, 1, 2]
			// [0, 1, 3]
			// [0, 1, 4]
			// [0, 2, 3]
			// [0, 2, 4]
			// [0, 3, 4]
			// [1, 2, 3]
			// [1, 2, 4]
			// [1, 3, 4]
			// [2, 3, 4]
			// @end
		}

		void compare() {
			// @start region="KSubset.compare"
			final var ksubset = new KSubset(5, 3);
			assert ksubset.compare(new int[] {0, 2, 4}, new int[] {0, 1, 4}) > 0;
			// @end
		}

		void randomCursor() {
			// @start region="KSubset.randomCursor"
			// Creating a random 12-subset.
			final int[] subset = new KSubset(23, 12)
				.cursor(RandomGenerator.getDefault())
				.next();
			// @end
		}

		void staticRandomCursor() {
			// @start region="KSubset.staticRandomCursor"
			// Creating a random 12-subset.
			final int[] subset = KSubset
				.cursor(RandomGenerator.getDefault(), 23, 12)
				.next();
			// @end
		}

	}


}
