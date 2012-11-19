package org.jenetics.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import org.testng.Assert;
import org.testng.annotations.Test;

public class XORShiftRandomTest {


	@Test
	public void testClone() {
		final XORShiftRandom rand1 = new XORShiftRandom();
		for (int i = 0; i < 100; ++i) {
			rand1.nextLong();
		}

		final XORShiftRandom rand2 = rand1.clone();
		Assert.assertNotSame(rand2, rand1);

		for (int i = 0; i < 1000; ++i) {
			Assert.assertEquals(rand2.nextLong(), rand1.nextLong());
		}
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

	//@Test
	public void nextDouble() {
		final Random jrand = new Random();
		final Random tljrand = ThreadLocalRandom.current();
		final Random xorrand = new XORShiftRandom(); //.INSTANCE.get();
		final Random sxorrand = new random.XORShiftRandom(new ReentrantLock());
		final Random esxorrand = new random.XORShiftRandom(random.NULL);
		final Random axorrand = new random.AXORShiftRandom();

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

		// SXORShiftRandom
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			sxorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("SXORRand: " + (stop - start) + "ms");

		// ESXORShiftRandom
		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			esxorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("ESXORRand: " + (stop - start) + "ms");

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
