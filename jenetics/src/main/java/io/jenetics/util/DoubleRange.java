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

/**
 * Double range class.
 *
 * @param min the minimum value of the range
 * @param max the maximum value of the range
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.3
 * @since 3.2
 */
public record DoubleRange(double min, double max) implements Serializable {

	/**
	 * Create a new {@code DoubleRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the double range
	 * @param max the upper bound of the double range
	 * @throws IllegalArgumentException if {@code min > max} or one of the
	 *         parameters is not finite
	 */
	public DoubleRange {
		if (!Double.isFinite(min) || !Double.isFinite(max) || min > max) {
			throw new IllegalArgumentException(format(
				"Min greater than max: %s > %s", min, max
			));
		}
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
	public boolean contains(final double value) {
		return Double.isFinite(value) &&
			Double.compare(value, min) >= 0 &&
			Double.compare(value, max) < 0;
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
	public Optional<DoubleRange> intersect(final DoubleRange other) {
		if (Double.compare(max, other.min) <= 0 ||
			Double.compare(min, other.max) >= 0)
		{
			return Optional.empty();
		} else {
			return Optional.of(
				new DoubleRange(
					Math.max(min, other.min),
					Math.min(max, other.max)
				)
			);
		}
	}

	@Override
	public String toString() {
		return "[" + min + ", " + max + "]";
	}

}
