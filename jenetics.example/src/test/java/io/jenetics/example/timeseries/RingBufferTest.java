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
package io.jenetics.example.timeseries;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RingBufferTest {

	@Test
	public void snapshot() {
		final int max = 10;
		final RingBuffer<Integer> ints = new RingBuffer<>(max);

		for (int i = 0; i < 33; ++i) {
			ints.add(i);

			final Object[] snapshot = ints.snapshot();
			final int size  = Math.min(i + 1, max);
			Assert.assertEquals(snapshot.length, size);

			final Object[] expected = new Object[size];
			for (int j = 0; j < size; ++j) {
				expected[size - j - 1] = i - j;
			}

			Assert.assertEquals(snapshot, expected);

			/*
			System.out.println(Arrays.toString(ints._buffer) + ": " + ints._cursor._size + ": " + ints._cursor._cursor);
			System.out.println(Arrays.toString(expected));
			System.out.println(Arrays.toString(snapshot));
			System.out.println();
			 */
		}

	}

}
