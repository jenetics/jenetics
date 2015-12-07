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

import static java.lang.Double.compare;
import static java.lang.Double.NaN;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.jaxb.adapterFor;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Stream;

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
 * This class allows to read and write GPS points in GPX format.
 *
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
		final ZonedDateTime _time;
		final double _latitude;
		final double _longitude;
		final double _elevation;
		final double _speed;

		private Location(
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
				compare(_latitude, ((Location)obj)._latitude) ==  0 &&
				compare(_longitude, ((Location)obj)._latitude) == 0&&
				compare(_elevation, ((Location)obj)._elevation) == 0&&
				compare(_speed, ((Location)obj)._speed) == 0 &&
				Objects.equals(_name, ((Location)obj)._name) &&
				Objects.equals(_time, ((Location)obj)._time);
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
			final ZonedDateTime time,
			final double latitude,
			final double longitude,
			final double elevation,
			final double speed
		) {
			return new Location(name, time, latitude,longitude, elevation, speed);
		}

		public static Location of(
			final String name,
			final double latitude,
			final double longitude
		) {
			return new Location(name, null, latitude,longitude, NaN, NaN);
		}

		public static Location of(
			final String name,
			final double latitude,
			final double longitude,
			final double elevation
		) {
			return new Location(name, null, latitude,longitude, elevation, NaN);
		}

		public static Location of(
			final double latitude,
			final double longitude
		) {
			return new Location(null, null, latitude,longitude, NaN, NaN);
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

			@XmlElement(name = "speed", required = false)
			public Double speed;

			@XmlElement(name = "time", required = false)
			public String time;

			@XmlElement(name = "name", required = false)
			public String name;

			public static final class Adapter
				extends XmlAdapter<Model, Location>
			{
				private static final DateTimeFormatter DTF =
					DateTimeFormatter.ISO_INSTANT;

				@Override
				public Model marshal(final Location wp) {
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
				public Location unmarshal(final Model model) {
					return new Location(
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

	/**
	 * Represents a GPX route.
	 */
	@XmlJavaTypeAdapter(Route.Model.Adapter.class)
	public static final class Route implements Iterable<Location> {

		private final String _name;
		private final List<Location> _points = new ArrayList<>();

		public Route(final String name) {
			_name = name;
		}

		public Route() {
			this(null);
		}

		public Optional<String> getName() {
			return Optional.ofNullable(_name);
		}

		public Route add(final Location location) {
			_points.add(requireNonNull(location));
			return this;
		}

		@Override
		public Iterator<Location> iterator() {
			return _points.iterator();
		}

		public Stream<Location> stream() {
			return _points.stream();
		}

		@XmlRootElement(name = "rte")
		@XmlType(name = "gpx.Route")
		@XmlAccessorType(XmlAccessType.FIELD)
		static final class Model {

			@XmlElement(name = "name", required = false)
			public String name;

			@XmlElement(name = "rtept", required = false, nillable = true)
			public List<Location> points;

			public static final class Adapter
				extends XmlAdapter<Model, Route>
			{
				@Override
				public Model marshal(final Route route) {
					final Model model = new Model();
					model.points = !route._points.isEmpty()
						? route._points
						: null;

					return model;
				}

				@Override
				public Route unmarshal(final Model model) {
					final Route route = new Route(model.name);
					if (model.points != null) {
						model.points.forEach(route::add);
					}

					return route;
				}
			}

		}
	}

	/**
	 * Represents a GPX track.
	 */
	@XmlJavaTypeAdapter(Track.Model.Adapter.class)
	public static final class Track implements Iterable<Track.Segment> {

		/**
		 * Represents a GPX track segment
		 */
		@XmlJavaTypeAdapter(Segment.Model.Adapter.class)
		public static final class Segment implements Iterable<Location> {
			private final String _name;
			private final List<Location> _points = new ArrayList<>();

			public Segment(final String name) {
				_name = name;
			}

			public Segment() {
				this(null);
			}

			public Optional<String> getName() {
				return Optional.ofNullable(_name);
			}

			public Segment add(final Location location) {
				_points.add(requireNonNull(location));
				return this;
			}

			@Override
			public Iterator<Location> iterator() {
				return _points.iterator();
			}

			public Stream<Location> stream() {
				return _points.stream();
			}

			@XmlRootElement(name = "trkseg")
			@XmlType(name = "gpx.Track.Segment")
			@XmlAccessorType(XmlAccessType.FIELD)
			static final class Model {

				@XmlElement(name = "name", required = false)
				public String name;

				@XmlElement(name = "trkpt", required = false, nillable = true)
				public List<Location> points;

				public static final class Adapter
					extends XmlAdapter<Model, Segment>
				{
					@Override
					public Model marshal(final Segment segment) {
						final Model model = new Model();
						model.points = !segment._points.isEmpty()
							? segment._points
							: null;

						return model;
					}

					@Override
					public Segment unmarshal(final Model model) {
						final Segment segment = new Segment(model.name);
						if (model.points != null) {
							model.points.forEach(segment::add);
						}

						return segment;
					}
				}
			}

		}

		private final List<Segment> _segments = new ArrayList<>();

		public Track() {
		}

		public void add(final Segment segment) {
			_segments.add(requireNonNull(segment));
		}

		@Override
		public Iterator<Segment> iterator() {
			return null;
		}

		public Stream<Segment> stream() {
			return _segments.stream();
		}

		@XmlRootElement(name = "trk")
		@XmlType(name = "gpx.Track")
		@XmlAccessorType(XmlAccessType.FIELD)
		static final class Model {

			@XmlElement(name = "trkseg", required = false, nillable = true)
			public List<Segment> segments;

			public static final class Adapter
				extends XmlAdapter<Model, Track>
			{
				@Override
				public Model marshal(final Track track) {
					final Model model = new Model();
					model.segments = !track._segments.isEmpty()
						? track._segments
						: null;

					return model;
				}

				@Override
				public Track unmarshal(final Model model) {
					final Track track = new Track();
					if (model.segments != null) {
						model.segments.forEach(track::add);
					}

					return track;
				}
			}
		}

	}

	private final List<Location> _wayPoints = new ArrayList<>();
	private final List<Route> _routes = new ArrayList<>();
	private final List<Track> _tracks = new ArrayList<>();

	public GPX() {
	}

	public GPX addWayPoint(final Location point) {
		_wayPoints.add(requireNonNull(point));
		return this;
	}

	public GPX addRoute(final Route route) {
		_routes.add(requireNonNull(route));
		return this;
	}

	public GPX addTrack(final Track track) {
		_tracks.add(requireNonNull(track));
		return this;
	}

	public List<Location> getWayPoints() {
		return Collections.unmodifiableList(_wayPoints);
	}

	public List<Route> getRoutes() {
		return Collections.unmodifiableList(_routes);
	}

	public List<Track> getTracks() {
		return Collections.unmodifiableList(_tracks);
	}

	@XmlRootElement(name = "gpx")
	@XmlType(name = "gpx.GPX")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "version", required = false)
		public String version = "1.1";

		@XmlAttribute(name = "creator", required = false)
		public String creator = "Jenetics TSP";

		@XmlElement(name = "wpt", required = false, nillable = true)
		public List<Location> wayPoints;

		@XmlElement(name = "rte", required = false, nillable = true)
		public List<Route> routes;

		@XmlElement(name = "trk", required = false, nillable = true)
		public List<Track> tracks;

		public static final class Adapter
			extends XmlAdapter<Model, GPX>
		{
			@Override
			public Model marshal(final GPX gpx) {
				final Model model = new Model();
				model.wayPoints = !gpx._wayPoints.isEmpty()
					? gpx._wayPoints : null;
				model.routes = !gpx._routes.isEmpty()
					? gpx._routes : null;
				model.tracks = !gpx._tracks.isEmpty()
					? gpx._tracks : null;

				return model;
			}

			@Override
			public GPX unmarshal(final Model model) {
				final GPX gpx = new GPX();
				if (model.wayPoints != null) {
					model.wayPoints.forEach(gpx::addWayPoint);
				}
				if (model.routes != null) {
					model.routes.forEach(gpx::addRoute);
				}
				if (model.tracks != null) {
					model.tracks.forEach(gpx::addTrack);
				}

				return gpx;
			}
		}
	}



	public static void main(final String[] args) throws IOException {
		write(GPX.Location.of("Innsbruck", 34, 43, 23.0), System.out);
	}











	private static final class JAXBContextHolder {
		private static final JAXBContext CONTEXT; static {
			try {
				CONTEXT = JAXBContext.newInstance(
					Location.Model.class,
					Route.Model.class,
					Track.Model.class,
					Track.Segment.Model.class,
					GPX.Model.class
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
