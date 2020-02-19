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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Double range class.
 *
 * @implNote
 * This class is immutable and thread-safe.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 6.0
 * @since 3.2
 */
public final /*record*/ class DoubleRange implements Serializable {

	private static final long serialVersionUID = 2L;

	private final double _min;
	private final double _max;

	private DoubleRange(final double min, final double max) {
		if (min > max) {
			throw new IllegalArgumentException(format(
				"Min greater than max: %s > %s", min, max
			));
		}

		_min = min;
		_max = max;
	}

	/**
	 * Return the minimum value of the double range.
	 *
	 * @return the minimum value of the double range
	 */
	public double min() {
		return _min;
	}

	/**
	 * Return the maximum value of the double range.
	 *
	 * @return the maximum value of the double range
	 */
	public double max() {
		return _max;
	}

	/**
	 * Create a new {@code DoubleRange} object with the given {@code min} and
	 * {@code max} values.
	 *
	 * @param min the lower bound of the double range
	 * @param max the upper bound of the double range
	 * @return a new {@code DoubleRange} object
	 * @throws IllegalArgumentException if {@code min > max}
	 */
	public static DoubleRange of(final double min, final double max) {
		return new DoubleRange(min, max);
	}

	@Override
	public int hashCode() {
		return hash(_min, hash(_max));
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof DoubleRange &&
			Double.compare(_min, ((DoubleRange)obj)._min) == 0 &&
			Double.compare(_max, ((DoubleRange)obj)._max) == 0;
	}

	@Override
	public String toString() {
		return "[" + _min + ", " + _max + "]";
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.DOUBLE_RANGE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		out.writeDouble(_min);
		out.writeDouble(_max);
	}

	static DoubleRange read(final DataInput in) throws IOException {
		return of(in.readDouble(), in.readDouble());
	}

}
