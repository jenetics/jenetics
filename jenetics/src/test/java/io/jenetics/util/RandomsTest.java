/*
/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import static io.jenetics.internal.math.Randoms.toDouble;
import static io.jenetics.internal.math.Randoms.toDouble2;
import static io.jenetics.internal.math.Randoms.toFloat;
import static io.jenetics.internal.math.Randoms.toFloat2;
import static io.jenetics.testfixtures.stat.StatisticsAssert.assertThatObservation;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.math.Randoms;
import io.jenetics.testfixtures.stat.Histogram;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class RandomsTest {

	private static final int INVOCATION_COUNT = 10;
	private static final int SUCCESS_PERCENTAGE = 70;

	@DataProvider(name = "nextBigIntegerData")
	public Object[][] nextBigIntegerData() {
		return new Object[][] {
			{"1000000000000000000000000000000000000"},
			{"10000000000000000000000000000000000000000"},
			{"100000000000000000000000000000000000000000000"},
			{"1000000000000000000000000000000000000000000000000"},
			{"10000000000000000000000000000000000000000000000000000"},
			{"100000000000000000000000000000000000000000000000000000000"}
		};
	}

	@Test
	public void seed() {
		for (int i = 0; i < 100; ++i) {
			final long seed1 = Randoms.seed();
			final long seed2 = Randoms.seed();
			Assert.assertNotEquals(seed1, seed2);
		}
	}

	@Test
	public void seedLong() {
		for (int i = 0; i < 100; ++i) {
			final long seed1 = Randoms.seed(i);
			final long seed2 = Randoms.seed(i);
			Assert.assertNotEquals(seed1, seed2);
		}
	}

	@Test
	public void seedBytes() {
		final int length = 123;

		for (int i = 0; i < 100; ++i) {
			final byte[] seed1 = Randoms.seedBytes(length);
			final byte[] seed2 = Randoms.seedBytes(length);
			Assert.assertFalse(Arrays.equals(seed1, seed2));
		}
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toFloat_int() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toFloat(random.nextInt()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toFloat_long() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toFloat(random.nextLong()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toDouble_long() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toDouble(random.nextLong()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toDouble_int_int() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accept(toDouble((int)(value >>> 32), (int)value));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toFloat2_int() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toFloat2(random.nextInt()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toFloat2_long() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toFloat2(random.nextLong()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toDouble2_long() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accept(toDouble2(random.nextLong()));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

	@Test(invocationCount = INVOCATION_COUNT, successPercentage = SUCCESS_PERCENTAGE)
	public void toDouble2_int_int() {
		final var random = RandomGenerator.getDefault();
		final var histogram = Histogram.Builder.of(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accept(toDouble2((int)(value >>> 32), (int)value));
		}

		assertThatObservation(histogram.build()).isUniform();
	}

}
