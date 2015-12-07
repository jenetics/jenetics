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

import static java.lang.Double.compare;
import static java.lang.String.format;
import static org.jenetics.internal.util.jaxb.adapterFor;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(GPX.Model.Adapter.class)
public class GPX {

	/**
	 * The base class for {@code WayPoint} and {@code TrackPoint} classes.
	 */
	@XmlJavaTypeAdapter(Location.Model.Adapter.class)
	public static final class Location {
		final String _name;
		final double _latitude;
		final double _longitude;
		final double _elevation;

		private Location(
			final String name,
			final double latitude,
			final double longitude,
			final double elevation
		) {
			_name = name;
			_latitude = latitude;
			_longitude = longitude;
			_elevation = elevation;
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

		@Override
		public int hashCode() {
			int hash = getClass().hashCode();
			hash += 31*Double.hashCode(getLatitude()) + 17;
			hash += 31*Double.hashCode(getLongitude()) + 17;
			hash += 31*Double.hashCode(getElevation().orElse(0)) + 17;
			hash += 31*getName().map(String::hashCode).orElse(0) + 17;

			return hash;
		}

		@Override
		public boolean equals(final Object obj) {
			return obj != null &&
				getClass() == obj.getClass() &&
				compare(getLatitude(), ((Location)obj).getLatitude()) ==  0 &&
				compare(getLongitude(), ((Location)obj).getLongitude()) == 0&&
				compare(
					getElevation().orElse(Double.NaN),
					((Location)obj).getElevation().orElse(Double.NaN)) == 0&&
				getName().equals(((Location)obj).getName());
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

		public static Location of(
			final String name,
			final double latitude,
			final double longitude,
			final double elevation
		) {
			return new Location(name, latitude,longitude, elevation);
		}

		public static Location of(
			final String name,
			final double latitude,
			final double longitude
		) {
			return new Location(name, latitude,longitude, Double.NaN);
		}

		public static Location of(
			final double latitude,
			final double longitude
		) {
			return new Location(null, latitude,longitude, Double.NaN);
		}

		public static Location of(
			final double latitude,
			final double longitude,
			final double elevation
		) {
			return new Location(null, latitude,longitude, elevation);
		}

		@XmlRootElement(name = "loc")
		@XmlType(name = "gpx.Location")
		@XmlAccessorType(XmlAccessType.FIELD)
		static final class Model {

			@XmlAttribute(name = "lat", required = true)
			public double latitude;

			@XmlAttribute(name = "lon", required = true)
			public double longitude;

			@XmlElement(name = "ele", required = false)
			public Double elevation;

			@XmlElement(name = "name", required = false)
			public String name;

			public static final class Adapter
				extends XmlAdapter<Model, Location>
			{
				@Override
				public Model marshal(final Location wp) {
					final Model model = new Model();
					model.latitude = wp.getLatitude();
					model.longitude = wp.getLongitude();
					model.elevation = wp.getElevation().isPresent()
						? wp.getElevation().getAsDouble()
						: null;
					model.name = wp.getName().orElse(null);

					return model;
				}

				@Override
				public Location unmarshal(final Model model) {
					return new Location(
						model.name,
						model.latitude,
						model.longitude,
						model.elevation != null ? model.elevation : Double.NaN
					);
				}
			}
		}

	}
	

	public static final class Route {

	}

	public static final class Track {

	}

	private final List<Location> _wayPoints = new ArrayList<>();

	public GPX() {
	}

	public void addWayPoint(final Location point) {
		_wayPoints.add(point);
	}

	@XmlRootElement(name = "gpx", namespace = "http://www.topografix.com/GPX/1/1")
	@XmlType(name = "gpx.GPX")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute()
		public String version = "1.1";

		@XmlAttribute()
		public String creator = "Jenetics TSP";

		@XmlElement(name = "wpt", required = true, nillable = false)
		public List<Location> wayPoints;

		public static final class Adapter
			extends XmlAdapter<Model, GPX>
		{
			@Override
			public Model marshal(final GPX gpx) {
				final Model model = new Model();
				model.wayPoints = !gpx._wayPoints.isEmpty()
					? gpx._wayPoints
					: null;

				return model;
			}

			@Override
			public GPX unmarshal(final Model model) {
				final GPX gpx = new GPX();
				if (model.wayPoints != null) {
					model.wayPoints.forEach(gpx::addWayPoint);
				}

				return gpx;
			}
		}

		public static final Adapter ADAPTER = new Adapter();
	}



	public static void main(final String[] args) throws IOException {
		write(GPX.Location.of("Innsbruck", 34, 43, 23.0), System.out);
	}











	private static final class JAXBContextHolder {
		private static final JAXBContext CONTEXT; static {
			try {
				CONTEXT = JAXBContext.newInstance(
					"org.jenetics.example.tsp"
				);
			} catch (JAXBException e) {
				throw new DataBindingException(
					"Something went wrong while creating JAXBContext.", e
				);
			}
		}
	}

	private static JAXBContext context() {
		return JAXBContextHolder.CONTEXT;
	}


	static void write(final Object object, final OutputStream out)
		throws IOException
	{
		try {
			final Marshaller marshaller = context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(marshal(object), out);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	static <T> T read(final Class<T> type, final InputStream in)
		throws IOException
	{
		try {
			final Unmarshaller unmarshaller = context().createUnmarshaller();

			//final XMLInputFactory factory = XMLInputFactory.newInstance();
			//final XMLStreamReader reader = factory.createXMLStreamReader(in);
			//try {
			final Object object = unmarshaller.unmarshal(in);
			final XmlAdapter<Object, Object> adapter = adapterFor(object);
			if (adapter != null) {
				return type.cast(adapter.unmarshal(object));
			} else {
				return type.cast(object);
			}
			//} finally {
			//	reader.close();
			//}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
