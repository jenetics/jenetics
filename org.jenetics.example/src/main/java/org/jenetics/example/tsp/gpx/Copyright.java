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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
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

/**
 * Information about the copyright holder and any license governing use of this
 * file. By linking to an appropriate license, you may place your data into the
 * public domain or grant additional usage rights.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Copyright.Model.Adapter.class)
public final class Copyright implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String _author;
	private final Year _year;
	private final URI _license;

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	private Copyright(final String author, final Year year, final URI license) {
		_author = requireNonNull(author);
		_year = year;
		_license = license;
	}

	/**
	 * Return the copyright holder.
	 *
	 * @return the copyright holder
	 */
	public String getAuthor() {
		return _author;
	}

	/**
	 * Return the year of copyright.
	 *
	 * @return the year of copyright
	 */
	public Optional<Year> getYear() {
		return Optional.ofNullable(_year);
	}

	/**
	 * Return the link to external file containing license text.
	 *
	 * @return link to external file containing license text
	 */
	public Optional<URI> getLicense() {
		return Optional.ofNullable(_license);
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*_author.hashCode();
		hash += 17*Objects.hashCode(_year) + 37;
		hash += 17*Objects.hashCode(_license) + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Copyright &&
			((Copyright) obj)._author.equals(_author) &&
			Objects.equals(((Copyright) obj)._year, _year) &&
			Objects.equals(((Copyright) obj)._license, _license);
	}

	@Override
	public String toString() {
		return _author + getYear().map(y -> " (c) " + y).orElse("");
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(
		final String author,
		final Year year,
		final URI license
	) {
		return new Copyright(author, year, license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(
		final String author,
		final int year,
		final URI license
	) {
		return new Copyright(author, Year.of(year), license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 * @throws IllegalArgumentException if the given {@code license} is not a
	 *         valid {@code URI} object
	 */
	public static Copyright of(
		final String author,
		final int year,
		final String license
	) {
		try {
			return new Copyright(author, Year.of(year), new URI(license));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final Year year) {
		return new Copyright(author, year, null);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param year year of copyright.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final int year) {
		return new Copyright(author, Year.of(year), null);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @param license link to external file containing license text.
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author, final URI license) {
		return new Copyright(author, null, license);
	}

	/**
	 * Create a new {@code Copyright} object with the given data.
	 *
	 * @param author copyright holder (TopoSoft, Inc.)
	 * @return a new {@code Copyright} object with the given data
	 * @throws NullPointerException if the {@code author} is {@code null}
	 */
	public static Copyright of(final String author) {
		return new Copyright(author, null, null);
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "copyright")
	@XmlType(name = "gpx.Copyright")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "author", required = true)
		public String author;

		@XmlElement(name = "year")
		public Integer year;

		@XmlElement(name = "license")
		public String license;

		public static final class Adapter
			extends XmlAdapter<Model, Copyright>
		{
			@Override
			public Model marshal(final Copyright copyright) {
				final Model model = new Model();
				model.author = copyright._author;
				model.year = copyright.getYear()
					.map(Year::getValue)
					.orElse(null);
				model.license = copyright.getLicense()
					.map(URI::toString)
					.orElse(null);

				return model;
			}

			@Override
			public Copyright unmarshal(final Model model) {
				return Copyright.of(
					model.author,
					Optional.ofNullable(model.year).orElse(null),
					Optional.ofNullable(model.license).orElse(null)
				);
			}
		}

		static final Adapter ADAPTER = new Adapter();

	}

}
