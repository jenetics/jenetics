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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.internal.util;

import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-07-11 $</em>
 */
public class ArrayProxyMListTest {

	@Test
	public void set() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = 0; i < proxy._length; ++i) {
			proxy.set(i, random.nextInt());
		}

		final List<Integer> list = new ArrayProxyMList<>(proxy);

		seed = math.random.seed();
		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			list.set(i, random.nextInt());
		}

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			Assert.assertEquals(proxy.get(i), list.get(i));
			Assert.assertEquals(list.get(i).intValue(), random.nextInt());
		}
	}

}
