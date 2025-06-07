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
import java.util.stream.LongStream;

/**
 * Long range class.
 *
 * @param min the minimum value of the range
 * @param max the maximum value of the range
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 3.2
 */
public record LongRange(long min, long max) implements Serializable {

	/**
	 * Create a new {@code LongRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the long range
	 * @param max the upper bound of the long range
	 * @throws IllegalArgumentException if {@code min > max}
	 */
	public LongRange {
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
	public LongRange(final long value) {
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
	public boolean contains(final long value) {
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
	public Optional<LongRange> intersect(final LongRange other) {
		if (max <= other.min || min >= other.max) {
			return Optional.empty();
		} else {
			return Optional.of(
				new LongRange(
					Math.max(min, other.min),
					Math.min(max, other.max)
				)
			);
		}
	}

	/**
	 * Returns a sequential ordered {@code LongStream} from {@link #min()}
	 * (inclusive) to {@link #max()} (exclusive) by an incremental step of
	 * {@code 1}.
	 * <p>
	 * An equivalent sequence of increasing values can be produced sequentially
	 * using a {@code for} loop as follows:
	 * {@snippet lang="java":
	 * for (long i = range.min(); i < range.max(); ++i) {
	 *     // ...
	 * }
	 * }
	 *
	 * @since 3.4
	 *
	 * @return a sequential {@link LongStream} for the range of {@code long}
	 *         elements
	 */
	public LongStream stream() {
		return LongStream.range(min, max);
	}

	/**
	 * Create a new {@code LongRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the long range
	 * @param max the upper bound of the long range
	 * @return a new {@code LongRange} object
	 * @throws IllegalArgumentException if {@code min > max}
	 *
	 * @deprecated Class is a record now, and this factory method will be
	 *             removed in the next major version. Use
	 *             {@link #LongRange(long, long)} instead.
	 */
	@Deprecated(since = "8.2", forRemoval = true)
	public static LongRange of(final long min, final long max) {
		return new LongRange(min, max);
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
	 *             removed in the next major version. Use {@link #LongRange(long)}
	 *             instead.
	 */
	@Deprecated(since = "8.2", forRemoval = true)
	public static LongRange of(final long value) {
		return of(value, value + 1);
	}

	@Override
	public String toString() {
		return "[" + min + ", " + max + "]";
	}

}
