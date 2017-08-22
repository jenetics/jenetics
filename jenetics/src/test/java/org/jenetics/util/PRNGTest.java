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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class PRNGTest {

	private final PRNG prng = new PRNG(System.currentTimeMillis()){
		private static final long serialVersionUID = 1L;
	};

	@Test
	public void nextIntMinMax() {
		final int min = 10;
		final int max = Integer.MAX_VALUE;

		for (int i = 0; i < 1000; ++i) {
			final int value = prng.nextInt(min, max - 1);
			Assert.assertTrue(value < max - 1);
			Assert.assertTrue(value >= min);
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nextIntIllegalArgumentException() {
		prng.nextInt(1000, 10);
	}

	@Test
	public void nextLongMinMax() {
		final long min = 10;
		final long max = Long.MAX_VALUE;

		for (int i = 0; i < 1000; ++i) {
			final long value = prng.nextLong(min, max - i);
			Assert.assertTrue(value < max - 1);
			Assert.assertTrue(value >= min);
		}
	}

	@Test
	public void nextLongMax() {
		final long max = 100000;

		for (int i = 0; i < 1000; ++i) {
			final long value = prng.nextLong(max);
			Assert.assertTrue(value < max);
			Assert.assertTrue(value >= 0);
		}
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nextLongIllegalArgumentException() {
		prng.nextLong(1000, 10);
	}

	@Test
	public void nextFloatMinMax() {
		final float min = 10;
		final float max = 100000;

		for (int i = 0; i < 1000; ++i) {
			final float value = prng.nextFloat(min, max);
			Assert.assertTrue(value < max);
			Assert.assertTrue(value >= min);
		}
	}

	@Test
	public void nextDoubleMinMax() {
		final double min = 10;
		final double max = 100000;

		for (int i = 0; i < 1000; ++i) {
			final double value = prng.nextDouble(min, max);
			Assert.assertTrue(value < max);
			Assert.assertTrue(value >= min);
		}
	}

}
