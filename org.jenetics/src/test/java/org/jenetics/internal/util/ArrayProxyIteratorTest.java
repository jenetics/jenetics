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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class ArrayProxyIteratorTest {

	@Test
	public void iterateForward() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final Iterator<Integer> it = new ArrayProxyIterator<>(proxy);
		int count = 0;
		while (it.hasNext()) {
			Assert.assertEquals(it.next().intValue(), random.nextInt());
			++count;
		}

		Assert.assertEquals(count, proxy._length);
	}

	@Test
	public void iterateBackward() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = proxy._length; --i >= 0;) {
			proxy.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final ListIterator<Integer> it = new ArrayProxyIterator<>(proxy);
		while (it.hasNext()) {
			it.next();
		}

		int count = 0;
		while (it.hasPrevious()) {
			final int value = it.previous().intValue();
			Assert.assertEquals(value, random.nextInt());
			++count;
		}

		Assert.assertEquals(count, proxy._length);
	}

	@Test
	public void nextIndex() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final ListIterator<Integer> it = new ArrayProxyIterator<>(proxy);
		int count = 0;
		while (it.hasNext()) {
			Assert.assertEquals(it.nextIndex(), count);
			Assert.assertEquals(proxy.get(it.nextIndex()), it.next());

			++count;
		}

		Assert.assertEquals(count, proxy._length);
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void set() {
		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		final ListIterator<Integer> it = new ArrayProxyIterator<>(proxy);

		it.set(23);
	}

	@Test
	public void previousIndex() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextInt());
		}

		final ListIterator<Integer> it = new ArrayProxyIterator<>(proxy);
		while (it.hasNext()) {
			it.next();
		}

		int count = proxy._length;
		while (it.hasPrevious()) {
			--count;

			Assert.assertEquals(it.previousIndex(), count);
			Assert.assertEquals(proxy.get(it.previousIndex()), it.previous());
		}

		Assert.assertEquals(count, 0);
	}


}
