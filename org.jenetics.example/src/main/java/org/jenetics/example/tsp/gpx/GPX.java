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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
 * GPX documents contain a metadata header, followed by way-points, routes, and
 * tracks. You can add your own elements to the extensions section of the GPX
 * document.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(GPX.Model.Adapter.class)
public final class GPX implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String VERSION = "1.1";

	private final String _creator;
	private final Metadata _metadata;
	private final List<WayPoint> _wayPoints;;
	private final List<Route> _routes;
	private final List<Track> _tracks;

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	private GPX(
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		_creator = requireNonNull(creator);
		_metadata = metadata;
		_wayPoints = unmodifiableList(requireNonNull(wayPoints));
		_routes = unmodifiableList(requireNonNull(routes));
		_tracks = unmodifiableList(requireNonNull(tracks));
	}

	/**
	 * Return the version number of the GPX file.
	 *
	 * @return the version number of the GPX file
	 */
	public String getVersion() {
		return VERSION;
	}

	/**
	 * Return the name or URL of the software that created your GPX document.
	 * This allows others to inform the creator of a GPX instance document that
	 * fails to validate.
	 *
	 * @return the name or URL of the software that created your GPX document
	 */
	public String getCreator() {
		return _creator;
	}

	/**
	 * Return the metadata of the GPX file.
	 *
	 * @return the metadata of the GPX file
	 */
	public Optional<Metadata> getMetadata() {
		return Optional.ofNullable(_metadata);
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} way-points.
	 *
	 * @return an unmodifiable list of the {@code GPX} way-points.
	 */
	public List<WayPoint> getWayPoints() {
		return _wayPoints;
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} routes.
	 *
	 * @return an unmodifiable list of the {@code GPX} routes.
	 */
	public List<Route> getRoutes() {
		return _routes;
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} tracks.
	 *
	 * @return an unmodifiable list of the {@code GPX} tracks.
	 */
	public List<Track> getTracks() {
		return _tracks;
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(getVersion()) + 31;
		hash += 17*Objects.hashCode(_creator) + 31;
		hash += 17*Objects.hashCode(_metadata) + 31;
		hash += 17*Objects.hashCode(_wayPoints) + 31;
		hash += 17*Objects.hashCode(_routes) + 31;
		hash += 17*Objects.hashCode(_tracks) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GPX &&
			Objects.equals(((GPX)obj).getVersion(), getVersion()) &&
			Objects.equals(((GPX)obj)._creator, _creator) &&
			Objects.equals(((GPX)obj)._metadata, _metadata) &&
			Objects.equals(((GPX)obj)._wayPoints, _wayPoints) &&
			Objects.equals(((GPX)obj)._routes, _routes) &&
			Objects.equals(((GPX)obj)._tracks, _tracks);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @return a new {@code GPX} object with the given data
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	public static GPX of(
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		return new GPX(
			creator,
			metadata,
			wayPoints,
			routes,
			tracks
		);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "gpx")
	@XmlType(name = "gpx.GPX")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "version", required = true)
		public String version;

		@XmlAttribute(name = "creator", required = true)
		public String creator;

		@XmlElement(name = "metadata", nillable = true)
		public Metadata metadata;

		@XmlElement(name = "wpt", nillable = true)
		public List<WayPoint> wayPoints;

		@XmlElement(name = "rte", nillable = true)
		public List<Route> routes;

		@XmlElement(name = "trk", nillable = true)
		public List<Track> tracks;

		public static final class Adapter
			extends XmlAdapter<Model, GPX>
		{
			@Override
			public Model marshal(final GPX gpx) {
				final Model model = new Model();
				model.version = gpx.getVersion();
				model.creator = gpx._creator;
				model.metadata = gpx._metadata;
				model.wayPoints = gpx._wayPoints;
				model.routes = gpx._routes;
				model.tracks = gpx._tracks;

				return model;
			}

			@Override
			public GPX unmarshal(final Model model) {
				return GPX.of(
					model.creator,
					model.metadata,
					model.wayPoints != null
						? model.wayPoints
						: emptyList(),
					model.routes != null
						? model.routes
						: emptyList(),
					model.tracks != null
						? model.tracks
						: emptyList()
				);
			}
		}

		static final Adapter ADAPTER = new Adapter();
	}


	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param output the output stream where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final OutputStream output)
		throws IOException
	{
		try {
			final Marshaller marshaller = context().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(Model.ADAPTER.marshal(gpx), output);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param input the input stream from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final InputStream input)
		throws IOException
	{
		try {
			final Unmarshaller unmarshaller = context().createUnmarshaller();
			final Object object = unmarshaller.unmarshal(input);
			return Model.ADAPTER.unmarshal((Model)object);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Private helper class for lazy instantiation of the JAXBContext.
	 */
	private static final class JAXBContextHolder {
		private static final JAXBContext CONTEXT; static {
			try {
				CONTEXT = JAXBContext.newInstance(
					WayPoint.Model.class,
					Route.Model.class,
					Track.Model.class,
					TrackSegment.Model.class,
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

}
