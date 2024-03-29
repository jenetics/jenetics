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
package io.jenetics.internal.util;

import java.util.random.RandomGenerator;

/**
 * Helper class which contains array helper methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public final class Arrays {
	private Arrays() {}

	public static <T> void swap(final T[] array, final int i, final int j) {
		final T temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static void swap(final int[] array, final int i, final int j) {
		final int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static void swap(final long[] array, final int i, final int j) {
		final long temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static void swap(final double[] array, final int i, final int j) {
		final double temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	public static <T> T[] revert(final T[] array) {
		for (int i = 0, j = array.length - 1; i < j; ++i, --j) {
			swap(array, i, j);
		}

		return array;
	}

	public static int[] revert(final int[] array) {
		for (int i = 0, j = array.length - 1; i < j; ++i, --j) {
			swap(array, i, j);
		}

		return array;
	}

	public static long[] revert(final long[] array) {
		for (int i = 0, j = array.length - 1; i < j; ++i, --j) {
			swap(array, i, j);
		}

		return array;
	}

	public static double[] revert(final double[] array) {
		for (int i = 0, j = array.length - 1; i < j; ++i, --j) {
			swap(array, i, j);
		}

		return array;
	}

	/**
	 * Randomize the {@code array} using the given {@link RandomGenerator}
	 * object. The used shuffling algorithm is from D. Knuth TAOCP, Seminumerical
	 * Algorithms, Third edition, page 142, Algorithm S (Selection sampling
	 * technique).
	 *
	 * @param array the array to shuffle
	 * @param random the PRNG
	 * @return the shuffled array
	 */
	public static double[] shuffle(
		final double[] array,
		final RandomGenerator random
	) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
		return array;
	}

	public static int[] shuffle(
		final int[] array,
		final RandomGenerator random
	) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
		return array;
	}

	public static int[] add(final int[] array, final int b) {
		for (int i = 0; i < array.length; ++i) {
			array[i] += b;
		}
		return array;
	}

	public static void rangeCheck(final int from, final int to) {
		if (from > to) {
			throw new IllegalArgumentException(
				"fromIndex(" + from + ") > toIndex(" + to + ")");
		}
		if (from < 0) {
			throw new ArrayIndexOutOfBoundsException(from);
		}
	}

	public static long sum(final long[] values) {
		long sum = 0;
		for (var value : values) {
			sum += value;
		}
		return sum;
	}

}
