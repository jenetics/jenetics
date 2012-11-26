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
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LCG64ShiftRandomTest {
	private static final String TEST_DATA = "/org/jenetics/util/LGC64ShiftRandom.dat";


	@Test
	public void default_PRN() throws IOException {
		final Random random = new LCG64ShiftRandom();

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					final long expected = Long.parseLong(value[0]);
					Assert.assertEquals(random.nextLong(), expected);
					return null;
				}

			});
		}
	}

	@Test
	public void seed111_PRN() throws IOException {
		final Random random = new LCG64ShiftRandom(111);

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					final long expected = Long.parseLong(value[1]);
					Assert.assertEquals(random.nextLong(), expected);
					return null;
				}

			});
		}
	}


	@Test
	public void split_3_0_PRN() throws IOException {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();
		random.split(3, 0);

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					final long expected = Long.parseLong(value[2]);
					final long actuall = random.nextLong();
					//System.out.println(actuall);

					Assert.assertEquals(actuall, expected);
					return null;
				}

			});
		}
	}

	@Test
	public void split_3_1_PRN() throws IOException {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();
		random.split(3, 1);

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					final long expected = Long.parseLong(value[3]);
					final long actuall = random.nextLong();
					//System.out.println(actuall);

					Assert.assertEquals(actuall, expected);
					return null;
				}

			});
		}
	}

	@Test
	public void split_3_2_PRN() throws IOException {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();
		random.split(3, 2);

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					final long expected = Long.parseLong(value[4]);
					final long actuall = random.nextLong();
					//System.out.println(actuall);

					Assert.assertEquals(actuall, expected);
					return null;
				}

			});
		}
	}


	@Test
	public void jump_PRN() throws IOException {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			final AtomicInteger i = new AtomicInteger(0);
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {
					random.jump(i.getAndIncrement());

					final long expected = Long.parseLong(value[5]);
					Assert.assertEquals(random.nextLong(), expected);
					return null;
				}

			});
		}
	}


	@Test
	public void jump2_PRN() throws IOException {
		final LCG64ShiftRandom random = new LCG64ShiftRandom();

		try (TestDataReader reader = new TestDataReader(TEST_DATA)) {
			final AtomicInteger i = new AtomicInteger(0);
			reader.foreach(new Function<String[], Void>() {
				@Override public Void apply(String[] value) {

					if (i.get() < 64) {
						random.jump2(i.get());
						final long expected = Long.parseLong(value[6]);
						final long actuall = random.nextLong();
						//System.out.println(i.get() + " =====> " + actuall);

						Assert.assertEquals(actuall, expected);
					}
					i.getAndIncrement();
					return null;
				}

			});
		}
	}


}




















