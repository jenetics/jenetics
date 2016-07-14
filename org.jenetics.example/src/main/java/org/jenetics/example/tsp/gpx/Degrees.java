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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Degrees implements Serializable {

	private static final long serialVersionUID = 1L;

	private final double _value;

	private Degrees(final double value) {
		if (value < 0 || value >= 360) {
			throw new IllegalArgumentException(format(
				"%f not in the range [0, 360).", value
			));
		}

		_value = value;
	}

	public double getValue() {
		return _value;
	}

	public double toRadians() {
		return Math.toRadians(_value);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(_value);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Degrees &&
			Double.compare(((Degrees)obj)._value, _value) == 0;
	}

	@Override
	public String toString() {
		return Double.toString(_value);
	}

	public static Degrees of(final double degrees) {
		return new Degrees(degrees);
	}

	public static Degrees ofRadians(final double radians) {
		return new Degrees(Math.toDegrees(radians));
	}

}
