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
import static io.jenetics.internal.util.Hashes.hash;
import static io.jenetics.internal.util.SerialIO.readInt;
import static io.jenetics.internal.util.SerialIO.writeInt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.stream.IntStream;

/**
 * Integer range class.
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.2
 */
public final /*record*/ class IntRange implements Serializable {

	private static final long serialVersionUID = 2L;

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
	public int min() {
		return _min;
	}

	/**
	 * Return the maximum value of the integer range.
	 *
	 * @return the maximum value of the integer range
	 */
	public int max() {
		return _max;
	}

	/**
	 * Return the size of the {@code IntRange}: {@code max - min}.
	 *
	 * @since 3.9
	 *
	 * @return the size of the int range
	 */
	public int size() {
		return _max - _min;
	}

	/**
	 * Returns a sequential ordered {@code IntStream} from {@link #min()}
	 * (inclusive) to {@link #max()} (exclusive) by an incremental step of
	 * {@code 1}.
	 * <p>
	 * An equivalent sequence of increasing values can be produced sequentially
	 * using a {@code for} loop as follows:
	 * <pre>{@code
	 * for (int i = range.min(); i < range.max(); ++i) {
	 *     ...
	 * }
	 * }</pre>
	 *
	 * @since 3.4
	 *
	 * @return a sequential {@link IntStream} for the range of {@code int}
	 *         elements
	 */
	public IntStream stream() {
		return IntStream.range(_min, _max);
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

	/**
	 * Return a new (half open) range, which contains only the given value:
	 * {@code [value, value + 1)}.
	 *
	 * @since 4.0
	 *
	 * @param value the value of the created (half open) integer range
	 * @return a new (half open) range, which contains only the given value
	 */
	public static IntRange of(final int value) {
		return of(value, value + 1);
	}

	@Override
	public int hashCode() {
		return hash(_min, hash(_max));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof IntRange &&
			_min == ((IntRange)obj)._min &&
			_max == ((IntRange)obj)._max;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.INT_RANGE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeInt(_min, out);
		writeInt(_max, out);
	}

	static IntRange read(final DataInput in) throws IOException {
		return of(readInt(in), readInt(in));
	}

}
