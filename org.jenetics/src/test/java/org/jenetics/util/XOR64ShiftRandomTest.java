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

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2012-12-05 $</em>
 */
public class XOR64ShiftRandomTest /*extends RandomTestBase*/ {

	//@Override
	protected String getDataResource() {
		return String.format(
			"/org/jenetics/util/%s_%d",
			XOR64ShiftRandom.class.getName(), 12345
		);
	}

	//@Override
	protected Random getRandom() {
		return new XOR64ShiftRandom(12345);
	}
/*
	@Test
	public void serialize() throws IOException, ClassNotFoundException {
		final XOR64ShiftRandom rand1 = new XOR64ShiftRandom();
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
		final XOR64ShiftRandom rand1 = new XOR64ShiftRandom.ThreadSafe();
		for (int i = 0; i < 100; ++i) {
			rand1.nextLong();
		}

		final Random rand2 = serialize(rand1);
		Assert.assertNotSame(rand2, rand1);

		for (int i = 0; i < 1000; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
	}
*/
	@Test
	public void sameRandomSequence() {
		//final XOR64ShiftRandom rand1 = new XOR64ShiftRandom();
		//final XOR64ShiftRandom rand2 = new XOR64ShiftRandom.ThreadSafe(rand1.getSeed());

		for (int i = 0; i < 100; ++i) {
			System.out.println(random.seed());
			//Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
	}

	//@Test
	public void nextDouble() {
		final Random jrand = new Random();
		final Random tljrand = ThreadLocalRandom.current();
		final Random xorrand = new XOR64ShiftRandom(); //.INSTANCE.get();
		final Random hq64rand = new HQ64Random();
		final Random lgc64shift = new LCG64ShiftRandom(System.nanoTime());
		final Random tshq64rand = new HQ64Random.ThreadSafe();
		final Random axorrand = new XOR64ShiftRandom.ThreadSafe();

		final int loops = 100_000_000;

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

		// LGC64Shift
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			lgc64shift.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("LGC64Shift: " + (stop - start) + "ms");


	}

	public static void main(final String[] args) {
		new XOR64ShiftRandomTest().nextDouble();
	}

}
