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
 * Represents a link to an external resource (Web page, digital photo, video
 * clip, etc) with additional information.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@XmlJavaTypeAdapter(Link.Model.Adapter.class)
public final class Link implements Serializable {

	private static final long serialVersionUID = 1L;

	private final URI _href;
	private final String _text;
	private final String _type;

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	private Link(final URI href, final String text, final String type) {
		_href = requireNonNull(href);
		_text = text;
		_type = type;
	}

	/**
	 * Return the hyperlink.
	 *
	 * @return the hyperlink
	 */
	public URI getHref() {
		return _href;
	}

	/**
	 * Return the hyperlink text.
	 *
	 * @return the hyperlink text
	 */
	public Optional<String> getText() {
		return Optional.ofNullable(_text);
	}

	/**
	 * Return the mime type of the hyperlink
	 *
	 * @return the mime type
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*_href.hashCode() + 31;
		hash += 17*Objects.hashCode(_text) + 31;
		hash += 17*Objects.hashCode(_type) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Link &&
			((Link)obj)._href.equals(_href) &&
			Objects.equals(((Link)obj)._text, _text) &&
			Objects.equals(((Link)obj)._type, _type);
	}

	@Override
	public String toString() {
		return _href.toString();
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @return a new {@code Link} object with the given parameters
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	public static Link of(final URI href, final String text, final String type) {
		return new Link(href, text, type);
	}

	/**
	 * Create a new {@code Link} object with the given parameters.
	 *
	 * @param href the hyperlink (mandatory)
	 * @param text the text of the hyperlink (optional)
	 * @param type the mime type of the content, e.g. {@code image/jpeg}
	 *        (optional)
	 * @return a new {@code Link} object with the given parameters
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 * @throws IllegalArgumentException if the given {@code href} is not a valid
	 *         URL
	 */
	public static Link of(final String href, final String text, final String type) {
		try {
			return new Link(new URI(requireNonNull(href)), text, type);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a new {@code Link} object with the given {@code href}.
	 *
	 * @param href the hyperlink (mandatory)
	 * @return a new {@code Link} object with the given {@code href}
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 */
	public static Link of(final URI href) {
		return new Link(href, null, null);
	}

	/**
	 * Create a new {@code Link} object with the given {@code href}.
	 *
	 * @param href the hyperlink (mandatory)
	 * @return a new {@code Link} object with the given {@code href}
	 * @throws NullPointerException if the given {@code href} is {@code null}
	 * @throws IllegalArgumentException if the given {@code href} is not a valid
	 *         URL
	 */
	public static Link of(final String href) {
		try {
			return new Link(new URI(requireNonNull(href)), null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "link")
	@XmlType(name = "gpx.Link")
	@XmlAccessorType(XmlAccessType.FIELD)
	static final class Model {

		@XmlAttribute(name = "href", required = true)
		public String href;

		@XmlElement(name = "text")
		public String text;

		@XmlElement(name = "type")
		public String type;

		public static final class Adapter
			extends XmlAdapter<Link.Model, Link>
		{
			@Override
			public Link.Model marshal(final Link link) {
				final Model model = new Model();
				model.href = link._href.toString();
				model.text = link._text;
				model.type = link._type;
				return model;
			}

			@Override
			public Link unmarshal(final Link.Model model) {
				return Link.of(model.href, model.text, model.type);
			}
		}

		static final Adapter ADAPTER = new Adapter();

	}


}
