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
 * Integer range class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.2
 * @since 3.2
 */
public final class IntRange implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int _min;
	private final int _max;

	private IntRange(final int min, final int max) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"Min greater than max: %s > %s", min, max
			));
		}

		_min = min;
		_max = max;
	}

	/**
	 * Return the minimum value of the integer range.
	 *
	 * @return the minimum value of the integer range
	 */
	public int getMin() {
		return _min;
	}

	/**
	 * Return the maximum value of the integer range.
	 *
	 * @return the maximum value of the integer range
	 */
	public int getMax() {
		return _max;
	}

	/**
	 * Create a new {@code IntRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the integer range
	 * @param max the upper bound of the integer range
	 * @return a new {@code IntRange} object
	 * @throws IllegalArgumentException if {@code min > max}
	 */
	public static IntRange of(final int min, final int max) {
		return new IntRange(min, max);
	}

	@Override
	public int hashCode() {
		return _min + 31*_max;
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof IntRange &&
			_min == ((IntRange)other)._min &&
			_max == ((IntRange)other)._max;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}

}
