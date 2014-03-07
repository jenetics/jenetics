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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-02-15 $</em>
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

		final Random random1 = new Random();
		try (Scoped<Random> s = RandomRegistry.scope(random1)) {
			final Random random2 = new Random();
			try (Scoped<Random> s2 = RandomRegistry.scope(random2)) {
				Assert.assertSame(RandomRegistry.getRandom(), random2);
			}

			Assert.assertSame(RandomRegistry.getRandom(), random1);
		}

		Assert.assertSame(RandomRegistry.getRandom(), random);
	}

	@Test(invocationCount = 10)
	public void concurrentLocalContext() {
		try (Concurrency c = Concurrency.start()) {
			for (int i = 0; i < 25; ++i) {
				c.execute(new ContextRunnable());
			}
		}
	}

	private static final class ContextRunnable implements Runnable {
		@Override
		public void run() {
			try (Scoped<Random> c = RandomRegistry.scope(new Random())) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				Assert.assertSame(c.get(), RandomRegistry.getRandom());

				final Random random2 = new Random();
				try (Scoped<Random> c2 = RandomRegistry.scope(random2)) {
					Assert.assertSame(RandomRegistry.getRandom(), random2);
					Assert.assertSame(c2.get(), random2);

					final Random random2_2 = new Random();
					RandomRegistry.setRandom(random2_2);
					Assert.assertSame(RandomRegistry.getRandom(), random2_2);

					final Random random3 = new Random();
					try (Scoped<Random> c3 = RandomRegistry.scope(random3)) {
						Assert.assertSame(RandomRegistry.getRandom(), random3);
						Assert.assertSame(c3.get(), random3);
					}

					Assert.assertSame(RandomRegistry.getRandom(), random2_2);
					Assert.assertNotEquals(c.get(), RandomRegistry.getRandom());
				}

				Assert.assertSame(c.get(), RandomRegistry.getRandom());
			}
		}
	}

}
