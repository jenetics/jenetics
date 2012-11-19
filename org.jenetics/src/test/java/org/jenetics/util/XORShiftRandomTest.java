package org.jenetics.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import org.testng.annotations.Test;

public class XORShiftRandomTest {


	@Test
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
