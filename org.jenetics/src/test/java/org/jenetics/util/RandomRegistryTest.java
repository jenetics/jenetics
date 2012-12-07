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

import javolution.context.LocalContext;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class RandomRegistryTest {

	@Test
	public void setDefault() {
		final Random devault = RandomRegistry.getRandom();
		Assert.assertNotNull(devault);

		RandomRegistry.setRandom(new Random());
		Assert.assertNotNull(RandomRegistry.getRandom());
		RandomRegistry.reset();

		Assert.assertSame(RandomRegistry.getRandom(), devault);
	}

	@Test
	public void setRandom() {
		final Random random = new Random();
		RandomRegistry.setRandom(random);

		Assert.assertSame(RandomRegistry.getRandom(), random);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void setNullRandom() {
		RandomRegistry.setRandom((Random)null);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void setNullTLRandom() {
		RandomRegistry.setRandom((ThreadLocal<Random>)null);
	}

	@Test
	public void localContext() {
		final Random random = RandomRegistry.getRandom();

		LocalContext.enter();
		try {
			final Random random1 = new Random();
			RandomRegistry.setRandom(random1);

			LocalContext.enter();
			try {
				final Random random2 = new Random();
				RandomRegistry.setRandom(random2);

				Assert.assertSame(RandomRegistry.getRandom(), random2);
			} finally {
				LocalContext.exit();
			}

			Assert.assertSame(RandomRegistry.getRandom(), random1);
		} finally {
			LocalContext.exit();
		}

		Assert.assertSame(RandomRegistry.getRandom(), random);
	}

}




