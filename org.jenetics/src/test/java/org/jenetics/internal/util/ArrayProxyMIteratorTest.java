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

import java.util.ListIterator;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.math;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class ArrayProxyMIteratorTest {

	@Test
	public void set() {
		long seed = math.random.seed();
		final Random random = new Random(seed);

		final ArrayProxy<Integer> proxy = new ArrayProxyImpl<>(1000);
		for (int i = proxy._length; --i >= 0;) {
			proxy.set(i, random.nextInt());
		}

		seed = math.random.seed();
		random.setSeed(seed);
		final ListIterator<Integer> it = new ArrayProxyMIterator<>(proxy);
		while (it.hasNext()) {
			it.set(random.nextInt());
			it.next();
		}

		random.setSeed(seed);
		for (int i = 0; i < proxy._length; ++i) {
			Assert.assertEquals(proxy.get(i).intValue(), random.nextInt());
		}
	}

}
