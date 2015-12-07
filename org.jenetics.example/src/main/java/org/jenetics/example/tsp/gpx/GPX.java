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
import static org.jenetics.internal.util.jaxb.adapterFor;
import static org.jenetics.internal.util.jaxb.marshal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class GPX {
	private final List<WayPoint> _wayPoints = new ArrayList<>();
	private final List<Route> _routes = new ArrayList<>();
	private final List<Track> _tracks = new ArrayList<>();

	public GPX() {
	}

	public GPX addWayPoint(final WayPoint point) {
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

	public List<WayPoint> getWayPoints() {
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
	}



	public static void main(final String[] args) throws IOException {
		write(WayPoint.of("Innsbruck", 34, 43, 23.0), System.out);
	}











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
