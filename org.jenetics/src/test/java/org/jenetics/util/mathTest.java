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

import static org.jenetics.util.math.sum;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class mathTest {

	@Test(dataProvider = "validSum")
	public void validAdd(final Long a, final Long b) {
		math.plus(a, b);
	}

	@DataProvider(name = "validSum")
	public Object[][] validSum() {
		return new Object[][] {
			{Long.MAX_VALUE, 0L},
			{Long.MAX_VALUE - 1, 1L},
			{Long.MAX_VALUE - 100, 100L},
			{Long.MAX_VALUE, Long.MIN_VALUE},

			{Long.MIN_VALUE, 10L},
			{Long.MIN_VALUE + 10, -10L},
			{Long.MIN_VALUE + 100, -100L},
			{Long.MIN_VALUE, Long.MAX_VALUE}
		};
	}

	@Test(dataProvider = "invalidSum", expectedExceptions = ArithmeticException.class)
	public void invalidAdd(final Long a, final Long b) {
		math.plus(a, b);
	}

	@DataProvider(name = "invalidSum")
	public Object[][] invalidSum() {
		return new Object[][] {
			{Long.MAX_VALUE, 1L},
			{Long.MAX_VALUE - 1, 2L},
			{Long.MAX_VALUE - 100, 101L},
			{Long.MAX_VALUE, Long.MAX_VALUE},

			{Long.MIN_VALUE, -1L},
			{Long.MIN_VALUE, -10L},
			{Long.MIN_VALUE + 100, Long.MIN_VALUE},
			{Long.MIN_VALUE, Long.MIN_VALUE}
		};
	}

	@Test(dataProvider = "validDifference")
	public void validSub(final Long a, final Long b) {
		math.minus(a, b);
	}

	@DataProvider(name = "validDifference")
	public Object[][] validDifference() {
		return new Object[][]{
			{Long.MIN_VALUE, 0L},
			{Long.MIN_VALUE + 1, 1L},
			{Long.MIN_VALUE + 100, 100L},
			{Long.MIN_VALUE, Long.MIN_VALUE},

			{Long.MAX_VALUE, 0L},
			{Long.MAX_VALUE, 1L},
			{Long.MAX_VALUE, 100L},
			{Long.MAX_VALUE, Long.MAX_VALUE}
		};
	}

	@Test(dataProvider = "invalidDifference", expectedExceptions = ArithmeticException.class)
	public void invalidSub(final Long a, final Long b) {
		math.minus(a, b);
	}

	@DataProvider(name = "invalidDifference")
	public Object[][] invalidDifference() {
		return new Object[][]{
			{Long.MIN_VALUE, 1L},
			{Long.MIN_VALUE + 1, 2L},
			{Long.MIN_VALUE + 100, 101L},

			{Long.MAX_VALUE - 1, Long.MAX_VALUE + 1},
			{Long.MAX_VALUE - 100, Long.MAX_VALUE + 100},
			{Long.MAX_VALUE, Long.MIN_VALUE + 1},
			{Long.MAX_VALUE, Long.MIN_VALUE}
		};
	}

	@Test
	public void summarize() {
		final double[] values = new double[150000];
		for (int i = 0; i < values.length; ++i) {
			values[i] = 1.0/values.length;
		}

		Assert.assertEquals(sum(values), 1.0);
	}

	@Test
	public void probabilityToInt() {
		Assert.assertEquals(math.probability.toInt(0), Integer.MIN_VALUE);
		Assert.assertEquals(math.probability.toInt(1), Integer.MAX_VALUE);
		Assert.assertEquals(math.probability.toInt(0.5), 0);
		Assert.assertEquals(math.probability.toInt(0.25), Integer.MIN_VALUE/2);
		Assert.assertEquals(math.probability.toInt(0.75), Integer.MAX_VALUE/2);
	}

}







