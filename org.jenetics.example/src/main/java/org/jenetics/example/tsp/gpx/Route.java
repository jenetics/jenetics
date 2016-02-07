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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Represents a GPX route.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Route.Model.Adapter.class)
public final class Route implements Iterable<WayPoint>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final List<WayPoint> _points = new ArrayList<>();

	public Route(final String name) {
		_name = name;
	}

	public Route() {
		this(null);
	}

	/**
	 * Return the route name.
	 *
	 * @return the route name
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Add a new way-point to the route.
	 *
	 * @param point the way-point to add to this route.
	 * @return this track, for command chaining
	 * @throws NullPointerException if the given way-point is {@code null}
	 */
	public Route add(final WayPoint point) {
		_points.add(requireNonNull(point));
		return this;
	}

	@Override
	public Iterator<WayPoint> iterator() {
		return _points.iterator();
	}

	/**
	 * Return a stream of {@link WayPoint} objects this route contains.
	 *
	 * @return a stream of {@link WayPoint} objects this route contains
	 */
	public Stream<WayPoint> stream() {
		return _points.stream();
	}


	/**
	 * Model class for XML serialization/deserialization.
	 */
	@XmlRootElement(name = "rte")
	@XmlType(name = "gpx.Route")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlElement(name = "name", required = false)
		public String name;

		@XmlElement(name = "rtept", required = false, nillable = true)
		public List<WayPoint> points;

		public static final class Adapter
			extends XmlAdapter<Model, Route>
		{
			@Override
			public Model marshal(final Route route) {
				final Model model = new Model();
				model.name = route._name;
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
