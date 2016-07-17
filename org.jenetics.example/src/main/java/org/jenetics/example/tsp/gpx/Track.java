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

import org.jenetics.util.ISeq;

/**
 * Represents a GPX track - an ordered list of points describing a path.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Track.Model.Adapter.class)
public final class Track implements Iterable<TrackSegment>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final ISeq<Link> _links;
	private final UInt _number;
	private final String _type;
	private final ISeq<TrackSegment> _segments;

	/**
	 * Create a new {@code Track} with the given parameters.
	 *
	 * @param name the GPS name of the track
	 * @param comment the GPS comment for the track
	 * @param description user description of the track
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about track
	 * @param number the GPS track number
	 * @param type the type (classification) of track
	 * @param segments the track-segments holds a list of track-points which are
	 *        logically connected in order. To represent a single GPS track
	 *        where GPS reception was lost, or the GPS receiver was turned off,
	 *        start a new track-segment for each continuous span of track data.
	 * @throws NullPointerException if the {@code links} or the {@code segments}
	 *         sequence is {@code null}
	 */
	private Track(
		final String name,
		final String comment,
		final String description,
		final String source,
		final ISeq<Link> links,
		final UInt number,
		final String type,
		final ISeq<TrackSegment> segments
	) {
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = requireNonNull(links);
		_number = number;
		_type = type;
		_segments = requireNonNull(segments);
	}

	/**
	 * Return the track name.
	 *
	 * @return the track name
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return the GPS comment of the track.
	 *
	 * @return the GPS comment of the track
	 */
	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	/**
	 * Return the text description of the track.
	 *
	 * @return the text description of the track
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
	 * Return the links to external information about the track.
	 *
	 * @return the links to external information about the track
	 */
	public ISeq<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the GPS track number.
	 *
	 * @return the GPS track number
	 */
	public Optional<UInt> getNumber() {
		return Optional.ofNullable(_number);
	}

	/**
	 * Return the type (classification) of the track.
	 *
	 * @return the type (classification) of the track
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	/**
	 * Return the sequence of route points.
	 *
	 * @return the sequence of route points
	 */
	public ISeq<TrackSegment> getSegments() {
		return _segments;
	}

	@Override
	public Iterator<TrackSegment> iterator() {
		return _segments.iterator();
	}

	/**
	 * Return a stream of {@link TrackSegment} objects this track contains.
	 *
	 * @return a stream of {@link TrackSegment} objects this track contains
	 */
	public Stream<TrackSegment> stream() {
		return _segments.stream();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Track} with the given parameters.
	 *
	 * @param name the GPS name of the track
	 * @param comment the GPS comment for the track
	 * @param description user description of the track
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about track
	 * @param number the GPS track number
	 * @param type the type (classification) of track
	 * @param segments the track-segments holds a list of track-points which are
	 *        logically connected in order. To represent a single GPS track
	 *        where GPS reception was lost, or the GPS receiver was turned off,
	 *        start a new track-segment for each continuous span of track data.
	 * @return a new {@code Track} with the given parameters
	 * @throws NullPointerException if the {@code links} or the {@code segments}
	 *         sequence is {@code null}
	 */
	public static Track of(
		final String name,
		final String comment,
		final String description,
		final String source,
		final ISeq<Link> links,
		final UInt number,
		final String type,
		final ISeq<TrackSegment> segments
	) {
		return new Track(
			name,
			comment,
			description,
			source,
			links,
			number,
			type,
			segments
		);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "trk")
	@XmlType(name = "gpx.Track")
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

		@XmlElement(name = "trkseg", nillable = true)
		public List<TrackSegment.Model> segments;

		public static final class Adapter
			extends XmlAdapter<Model, Track>
		{
			@Override
			public Model marshal(final Track track) {
				final Model model = new Model();
				model.name = track._name;
				model.cmt = track._comment;
				model.desc = track._description;
				model.src = track._source;
				model.link = track._links
					.map(Link.Model.ADAPTER::marshal)
					.asList();
				model.number = Optional.ofNullable(track._number)
					.map(UInt::intValue)
					.orElse(null);
				model.type = track._type;
				model.segments = track._segments
					.map(TrackSegment.Model.ADAPTER::marshal)
					.asList();

				return model;
			}

			@Override
			public Track unmarshal(final Model model) {
				return Track.of(
					model.name,
					model.cmt,
					model.desc,
					model.src,
					model.link.stream()
						.map(Link.Model.ADAPTER::unmarshal)
						.collect(ISeq.toISeq()),
					Optional.ofNullable(model.number)
						.map(UInt::of)
						.orElse(null),
					model.type,
					model.segments.stream()
						.map(TrackSegment.Model.ADAPTER::unmarshal)
						.collect(ISeq.toISeq())
				);
			}
		}
	}

}
