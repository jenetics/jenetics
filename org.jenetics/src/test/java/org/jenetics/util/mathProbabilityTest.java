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

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-06-07 $</em>
 */
public class mathProbabilityTest {

	@Test
	public void toIntToFloat() {
		final Random random = RandomRegistry.getRandom();

		for (int i = 0; i < 100000; ++i) {
			final float p = random.nextFloat();

			final int ip = math.probability.toInt(p);
			final float fip = math.probability.toFloat(ip);
			Assert.assertEquals(fip, p, 0.000001F);
		}
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
