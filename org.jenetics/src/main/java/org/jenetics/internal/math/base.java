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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.math;

import static java.lang.Double.doubleToLongBits;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;

import org.jenetics.internal.util.require;

import org.jenetics.util.RandomRegistry;

/**
 * This object contains mathematical helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
public final class base {
	private base() {require.noInstance();}

	/**
	 * <i>Clamping</i> a value between a pair of boundary values.
	 * <i>Note: using clamp with floating point numbers may give unexpected
	 * results if one of the values is {@code NaN}.</i>
	 *
	 * @param v the value to <i>clamp</i>
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return The clamped value:
	 *        <ul>
	 *            <li>{@code lo if v < lo}</li>
	 *            <li>{@code hi if hi < v}</li>
	 *            <li>{@code otherwise, v}</li>
	 *        </ul>
	 */
	public static double clamp(final double v, final double lo, final double hi) {
		return v < lo ? lo : v > hi ? hi : v;
	}

	/**
	 * Return the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * distance of the given two double values.
	 *
	 * @param a first double.
	 * @param b second double.
	 * @return the ULP distance.
	 * @throws ArithmeticException if the distance doesn't fit in a long value.
	 */
	public static long ulpDistance(final double a, final double b) {
		return Math.subtractExact(ulpPosition(a), ulpPosition(b));
	}

	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * position of a double number.
	 *
	 * <pre>{@code
	 * double a = 0.0;
	 * for (int i = 0; i < 10; ++i) {
	 *     a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 * }
	 *
	 * for (int i = 0; i < 19; ++i) {
	 *     a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
	 *     System.out.println(
	 *          a + "\t" + ulpPosition(a) + "\t" + ulpDistance(0.0, a)
	 *     );
	 * }
	 * }</pre>
	 *
	 * The code fragment above will create the following output:
	 * <pre>
	 *   4.4E-323    9  9
	 *   4.0E-323    8  8
	 *   3.5E-323    7  7
	 *   3.0E-323    6  6
	 *   2.5E-323    5  5
	 *   2.0E-323    4  4
	 *   1.5E-323    3  3
	 *   1.0E-323    2  2
	 *   4.9E-324    1  1
	 *   0.0         0  0
	 *  -4.9E-324   -1  1
	 *  -1.0E-323   -2  2
	 *  -1.5E-323   -3  3
	 *  -2.0E-323   -4  4
	 *  -2.5E-323   -5  5
	 *  -3.0E-323   -6  6
	 *  -3.5E-323   -7  7
	 *  -4.0E-323   -8  8
	 *  -4.4E-323   -9  9
	 * </pre>
	 *
	 * @param a the double number.
	 * @return the ULP position.
	 */
	public static long ulpPosition(final double a) {
		long t = doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}

	/**
	 * Selects a random subset of size {@code k} from a set of size {@code n}.
	 *
	 * @see #subset(int, int[])
	 *
	 * @param n the size of the set.
	 * @param k the size of the subset.
	 * @throws IllegalArgumentException if {@code n < k}, {@code k == 0} or if
	 *          {@code n*k} will cause an integer overflow.
	 * @return the subset array.
	 */
	public static int[] subset(final int n, final int k) {
		return subset(n, k, RandomRegistry.getRandom());
	}

	/**
	 * Selects a random subset of size {@code k} from a set of size {@code n}.
	 *
	 * @see #subset(int, int[], Random)
	 *
	 * @param n the size of the set.
	 * @param k the size of the subset.
	 * @param random the random number generator used.
	 * @throws NullPointerException if {@code random} is {@code null}.
	 * @throws IllegalArgumentException if {@code n < k}, {@code k == 0} or if
	 *         {@code n*k} will cause an integer overflow.
	 * @return the subset array.
	 */
	public static int[] subset(final int n, final int k, final Random random) {
		requireNonNull(random, "Random");
		if (k <= 0) {
			throw new IllegalArgumentException(format(
					"Subset size smaller or equal zero: %s", k
				));
		}
		if (n < k) {
			throw new IllegalArgumentException(format(
					"n smaller than k: %s < %s.", n, k
				));
		}

		final int[] sub = new int[k];
		subset(n, sub,random);
		return sub;
	}

	/**
	 * <p>
	 * Selects a random subset of size {@code sub.length} from a set of size
	 * {@code n}.
	 * </p>
	 *
	 * <p>This is a Java re-implementation of the C++ version by John Burkardt.</p>
	 *
	 * <p><em><a href="https://people.scs.fsu.edu/~burkardt/c_src/subset/subset.html">
	 *  Reference:</a></em>
	 * 	 Albert Nijenhuis, Herbert Wilf,
	 * 	 Combinatorial Algorithms for Computers and Calculators,
	 * 	 Second Edition,
	 * 	 Academic Press, 1978,
	 * 	 ISBN: 0-12-519260-6,
	 * 	 LC: QA164.N54.
	 * </p>
	 *
	 * @param n the size of the set.
	 * @param sub the sub set array.
	 * @throws NullPointerException if {@code sub} is {@code null}.
	 * @throws IllegalArgumentException if {@code n < sub.length},
	 *         {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *         integer overflow.
	 */
	public static void subset(final int n, final int sub[]) {
		subset(n, sub, RandomRegistry.getRandom());
	}

	/**
	 * <p>
	 * Selects a random subset of size {@code sub.length} from a set of size
	 * {@code n}.
	 * </p>
	 *
	 * <p>This is a Java re-implementation of the  C++ version by John Burkardt.</p>
	 *
	 * <p><em><a href="https://people.scs.fsu.edu/~burkardt/c_src/subset/subset.html">
	 *  Reference:</a></em>
	 *      Albert Nijenhuis, Herbert Wilf,
	 *      Combinatorial Algorithms for Computers and Calculators,
	 *      Second Edition,
	 *      Academic Press, 1978,
	 *      ISBN: 0-12-519260-6,
	 *      LC: QA164.N54.
	 * </p>
	 *
	 * @param n the size of the set.
	 * @param sub the sub set array.
	 * @param random the random number generator used.
	 * @return the sub-set array for the given parameter
	 * @throws NullPointerException if {@code sub} or {@code random} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if {@code n < sub.length},
	 *         {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *         integer overflow.
	 */
	public static int[] subset(final int n, final int sub[], final Random random) {
		requireNonNull(random, "Random");
		requireNonNull(sub, "Sub set array");

		final int k = sub.length;
		checkSubSet(n, k);

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

		return sub;
	}

	public static void checkSubSet(final int n, final int k) {
		if (k <= 0) {
			throw new IllegalArgumentException(format(
				"Subset size smaller or equal zero: %s", k
			));
		}
		if (n < k) {
			throw new IllegalArgumentException(format(
				"n smaller than k: %s < %s.", n, k
			));
		}
		if (!arithmetic.isMultiplicationSave(n, k)) {
			throw new IllegalArgumentException(format(
				"n*sub.length > Integer.MAX_VALUE (%s*%s = %s > %s)",
				n, k, (long)n*(long)k, Integer.MAX_VALUE
			));
		}
	}

	private static int nextInt(final Random random, final int a, final int b) {
		return a == b ? a - 1 : random.nextInt(b - a) + a;
	}

}
