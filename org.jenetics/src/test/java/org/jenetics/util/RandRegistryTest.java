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

import javolution.context.LocalContext;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date: 2014-01-30 $</em>
 * @since @__version__@
 */
public class RandRegistryTest {

	@Test
	public void localContext() {
		final Random random = RandRegistry.getRandom();

		final Random random1 = new Random();
		try (Context<Random> c1 = RandRegistry.with(random1)) {

			final Random random2 = new Random();
			try (Context<Random> c2 = RandRegistry.with(random2)) {
				Assert.assertSame(RandRegistry.getRandom(), random2);
				Assert.assertSame(c2.get(), random2);
			}

			Assert.assertSame(RandRegistry.getRandom(), random1);
			Assert.assertSame(c1.get(), random1);
		}

		Assert.assertSame(RandRegistry.getRandom(), random);
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
			try (Context<Random> c = RandRegistry.with(new Random())) {
				try {
					Thread.sleep(1); // Just allow context switches.
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				Assert.assertSame(c.get(), RandRegistry.getRandom());
			}
		}
	}

}
