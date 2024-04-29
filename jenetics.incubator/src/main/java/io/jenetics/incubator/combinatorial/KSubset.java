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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public final class KSubset implements Iterable<int[]> {
	private final int n;
	private final int k;
	private final long size;

	/**
	 * Create a new k-subset object with the given {@code n} and {@code k}.
	 *
	 * @throws IllegalArgumentException if {@code n < 0 || n < k}
	 */
	public KSubset(int n, int k) {
		BinomialCoefficient.check(n, k);

		this.n = n;
		this.k = k;
		try {
			this.size = BinomialCoefficient.apply(n, k);
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException(this + " to big.");
		}
	}

	/**
	 * Return the overall element count of the set.
	 *
	 * @return the overall element count of the set
	 */
	public int n() {
		return n;
	}

	/**
	 * Return the number of subset elements.
	 *
	 * @return the number of subset elements
	 */
	public int k() {
		return k;
	}

	/**
	 * Return the number of k-subset elements.
	 *
	 * @return the number of k-subset elements
	 */
	public long size() {
		return size;
	}

	/**
	 * Return the first k-subset.
	 *
	 * @return the first k-subset
	 */
	public int[] first() {
		final var T = new int[k];
		for (int i = 0; i < k; ++i) {
			T[i] = i;
		}
		return T;
	}

	/**
	 * Return a cursor, which lets you iterate over <em>all</em> k-subset
	 * elements.
	 *
	 * @return the k-subset cursor
	 */
	public Cursor cursor() {
		return new Cursor() {
			final int[] next = first();

			@Override
			public boolean next(int[] index) {
				final boolean hasNext = KSubset.next(next, n);
				System.arraycopy(next, 0, index, 0, k);
				return hasNext;
			}
		};
	}

	@Override
	public Iterator<int[]> iterator() {
		return new Iterator<>() {
			private final Cursor cursor = cursor();
			private final int[] value = new int[k];

			private boolean hasNext = false;
			private boolean finished = false;

			@Override
			public boolean hasNext() {
				if (finished) return false;
				if (hasNext) return true;

				try {
					return (hasNext = cursor.next(value));
				} finally {
					if (!hasNext) finished = true;
				}
			}

			@Override
			public int[] next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				hasNext = false;
				return value.clone();
			}
		};
	}

	/**
	 * Return the <em>rank</em> of the given k-subset {@code T}.
	 *
	 * @param T the k-subset
	 * @return the rank of the k-subset
	 * @throws IllegalArgumentException if {@code T.length < k}
	 * @throws NullPointerException if {@code T} is {@code null}
	 */
	public long rank(final int[] T) {
		if (T.length < k) {
			throw new IllegalArgumentException(
				"Given array too short: %d < %d.".formatted(T.length, k)
			);
		}

		long rank = 0;

		for (int i = 0; i < k; ++i) {
			for (int j = (i == 0 ? 0 : T[i - 1] + 1); j < T[i]; ++j) {
				rank += BinomialCoefficient.apply(n - j - 1, k - i - 1);
			}
		}

		return rank;
	}

	/**
	 * Calculates the k-subset from the given {@code rank}.
	 *
	 * @param rank the k-subset rank
	 * @param T the array where the k-subset is written to
	 * @throws IllegalArgumentException if {@code T.length < k}
	 * @throws NullPointerException if {@code T} is {@code null}
	 */
	public void unrank(final long rank, final int[] T) {
		if (T.length < k) {
			throw new IllegalArgumentException(
				"Given array too short: %d < %d.".formatted(T.length, k)
			);
		}

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

	@Override
	public boolean equals(Object obj) {
		return obj instanceof KSubset ks &&
			ks.n == n &&
			ks.k == k;
	}

	@Override
	public int hashCode() {
		return Objects.hash(n, k);
	}

	@Override
	public String toString() {
		return "KSubset[" + "n=" + n + ", " + "k=" + k + ']';
	}

}

