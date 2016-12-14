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
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Represents a route - an ordered list of way-points representing a series of
 * turn points leading to a destination.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Route.Model.Adapter.class)
public final class Route implements Iterable<WayPoint>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final List<Link> _links;
	private final UInt _number;
	private final String _type;
	private final List<WayPoint> _points;

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @param name the GPS name of the route
	 * @param comment the GPS comment of the route
	 * @param description the Text description of route for user. Not sent to GPS.
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about the route
	 * @param number the GPS route number
	 * @param type the type (classification) of the route
	 * @param points the sequence of route points
	 * @throws NullPointerException if the {@code links} or the {@code points}
	 *         sequence is {@code null}
	 */
	private Route(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final List<WayPoint> points
	) {
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = unmodifiableList(requireNonNull(links));
		_number = number;
		_type = type;
		_points = unmodifiableList(requireNonNull(points));
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
	 * Return the GPS comment of the route.
	 *
	 * @return the GPS comment of the route
	 */
	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	/**
	 * Return the Text description of route for user. Not sent to GPS.
	 *
	 * @return the Text description of route for user. Not sent to GPS
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the source of data. Included to give user some idea of reliability
	 * and accuracy of data.
	 *
	 * @return the source of data
	 */
	public Optional<String> getSource() {
		return Optional.ofNullable(_source);
	}

	/**
	 * Return the links to external information about the route.
	 *
	 * @return the links to external information about the route
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the GPS route number.
	 *
	 * @return the GPS route number
	 */
	public Optional<UInt> getNumber() {
		return Optional.ofNullable(_number);
	}

	/**
	 * Return the type (classification) of the route.
	 *
	 * @return the type (classification) of the route
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	/**
	 * Return the sequence of route points.
	 *
	 * @return the sequence of route points
	 */
	public List<WayPoint> getPoints() {
		return _points;
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

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*Objects.hashCode(_name) + 37;
		hash += 17*Objects.hashCode(_comment) + 37;
		hash += 17*Objects.hashCode(_description) + 37;
		hash += 17*Objects.hashCode(_source) + 37;
		hash += 17*Objects.hashCode(_links) + 37;
		hash += 17*Objects.hashCode(_number) + 37;
		hash += 17*Objects.hashCode(_points) + 37;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Route &&
			Objects.equals(((Route)obj)._name, _name) &&
			Objects.equals(((Route)obj)._comment, _comment) &&
			Objects.equals(((Route)obj)._description, _description) &&
			Objects.equals(((Route)obj)._source, _source) &&
			Objects.equals(((Route)obj)._links, _links) &&
			Objects.equals(((Route)obj)._number, _number) &&
			Objects.equals(((Route)obj)._points, _points);
	}

	@Override
	public String toString() {
		return format("Rout[name=%s, points=%s]", _name, _points.size());
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Return a new {@code Route} builder object.
	 *
	 * @return a new {@code Route} builder object
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder class for building {@code Route} objects.
	 */
	public static final class Builder {

		private String _name;
		private String _comment;
		private String _description;
		private String _source;
		private final List<Link> _links = new ArrayList<>();
		private UInt _number;
		private String _type;
		private final List<WayPoint> _points = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Set the route name.
		 *
		 * @param name the route name.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder name(final String name) {
			_name = name;
			return this;
		}

		/**
		 * Set the route comment.
		 *
		 * @param comment the route comment
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder comment(final String comment) {
			_comment = comment;
			return this;
		}

		/**
		 * Set the route description.
		 *
		 * @param description the route description
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder description(final String description) {
			_description = description;
			return this;
		}

		/**
		 * Set the source of the data. Included to give user some idea of
		 * reliability and accuracy of data.
		 *
		 * @param source the source of the data
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder source(final String source) {
			_source = source;
			return this;
		}

		/**
		 * Set the links to external information about the route.
		 *
		 * @param link the links to external information about the route.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder addLink(final Link link) {
			_links.add(requireNonNull(link));
			return this;
		}

		/**
		 * Removes all previously set links.
		 *
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder clearLinks() {
			_links.clear();
			return this;
		}

		/**
		 * Set the GPS route number.
		 *
		 * @param number the GPS route number
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder number(final UInt number) {
			_number = number;
			return this;
		}

		/**
		 * Set the GPS route number.
		 *
		 * @param number the GPS route number
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder number(final int number) {
			_number = UInt.of(number);
			return this;
		}

		/**
		 * Set the type (classification) of the route.
		 *
		 * @param type the type (classification) of the route.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder type(final String type) {
			_type = type;
			return this;
		}

		/**
		 * Adds a way-point to the route.
		 *
		 * @param point the way-point which is added to the route
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the {@code point} is {@code null}
		 */
		public Builder addWayPoint(final WayPoint point) {
			_points.add(requireNonNull(point));
			return this;
		}

		/**
		 * Removes all previously added way-points.
		 *
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder clearWayPoints() {
			_points.clear();
			return this;
		}

		/**
		 * Create a new {@code Route} object with the set values.
		 *
		 * @return a new {@code Route} object with the set values
		 */
		public Route build() {
			return new Route(
				_name,
				_comment,
				_description,
				_source,
				new ArrayList<>(_links),
				_number,
				_type,
				new ArrayList<>(_points)
			);
		}

	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "rte")
	@XmlType(name = "gpx.Route")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlElement(name = "name")
		public String name;

		@XmlElement(name = "cmt")
		public String cmt;

		@XmlElement(name = "desc")
		public String desc;

		@XmlElement(name = "src")
		public String src;

		@XmlElement(name = "link")
		public List<Link.Model> link;

		@XmlElement(name = "number")
		public Integer number;

		@XmlElement(name = "type")
		public String type;

		@XmlElement(name = "rtept")
		public List<WayPoint.Model> points;

		public static final class Adapter
			extends XmlAdapter<Model, Route>
		{
			@Override
			public Model marshal(final Route route) {
				final Model model = new Model();
				model.name = route._name;
				model.cmt = route._comment;
				model.desc = route._description;
				model.src = route._source;
				model.link = route.getLinks().stream()
					.map(Link.Model.ADAPTER::marshal)
					.collect(Collectors.toList());
				model.number = route.getNumber()
					.map(UInt::intValue)
					.orElse(null);
				model.type = route._type;
				model.points = route.getPoints().stream()
					.map(WayPoint.Model.ADAPTER::marshal)
					.collect(Collectors.toList());

				return model;
			}

			@Override
			public Route unmarshal(final Model model) {
				return new Route(
					model.name,
					model.cmt,
					model.desc,
					model.src,
					model.link.stream()
						.map(Link.Model.ADAPTER::unmarshal)
						.collect(Collectors.toList()),
					Optional.ofNullable(model.number)
						.map(UInt::of)
						.orElse(null),
					model.type,
					model.points.stream()
						.map(WayPoint.Model.ADAPTER::unmarshal)
						.collect(Collectors.toList())
				);
			}
		}

	}
}
