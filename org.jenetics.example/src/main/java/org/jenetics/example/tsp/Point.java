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

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@JsonAdapter(Point.Adapter.class)
public final class Point {

	// The earth radius used for calculating distances.
	private static final double R = 6_371_000.785;

	private final double _latitude;
	private final double _longitude;
	private final double _elevation;

	private Point(
		final double latitude,
		final double longitude,
		final double elevation
	) {
		_latitude = latitude;
		_longitude = longitude;
		_elevation = elevation;
	}

	public Point minus(final Point other) {
		return Point.of(
			_latitude - other._latitude,
			_longitude - other._longitude
		);
	}

	public Point mult(final double factor) {
		return Point.of(_latitude*factor, _longitude*factor);
	}

	public double dist(final Point other) {
		final double phi1 = _latitude;
		final double theta1 = PI/2.0 - _latitude;
		final double r1 = R + _elevation;

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

	public double getElevation() {
		return _elevation;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(_latitude) + 31*Double.hashCode(_latitude);
	}

	@Override
	public boolean equals(final Object other) {
		return other instanceof Point &&
			Double.compare(((Point)other)._latitude, _latitude) == 0 &&
			Double.compare(((Point)other)._longitude, _longitude) == 0;
	}

	@Override
	public String toString() {
		return format(
			"[lat=%f, long=%f]",
			Math.toDegrees(_latitude),
			Math.toDegrees(_longitude)
		);
	}

	public static Point of(final double latitude, final double longitude) {
		/*
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
			*/

		return new Point(latitude, longitude, 0);
	}

	public static Point of(final double latitude, final double longitude, final double elevation) {
		return new Point(latitude, longitude, elevation);
	}

	public static Point ofDegrees(final double latitude, final double longitude) {
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

		return new Point(Math.toRadians(latitude), Math.toRadians(longitude), 0);
	}

	public static Point ofCSVLine(final String[] values) {
		return Point.ofDegrees(
			Double.parseDouble(values[2]),
			Double.parseDouble(values[3])
		);
	}



	static final class Adapter extends TypeAdapter<Point> {

		private static final String LATITUDE = "lat";
		private static final String LONGITUDE = "long";
		private static final String ELEVATION = "ele";

		@Override
		public void write(final JsonWriter out, final Point point)
			throws IOException
		{
			out.beginObject();
			out.name(LATITUDE).value(point.getLatitude());
			out.name(LONGITUDE).value(point.getLongitude());
			out.name("elevation").value(point.getElevation());
			out.endObject();
		}

		@Override
		public Point read(final JsonReader in) throws IOException {
			in.beginObject();

			double latitude = 0;
			double longitude = 0;
			double elevation = 0;
			switch (in.nextName()) {
				case LATITUDE: latitude = in.nextDouble(); break;
				case LONGITUDE: longitude = in.nextDouble(); break;
				case ELEVATION: elevation = in.nextDouble(); break;
			}

			return new Point(latitude, longitude, elevation);
		}
	}

	public static void main(final String[] args) throws Exception {
		final GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		final Gson gson = builder.create();
		System.out.println(gson.toJson(Point.of(123, 123)));
	}

}
