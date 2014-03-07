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
 * @version 1.6 &mdash; <em>$Date: 2014-02-15 $</em>
 * @since 1.6
 */
public class RandRegistryTest {

	@Test
	public void localContext() {
		final Random random = RandRegistry.getRandom();

		final Random random1 = new Random();
		try (Scoped<Random> c1 = RandRegistry.scope(random1)) {

			final Random random2 = new Random();
			try (Scoped<Random> c2 = RandRegistry.scope(random2)) {
				Assert.assertSame(RandRegistry.getRandom(), random2);
				Assert.assertSame(c2.get(), random2);
			}

			Assert.assertSame(RandRegistry.getRandom(), random1);
			Assert.assertSame(c1.get(), random1);

			final Random random3 = new Random();
			RandRegistry.setRandom(random3);

			Assert.assertSame(RandRegistry.getRandom(), random3);
			Assert.assertNotEquals(RandRegistry.getRandom(), c1.get());
		}

		Assert.assertSame(RandRegistry.getRandom(), random);
	}

	@Test(invocationCount = 10)
	public void concurrentLocalContext() {
		final Random random = RandRegistry.getRandom();
		RandRegistry.setRandom(random);

		try (Concurrency c = Concurrency.start()) {
			for (int i = 0; i < 25; ++i) {
				c.execute(new ContextRunnable());
			}
		}

		Assert.assertSame(RandRegistry.getRandom(), random);
	}

	private static final class ContextRunnable implements Runnable {
		@Override
		public void run() {
			try (Scoped<Random> c = RandRegistry.scope(new Random())) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				Assert.assertSame(c.get(), RandRegistry.getRandom());

				final Random random2 = new Random();
				try (Scoped<Random> c2 = RandRegistry.scope(random2)) {
					Assert.assertSame(RandRegistry.getRandom(), random2);
					Assert.assertSame(c2.get(), random2);

					final Random random2_2 = new Random();
					RandRegistry.setRandom(random2_2);
					Assert.assertSame(RandRegistry.getRandom(), random2_2);

					final Random random3 = new Random();
					try (Scoped<Random> c3 = RandRegistry.scope(random3)) {
						Assert.assertSame(RandRegistry.getRandom(), random3);
						Assert.assertSame(c3.get(), random3);
					}

					Assert.assertSame(RandRegistry.getRandom(), random2_2);
					Assert.assertNotEquals(c.get(), RandRegistry.getRandom());
				}

				Assert.assertSame(c.get(), RandRegistry.getRandom());
			}
		}
	}

}
