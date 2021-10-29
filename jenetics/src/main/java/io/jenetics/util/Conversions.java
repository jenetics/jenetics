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

public final class Conversions {
	private Conversions() {
	}

	public static int[] toIntArray(final double... values) {
		final var result = new int[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = (int)values[i];
		}
		return result;
	}

	public static long[] toLongArray(final double... values) {
		final var result = new long[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = (long)values[i];
		}
		return result;
	}

	public static Double[] box(final double... values) {
		final Double[] result = new Double[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i];
		}
		return result;
	}

	public static double[] unbox(final Double... values) {
		final double[] result = new double[values.length];
		for (int i = values.length; --i >= 0;) {
			result[i] = values[i];
		}
		return result;
	}

}
