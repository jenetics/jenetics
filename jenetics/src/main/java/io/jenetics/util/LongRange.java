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
import static io.jenetics.internal.util.SerialIO.readLong;
import static io.jenetics.internal.util.SerialIO.writeLong;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.stream.LongStream;

/**
 * Long range class.
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.2
 */
public final /*record*/ class LongRange implements Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

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
	public long min() {
		return _min;
	}

	/**
	 * Return the maximum value of the long range.
	 *
	 * @return the maximum value of the long range
	 */
	public long max() {
		return _max;
	}

	/**
	 * Returns a sequential ordered {@code LongStream} from {@link #min()}
	 * (inclusive) to {@link #max()} (exclusive) by an incremental step of
	 * {@code 1}.
	 * <p>
	 * An equivalent sequence of increasing values can be produced sequentially
	 * using a {@code for} loop as follows:
	 * <pre>{@code
	 * for (long i = range.min(); i < range.max(); ++i) {
	 *     ...
	 * }
	 * }</pre>
	 *
	 * @since 3.4
	 *
	 * @return a sequential {@link LongStream} for the range of {@code long}
	 *         elements
	 */
	public LongStream stream() {
		return LongStream.range(_min, _max);
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

	/**
	 * Return a new (half open) range, which contains only the given value:
	 * {@code [value, value + 1)}.
	 *
	 * @since 4.0
	 *
	 * @param value the value of the created (half open) integer range
	 * @return a new (half open) range, which contains only the given value
	 */
	public static LongRange of(final long value) {
		return of(value, value + 1);
	}

	@Override
	public int hashCode() {
		return hash(_min, hash(_max, hash(getClass())));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof LongRange other &&
			_min == other._min &&
			_max == other._max;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.LONG_RANGE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		writeLong(_min, out);
		writeLong(_max, out);
	}

	static LongRange read(final DataInput in) throws IOException {
		return of(readLong(in), readLong(in));
	}

}
