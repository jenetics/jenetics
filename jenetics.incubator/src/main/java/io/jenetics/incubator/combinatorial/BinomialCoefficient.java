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

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public final class BinomialCoefficient {
	private BinomialCoefficient() {
	}

	public static long apply(final int n, final int k) {
		check(n, k);

		// Handling some special cases.
		if (n == k || k == 0) {
			return 1;
		}
		if (k == 1 || k == n - 1) {
			return n;
		}
		if (k > n/2) {
			return apply(n, n - k);
		}

		// For n <= 61, the algorithm cannot overflow.
		if (n <= 61) {
			long result = 1L;
			for (int i = n - k + 1, j = 1; j <= k; ++i, ++j) {
				result = result*i/j;
			}
			return result;
		}

		// For 61 < n <= 66, the result cannot overflow, but intermediate
		// results can.
		if (n <= 66) {
			long result = 1L;
			for (int i = n - k + 1, j = 1; j <= k; ++i, ++j) {
				final long d = gcd(i, j);
				result = result/(j/d)*(i/d);
			}
			return result;
		}

		// Calculate and check for overflow.
		long result = 1L;
		for (int i = n - k + 1, j = 1; j <= k; ++i, ++j) {
			final long d = gcd(i, j);
			result = Math.multiplyExact(result/(j/d), i/d);
		}
		return result;
	}

	private static int gcd(int a, int b) {
		if (a == 0) {
			return b;
		} else if (b == 0) {
			return a;
		} else {
			final int a2 = Integer.numberOfTrailingZeros(a);
			a >>= a2;
			final int b2 = Integer.numberOfTrailingZeros(b);
			b >>= b2;

			int shift;
			for (shift = Math.min(a2, b2);
			    a != b;
				a >>= Integer.numberOfTrailingZeros(a))
			{
				final int delta = a - b;
				b = Math.min(a, b);
				a = Math.abs(delta);
			}

			return a << shift;
		}
	}

	private static void check(final int n, final int k) {
		if (n < 0 || n < k) {
			throw new IllegalArgumentException(
				"Invalid coefficients [n=%d, k=%d].".formatted(n, k)
			);
		}
	}

}
