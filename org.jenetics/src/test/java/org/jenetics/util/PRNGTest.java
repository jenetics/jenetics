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
 * @version <em>$Date$</em>
 */
public class PRNGTest {

	private final PRNG prng = new PRNG(){
		private static final long serialVersionUID = 1L;
	};

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nextIntIllegalArgumentException() {
		prng.nextInt(1000, 10);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void nextLongIllegalArgumentException() {
		prng.nextLong(1000, 10);
	}

	@Test
	public void nextFloat() {
		final float value = prng.nextFloat(1000, 10);
		Assert.assertTrue(value < 1000);
		Assert.assertTrue(value >= 10);
	}

	@Test
	public void nextDouble() {
		final double value = prng.nextDouble(1000, 10);
		Assert.assertTrue(value < 1000);
		Assert.assertTrue(value >= 10);
	}

}
