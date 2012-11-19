package org.jenetics.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.Test;

public class XORShiftRandomTest {


	@Test
	public void nextDouble() {
		final Random jrand = ThreadLocalRandom.current();
		final Random xorrand = new XORShiftRandom(); //.INSTANCE.get();

		final int loops = 100000000;

		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			jrand.nextDouble();
		}
		long stop = System.currentTimeMillis();
		System.out.println("JRand: " + (stop - start) + "ms");

		start = System.currentTimeMillis();
		for (int i = 0; i < loops; ++i) {
			xorrand.nextDouble();
		}
		stop = System.currentTimeMillis();
		System.out.println("XORRand: " + (stop - start) + "ms");

	}

	public static void main(final String[] args) {
		new XORShiftRandomTest().nextDouble();
	}

}
