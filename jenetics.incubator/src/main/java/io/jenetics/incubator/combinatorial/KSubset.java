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
package io.jenetics.incubator.combinatorial;

import java.util.Arrays;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public final class KSubset {
	private KSubset() {
	}

	public static int[] first(final int k) {
		final var T = new int[k];
		for (int i = 0; i < k; ++i) {
			T[i] = i;
		}
		return T;
	}

	public static boolean next(final int[] T, final int n) {
		final int k = T.length;

		int i = k - 1;
		while (i >= 0 && T[i] == n - k + i) {
			--i;
		}
		if (i == -1) {
			return false;
		}

		final int Ti = T[i] + 1 - i;
		for (int j = i; j < k; ++j) {
			T[j] = Ti + j;
		}
		return true;
	}

	public static long rank(final int[] T, final int n) {
		final int k = T.length;
		long rank = 0;

		for (int i = 0; i < k; ++i) {
			for (int j = (i == 0 ? 0 : T[i - 1] + 1); j < T[i]; ++j) {
				rank += BinomialCoefficient.apply(n - j - 1, k - i - 1);
			}
		}

		return rank;
	}

	public static void unrank(final long rank, final int n, final int[] T) {
		final int k = T.length;

		long r = rank;
		int x = 0;

		for (int i = 0; i < k; ++i) {
			long bc;
			while ((bc = BinomialCoefficient.apply(n - x - 1, k - i - 1)) <= r) {
				r -= bc;
				++x;
			}

			T[i] = x;
			++x;
		}
	}

	public static void main(String[] args) {
		final int n = 6;
		final int k = 3;
		final var T = first(k);

		System.out.println(Arrays.toString(T) + ": " + rank(T, n));
		while (next(T, n)) {
			long rank = rank(T, n);
			var unrank = new int[k];
			unrank(rank, n, unrank);
			System.out.println(Arrays.toString(T) + ": " + rank + ": " + Arrays.toString(unrank));
		}
	}

}

