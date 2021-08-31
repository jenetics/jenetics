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
import static io.jenetics.stat.StatisticsAssert.assertUniformDistribution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.Histogram;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CombinatoricsTest {

	@Test
	public void compatibility() {
//		final String resource = "/io/jenetics/internal/math/comb/subset";
//		final TestData data = TestData.of(resource);
//		final int[][] sub = data.stream()
//			.map(s -> Stream.of(s).mapToInt(Integer::parseInt).toArray())
//			.toArray(int[][]::new);

		for (int i = 1; i <= 1000; ++i) {
			int[] sub1 = subset(1000, new int[i], new Random(123));
 			int[] sub2 = new int[i];
			Combinatorics.subset(1000, sub2, new Random(123));
			//System.out.println(IntStream.of(sub1).mapToObj(Objects::toString).collect(Collectors.joining(",")));

			Assert.assertTrue(Arrays.equals(sub2, sub1), "K: " + i);
		}
	}

	@Test(dataProvider = "combinations")
	public void allCombinations(final int n, final int k) {
		final Random random = new Random();

		final Set<String> subsets = new HashSet<>();
		for (int i = 0; i < 3000; ++i) {
			subsets.add(Arrays.toString(Combinatorics.subset(n, new int[k], random)));
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
			{2, 1},
			{3, 2},
			{4, 2},
			{4, 3},
			{5, 1},
			{5, 2},
			{5, 3},
			{5, 4},
			{5, 5},
			{9, 4}
		};
	}

	@Test
	public void subset() {
		final Random random = new Random();

		for (int i = 1; i <= 1000; ++i) {
			int[] sub = new int[i];
			Combinatorics.subset(1000, sub, random);

			Assert.assertTrue(isSortedAndUnique(sub), "K: " + i);
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
			.flatMap(i -> IntStream.of(Combinatorics.subset(n, sub, random)))
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
