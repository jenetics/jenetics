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

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Point.Model.Adapter.class)
public final class Point implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Latitude _latitude;
	private final Longitude _longitude;

	private final Double _elevation;
	private final Double _speed;
	private final ZonedDateTime _time;
	private final Degrees _magvar;
	private final Double _geoidHeight;
	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final ISeq<Link> _links;
	private final String _symbol;
	private final String _type;
	private final Fix _fix;
	private final UInt _sat;
	private final Double _hdop;
	private final Double _vdop;
	private final Double _pdop;
	private final Double _ageofdgpsdata;
	private final DGPSStation _dgpsid;

	private Point(
		final Latitude latitude,
		final Longitude longitude,
		final Double elevation,
		final Double speed,
		final ZonedDateTime time,
		final Degrees magvar,
		final Double geoidHeight,
		final String name,
		final String comment,
		final String description,
		final String source,
		final ISeq<Link> links,
		final String symbol,
		final String type,
		final Fix fix,
		final UInt sat,
		final Double hdop,
		final Double vdop,
		final Double pdop,
		final Double ageofdgpsdata,
		final DGPSStation dgpsid
	) {
		_latitude = requireNonNull(latitude);
		_longitude = requireNonNull(longitude);

		_elevation = elevation;
		_speed = speed;
		_time = time;
		_magvar = magvar;
		_geoidHeight = geoidHeight;
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = requireNonNull(links);
		_symbol = symbol;
		_type = type;
		_fix = fix;
		_sat = sat;
		_hdop = hdop;
		_vdop = vdop;
		_pdop = pdop;
		_ageofdgpsdata = ageofdgpsdata;
		_dgpsid = dgpsid;
	}

	public Latitude getLatitude() {
		return _latitude;
	}

	public Longitude getLongitude() {
		return _longitude;
	}

	public Optional<Double> getElevation() {
		return Optional.ofNullable(_elevation);
	}

	public Optional<Double> getSpeed() {
		return Optional.ofNullable(_speed);
	}

	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(_time);
	}

	public Optional<Degrees> getMagvar() {
		return Optional.ofNullable(_magvar);
	}

	public Optional<Double> getGeoidHeight() {
		return Optional.ofNullable(_geoidHeight);
	}

	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	public Optional<String> getSource() {
		return Optional.ofNullable(_source);
	}

	public ISeq<Link> getLinks() {
		return _links;
	}

	public Optional<String> getSymbol() {
		return Optional.ofNullable(_symbol);
	}

	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	public Optional<Fix> getFix() {
		return Optional.ofNullable(_fix);
	}

	public Optional<UInt> getSat() {
		return Optional.ofNullable(_sat);
	}

	public Optional<Double> getHdop() {
		return Optional.ofNullable(_hdop);
	}

	public Optional<Double> getVdop() {
		return Optional.ofNullable(_vdop);
	}

	public Optional<Double> getPdop() {
		return Optional.ofNullable(_pdop);
	}

	public Optional<Double> getAgeofdgpsdata() {
		return Optional.ofNullable(_ageofdgpsdata);
	}

	public Optional<DGPSStation> getDgpsid() {
		return Optional.ofNullable(_dgpsid);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*_latitude.hashCode() + 31;
		hash += 17*_longitude.hashCode() + 31;
		hash += 17*Objects.hashCode(_elevation) + 31;
		hash += 17*Objects.hashCode(_speed) + 31;
		hash += 17*Objects.hashCode(_time) + 31;
		hash += 17*Objects.hashCode(_magvar) + 31;
		hash += 17*Objects.hashCode(_geoidHeight) + 31;
		hash += 17*Objects.hashCode(_name) + 31;
		hash += 17*Objects.hashCode(_comment) + 31;
		hash += 17*Objects.hashCode(_description) + 31;
		hash += 17*Objects.hashCode(_source) + 31;
		hash += 17*Objects.hashCode(_links) + 31;
		hash += 17*Objects.hashCode(_symbol) + 31;
		hash += 17*Objects.hashCode(_type) + 31;
		hash += 17*Objects.hashCode(_fix) + 31;
		hash += 17*Objects.hashCode(_sat) + 31;
		hash += 17*Objects.hashCode(_hdop) + 31;
		hash += 17*Objects.hashCode(_vdop) + 31;
		hash += 17*Objects.hashCode(_pdop) + 31;
		hash += 17*Objects.hashCode(_ageofdgpsdata) + 31;
		hash += 17*Objects.hashCode(_dgpsid) + 31;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Point &&
			((Point)obj)._latitude.equals(_latitude) &&
			((Point)obj)._longitude.equals(_longitude) &&
			Objects.equals(((Point)obj)._elevation, _elevation) &&
			Objects.equals(((Point)obj)._speed, _speed) &&
			Objects.equals(((Point)obj)._time, _time) &&
			Objects.equals(((Point)obj)._magvar, _magvar) &&
			Objects.equals(((Point)obj)._geoidHeight, _geoidHeight) &&
			Objects.equals(((Point)obj)._name, _name) &&
			Objects.equals(((Point)obj)._comment, _comment) &&
			Objects.equals(((Point)obj)._description, _description) &&
			Objects.equals(((Point)obj)._source, _source) &&
			Objects.equals(((Point)obj)._links, _links) &&
			Objects.equals(((Point)obj)._symbol, _symbol) &&
			Objects.equals(((Point)obj)._type, _type) &&
			Objects.equals(((Point)obj)._fix, _fix) &&
			Objects.equals(((Point)obj)._sat, _sat) &&
			Objects.equals(((Point)obj)._hdop, _hdop) &&
			Objects.equals(((Point)obj)._vdop, _vdop) &&
			Objects.equals(((Point)obj)._pdop, _pdop) &&
			Objects.equals(((Point)obj)._ageofdgpsdata, _ageofdgpsdata) &&
			Objects.equals(((Point)obj)._dgpsid, _dgpsid);
	}

	@Override
	public String toString() {
		return format("[lat=%s, lon=%s]", _latitude, _latitude);
	}


	public static final class Builder {
		private Double _elevation;
		private Double _speed;
		private ZonedDateTime _time;
		private Degrees _magvar;
		private Double _geoidHeight;
		private String _name;
		private String _comment;
		private String _description;
		private String _source;
		private ISeq<Link> _links;
		private String _symbol;
		private String _type;
		private Fix _fix;
		private UInt _sat;
		private Double _hdop;
		private Double _vdop;
		private Double _pdop;
		private Double _ageofdgpsdata;
		private DGPSStation _dgpsid;


		public Builder elevation(final Double elevation) {
			_elevation = elevation;
			return this;
		}

		public Builder speed(final Double speed) {
			_speed = speed;
			return this;
		}

		public Builder time(final ZonedDateTime time) {
			_time = time;
			return this;
		}

		public Builder magvar(final Degrees magvar) {
			_magvar = magvar;
			return this;
		}

		public Builder magvar(final double magvar) {
			_magvar = Degrees.of(magvar);
			return this;
		}

		public Builder geoidHeight(final Double geoidHeight) {
			_geoidHeight = geoidHeight;
			return this;
		}

		public Builder name(final String name) {
			_name = name;
			return this;
		}

		public Builder comment(final String comment) {
			_comment = comment;
			return this;
		}

		public Builder links(final ISeq<Link> links) {
			_links = links;
			return this;
		}

		public Builder symbol(final String symbol) {
			_symbol = symbol;
			return this;
		}

		public Builder type(final String type) {
			_type = type;
			return this;
		}

		public Builder fix(final Fix fix) {
			_fix = fix;
			return this;
		}

		public Builder sat(final UInt sat) {
			_sat = sat;
			return this;
		}

		public Builder sat(final int sat) {
			_sat = UInt.of(sat);
			return this;
		}

		public Builder hdop(final Double hdop) {
			_hdop = hdop;
			return this;
		}

		public Builder vdop(final Double vdop) {
			_vdop = vdop;
			return this;
		}

		public Builder pdop(final Double pdop) {
			_pdop = pdop;
			return this;
		}

		public Builder ageofdgpsdata(final Double age) {
			_ageofdgpsdata = age;
			return this;
		}

		public Builder dgpsStation(final DGPSStation station) {
			_dgpsid = station;
			return this;
		}

		public Builder dgpsid(final int station) {
			_dgpsid = DGPSStation.of(station);
			return this;
		}


		public Point build(final Latitude latitude, final Longitude longitude) {
			return new Point(
				latitude,
				longitude,
				_elevation,
				_speed,
				_time,
				_magvar,
				_geoidHeight,
				_name,
				_comment,
				_description,
				_source,
				_links != null ? _links : ISeq.empty(),
				_symbol,
				_type,
				_fix,
				_sat,
				_hdop,
				_vdop,
				_pdop,
				_ageofdgpsdata,
				_dgpsid
			);
		}

		public Point build(final double latitude, final double longitude) {
			return build(Latitude.of(latitude), Longitude.of(longitude));
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	@XmlRootElement(name = "wpt")
	@XmlType(name = "gpx.WayPoint")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "lat", required = true)
		public double latitude;

		@XmlAttribute(name = "lon", required = true)
		public double longitude;

		@XmlElement(name = "ele")
		public Double elevation;

		@XmlElement(name = "speed")
		public Double speed;

		@XmlElement(name = "time")
		public String time;

		@XmlElement(name = "magvar")
		public Double magvar;

		@XmlElement(name = "geoidheight")
		public Double geoidheight;

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

		@XmlElement(name = "sym")
		public String sym;

		@XmlElement(name = "type")
		public String type;

		@XmlElement(name = "fix")
		public String fix;

		@XmlElement(name = "sat")
		public Integer sat;

		@XmlElement(name = "hdop")
		public Double hdop;

		@XmlElement(name = "vdop")
		public Double vdop;

		@XmlElement(name = "pdop")
		public Double pdop;

		@XmlElement(name = "ageofdgpsdata")
		public Double ageofdgpsdata;

		@XmlElement(name = "dgpsid")
		public Integer dgpsid;


		public static final class Adapter
			extends XmlAdapter<Model, Point>
		{
			private static final DateTimeFormatter
			DTF = DateTimeFormatter.ISO_INSTANT;

			@Override
			public Point.Model marshal(final Point point) {
				final Point.Model model = new Point.Model();
				model.latitude = point.getLatitude().getValue();
				model.longitude = point.getLongitude().getValue();
				model.elevation = point.getElevation().orElse(null);
				model.speed = point.getSpeed().orElse(null);
				model.time = point.getTime().map(DTF::format).orElse(null);
				model.magvar = point.getMagvar().map(Degrees::getValue).orElse(null);
				model.geoidheight = point.getGeoidHeight().orElse(null);
				model.name = point.getName().orElse(null);
				model.cmt = point.getComment().orElse(null);
				model.src = point.getSource().orElse(null);
				model.link = point.getLinks().map(Link.Model.ADAPTER::marshal).asList();
				model.sym = point.getSymbol().orElse(null);
				model.type = point.getType().orElse(null);
				model.fix = point.getFix().map(Fix::getValue).orElse(null);
				model.sat = point.getSat().map(UInt::getValue).orElse(null);
				model.hdop = point.getHdop().orElse(null);
				model.vdop = point.getVdop().orElse(null);
				model.pdop = point.getPdop().orElse(null);
				model.ageofdgpsdata = point.getAgeofdgpsdata().orElse(null);
				model.dgpsid = point.getDgpsid().map(DGPSStation::getValue).orElse(null);

				return model;
			}

			@Override
			public Point unmarshal(final Point.Model model) {
				return new Point(
					Latitude.of(model.latitude),
					Longitude.of(model.longitude),
					model.elevation,
					model.speed,
					Optional.ofNullable(model.time)
						.map(t -> ZonedDateTime.parse(t, DTF))
						.orElse(null),
					Optional.ofNullable(model.magvar)
						.map(Degrees::of)
						.orElse(null),
					model.geoidheight,
					model.name,
					model.cmt,
					model.desc,
					model.src,
					model.link.stream()
						.map(Link.Model.ADAPTER::unmarshal)
						.collect(ISeq.toISeq()),
					model.sym,
					model.type,
					Optional.ofNullable(model.fix)
						.map(Fix::of)
						.orElse(null),
					Optional.ofNullable(model.sat)
						.map(UInt::of)
						.orElse(null),
					model.hdop,
					model.vdop,
					model.pdop,
					model.ageofdgpsdata,
					Optional.ofNullable(model.dgpsid)
						.map(DGPSStation::of)
						.orElse(null)
				);
			}
		}

	}
}


