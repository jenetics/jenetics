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
package org.jenetics.internal.util;

import static java.lang.String.format;

/**
 * Some helper methods for creating hash codes and comparing values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6
 */
public final class require {
	private require() { noInstance(); }


	/**
	 * Calling the constructor of an {@code StaticObject} will always throw an
	 * {@link AssertionError}.
	 *
	 * @throws AssertionError always.
	 */
	public static void noInstance() {
		String message = "Object instantiation is not allowed";

		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace.length >= 3) {
			message = format(
				"Instantiation of '%s' is not allowed.",
				trace[2].getClassName()
			);
		}

		throw new AssertionError(message);
	}

	/**
	 * Check if the specified value is not negative.
	 *
	 * @param value the value to check.
	 * @param message the exception message.
	 * @return the given value.
	 * @throws IllegalArgumentException if {@code value < 0}.
	 */
	public static double nonNegative(final double value, final String message) {
		if (value < 0) {
			throw new IllegalArgumentException(format(
				"%s must not negative: %f.", message, value
			));
		}
		return value;
	}

	/**
	 * Check if the specified value is not negative.
	 *
	 * @param value the value to check.
	 * @return the given value.
	 * @throws IllegalArgumentException if {@code value < 0}.
	 */
	public static double nonNegative(final double value) {
		return nonNegative(value, "Value");
	}

	/**
	 * Check if the given integer is negative.
	 *
	 * @param length the value to check.
	 * @throws NegativeArraySizeException if the given {@code length} is smaller
	 * 		  than zero.
	 */
	public static int nonNegative(final int length) {
		if (length < 0) {
			throw new NegativeArraySizeException(
				"Length must be greater than zero, but was " + length + ". "
			);
		}
		return length;
	}

	/**
	 * Require the given {@code value} to be positive (&gt; 0).
	 * @param value the value to check
	 * @return the given value
	 * @throws IllegalArgumentException if the given {@code value} is smaller than
	 *         or equal zero.
	 */
	public static int positive(final int value) {
		if (value <= 0) {
			throw new IllegalArgumentException(format(
				"Value is not positive: %d", value
			));
		}
		return value;
	}

	public static long positive(final long value) {
		if (value <= 0) {
			throw new IllegalArgumentException(format(
				"Value is not positive: %d", value
			));
		}
		return value;
	}

	/**
	 * Check if the given double value is within the closed range {@code [0, 1]}.
	 *
	 * @param p the probability to check.
	 * @return p if it is a valid probability.
	 * @throws IllegalArgumentException if {@code p} is not a valid probability.
	 */
	public static double probability(final double p) {
		if (p < 0.0 || p > 1.0) {
			throw new IllegalArgumentException(format(
				"The given probability is not in the range [0, 1]: %f", p
			));
		}
		return p;
	}

}
