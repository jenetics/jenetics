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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class LGC64ShiftRandomTest {
	private static final String TEST_DATA = "/org/jenetics/util/LGC64ShiftRandom.dat";


	@Test
	public void defaultPRN() throws IOException {
		final Random random = new LGC64ShiftRandom();

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
	public void seed111PRN() throws IOException {
		final Random random = new LGC64ShiftRandom(111);

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

}
