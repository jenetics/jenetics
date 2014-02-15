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
package org.jenetics.util;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
 */
public class ArraySeqIteratorTest {

	@Test
	public void iterateForward() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final Array<Integer> array = new Array<>(1000);
		for (int i = 0; i < array._length; ++i) {
			array.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final Iterator<Integer> it = array.iterator();
		int count = 0;
		while (it.hasNext()) {
			Assert.assertEquals(it.next().intValue(), random.nextInt());
			++count;
		}

		Assert.assertEquals(count, array._length);
	}

	@Test
	public void iterateBackward() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final Array<Integer> array = new Array<>(1000);
		for (int i = array._length; --i >= 0;) {
			array.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final ListIterator<Integer> it = array.listIterator();
		while (it.hasNext()) {
			it.next();
		}

		int count = 0;
		while (it.hasPrevious()) {
			final int value = it.previous().intValue();
			Assert.assertEquals(value, random.nextInt());
			++count;
		}

		Assert.assertEquals(count, array._length);
	}

	@Test
	public void nextIndex() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final Array<Integer> array = new Array<>(1000);
		for (int i = 0; i < array._length; ++i) {
			array.set(i, random.nextInt());
		}

		random.setSeed(seed);
		final ListIterator<Integer> it = array.listIterator();
		int count = 0;
		while (it.hasNext()) {
			Assert.assertEquals(it.nextIndex(), count);

			it.next();
			++count;
		}

		Assert.assertEquals(count, array._length);
	}

	@Test
	public void previousIndex() {
		final long seed = math.random.seed();
		final Random random = new Random(seed);

		final Array<Integer> array = new Array<>(1000);
		for (int i = 0; i < array._length; ++i) {
			array.set(i, random.nextInt());
		}

		final ListIterator<Integer> it = array.listIterator();
		while (it.hasNext()) {
			it.next();
		}

		int count = array.length();
		while (it.hasPrevious()) {
			--count;
			Assert.assertEquals(it.previousIndex(), count);
			it.previous();
		}

		Assert.assertEquals(count, 0);
	}

	@Test
	public void setValueForward() {
		final Array<Integer> array = new Array<>(1000);
		array.setAll(111);

		for (Integer value : array) {
			Assert.assertEquals(value, new Integer(111));
		}

		int count = 0;
		final ListIterator<Integer> it = array.listIterator();
		while (it.hasNext()) {
			it.next();
			it.set(222);
			++count;
		}

		Assert.assertEquals(count, array.length());
		for (Integer value : array) {
			Assert.assertEquals(value, new Integer(222));
		}
	}

	@Test
	public void setValueBackward() {
		final Array<Integer> array = new Array<>(1000);
		array.setAll(111);

		for (Integer value : array) {
			Assert.assertEquals(value, new Integer(111));
		}

		final ListIterator<Integer> it = array.listIterator();
		while (it.hasNext()) {
			it.next();
		}

		int count = 0;
		while (it.hasPrevious()) {
			it.previous();
			it.set(222);
			++count;
		}

		Assert.assertEquals(count, array.length());
		for (Integer value : array) {
			Assert.assertEquals(value, new Integer(222));
		}
	}

}
