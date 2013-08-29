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

import javolution.context.LocalContext;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
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




