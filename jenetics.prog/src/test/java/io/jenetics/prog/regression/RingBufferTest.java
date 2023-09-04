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
package io.jenetics.prog.regression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RingBufferTest {

	@Test(dataProvider = "capacities")
	public void addAll(final int capacity) {
		final RingBuffer ints = new RingBuffer(capacity);

		// values.length < capacity
		int n = Math.max(1, capacity - 10);
		Object[] values = RandomGenerator.getDefault().ints(n)
			.boxed()
			.toArray();

		ints.addAll(Arrays.asList(values));

		Object[] snapshot = ints.snapshot();
		assertThat(snapshot).isEqualTo(values);
		ints.clear();

		// values.length == capacity
		n = capacity;
		values = RandomGenerator.getDefault().ints(n)
			.boxed()
			.toArray();

		ints.addAll(Arrays.asList(values));

		snapshot = ints.snapshot();
		assertThat(snapshot).isEqualTo(values);
		ints.clear();

		// values.length > capacity
		n = capacity + 20;
		values = RandomGenerator.getDefault().ints(n)
			.boxed()
			.toArray();

		ints.addAll(Arrays.asList(values));

		final Object[] expected = new Object[capacity];
		System.arraycopy(values, n - capacity, expected, 0, capacity);
		snapshot = ints.snapshot();
		assertThat(snapshot).isEqualTo(expected);
	}

	@Test(dataProvider = "capacities")
	public void snapshot(final int capacity) {
		final RingBuffer ints = new RingBuffer(capacity);

		for (int i = 0; i < 33; ++i) {
			ints.add(i);

			final Object[] snapshot = ints.snapshot();
			final int size  = Math.min(i + 1, capacity);
			Assert.assertEquals(snapshot.length, size);

			final Object[] expected = new Object[size];
			for (int j = 0; j < size; ++j) {
				expected[size - j - 1] = i - j;
			}
			Assert.assertEquals(snapshot, expected);
		}

		for (int i = 0; i < 1000; ++i) {
			ints.add(i);
		}
	}

	@DataProvider
	public Object[][] capacities() {
		return new Object[][] {
			{1}, {2}, {3}, {5}, {7}, {11}, {33}, {100}, {9999}
		};
	}

	@Test(dataProvider = "capacities")
	public void capacity(final int capacity) {
		final RingBuffer ints = new RingBuffer(capacity);
		assertThat(ints.capacity()).isEqualTo(capacity);
	}

	@Test(dataProvider = "capacities")
	public void size(final int capacity) {
		final RingBuffer ints = new RingBuffer(capacity);

		for (int i = 0; i < capacity + 10; ++i) {
			ints.add(i);

			final int size  = Math.min(i + 1, capacity);
			assertThat(ints.size()).isEqualTo(size);
		}
	}

}
