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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-05 $</em>
 */
public class LCG64ShiftRandomTest extends RandomTestBase {

	private final TestData _data = new TestData(
		"/org/jenetics/util/LGC64ShiftRandom.dat"
	);

	@Test(dataProvider = "PRNG2")
	public void seed0(final LCG64ShiftRandom random) {
		random.setSeed(0);

		for (final String[] value : _data) {
			final long expected = Long.parseLong(value[0]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}

	@Test(dataProvider = "PRNG2")
	public void seed111(final LCG64ShiftRandom random) {
		random.setSeed(111);

		for (final String[] value : _data) {
			final long expected = Long.parseLong(value[1]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}


	@Test(dataProvider = "PRNG2")
	public void split3_0(final LCG64ShiftRandom random) {
		random.setSeed(0);
		random.split(3, 0);

		for (final String[] value : _data) {
			final long expected = Long.parseLong(value[2]);
			final long actuall = random.nextLong();
			Assert.assertEquals(actuall, expected);
		}
	}

	@Test(dataProvider = "PRNG2")
	public void split3_1(final LCG64ShiftRandom random) {
		random.setSeed(0);
		random.split(3, 1);

		for (final String[] value : _data) {
			final long expected = Long.parseLong(value[3]);
			final long actuall = random.nextLong();
			Assert.assertEquals(actuall, expected);
		}
	}

	@Test(dataProvider = "PRNG2")
	public void split3_2(final LCG64ShiftRandom random) {
		random.setSeed(0);
		random.split(3, 2);

		for (final String[] value : _data) {
			final long expected = Long.parseLong(value[4]);
			final long actuall = random.nextLong();
			Assert.assertEquals(actuall, expected);
		}
	}


	@Test(dataProvider = "PRNG2")
	public void jump(final LCG64ShiftRandom random) {
		random.setSeed(0);

		final AtomicInteger i = new AtomicInteger(0);
		for (final String[] value : _data) {
			random.jump(i.getAndIncrement());

			final long expected = Long.parseLong(value[5]);
			Assert.assertEquals(random.nextLong(), expected);
		}
	}


	@Test(dataProvider = "PRNG2")
	public void jump2(final LCG64ShiftRandom random) {
		random.setSeed(0);

		int index = 0;
		for (final String[] value : _data) {
			random.jump2(index%64);
			final long expected = Long.parseLong(value[6]);
			final long actuall = random.nextLong();

			Assert.assertEquals(actuall, expected);
			++index;
		}
	}

	@DataProvider(name = "PRNG2")
	Object[][] getPRNG2() {
		return new Object[][]{
			{new LCG64ShiftRandom(666)},
			{new LCG64ShiftRandom.ThreadSafe(666)}
		};
	}

	@Override @DataProvider(name = "PRNG22")
	protected Object[][] getPRNG22() {
		final long seed = random.seed();
		return new Object[][]{
			{new LCG64ShiftRandom(seed), new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed), new LCG64ShiftRandom.ThreadSafe(seed)}
		};
	}

	@Override @DataProvider(name = "PRNG3")
	protected Object[][] getPRNG3() {
		final long seed = random.seed();
		return new Object[][]{
			{new LCG64ShiftRandom(seed)},
			{new LCG64ShiftRandom.ThreadSafe(seed)},
			{new LCG64ShiftRandom.ThreadLocal().get()}
		};
	}

}



