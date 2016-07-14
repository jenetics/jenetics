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


	private Link(final URI href, final String text, final String type) {
		_href = Objects.requireNonNull(href);
		_text = text;
		_type = type;
	}

	public URI getHref() {
		return _href;
	}

	public Optional<String> getText() {
		return Optional.ofNullable(_text);
	}

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

	public static Link of(final URI href, final String text, final String type) {
		return new Link(href, text, type);
	}

	public static Link of(final String href, final String text, final String type) {
		try {
			return new Link(new URI(href), text, type);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Link of(final URI href) {
		return new Link(href, null, null);
	}

	public static Link of(final String href) {
		try {
			return new Link(new URI(href), null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}


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
