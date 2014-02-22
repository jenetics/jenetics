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

import static java.lang.Math.min;
import static org.jenetics.util.bit.get;
import static org.jenetics.util.bit.set;
import static org.jenetics.util.bit.toByteLength;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.bit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-02-22 $</em>
 * @since @__version__@
 */
public class internalbitTest {


	@Test(dataProvider = "copyParameter")
	public void copy(final Integer length, final Integer start, final Integer end) {
		final Random random = new Random(12341);
		final byte[] data = new byte[length];
		random.nextBytes(data);

		Assert.assertEquals(
			bit.toByteString(internalbit.copy(data, start, end)),
			bit.toByteString(copySafe(data, start, end)),
			"Original: " + bit.toByteString(data)
		);
	}

	@DataProvider(name = "copyParameter")
	public Object[][] copyParameter() {
		final List<Object[]> list = new ArrayList<>();
		for (int length = 1; length < 10; length += 3) {
			for (int start = 0; start < length*8; start += 3) {
				for (int end = start; end < length*8 + 2; end += 5) {
					list.add(new Integer[]{length, start, end});
				}
			}
		}

		return list.toArray(new Object[0][]);
	}

	private static byte[] copySafe(final byte[] data, final int start, final int end) {
		final int endIndex = min(data.length*8, end);
		final int newLength = toByteLength(endIndex - start);
		final byte[] copy = new byte[newLength];

		for (int i = 0, n = endIndex - start; i < n; ++i) {
			set(copy, i, get(data, i + start));
		}

		return copy;
	}

}
