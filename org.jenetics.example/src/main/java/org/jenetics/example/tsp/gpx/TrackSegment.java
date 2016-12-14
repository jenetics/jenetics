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
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A Track Segment holds a list of Track Points which are logically connected in
 * order. To represent a single GPS track where GPS reception was lost, or the
 * GPS receiver was turned off, start a new Track Segment for each continuous
 * span of track data.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(TrackSegment.Model.Adapter.class)
public final class TrackSegment implements Iterable<WayPoint>, Serializable {

	private static final long serialVersionUID = 1L;

	private final List<WayPoint> _points;

	/**
	 * Create a new track-segment with the given points.
	 *
	 * @param points the points of the track-segment
	 * @throws NullPointerException if the given {@code points} sequence is
	 *        {@code null}
	 */
	private TrackSegment(final List<WayPoint> points) {
		_points = unmodifiableList(requireNonNull(points));
	}

	/**
	 * Return the track-points of this segment.
	 *
	 * @return the track-points of this segment
	 */
	public List<WayPoint> getPoints() {
		return _points;
	}

	/**
	 * Return {@code true} if {@code this} track-segment doesn't contain any
	 * track-point.
	 *
	 * @return {@code true} if {@code this} track-segment is empty, {@code false}
	 *         otherwise
	 */
	public boolean isEmpty() {
		return _points.isEmpty();
	}

	@Override
	public Iterator<WayPoint> iterator() {
		return _points.iterator();
	}

	/**
	 * Return a stream of {@link WayPoint} objects this track-segments contains.
	 *
	 * @return a stream of {@link WayPoint} objects this track-segment contains
	 */
	public Stream<WayPoint> stream() {
		return _points.stream();
	}

	@Override
	public int hashCode() {
		return _points.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof TrackSegment &&
			((TrackSegment)obj)._points.equals(_points);
	}

	@Override
	public String toString() {
		return format("TrackSegment[points=%s]", _points.size());
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new track-segment with the given points.
	 *
	 * @param points the points of the track-segment
	 * @return a new track-segment with the given points
	 * @throws NullPointerException if the given {@code points} sequence is
	 *        {@code null}
	 */
	public static TrackSegment of(final List<WayPoint> points) {
		return new TrackSegment(points);
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "trkseg")
	@XmlType(name = "gpx.TrackSegment")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlElement(name = "trkpt", nillable = true)
		public List<WayPoint> points;

		public static final class Adapter
			extends XmlAdapter<Model, TrackSegment>
		{
			@Override
			public Model marshal(final TrackSegment segment) {
				final Model model = new Model();
				model.points = segment.getPoints();
				return model;
			}

			@Override
			public TrackSegment unmarshal(final Model model) {
				return new TrackSegment(
					model.points != null
						? model.points
						: emptyList()
				);
			}
		}

		public static final Adapter ADAPTER = new Adapter();
	}

}
