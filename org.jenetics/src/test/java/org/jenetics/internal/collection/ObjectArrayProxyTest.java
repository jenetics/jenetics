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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-06-17 $</em>
 */
public class ObjectArrayProxyTest extends ArrayProxyTestBase<Integer> {

	@Override
	public ArrayProxy<Integer, ?, ?> newArrayProxy(final int length) {
		return new ObjectArrayProxy<>(length);
	}

	@Override
	public Integer newArrayProxyElement(final Random random) {
		return random.nextInt();
	}

	@Test
	public void slice() {
		final ArrayProxy<Integer, ?, ?> proxy = new ObjectArrayProxy<>(1000);
		for (int i = 0; i < proxy.length; ++i) {
			proxy.set(i, i);
		}

		final int sliceStart = 50;
		final int sliceEnd = 500;
		final ArrayProxy<Integer, ?, ?> slice = proxy.slice(sliceStart, sliceEnd);
		Assert.assertEquals(sliceEnd - sliceStart, slice.length);

		for (int i = 0; i < slice.length; ++i) {
			Assert.assertEquals(slice.get(i), Integer.valueOf(i + sliceStart));
		}
	}

	@Test
	public void sliceGet() {
		final ArrayProxy<Integer, ?, ?> proxy = new ObjectArrayProxy<>(1000);
		for (int i = 0; i < proxy.length; ++i) {
			proxy.set(i, i);
		}

		final int sliceStart = 50;
		final int sliceEnd = 500;
		final ArrayProxy<Integer, ?, ?> slice = proxy.slice(sliceStart, sliceEnd);
		for (int i = 0; i < slice.length; ++i) {
			Assert.assertEquals(slice.get(i), Integer.valueOf(i + sliceStart));
		}
	}

	@Test
	public void sliceSet() {
		final ArrayProxy<Integer, ?, ?> proxy = new ObjectArrayProxy<>(1000);
		for (int i = 0; i < proxy.length; ++i) {
			proxy.set(i, i);
		}

		final int sliceStart = 50;
		final int sliceEnd = 500;
		final ArrayProxy<Integer, ?, ?> slice = proxy.slice(sliceStart, sliceEnd);
		for (int i = 0; i < slice.length; ++i) {
			slice.set(i, i*2);
			Assert.assertEquals(slice.get(i), Integer.valueOf(i*2));
		}
	}

}
