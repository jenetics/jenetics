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

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class LCG64ShiftRandomTest {

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
			if (index < 64) {
				random.jump2(index);
				final long expected = Long.parseLong(value[6]);
				final long actuall = random.nextLong();

				Assert.assertEquals(actuall, expected);
			}
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

	@Test(dataProvider = "PRNG22")
	public void equals(final Random rand1, final Random rand2) {
		Assert.assertNotSame(rand2, rand1);
		Assert.assertEquals(rand2, rand1);

		for (int i = 0; i < 666; ++i) {
			rand1.nextLong();
		}
		Assert.assertNotEquals(rand1, rand2);

		for (int i = 0; i < 666; ++i) {
			rand2.nextLong();
		}
		Assert.assertEquals(rand2, rand1);
	}

	@Test(dataProvider = "PRNG22")
	public void hashCode(final Random rand1, final Random rand2) {
		Assert.assertNotSame(rand2, rand1);
		Assert.assertEquals(rand2, rand1);
		Assert.assertEquals(rand2.hashCode(), rand1.hashCode());

		for (int i = 0; i < 666; ++i) {
			rand1.nextLong();
		}
		Assert.assertNotEquals(rand1, rand2);
		Assert.assertNotEquals(rand2.hashCode(), rand1.hashCode());

		for (int i = 0; i < 666; ++i) {
			rand2.nextLong();
		}
		Assert.assertEquals(rand2, rand1);
		Assert.assertEquals(rand2.hashCode(), rand1.hashCode());
	}

	@DataProvider(name = "PRNG22")
	Object[][] getPRNG22() {
		return new Object[][]{
			{new LCG64ShiftRandom(666), new LCG64ShiftRandom(666)},
			{new LCG64ShiftRandom.ThreadSafe(666), new LCG64ShiftRandom.ThreadSafe(666)}
		};
	}

	@Test(dataProvider = "PRNG3")
	public void serialize(final Random rand1)
		throws IOException, ClassNotFoundException
	{
		for (int i = 0; i < 12734; ++i) {
			rand1.nextLong();
		}

		final Random rand2 = RandomTestBase.serialize(rand1);
		Assert.assertNotSame(rand2, rand1);
		Assert.assertTrue(
			rand2 instanceof LCG64ShiftRandom,
			"Must be of type LCG64ShiftRandom."
		);

		for (int i = 0; i < 2489248; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}

	}

	@DataProvider(name = "PRNG3")
	Object[][] getPRNG3() {
		return new Object[][]{
			{new LCG64ShiftRandom(666)},
			{new LCG64ShiftRandom.ThreadSafe(666)},
			{new LCG64ShiftRandom.ThreadLocal().get()}
		};
	}

}



