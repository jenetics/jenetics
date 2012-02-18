/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class PermutationIndexStreamTest {

	
	@Test
	public void constantSequence100() throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/org/jenetics/util/123456_100.permutation"))
		);
		
		try {
			final IndexStream stream = PermutationIndexStream.valueOf(100, new Random(123456));
			String line = null;
			while((line = reader.readLine()) != null) {
				Assert.assertEquals(stream.next(), Integer.parseInt(line));
			}
		} finally {
			reader.close();
		}
	}
	
	@Test
	public void constantSequence10000() throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
			getClass().getResourceAsStream("/org/jenetics/util/123456_10000.permutation"))
		);
		
		try {
			final IndexStream stream = PermutationIndexStream.valueOf(10000, new Random(123456));
			String line = null;
			while((line = reader.readLine()) != null) {
				Assert.assertEquals(stream.next(), Integer.parseInt(line));
			}
		} finally {
			reader.close();
		}
	}
	
	@Test(dataProvider = "lengths")
	public void repeatable(final int length) {
		final IndexStream stream1 = PermutationIndexStream.valueOf(length, new Random(1));
		final IndexStream stream2 = PermutationIndexStream.valueOf(length, new Random(1));
		Assert.assertNotSame(stream1, stream2);
		
		for (int i = stream1.next(); i != -1; i = stream1.next()) {
			Assert.assertEquals(i, stream2.next());
		}		
		Assert.assertEquals(stream2.next(), -1);
	}
	
	@Test(dataProvider = "lengths")
	public void iterationLength(final int length) {
		final IndexStream stream = PermutationIndexStream.valueOf(length);
		
		int count = 0;
		for (int i = stream.next(); i != -1; i = stream.next()) {
			++count;
		}
		
		Assert.assertEquals(count, length);
	}
	
	@Test(dataProvider = "lengths")
	public void completeness(final int length) {
		final IndexStream stream = PermutationIndexStream.valueOf(length);
		
		final Set<Integer> values = new HashSet<>(length);
		for (int i = stream.next(); i != -1; i = stream.next()) {
			Assert.assertFalse(
					values.contains(i),
					String.format("Double value %d for stream %s.", i, stream)
				);
			
			values.add(i);
		}
		
		for (int i = 0; i < length; ++i) {
			Assert.assertTrue(
					values.contains(i),
					String.format("Missing value %d for stream %s.", i, stream)
				);
		}
	}
	
	
	@DataProvider
	public Object[][] lengths() {
		return new Object[][] {
			{0}, {1}, {34}, {Byte.MAX_VALUE - 1}, {Byte.MAX_VALUE}, {Byte.MAX_VALUE + 1},
			{1000}, {100000}
		};
	}
	
}
