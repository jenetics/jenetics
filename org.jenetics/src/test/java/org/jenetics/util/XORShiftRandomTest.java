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
import java.util.concurrent.ThreadLocalRandom;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class XORShiftRandomTest extends RandomTestBase {

	@Override
	protected String getDataResource() {
		return String.format(
			"/org/jenetics/util/%s_%d",
			XORShiftRandom.class.getName(), 12345
		);
	}

	@Override
	protected Random getRandom() {
		return new XORShiftRandom(12345);
	}

	@Test
	public void serialize() throws IOException, ClassNotFoundException {
		final XORShiftRandom rand1 = new XORShiftRandom();
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
		final XORShiftRandom rand1 = new XORShiftRandom.ThreadSafe();
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
		final XORShiftRandom rand1 = new XORShiftRandom();
		final XORShiftRandom rand2 = new XORShiftRandom.ThreadSafe(rand1.getSeed());

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

	@Test
	public void nextDouble() {
		final Random jrand = new Random();
		final Random tljrand = ThreadLocalRandom.current();
		final Random xorrand = new XORShiftRandom(); //.INSTANCE.get();
		final Random hq64rand = new HQ64Random();
		final Random tshq64rand = new HQ64Random.ThreadSafe();
		final Random axorrand = new XORShiftRandom.ThreadSafe();

		final int loops = 100000000;

		// Random
		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			jrand.nextDouble();
		}
		long stop = System.currentTimeMillis();
		System.out.println("JRand: " + (stop - start) + "ms");

		// TL Random
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			tljrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("TLJRand: " + (stop - start) + "ms");

		// XORShiftRandom
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			xorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("XORRand: " + (stop - start) + "ms");

		// XORShiftRandom
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			xorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("XORRand: " + (stop - start) + "ms");

		// HQ64Random
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			hq64rand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("HQRand: " + (stop - start) + "ms");

		// TSHQ64Random
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			tshq64rand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("TSHQRand: " + (stop - start) + "ms");


		// AXORShiftRandom
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			axorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("AXORRand: " + (stop - start) + "ms");


	}

	public static void main(final String[] args) {
		new XORShiftRandomTest().nextDouble();
	}

}
