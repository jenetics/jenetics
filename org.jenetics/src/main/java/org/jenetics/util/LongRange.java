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
package org.jenetics.util;

import static java.lang.String.format;

import java.io.Serializable;

/**
 * Long range class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.2
 * @since 3.2
 */
public final class LongRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long _min;
	private final long _max;

	private LongRange(final long min, final long max) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"Min greater than max: %s > %s", min, max
			));
		}

		_min = min;
		_max = max;
	}

	/**
	 * Return the minimum value of the long range.
	 *
	 * @return the minimum value of the long range
	 */
	public long getMin() {
		return _min;
	}

	/**
	 * Return the maximum value of the long range.
	 *
	 * @return the maximum value of the long range
	 */
	public long getMax() {
		return _max;
	}

	/**
	 * Returns the range of this {@code LongRange} as a {@code IntRange} after
	 * a narrowing primitive conversion.
	 *
	 * @since !__version__!
	 *
	 * @return this {@code LongRange} as {@code IntRange}
	 */
	public IntRange intRange() {
		return IntRange.of((int)_min, (int)_max);
	}

	/**
	 * Returns the range of this {@code IntRange} as a {@code DoubleRange} after
	 * a widening primitive conversion.
	 *
	 * @since !__version__!
	 *
	 * @return this {@code IntRange} as {@code DoubleRange}
	 */
	public DoubleRange doubleRange() {
		return DoubleRange.of(_min, _max);
	}

	/**
	 * Create a new {@code LongRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the long range
	 * @param max the upper bound of the long range
	 * @return a new {@code LongRange} object
	 * @throws IllegalArgumentException if {@code min > max}
	 */
	public static LongRange of(final long min, final long max) {
		return new LongRange(min, max);
	}

	@Override
	public int hashCode() {
		return (int)(_min + 31*_max);
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof LongRange &&
			_min == ((LongRange)other)._min &&
			_max == ((LongRange)other)._max;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}

}
