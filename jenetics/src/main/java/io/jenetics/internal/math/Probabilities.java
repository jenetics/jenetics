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

import static java.lang.Math.E;
import static java.lang.Math.abs;

/**
 * Mathematical functions regarding probabilities.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 7.2
 */
public final class Probabilities {
	private Probabilities() {}

	private static final long RANGE =
		(long)Integer.MAX_VALUE - (long)Integer.MIN_VALUE;

	/**
	 * Values smaller than this value are treated as zero.
	 *
	 * @since 7.2
	 */
	public static final double EPSILON = Math.pow(10, -23);

	/**
	 * Return {@code true} if the given value can be treated as probability
	 * <em>zero</em>.
	 *
	 * @since 7.2
	 *
	 * @param value the probability value to test
	 * @return {@code true} if the given value can be treated as probability 0
	 */
	public static boolean isZero(final double value) {
		return equals(0, value, EPSILON);
	}

	/**
	 * Return {@code true} if the given value can be treated as probability
	 * <em>one</em>.
	 *
	 * @since 7.2
	 *
	 * @param value the probability value to test
	 * @return {@code true} if the given value can be treated as probability 1
	 */
	public static boolean isOne(final double value) {
		return equals(1, value, EPSILON);
	}

	private static boolean
	equals(final double a, final double b, final double delta) {
		return abs(a - b) <= delta;
	}

	/**
	 * Maps the probability, given in the range {@code [0, 1]}, to an
	 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
	 *
	 * @see #toFloat(int)
	 *
	 * @param probability the probability to widen.
	 * @return the widened probability.
	 */
	public static int toInt(final double probability) {
		return (int)(RANGE*probability + Integer.MIN_VALUE);
	}

	/**
	 * Maps the <i>integer</i> probability, within the range
	 * {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]} back to a float
	 * probability within the range {@code [0, 1]}.
	 *
	 * @see  #toInt(double)
	 *
	 * @param probability the <i>integer</i> probability to map.
	 * @return the mapped probability within the range {@code [0, 1]}.
	 */
	public static float toFloat(final int probability) {
		final long value = (long)probability + Integer.MAX_VALUE + 1;
		return (float)(value/(double) RANGE);
	}
}
