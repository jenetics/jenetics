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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import io.jenetics.internal.math.Subsets;

/**
 * This class "contains" all k-subsets of an n element set. The subsets are
 * <em>calculated</em> lazily, on demand. This set can only contain
 * {@link Long#MAX_VALUE} elements. Although the order of the elements within a
 * subset doesn't matter, this k-subset implementation creates and requires only
 * <em>ordered</em> subsets.
 * <p>
 * The following code snippet shows the iteration order of a given k-subset.
 * {@snippet class="CombinatorialSnippets" region="KSubset.iteration"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public final class KSubset implements Iterable<int[]>, Comparator<int[]> {

	private record Subset(int n, int k, int[] value)
		implements Comparable<Subset>
	{

		private Subset {
			if (value.length != k) {
				throw new IllegalArgumentException(
					"Invalid array size: %d != %d.".formatted(value.length, k)
				);
			}
			for (var i : value) {
				if (i < 0 || i >= n) {
					throw new IllegalArgumentException(
						"Values out of range [%d, %d): %s."
							.formatted(0, n, Arrays.toString(value))
					);
				}
			}
		}

		@Override
		public int compareTo(final Subset other) {
			for (int i = 0; i < k; ++i) {
				if (value[i] < other.value[i]) {
					return -1;
				} else if (value[i] > other.value[i]) {
					return 1;
				}
			}

			return 0;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(value);
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Subset sub && Arrays.equals(value, sub.value);
		}

		@Override
		public String toString() {
			return Arrays.toString(value);
		}

	}

	private final int n;
	private final int k;

	private final Subset start;
	private final Subset end;

	private final long startRank;
	private final long endRank;

	private final long size;

	private KSubset(
		final int n,
		final int k,
		final Subset start,
		final Subset end,
		final long startRank,
		final long endRank
	) {
		this.n = n;
		this.k = k;
		this.start = start;
		this.end = end;
		this.startRank = startRank;
		this.endRank = endRank;
		this.size = endRank - startRank + 1;
	}

	/**
	 * Create a new k-subset object with the given {@code n} and {@code k}.
	 *
	 * @throws IllegalArgumentException if {@code n < 0 || n < k} or
	 *         {@code binomial(n, k) > Long.MAX_VALUE}
	 */
	public KSubset(int n, int k) {
		this(n, k, start(n, k), end(n, k), 0, size(n, k) - 1);
		BinomialCoefficient.check(n, k);
	}

	private static Subset start(final int n, final int k) {
		final var a = new int[k];
		for (int i = 0; i < k; ++i) {
			a[i] = i;
		}
		return new Subset(n, k, a);
	}

	private static Subset end(final int n, final int k) {
		final var a = new int[k];
		for (int i = n - k; i < n; ++i) {
			a[i - n + k] = i;
		}
		return new Subset(n, k, a);
	}

	private static long size(final int n, final int k) {
		try {
			return BinomialCoefficient.apply(n, k);
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException(
				"KSubset[" + "n=" + n + ", " + "k=" + k + ']' + " to big."
			);
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
	 * Return the first element according its <em>natural</em> order.
	 * <p>
	 * <b>First element of KSubset[n=20, k=8]</b>
	 * <pre>
	 * [0, 1, 2, 3, 4, 5, 6, 7]
	 * </pre>
	 *
	 * @return the first element
	 */
	public int[] start() {
		return start.value.clone();
	}

	/**
	 * Return the last element according its <em>natural</em> order.
	 * <p>
	 * <b>Last element of KSubset[n=20, k=8]</b>
	 * <pre>
	 * [12, 13, 14, 15, 16, 17, 18, 19]
	 * </pre>
	 *
	 * @return the last element of the k-subset
	 */
	public int[] end() {
		return end.value.clone();
	}

	public KSubset range(final int[] start, final int[] end) {
		final var s = new Subset(n, k, start.clone());
		final var e = new Subset(n, k, end.clone());

		if (this.start.compareTo(s) > 0 || this.end.compareTo(s) < 0) {
			throw new IllegalArgumentException("Invalid start value: " + s);
		}


		return null;
	}

	/**
	 * Return a cursor which lets you iterate over all k-subsets, starting from
	 * the given {@code start} element.
	 *
	 * @param start the start element, inclusively
	 * @return a cursor from the given {@code start} element
	 * @throws IllegalArgumentException if {@code start} is not part of
	 *         {@code this} k-subset
	 */
	public Cursor cursor(final int[] start) {
		if (!contains(start)) {
			throw new IllegalArgumentException(
				"%s not in %s."
					.formatted(Arrays.toString(start), this)
			);
		}

		return new Cursor() {
			final int[] next = start.clone();
			boolean hasNext = true;

			@Override
			public boolean next(int[] index) {
				if (hasNext) {
					System.arraycopy(next, 0, index, 0, k);
					hasNext = KSubset.next(next, n);
					return true;
				} else {
					return false;
				}
			}

			@Override
			public int size() {
				return k;
			}
		};
	}

	public Cursor cursor(final int[] start, final int[] end) {
		if (!contains(start)) {
			throw new IllegalArgumentException(
				"Start set %s not in %s."
					.formatted(Arrays.toString(start), this)
			);
		}
		if (!contains(end)) {
			throw new IllegalArgumentException(
				"End set %s not in %s."
					.formatted(Arrays.toString(start), this)
			);
		}
		if (compare(start, end) > 0) {
			throw new IllegalArgumentException(
				"Start must be smaller or equal to end: %s > %s."
					.formatted(Arrays.toString(start), Arrays.toString(end))
			);
		}

		if (compare(end, end()) == 0) {
			if (compare(start, start()) == 0) {
				return cursor();
			} else {
				return cursor(start);
			}
		}

		return new Cursor() {
			final int[] next = start.clone();
			final long size = rank(end) - rank(start) + 1;
			long pos = 0;

			@Override
			public boolean next(int[] index) {
				if (pos < size) {
					System.arraycopy(next, 0, index, 0, k);
					++pos;
					return true;
				} else {
					return false;
				}
			}

			@Override
			public int size() {
				return k;
			}
		};
	}

	/**
	 * Return a cursor which lets you iterate over <em>all</em> k-subset
	 * elements. This method is equivalent to {@code cursor(first()}.
	 *
	 * @see #cursor(int[])
	 *
	 * @return the k-subset cursor
	 */
	public Cursor cursor() {
		return cursor(start());
	}

	/**
	 * Return a new (<em>infinite</em>) cursor which creates k-subsets in a
	 * random order.
	 * {@snippet class="CombinatorialSnippets" region="KSubset.randomCursor"}
	 *
	 * @apiNote
	 * The returned cursor may create duplicate k-subsets.
	 *
	 * @param random the random generator used for creating random k-subsets
	 * @return a new randomized k-subset cursor
	 * @throws NullPointerException if the given {@code random} generator is
	 *         {@code null}
	 */
	public Cursor cursor(final RandomGenerator random) {
		return cursor(random, n, k);
	}

	/**
	 * Return a new (<em>infinite</em>) cursor which creates k-subsets in a
	 * random order.
	 * {@snippet class="CombinatorialSnippets" region="KSubset.staticRandomCursor"}
	 *
	 * @apiNote
	 * The returned cursor may create duplicate k-subsets.
	 *
	 * @param random the random generator used for creating random k-subsets
	 * @param n the cardinality of the superset
	 * @param k the cardinality of the subset
	 * @return a new randomized k-subset cursor
	 * @throws NullPointerException if the given {@code random} generator is
	 *         {@code null}
	 * @throws IllegalArgumentException if {@code k <= 0 || n < k} or if
	 *         {@code n*k} overflows
	 */
	public static Cursor cursor(
		final RandomGenerator random,
		final int n,
		final int k
	) {
		requireNonNull(random);
		Subsets.checkSubSet(n, k);

		return new Cursor() {
			@Override
			public boolean next(int[] index) {
				if (index.length != k) {
					throw new IllegalArgumentException(
						"Invalid array size: %d != %d.".formatted(index.length, k)
					);
				}

				Subsets.next(random, n, k);
				return true;
			}

			@Override
			public int size() {
				return k;
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
	 * Returns a sequential {@code Stream} with {@code this} k-subset as its
	 * source.
	 *
	 * @return a new k-subset element stream
	 */
	public Stream<int[]> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Return the <em>rank</em> of the given k-subset {@code T}.
	 *
	 * @apiNote
	 * This method doesn't check if the given array {@code T} represents a
	 * valid k-subset. If {@code T} is <em>invalid</em>, the returned value is
	 * just an arbitrary number. If a given array is a valid k-subset can be
	 * checked with the {@link #contains(int[])} method.
	 *
	 * @param T the k-subset
	 * @return the rank of the k-subset
	 * @throws IllegalArgumentException if {@code T.length != k}
	 * @throws NullPointerException if {@code T} is {@code null}
	 */
	public long rank(final int[] T) {
		if (T.length != k) {
			throw new IllegalArgumentException(
				"Invalid array size: %d != %d.".formatted(T.length, k)
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
	 * @throws IllegalArgumentException if {@code T.length != k} or when the
	 *         given {@code rank} is invalid for {@code this} k-subset
	 * @throws NullPointerException if {@code T} is {@code null}
	 */
	public void unrank(final long rank, final int[] T) {
		if (T.length != k) {
			throw new IllegalArgumentException(
				"Invalid array size: %d != %d.".formatted(T.length, k)
			);
		}

		long r = rank;
		int x = 0;

		try {
			for (int i = 0; i < k; ++i) {
				long bc;
				while ((bc = BinomialCoefficient.apply(n - x - 1, k - i - 1)) <= r) {
					r -= bc;
					++x;
				}

				T[i] = x;
				++x;
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid rank: " + rank, e);
		}
	}

	/**
	 * Return the k-subset for the given {@code rank}.
	 *
	 * @param rank the k-subset rank
	 * @return the k-subset for the given {@code rank}
	 * @throws IllegalArgumentException if the given {@code rank} is invalid
	 *         for {@code this} k-subset
	 */
	public int[] unrank(final long rank) {
		final var T = new int[k];
		unrank(rank, T);
		return T;
	}

	private static boolean next(final int[] T, final int n) {
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

	/**
	 * Checks whether the given array {@code T} is part of {@code this} k-subset.
	 * To be part of {@code this} set, the elements of {@code T} must be sorted
	 * in ascending order.
	 * <p>
	 * <b>Element of KSubset[n=20, k=8]</b>
	 * <pre>
	 * [3, 4, 9, 11, 13, 15, 16, 17]
	 * [5, 8, 10, 12, 14, 15, 17, 19]
	 * [9, 11, 13, 15, 16, 17, 18, 19]
	 * </pre>
	 * <p>
	 * <b>Not element of KSubset[n=20, k=8]</b>
	 * <pre>
	 * [4, 3, 9, 11, 13, 15, 16, 17]
	 * [5, 8, 10, 10, 14, 15, 17, 19]
	 * [9, 11, 13, 16, 15, 17, 19, 19]
	 * [9, 11, 13, 16]
	 * </pre>
	 *
	 * @param T the k-subset to check
	 * @return {@code true} if {@code T.length == k}, {@code T} is sorted in
	 *         ascending order, has no duplicate elements and all elements are
	 *         within {@code [0, n)}, {@code false} otherwise
	 */
	public boolean contains(final int[] T) {
		// Check the length of the k-subset.
		if (T.length != k) {
			return false;
		}

		// Check the range of the values.
		for (var i : T) {
			if (i < 0 || i >= n) {
				return false;
			}
		}

		// Check for unique elements. The elements of 'T' must
		// be sorted in ascending order.
		var previous = T[0];
		for (int i = 1; i < k; ++i) {
			if (previous >= T[i]) {
				return false;
			} else {
				previous = T[i];
			}
		}

		return compare(start.value, T) <= 0 && compare(end.value, T) >= 0;
	}

	/**
	 * Compares two k-subset elements according its natural order.
	 * {@snippet class="CombinatorialSnippets" region="KSubset.compare"}
	 *
	 * @param a the first object to be compared.
	 * @param b the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the
	 *         first argument is less than, equal to, or greater than the
	 *         second.
	 * @throws IllegalArgumentException if {@code !contains(a) || !contains(b)}
	 */
	@Override
	public int compare(final int[] a, final int[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException(
				"a and b have different size: %d != %d."
					.formatted(a.length, b.length)
			);
		}
		if (a.length != k) {
			throw new IllegalArgumentException(
				"Invalid array size: %d != %d.".formatted(a.length, k)
			);
		}
		for (int i = 0; i < k; ++i) {
			if (a[i] < 0 || a[i] >= n || b[i] < 0 || b[i] >= n) {
				throw new IllegalArgumentException(
					"a=%s or b=%s not part ot %s.".formatted(
						Arrays.toString(a),
						Arrays.toString(b),
						this
					)
				);
			}
		}

		for (int i = 0; i < k; ++i) {
			if (a[i] < b[i]) {
				return -1;
			} else if (a[i] > b[i]) {
				return 1;
			}
		}

		return 0;
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

