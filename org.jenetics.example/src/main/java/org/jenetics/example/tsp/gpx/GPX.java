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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class GPX implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<WayPoint> _wayPoints = new ArrayList<>();
	private final List<Route> _routes = new ArrayList<>();
	private final List<Track> _tracks = new ArrayList<>();

	public GPX() {
	}

	/**
	 * Add a new way-point to the {@code GPX} object.
	 *
	 * @param point the way-point to add.
	 * @return this GPX object, for method chaining
	 * @throws NullPointerException if the given {@code point} is {@code null}
	 */
	public GPX addWayPoint(final WayPoint point) {
		_wayPoints.add(requireNonNull(point));
		return this;
	}

	/**
	 * Add a new route to the {@code GPX} object.
	 *
	 * @param route the route to add
	 * @return this GPX object, for method chaining
	 * @throws NullPointerException if the given {@code route} is {@code null}
	 */
	public GPX addRoute(final Route route) {
		_routes.add(requireNonNull(route));
		return this;
	}

	/**
	 * Add a new track to the {@code GPX} object.
	 *
	 * @param track the track to add
	 * @return this GPX object, for method chaining
	 * @throws NullPointerException if the given {@code point} is {@code null}
	 */
	public GPX addTrack(final Track track) {
		_tracks.add(requireNonNull(track));
		return this;
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} way-points.
	 *
	 * @return an unmodifiable list of the {@code GPX} way-points.
	 */
	public List<WayPoint> getWayPoints() {
		return Collections.unmodifiableList(_wayPoints);
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} routes.
	 *
	 * @return an unmodifiable list of the {@code GPX} routes.
	 */
	public List<Route> getRoutes() {
		return Collections.unmodifiableList(_routes);
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} tracks.
	 *
	 * @return an unmodifiable list of the {@code GPX} tracks.
	 */
	public List<Track> getTracks() {
		return Collections.unmodifiableList(_tracks);
	}


	/**
	 * Model class for XML serialization/deserialization.
	 */
	@XmlRootElement(name = "gpx")
	@XmlType(name = "gpx.GPX")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "version", required = false)
		public String version = "1.1";

		@XmlAttribute(name = "creator", required = false)
		public String creator = "Jenetics TSP";

		@XmlElement(name = "wpt", required = false, nillable = true)
		public List<WayPoint> wayPoints;

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
