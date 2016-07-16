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
import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Two lat/lon pairs defining the extent of an element.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Bounds.Model.Adapter.class)
public final class Bounds implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Latitude _minLatitude;
	private final Longitude _minLongitude;
	private final Latitude _maxLatitude;
	private final Longitude _maxLongitude;

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private Bounds(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		_minLatitude = requireNonNull(minLatitude);
		_minLongitude = requireNonNull(minLongitude);
		_maxLatitude = requireNonNull(maxLatitude);
		_maxLongitude = requireNonNull(maxLongitude);
	}

	/**
	 * Return the minimum latitude.
	 *
	 * @return the minimum latitude
	 */
	public Latitude getMinLatitude() {
		return _minLatitude;
	}

	/**
	 * Return the minimum longitude.
	 *
	 * @return the minimum longitude
	 */
	public Longitude getMinLongitude() {
		return _minLongitude;
	}

	/**
	 * Return the maximum latitude.
	 *
	 * @return the maximum latitude
	 */
	public Latitude getMaxLatitude() {
		return _maxLatitude;
	}

	/**
	 * Return the maximum longitude
	 *
	 * @return the maximum longitude
	 */
	public Longitude getMaxLongitude() {
		return _maxLongitude;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*_minLatitude.hashCode() + 37;
		hash += 31*_minLongitude.hashCode() + 37;
		hash += 31*_maxLatitude.hashCode() + 37;
		hash += 31*_maxLongitude.hashCode() + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof  Bounds &&
			((Bounds)obj)._minLatitude.equals(_minLatitude) &&
			((Bounds)obj)._minLongitude.equals(_minLongitude) &&
			((Bounds)obj)._maxLatitude.equals(_maxLatitude) &&
			((Bounds)obj)._maxLongitude.equals(_maxLongitude);
	}

	@Override
	public String toString() {
		return format(
			"[%s, %s][%s, %s]",
			_minLatitude,
			_minLongitude,
			_maxLatitude,
			_maxLongitude
		);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Bounds} object with the given extent.
	 *
	 * @param minLatitude the minimum latitude
	 * @param minLongitude the minimum longitude
	 * @param maxLatitude the maximum latitude
	 * @param maxLongitude the maximum longitude
	 * @return a new {@code Bounds} object with the given extent
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Bounds of(
		final Latitude minLatitude,
		final Longitude minLongitude,
		final Latitude maxLatitude,
		final Longitude maxLongitude
	) {
		return new Bounds(minLatitude, minLongitude, maxLatitude, maxLongitude);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "bounds")
	@XmlType(name = "gpx.Bounds")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "minlat", required = true)
		public double minlat;

		@XmlAttribute(name = "minlon", required = true)
		public double minlon;

		@XmlAttribute(name = "maxlat", required = true)
		public double maxlat;

		@XmlAttribute(name = "maxlon", required = true)
		public double maxlon;

		public static final class Adapter
			extends XmlAdapter<Model, Bounds>
		{
			@Override
			public Model marshal(final Bounds bounds) {
				final Model model = new Model();
				model.minlat = bounds._minLatitude.toDegrees();
				model.minlon = bounds._minLongitude.toDegrees();
				model.maxlat = bounds._maxLatitude.toDegrees();
				model.maxlon = bounds._maxLongitude.toDegrees();
				return model;
			}

			@Override
			public Bounds unmarshal(final Model model) {
				return Bounds.of(
					Latitude.ofDegrees(model.minlat),
					Longitude.ofDegrees(model.minlon),
					Latitude.ofDegrees(model.maxlat),
					Longitude.ofDegrees(model.maxlon)
				);
			}
		}

		static final Email.Model.Adapter ADAPTER = new Email.Model.Adapter();

	}

}
