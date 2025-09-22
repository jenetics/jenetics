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

import static java.lang.String.format;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Integer range class.
 *
 * @param min the minimum value of the range
 * @param max the maximum value of the range
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 3.2
 */
public record IntRange(int min, int max) implements Serializable {

	/**
	 * Create a new range instance.
	 *
	 * @param min the minimum value of the range
	 * @param max the maximum value of the range
	 * @throws IllegalArgumentException if {@code min > max}
	 */
	public IntRange {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"Min greater than max: %s > %s", min, max
			));
		}
	}

	/**
	 * Create a new (half-open) range, which contains only the given value:
	 * {@code [value, value + 1)}.
	 *
	 * @param value the value of the created (half-open) integer range
	 */
	public IntRange(final int value) {
		this(value, value + 1);
	}

	/**
	 * Checks whether the given {@code value} is within the range
	 * {@code [min, max)}.
	 *
	 * @since 8.0
	 *
	 * @param value the value to check
	 * @return {@code true} if the {@code value} is with the range
	 *         {@code [min, max)}, {@code false} otherwise
	 */
	public boolean contains(final int value) {
		return value >= min && value < max;
	}

	/**
	 * Return the intersection of {@code this} range with the {@code other}.
	 *
	 * @since 8.0
	 *
	 * @param other the intersection range or {@link Optional#empty()} if there
	 *        is none
	 * @return the range intersection
	 */
	public Optional<IntRange> intersect(final IntRange other) {
		if (max <= other.min || min >= other.max) {
			return Optional.empty();
		} else {
			return Optional.of(
				new IntRange(
					Math.max(min, other.min),
					Math.min(max, other.max)
				)
			);
		}
	}

	/**
	 * Return the size of the {@code IntRange}: {@code max - min}.
	 *
	 * @since 3.9
	 *
	 * @return the size of the int range
	 */
	public int size() {
		return max - min;
	}

	/**
	 * Returns a sequential ordered {@code IntStream} from {@link #min()}
	 * (inclusive) to {@link #max()} (exclusive) by an incremental step of
	 * {@code 1}.
	 * <p>
	 * An equivalent sequence of increasing values can be produced sequentially
	 * using a {@code for} loop as follows:
	 * {@snippet lang="java":
	 * for (int i = range.min(); i < range.max(); ++i) {
	 *     // ...
	 * }
	 * }
	 *
	 * @since 3.4
	 *
	 * @return a sequential {@link IntStream} for the range of {@code int}
	 *         elements
	 */
	public IntStream stream() {
		return IntStream.range(min, max);
	}

	/**
	 * Create a new {@code IntRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the integer range
	 * @param max the upper bound of the integer range
	 * @return a new {@code IntRange} object
	 * @throws IllegalArgumentException if {@code min > max}
	 *
	 * @deprecated Class is a record now, and this factory method will be
	 *             removed in the next major version. Use {@link #IntRange(int, int)}
	 *             instead.
	 */
	@Deprecated(since = "8.2", forRemoval = true)
	public static IntRange of(final int min, final int max) {
		return new IntRange(min, max);
	}

	/**
	 * Return a new (half-open) range, which contains only the given value:
	 * {@code [value, value + 1)}.
	 *
	 * @since 4.0
	 *
	 * @param value the value of the created (half-open) integer range
	 * @return a new (half-open) range, which contains only the given value
	 *
	 * @deprecated Class is a record now, and this factory method will be
	 *             removed in the next major version. Use {@link #IntRange(int)}
	 *             instead.
	 */
	@Deprecated(since = "8.2", forRemoval = true)
	public static IntRange of(final int value) {
		return of(value, value + 1);
	}

	@Override
	public String toString() {
		return "[" + min + ", " + max + "]";
	}

}
