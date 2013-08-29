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
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class TimerTest extends ObjectTester<Timer> {

	private final Factory<Timer> _factory = new Factory<Timer>() {
		@Override
		public Timer newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Timer timer = new Timer(RandomUtils.nextString(random.nextInt(10) + 10));
			timer._start = random.nextLong();
			timer._stop = timer._start + random.nextInt(1000) + 1000;
			timer._sum = timer._stop - timer._start;
			return timer;
		}
	};
	@Override
	protected Factory<Timer> getFactory() {
		return _factory;
	}

	@Test
	public void label() {
		final Timer timer = new Timer("New Timer");
		Assert.assertEquals(timer.getLabel(), "New Timer");
	}

	@Test
	void timerClone() throws InterruptedException {
		final Timer timer = new Timer("ASDFASDF");
		timer.start();
		Thread.sleep(100);
		timer.stop();

		final Timer clone = timer.clone();
		Assert.assertEquals(clone, timer);
		Assert.assertNotSame(clone, timer);
		Assert.assertEquals(clone.compareTo(timer), 0);
	}

}





