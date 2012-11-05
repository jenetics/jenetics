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





