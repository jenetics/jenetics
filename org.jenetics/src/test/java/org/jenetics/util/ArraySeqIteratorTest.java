/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
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

}
