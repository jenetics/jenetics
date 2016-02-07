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

import static java.lang.Double.NaN;
import static java.lang.Double.compare;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Represents a way-point, route-point and/or track-point.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(WayPoint.Model.Adapter.class)
public final class WayPoint implements Serializable {

	private static final long serialVersionUID = 1L;

	// The earth radius used for calculating distances.
	private static final double R = 6_371_000.785;

	final String _name;
	final ZonedDateTime _time;
	final double _latitude;
	final double _longitude;
	final double _elevation;
	final double _speed;

	private WayPoint(
		final String name,
		final ZonedDateTime time,
		final double latitude,
		final double longitude,
		final double elevation,
		final double speed
	) {
		_name = name;
		_time = time;
		_latitude = latitude;
		_longitude = longitude;
		_elevation = elevation;
		_speed = speed;
	}

	/**
	 * Return the name of the way point.
	 *
	 * @return the name of the way point
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return the timestamp of the location.
	 *
	 * @return the timestamp of the location
	 */
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(_time);
	}

	/**
	 * Return the latitude value of the location.
	 *
	 * @return the latitude value of the location
	 */
	public double getLatitude() {
		return _latitude;
	}

	/**
	 * Return the longitude value of the location.
	 *
	 * @return the longitude value of the location
	 */
	public double getLongitude() {
		return _longitude;
	}

	/**
	 * Return the elevation of the location.
	 *
	 * @return the elevation of the location.
	 */
	public OptionalDouble getElevation() {
		return Double.isFinite(_elevation)
			? OptionalDouble.of(_elevation)
			: OptionalDouble.empty();
	}

	/**
	 * Return the elevation of the location.
	 *
	 * @return the elevation of the location.
	 */
	public OptionalDouble getSpeed() {
		return Double.isFinite(_speed)
			? OptionalDouble.of(_speed)
			: OptionalDouble.empty();
	}

	/**
	 * Return the distance between {@code this} and the {@code other}
	 * {@code WayPoint} in meter.
	 *
	 * @param other the second way-point
	 * @return the distance between {@code this} and the {@code other}
	 *         {@code WayPoint}
	 * @throws NullPointerException if the {@code other} way-point is
	 *         {@code null}
	 */
	public double distance(final WayPoint other) {
		requireNonNull(other);

		final double phi1 = toRadians(_latitude);
		final double theta1 = PI/2.0 - toRadians(_latitude);
		final double r1 = R + _elevation;

		final double phi2 = toRadians(other._latitude);
		final double theta2 = PI/2.0 - toRadians(other._latitude);
		final double r2 = R + toRadians(other._latitude);

		final double x1 = r1*sin(theta1)*cos(phi1);
		final double y1 = r1*sin(theta1)*sin(phi1);
		final double z1 = r1*cos(theta1);

		final double x2 = r2*sin(theta2)*cos(phi2);
		final double y2 = r2*sin(theta2)*sin(phi2);
		final double z2 = r2*cos(theta2);

		return sqrt(
			(x1 - x2)*(x1 - x2) +
			(y1 - y2)*(y1 - y2) +
			(z1 - z2)*(z1 - z2)
		);
	}

	@Override
	public int hashCode() {
		int hash = getClass().hashCode();
		hash += 31*Double.hashCode(_latitude) + 17;
		hash += 31*Double.hashCode(_latitude) + 17;
		hash += 31*Double.hashCode(_elevation) + 17;
		hash += 31*Double.hashCode(_speed) + 17;
		hash += 31*getName().map(String::hashCode).orElse(0) + 17;
		hash += 31*getTime().map(ZonedDateTime::hashCode).orElse(0) + 17;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj != null &&
			getClass() == obj.getClass() &&
			compare(_latitude, ((WayPoint)obj)._latitude) ==  0 &&
			compare(_longitude, ((WayPoint)obj)._latitude) == 0&&
			compare(_elevation, ((WayPoint)obj)._elevation) == 0&&
			compare(_speed, ((WayPoint)obj)._speed) == 0 &&
			Objects.equals(_name, ((WayPoint) obj)._name) &&
			Objects.equals(_time, ((WayPoint)obj)._time);
	}

	@Override
	public String toString() {
		return format(
			"%s[lat=%f, lon=%f, ele=%f]",
			getName().orElse(getClass().getName()),
			getLatitude(),
			getLongitude(),
			getElevation().orElse(0.0)
		);
	}

	/**
	 * Return a new {@code WayPoint} from the given input data.
	 *
	 * @param name the name of the way-point, maybe {@code null}
	 * @param time the time of the way-point, maybe {@code null}
	 * @param latitude the latitude value
	 * @param longitude the longitude value
	 * @param elevation the elevation value
	 * @param speed the speed value
	 * @return a new {@code WayPoint} from the given input data
	 */
	public static WayPoint of(
		final String name,
		final ZonedDateTime time,
		final double latitude,
		final double longitude,
		final double elevation,
		final double speed
	) {
		return new WayPoint(name, time, latitude,longitude, elevation, speed);
	}

	/**
	 * Return a new {@code WayPoint} from the given input data.
	 *
	 * @param name the name of the way-point, maybe {@code null}
	 * @param latitude the latitude value
	 * @param longitude the longitude value
	 * @return a new {@code WayPoint} from the given input data
	 */
	public static WayPoint of(
		final String name,
		final double latitude,
		final double longitude
	) {
		return new WayPoint(name, null, latitude,longitude, NaN, NaN);
	}

	/**
	 * Return a new {@code WayPoint} from the given input data.
	 *
	 * @param name the name of the way-point, maybe {@code null}
	 * @param latitude the latitude value
	 * @param longitude the longitude value
	 * @param elevation the elevation value
	 * @return a new {@code WayPoint} from the given input data
	 */
	public static WayPoint of(
		final String name,
		final double latitude,
		final double longitude,
		final double elevation
	) {
		return new WayPoint(name, null, latitude,longitude, elevation, NaN);
	}

	/**
	 * Return a new {@code WayPoint} from the given input data.
	 *
	 * @param latitude the latitude value
	 * @param longitude the longitude value
	 * @return a new {@code WayPoint} from the given input data
	 */
	public static WayPoint of(
		final double latitude,
		final double longitude
	) {
		return new WayPoint(null, null, latitude,longitude, NaN, NaN);
	}


	/**
	 * Model class for XML serialization/deserialization.
	 */
	@XmlRootElement(name = "wpt")
	@XmlType(name = "gpx.WayPoint")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "lat", required = true)
		public double latitude;

		@XmlAttribute(name = "lon", required = true)
		public double longitude;

		@XmlElement(name = "ele", required = false)
		public Double elevation;

		@XmlElement(name = "speed", required = false)
		public Double speed;

		@XmlElement(name = "time", required = false)
		public String time;

		@XmlElement(name = "name", required = false)
		public String name;

		public static final class Adapter
			extends XmlAdapter<Model, WayPoint>
		{
			private static final DateTimeFormatter DTF =
				DateTimeFormatter.ISO_INSTANT;

			@Override
			public Model marshal(final WayPoint wp) {
				final Model model = new Model();
				model.latitude = wp.getLatitude();
				model.longitude = wp.getLongitude();
				model.elevation = wp.getElevation().isPresent()
					? wp.getElevation().getAsDouble()
					: null;
				model.speed = wp.getSpeed().isPresent()
					? wp.getSpeed().getAsDouble()
					: null;
				model.name = wp.getName().orElse(null);
				model.time = wp.getTime().map(DTF::format).orElse(null);

				return model;
			}

			@Override
			public WayPoint unmarshal(final Model model) {
				return new WayPoint(
					model.name,
					Optional.ofNullable(model.time)
						.map(ZonedDateTime::parse)
						.orElse(null),
					model.latitude,
					model.longitude,
					model.elevation != null ? model.elevation : Double.NaN,
					model.speed != null ? model.speed : Double.NaN
				);
			}
		}
	}

}
