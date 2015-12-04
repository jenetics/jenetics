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
package org.jenetics.example.tsp;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.String.format;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Location {

	// The earth radius used for calculating distances.
	private static final double R = 6_371_000.785;

	private final double _latitude;
	private final double _longitude;
	private static final double _height = 0;

	private Location(final double latitude, final double longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}

	public double dist(final Location other) {
		final double phi1 = _latitude;
		final double theta1 = PI/2.0 - _latitude;
		final double r1 = R + _height;

		final double phi2 = other._latitude;
		final double theta2 = PI/2.0 - other._latitude;
		final double r2 = R + other._latitude;

		final double x1 = r1*sin(theta1)*cos(phi1);
		final double y1 = r1*sin(theta1)*sin(phi1);
		final double z1 = r1*cos(theta1);

		final double x2 = r2*sin(theta2)*cos(phi2);
		final double y2 = r2*sin(theta2)*sin(phi2);
		final double z2 = r2*cos(theta2);

		return Math.sqrt(
			(x1 - x2)*(x1 - x2) +
			(y1 - y2)*(y1 - y2) +
			(z1 - z2)*(z1 - z2)
		);
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(_latitude) + 31*Double.hashCode(_latitude);
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof Location &&
			Double.compare(((Location)other)._latitude, _latitude) == 0 &&
			Double.compare(((Location)other)._longitude, _longitude) == 0;
	}

	@Override
	public String toString() {
		return format("[lat=%f, long=%f]", _latitude, _longitude);
	}

	public static Location of(final double latitude, final double longitude) {
		if (latitude < -PI/2 || latitude > PI) {
			throw new IllegalArgumentException(format(
				"Latitude of %f not in valid range [-π/2..π/2]", latitude
			));
		}
		if (longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException(format(
				"Longitude of %f not in valid range [-π..π]", latitude
			));
		}

		return new Location(latitude, longitude);
	}

	public static Location ofDegrees(final double latitude, final double longitude) {
		if (latitude < -90 || latitude > 90) {
			throw new IllegalArgumentException(format(
				"Latitude of %f not in valid range [-90..90]", latitude
			));
		}
		if (longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException(format(
				"Longitude of %f not in valid range [-180..180]", latitude
			));
		}

		return new Location(Math.toRadians(latitude), Math.toRadians(longitude));
	}

}
