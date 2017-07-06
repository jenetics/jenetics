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
package org.jenetics.internal.collection;

import java.util.ListIterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.math.random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ArrayMIteratorTest {

	@Test
	public void set() {
		long seed = random.seed();
		final Random random = new Random(seed);

		final Array<Integer> impl = Array.of(ObjectStore.ofLength(1000));
		for (int i = 0; i < impl.length(); ++i) {
			impl.set(i, random.nextInt());
		}


		seed = org.jenetics.internal.math.random.seed();
		random.setSeed(seed);
		final ListIterator<Integer> it = new ArrayMIterator<>(impl);
		while (it.hasNext()) {
			it.next();
			it.set(random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < impl.length(); ++i) {
			Assert.assertEquals(impl.get(i).intValue(), random.nextInt());
		}
	}

	@Test
	public void setValueForward() {
		final Array<Integer> proxy = Array.of(ObjectStore.ofLength(1000));
		for (int i = 0; i < proxy.length(); ++i) {
			proxy.set(i, 111);
		}

		for (int i = 0; i < proxy.length(); ++i) {
			final Integer value = proxy.get(i);
			Assert.assertEquals(value, Integer.valueOf(111));
		}

		int count = 0;
		final ListIterator<Integer> it = new ArrayMIterator<>(proxy);
		while (it.hasNext()) {
			it.next();
			it.set(222);
			++count;
		}

		Assert.assertEquals(count, proxy.length());
		for (int i = 0; i < proxy.length(); ++i) {
			final Integer value = proxy.get(i);
			Assert.assertEquals(value, Integer.valueOf(222));
		}
	}

	@Test
	public void setValueBackward() {
		final Array<Integer> proxy = Array.of(ObjectStore.ofLength(1000));
		for (int i = 0; i < proxy.length(); ++i) {
			proxy.set(i, 111);
		}

		for (int i = 0; i < proxy.length(); ++i) {
			final Integer value = proxy.get(i);
			Assert.assertEquals(value, Integer.valueOf(111));
		}

		final ListIterator<Integer> it = new ArrayMIterator<>(proxy);
		while (it.hasNext()) {
			it.next();
		}

		int count = 0;
		while (it.hasPrevious()) {
			it.previous();
			it.set(222);
			++count;
		}

		Assert.assertEquals(count, proxy.length());
		for (int i = 0; i < proxy.length(); ++i) {
			final Integer value = proxy.get(i);
			Assert.assertEquals(value, Integer.valueOf(222));
		}
	}

}
