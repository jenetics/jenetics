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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class HQ64RandomTest extends RandomTestBase {

	@Override
	protected String getDataResource() {
		return String.format(
			"/org/jenetics/util/%s_%d",
			HQ64Random.class.getName(), 12345
		);
	}

	@Override
	protected Random getRandom() {
		return new HQ64Random(12345);
	}

	@Test
	public void serialize() throws IOException, ClassNotFoundException {
		final HQ64Random rand1 = new HQ64Random();
		for (int i = 0; i < 100; ++i) {
			rand1.nextLong();
		}

		final Random rand2 = serialize(rand1);
		Assert.assertNotSame(rand2, rand1);

		for (int i = 0; i < 1000; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
	}

	@Test
	public void serializeThreadSafe() throws IOException, ClassNotFoundException {
		final HQ64Random rand1 = new HQ64Random.ThreadSafe();
		for (int i = 0; i < 100; ++i) {
			rand1.nextLong();
		}

		final Random rand2 = serialize(rand1);
		Assert.assertNotSame(rand2, rand1);

		for (int i = 0; i < 1000; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
	}

	@Test
	public void sameRandomSequence() {
		final HQ64Random rand1 = new HQ64Random(12341234);
		final HQ64Random rand2 = new HQ64Random.ThreadSafe(12341234);

		for (int i = 0; i < 10000; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
	}

	private static Random serialize(final Random random)
		throws IOException, ClassNotFoundException
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(random);
		out.flush();

		final ObjectInputStream in = new ObjectInputStream(
			new ByteArrayInputStream(bytes.toByteArray())
		);


		return (Random)in.readObject();
	}

}
