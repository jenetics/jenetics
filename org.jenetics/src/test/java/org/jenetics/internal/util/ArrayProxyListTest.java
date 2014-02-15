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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public class ArrayProxyListTest {

	@Test
	public void size() {
		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		final List<Integer> list = new ArrayProxyList<>(proxy);

		Assert.assertEquals(list.size(), proxy._length);
	}

	@Test
	public void get() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextInt());
		}

		final List<Integer> list = new ArrayProxyList<>(proxy);

		for (int i = 0; i < proxy._length; ++i) {
			final Integer actual = list.get(i);
			final Integer expected = proxy.get(i);
			Assert.assertEquals(actual, expected);
		}
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void set() {
		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		final List<Integer> list = new ArrayProxyList<>(proxy);

		list.set(34, 23);
	}

	@Test
	public void indexOf() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Long> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextLong());
		}

		final List<Long> list = new ArrayProxyList<>(proxy);

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			final int index = list.indexOf(random.nextLong());
			Assert.assertEquals(index, i);
		}
	}

	@Test
	public void contains() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Long> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextLong());
		}

		final List<Long> list = new ArrayProxyList<>(proxy);

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			Assert.assertTrue(
				list.contains(random.nextLong()),
				"Must contain the given value."
			);
		}
	}

	@Test
	public void toArray() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Long> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextLong());
		}

		final List<Long> list = new ArrayProxyList<>(proxy);
		final Object[] array = list.toArray();

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(array[i], proxy.get(i));
		}
	}

	@Test
	public void toArrayLong() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Long> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextLong());
		}

		final List<Long> list = new ArrayProxyList<>(proxy);
		final Long[] array = list.toArray(new Long[0]);

		for (int i = 0; i < array.length; ++i) {
			Assert.assertEquals(array[i], proxy.get(i));
		}
	}

}
