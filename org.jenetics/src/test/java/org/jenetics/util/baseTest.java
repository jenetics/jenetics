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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.internal.math.DoubleAdder;
import org.jenetics.internal.math.base;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class baseTest {

	@Test
	public void summarize() {
		final double[] values = new double[150000];
		for (int i = 0; i < values.length; ++i) {
			values[i] = 1.0/values.length;
		}

		Assert.assertEquals(DoubleAdder.sum(values), 1.0);
	}

	@Test
	public void subset() {
		final Random random = new Random();

		for (int i = 1; i < 100; ++i) {
			int[] sub = new int[i];
			base.subset(1000, sub, random);

			Assert.assertTrue(isSortedd(sub));
		}
	}

	private static boolean isSortedd(int[] array) {
		boolean sorted = true;
		for (int i = 0; i < array.length - 1 && sorted; ++i) {
			sorted = array[i] < array[i + 1];
		}
		return sorted;
	}

}
