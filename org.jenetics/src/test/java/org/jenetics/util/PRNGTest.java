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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-05 $</em>
 */
public class PRNGTest {

	private final PRNG prng = new PRNG(System.currentTimeMillis()){
		private static final long serialVersionUID = 1L;
	};

	@Test
	public void nextIntMinMax() {
		final int min = 10;
		final int max = 100000;

		for (int i = 0; i < 1000; ++i) {
			final int value = prng.nextInt(min, max);
			Assert.assertTrue(value < max);
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
		final long max = 100000;

		for (int i = 0; i < 1000; ++i) {
			final long value = prng.nextLong(min, max);
			Assert.assertTrue(value < max);
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
