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

import static java.util.Objects.requireNonNull;

import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;

/**
 * This class contains methods for converting from and to the primitive arrays
 * {@code int[]}, {@code long[]} and {@code double[]}. Its main usage is to
 * unify numerical gene codecs, e.g.:
 * {@snippet lang=java:
 * final Codec<int[], DoubleGene> codec = Codecs
 *     .ofVector(DoubleRange.of(0, 100), 100)
 *     .map(ArrayConversions::doubleToIntArray);
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class ArrayConversions {

	private ArrayConversions() {
	}

	/**
	 * Converts the given {@code int[]} {@code array} to a {@code long[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static long[] intToLongArray(
		final int[] array,
		final IntToLongFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsLong(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<int[], long[]>
	intToLongFunction(final IntToLongFunction mapper) {
		requireNonNull(mapper);
		return array -> intToLongArray(array, mapper);
	}

	/**
	 * Converts the given {@code int[]} {@code array} to a {@code long[]} array.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static long[] intToLongArray(final int[] array) {
		return intToLongArray(array, i -> i);
	}

	/**
	 * Converts the given {@code int[]} {@code array} to a {@code double[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static double[] intToDoubleArray(
		final int[] array,
		final IntToDoubleFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsDouble(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<int[], double[]>
	intToDoubleArray(final IntToDoubleFunction mapper) {
		requireNonNull(mapper);
		return array -> intToDoubleArray(array, mapper);
	}

	/**
	 * Converts the given {@code int[]} {@code array} to a {@code double[]} array.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static double[] intToDoubleArray(final int[] array) {
		return intToDoubleArray(array, i -> i);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code int[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static int[] longToIntArray(
		final long[] array,
		final LongToIntFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsInt(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<long[], int[]>
	longToIntArray(final LongToIntFunction mapper) {
		requireNonNull(mapper);
		return array -> longToIntArray(array, mapper);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code int[]} array.
	 * The {@code int[]} is filled with the {@code long} values cast to
	 * {@code int} values.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static int[] longToIntArray(final long[] array) {
		return longToIntArray(array, l -> (int)l);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code double[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static double[] longToDoubleArray(
		final long[] array,
		final LongToDoubleFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsDouble(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<long[], double[]>
	longToDoubleArray(final LongToDoubleFunction mapper) {
		requireNonNull(mapper);
		return array -> longToDoubleArray(array, mapper);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code double[]} array.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static double[] longToDoubleArray(final long[] array) {
		return longToDoubleArray(array, l -> l);
	}

	/**
	 * Converts the given {@code double[]} {@code array} to a {@code int[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static int[] doubleToIntArray(
		final double[] array,
		final DoubleToIntFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final int[] result = new int[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsInt(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 * {@snippet lang=java:
	 * final Codec<int[], DoubleGene> codec = Codecs
	 *     .ofVector(DoubleRange.of(0, 100), 100)
	 *     .map(ArrayConversions.doubleToIntArray(v -> (int)Math.round(v)));
	 * }
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<double[], int[]>
	doubleToIntArray(final DoubleToIntFunction mapper) {
		requireNonNull(mapper);
		return array -> doubleToIntArray(array, mapper);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code double[]} array.
	 * The {@code int[]} is filled with the {@code double} values cast to
	 * {@code int} values.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static int[] doubleToIntArray(final double[] array) {
		return doubleToIntArray(array, l -> (int)l);
	}

	/**
	 * Converts the given {@code double[]} {@code array} to a {@code long[]} array.
	 *
	 * @param array the array to convert
	 * @param mapper the mapper function applied to each {@code array} element
	 * @return the converted array
	 */
	public static long[] doubleToLongArray(
		final double[] array,
		final DoubleToLongFunction mapper
	) {
		requireNonNull(array);
		requireNonNull(mapper);

		final long[] result = new long[array.length];
		for (int i = 0; i < array.length; ++i) {
			result[i] = mapper.applyAsLong(array[i]);
		}
		return result;
	}

	/**
	 * Return an array mapper function which applies the given {@code mapper} to
	 * every array element.
	 *
	 * @param mapper the array element mapper
	 * @return an array mapper function
	 */
	public static Function<double[], long[]>
	doubleToLongArray(final DoubleToLongFunction mapper) {
		requireNonNull(mapper);
		return array -> doubleToLongArray(array, mapper);
	}

	/**
	 * Converts the given {@code long[]} {@code array} to a {@code double[]} array.
	 * The {@code long[]} is filled with the {@code double} values cast to
	 * {@code long} values.
	 *
	 * @param array the array to convert
	 * @return the converted array
	 */
	public static long[] doubleToLongArray(final double[] array) {
		return doubleToLongArray(array, d -> (long)d);
	}

}
