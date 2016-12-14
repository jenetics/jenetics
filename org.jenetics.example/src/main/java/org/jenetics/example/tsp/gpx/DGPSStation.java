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
package org.jenetics.example.tsp.gpx;

import static java.lang.String.format;

import java.io.Serializable;

/**
 * Represents a differential GPS station. This object only holds int values in
 * the range of {@code [0..1023]}.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Value_object">Value object</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class DGPSStation extends Number implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int _value;

	/**
	 * Create a new {@code DGPSStation} object.
	 *
	 * @param value the differential GPS station number
	 * @throws IllegalArgumentException if the given station number is not in the
	 *         range of {@code [0..1023]}
	 */
	private DGPSStation(final int value) {
		if (value < 0 || value > 1023) {
			throw new IllegalArgumentException(format(
				"%d is out of range [0, 1023].", value
			));
		}

		_value = value;
	}

	/**
	 * Return the differential GPS station number.
	 *
	 * @return the differential GPS station number
	 */
	public int intValue() {
		return _value;
	}

	@Override
	public double doubleValue() {
		return (double)_value;
	}

	@Override
	public long longValue() {
		return (long)_value;
	}

	@Override
	public float floatValue() {
		return (float)_value;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof DGPSStation &&
			((DGPSStation)obj)._value == _value;
	}

	@Override
	public String toString() {
		return Integer.toString(_value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code DGPSStation} object.
	 *
	 * @param value the differential GPS station number
	 * @return a new {@code DGPSStation} object
	 * @throws IllegalArgumentException if the given station number is not in the
	 *         range of {@code [0..1023]}
	 */
	public static DGPSStation of(final int value) {
		return new DGPSStation(value);
	}

}
