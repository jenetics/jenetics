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
 * Represents a GPX track.
 */
@XmlJavaTypeAdapter(Track.Model.Adapter.class)
public final class Track implements Iterable<Track.Segment> {

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
				extends XmlAdapter<Segment.Model, Segment>
			{
				@Override
				public Segment.Model marshal(final Segment segment) {
					final Segment.Model model = new Segment.Model();
					model.points = !segment._points.isEmpty()
						? segment._points
						: null;

					return model;
				}

				@Override
				public Segment unmarshal(final Segment.Model model) {
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
