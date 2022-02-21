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

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;
import io.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class SubsetTest {

	@Test
	public void compatibility() {
		final int n = 1000;

		for (int k = 2; k <= 500; ++k) {
			int[] sub1 = subset(n, new int[k], new Random(123));
 			int[] sub2 = Subset.next(n, k, new Random(123));

			assertThat(sub2).isEqualTo(sub1);
		}
	}

	@Test(dataProvider = "subsets")
	public void compatibility(final int[] subset) {
		assertThat(Subset.next(1000, subset.length,  new Random(123)))
			.isEqualTo(subset);
	}

	@DataProvider
	public Object[][] subsets() {
		final String resource = "/io/jenetics/internal/math/subset";
		final TestData data = TestData.of(resource);
		return data.stream()
			.map(s -> Stream.of(s).mapToInt(Integer::parseInt).toArray())
			.map(s -> new Object[]{s})
			.toArray(Object[][]::new);
	}

	@Test(dataProvider = "combinations")
	public void allCombinations(final int n, final int k) {
		final Random random = new Random();

		final Set<String> subsets = new HashSet<>();
		for (int i = 0; i < 3_000; ++i) {
			subsets.add(Arrays.toString(Subset.next(n, k, random)));
		}
		Assert.assertEquals(subsets.size(), binomial(n, k));
	}

	private static long binomial(int n, int k) {
		return (n == k) || (k == 0)
			? 1
			: binomial(n - 1, k) + binomial(n - 1, k - 1);
	}

	@DataProvider
	public Object[][] combinations() {
		return new Object[][] {
			{1, 1},
			{2, 1},
			{3, 2},
			{4, 2},
			{4, 3},
			{5, 1},
			{5, 2},
			{5, 3},
			{5, 4},
			{5, 5},
			{9, 4},
			{10, 4},
			{10, 8}
		};
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void subset_1_0() {
		Subset.next(1,0, RandomGenerator.getDefault());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void subset_0_1() {
		Subset.next(0, 1, RandomGenerator.getDefault());
	}

	@Test
	public void subset() {
		final var random = RandomGenerator.getDefault();
		final int n = 2_500;

		for (int k = 1; k <= n; ++k) {
			int[] sub = Subset.next(n, k, random);

			for (int v : sub) {
				assertThat(v).isBetween(0, n);
			}
			Assert.assertTrue(isSortedAndUnique(sub), "K: " + k);
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
		final int n = 100_000;

		final Random random = new Random();
		final Histogram<Integer> histogram = Histogram.ofInteger(0, n, 13);

		IntStream.range(0, 10_000)
			.flatMap(i -> IntStream.of(Subset.next(n, 3, random)))
			.forEach(histogram::accept);

		assertUniformDistribution(histogram);
	}


	private static int[] subset(
		final int n,
		final int[] sub,
		final RandomGenerator random
	) {
		final int k = sub.length;

		if (sub.length == n) {
			for (int i = 0; i < k; ++i) {
				sub[i] = i;
			}
			return sub;
		}

		for (int i = 0; i < k; ++i) {
			sub[i] = (i*n)/k;
		}

		int l = 0;
		int ix = 0;
		for (int i = 0; i < k; ++i) {
			do {
				ix = nextInt(random, 1, n);
				l = (ix*k - 1)/n;
			} while (sub[l] >= ix);

			sub[l] = sub[l] + 1;
		}

		int m = 0;
		int ip = 0;
		int is = k;
		for (int i = 0; i < k; ++i) {
			m = sub[i];
			sub[i] = 0;

			if (m != (i*n)/k) {
				ip = ip + 1;
				sub[ip - 1] = m;
			}
		}

		int ihi = ip;
		int ids = 0;
		for (int i = 1; i <= ihi; ++i) {
			ip = ihi + 1 - i;
			l = 1 + (sub[ip - 1]*k - 1)/n;
			ids = sub[ip - 1] - ((l - 1)*n)/k;
			sub[ip - 1] = 0;
			sub[is - 1] = l;
			is = is - ids;
		}

		int ir = 0;
		int m0 = 0;
		for (int ll = 1; ll <= k; ++ll) {
			l = k + 1 - ll;

			if (sub[l - 1] != 0) {
				ir = l;
				m0 = 1 + ((sub[l - 1] - 1)*n)/k;
				m = (sub[l-1]*n)/k - m0 + 1;
			}

			ix = nextInt(random, m0, m0 + m - 1);

			int i = l + 1;
			while (i <= ir && ix >= sub[i - 1]) {
				ix = ix + 1;
				sub[ i- 2] = sub[i - 1];
				i = i + 1;
			}

			sub[i - 2] = ix;
			--m;
		}

		for (int i = 0; i < sub.length; ++i) sub[i] -= 1;
		return sub;
	}

	private static int nextInt(final RandomGenerator r, final int a, final int b) {
		return nextInt(a, b + 1, r);
	}

	private static int nextInt(
		final int origin,
		final int bound,
		final RandomGenerator random
	) {
		if (origin >= bound) {
			throw new IllegalArgumentException(format(
				"origin >= bound: %d >= %d", origin, bound
			));
		}

		final int value;
		int n = bound - origin;
		if (n > 0) {
			value = random.nextInt(n) + origin;
		} else {
			int r;
			do {
				r = random.nextInt();
			} while (r < origin || r >= bound);
			value = r;
		}

		return value;
	}

}
