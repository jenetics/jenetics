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
package io.jenetics.internal.math;

import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class combTest {

	@Test
	public void compatibility() {
		final String resource = "/io/jenetics/internal/math/comb/subset";
		final TestData data = TestData.of(resource);
		final int[][] sub = data.stream()
			.map(s -> Stream.of(s).mapToInt(Integer::parseInt).toArray())
			.toArray(int[][]::new);

		for (int i = 1; i < 100; ++i) {
			int[] sub2 = new int[i];
			comb.subset(1000, sub2, new Random(123));

			Assert.assertTrue(Arrays.equals(sub[i - 1], sub2));
		}
	}

	@Test
	public void subset() {
		final Random random = new Random();

		for (int i = 1; i < 100; ++i) {
			int[] sub = new int[i];
			comb.subset(1000, sub, random);

			Assert.assertTrue(isSortedAndUnique(sub));
		}
	}

	private static boolean isSortedAndUnique(final int[] array) {
		boolean sorted = true;
		for (int i = 0; i < array.length - 1 && sorted; ++i) {
			sorted = array[i] < array[i + 1];
		}
		return sorted;
	}

	@Test(invocationCount = 20, successPercentage = 95)
	public void subSetDistribution() {
		final int[] sub = new int[3];
		final int n = 100_000;

		final Random random = new Random();
		final Histogram<Integer> histogram = Histogram.ofInteger(0, n, 13);

		IntStream.range(0, 10_000)
			.flatMap(i -> IntStream.of(comb.subset(n, sub, random)))
			.forEach(histogram::accept);

		assertUniformDistribution(histogram);
	}

}
