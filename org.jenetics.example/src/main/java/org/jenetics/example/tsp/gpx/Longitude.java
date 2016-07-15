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
 * The longitude of the point. Decimal degrees, WGS84 datum, which must be within
 * the range of {@code [-180..180]}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Longitude extends Number implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double _value;

	/**
	 * Create a new (decimal degrees) {@code Longitude} value.
	 *
	 * @param value the longitude value in decimal degrees
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-180..180]}
	 */
	private Longitude(final double value) {
		if (value < -180 || value > 180) {
			throw new IllegalArgumentException(format(
				"%f is not in range [-180, 180].", value
			));
		}

		_value = value;
	}

	/**
	 * Return the longitude value in decimal degrees.
	 *
	 * @return the longitude value in decimal degrees
	 */
	@Override
	public double doubleValue() {
		return _value;
	}

	/**
	 * Return the longitude value in radians.
	 *
	 * @return the longitude value in radians
	 */
	public double toRadians() {
		return Math.toRadians(_value);
	}

	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	@Override
	public long longValue() {
		return (long)doubleValue();
	}

	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	@Override
	public int hashCode() {
		return Double.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Longitude &&
			Double.compare(((Longitude)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return Double.toString(_value);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new (decimal degrees) {@code Longitude} object.
	 *
	 * @param degrees the longitude value in decimal degrees
	 * @return a new (decimal degrees) {@code Longitude} object
	 * @throws IllegalArgumentException if the given value is not within the
	 *         range of {@code [-180..180]}
	 */
	public static Longitude of(final double degrees) {
		return new Longitude(degrees);
	}

	/**
	 * Create a new {@code Longitude} value for the given {@code radians}.
	 *
	 * @param radians the longitude value in radians
	 * @return a new {@code Longitude} value for the given {@code radians}
	 * @throws IllegalArgumentException if the given radians is not within the
	 *         range of {@code [-2*Pi..2*Pi]}
	 */
	public static Longitude ofRadians(final double radians) {
		return new Longitude(Math.toDegrees(radians));
	}

}
