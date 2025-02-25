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
package io.jenetics.incubator.stat;

import static java.lang.Double.doubleToLongBits;

import java.util.Optional;

/**
 * Defines a double interval.
 *
 * @param min the lower bound of the interval (inclusively)
 * @param max the upper bound of the interval (exclusively)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public record Interval(double min, double max) {

	/**
	 * Create a new interval with the given values.
	 *
	 * @param min the minimal value of the interval, inclusively. Might be
	 * {@link Double#NEGATIVE_INFINITY}
	 * @param max the maximal value of the interval, exclusively. Might be
	 * {@link Double#POSITIVE_INFINITY}
	 * @throws IllegalArgumentException if the {@code min} and {@code max}
	 * values are {@link Double#NaN} or {@code min >= max}
	 */
	public Interval {
		if (Double.isNaN(min) || Double.isNaN(max) || min >= max) {
			throw new IllegalArgumentException("Invalid interval: %s.".formatted(this));
		}
	}

	/**
	 * Test whether the given {@code value} lies within, below or above
	 * {@code this} interval.
	 *
	 * @param value the value to test
	 * @return {@code -1}, {@code 0} or {@code 1} if the given {@code value}
	 * lies below, within or above {@code this} interval
	 */
	public int compareTo(final double value) {
		if (value < min) {
			return -1;
		} else if (value >= max) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Return the number of <em>distinct</em> {@code double} values {@code this}
	 * interval can <em>hold</em>.
	 *
	 * @return the number of distinct double values of {@code this} interval
	 */
	long size() {
		if (Double.isInfinite(min) || Double.isInfinite(max)) {
			return Long.MAX_VALUE;
		}

		long left = min < 0 ? Long.MIN_VALUE - doubleToLongBits(min) : doubleToLongBits(min);
		long right = max < 0 ? Long.MIN_VALUE - doubleToLongBits(max) : doubleToLongBits(max);

		// Overflow safe subtraction.
		final long result = right - left;
		return ((right^left) & (right^result)) < 0 ? Long.MAX_VALUE : result;
	}

	/**
	 * Return the intersection of {@code this} interval with the {@code other}.
	 *
	 * @param other the intersection interval or {@link Optional#empty()} if
	 *        there is none
	 * @return the intersection interval
	 */
	public Optional<Interval> intersect(final Interval other) {
		if (Double.compare(max, other.min) <= 0 ||
			Double.compare(min, other.max) >= 0)
		{
			return Optional.empty();
		} else {
			return Optional.of(
				new Interval(
					Math.max(min, other.min),
					Math.min(max, other.max)
				)
			);
		}
	}

}
