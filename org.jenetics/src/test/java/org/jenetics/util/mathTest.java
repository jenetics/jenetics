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

import static org.jenetics.util.math.statistics.sum;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-14 $</em>
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

}







