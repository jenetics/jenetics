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
package org.jenetics.xml;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.jenetics.xml.Lists.immutable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Simplifies the usage of the {@link XMLStreamReader}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
abstract class XMLReader<T> {

	/**
	 * Represents an XML attribute, by its name.
	 */
	static final class Attr {

		/**
		 * The Attribute name.
		 */
		final String name;

		private Attr(final String name) {
			this.name = requireNonNull(name);
			if (name.isEmpty()) {
				throw new IllegalArgumentException(
					"Attribute must not be empty."
				);
			}
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			return object instanceof Attr &&
				((Attr)object).name.equals(name);
		}

		@Override
		public String toString() {
			return name;
		}

	}

	/**
	 * Create a new XML attribute with the given {@code name}.
	 *
	 * @param name the attribute name.
	 * @return a new attribute with the given {@code name}
	 * @throws NullPointerException if the given {@code name} is {@code null}
	 * @throws IllegalArgumentException if the given {@code name} is empty
	 */
	static Attr attr(final String name) {
		return new Attr(name);
	}


	private final String _name;
	private final List<Attr> _attrs;

	XMLReader(final String name, final List<Attr> attrs) {
		_name = requireNonNull(name);
		_attrs = immutable(attrs);
	}

	/**
	 * Return the name of the element processed by this reader.
	 *
	 * @return the element name the reader is processing
	 */
	public String name() {
		return _name;
	}

	/**
	 * Return the list of element attributes to read.
	 *
	 * @return the list of element attributes to read
	 */
	List<Attr> attrs() {
		return _attrs;
	}

	@Override
	public String toString() {
		return format("XMLReader[%s]", name());
	}


	/**
	 * Read the given type from the underlying XML stream {@code reader}.
	 *
	 * @param reader the underlying XML stream {@code reader}
	 * @return the read type, maybe {@code null}
	 * @throws XMLStreamException if an error occurs while reading the value
	 */
	public abstract T read(final XMLStreamReader reader, final boolean lenient)
		throws XMLStreamException;

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param attrs the element attributes
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> XMLReader<T> of(
		final XML.Function<Object[], T> creator,
		final String name,
		final List<Attr> attrs,
		final XMLReader<?>... children
	) {
		return new XMLReaderImpl<T>(name, attrs, asList(children), creator);
	}

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 * <pre>{@code
	 * XMLReader.of(
	 *     a -> Link.of((String)a[0], (String)a[1], (String)a[2]),
	 *     "link", attr("href"),
	 *     XMLReader.of("text"),
	 *     XMLReader.of("type")
	 * )
	 * }</pre>
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param attr the element attribute
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> XMLReader<T> of(
		final XML.Function<Object[], T> creator,
		final String name,
		final Attr attr,
		final XMLReader<?>... children
	) {
		return of(creator, name, singletonList(attr), children);
	}

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 * <pre>{@code
	 * XMLReader.of(
	 *     a -> Link.of((String)a[0], (String)a[1], (String)a[2]), (String)a[3],
	 *     "link",
	 *     attr("href"),
	 *     attr("img"),
	 *     XMLReader.of("text"),
	 *     XMLReader.of("type")
	 * )
	 * }</pre>
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param attr1 the first element attribute
	 * @param attr2 the second element attribute
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> XMLReader<T> of(
		final XML.Function<Object[], T> creator,
		final String name,
		final Attr attr1,
		final Attr attr2,
		final XMLReader<?>... children
	) {
		return of(creator, name, asList(attr1, attr2), children);
	}

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param attr1 the first element attribute
	 * @param attr2 the second element attribute
	 * @param attr3 the third element attribute
	 * @param attr4 the fourth element attribute
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> XMLReader<T> of(
		final XML.Function<Object[], T> creator,
		final String name,
		final Attr attr1,
		final Attr attr2,
		final Attr attr3,
		final Attr attr4
	) {
		return of(creator, name, asList(attr1, attr2, attr3, attr4));
	}

	/**
	 * Create a new {@code XMLReader} with the given elements.
	 *
	 * @param creator creates the final object from the read arguments
	 * @param name the element name
	 * @param children the child element readers
	 * @param <T> the object type
	 * @return the reader for the given element
	 */
	public static <T> XMLReader<T> of(
		final XML.Function<Object[], T> creator,
		final String name,
		final XMLReader<?>... children
	) {
		return of(creator, name, emptyList(), children);
	}

	/**
	 * Create a reader for a leaf element with the given {@code name}.
	 *
	 * @param name the element
	 * @return the reader for the given element
	 */
	public static XMLReader<String> of(final String name) {
		return new XMLTextReader(name, emptyList());
	}

	/**
	 * Create a reader for a leaf element with the given {@code name}.
	 *
	 * @param name the element
	 * @param attrs the element attributes
	 * @return the reader for the given element
	 */
	public static XMLReader<String> of(final String name, final Attr... attrs) {
		return new XMLTextReader(name, asList(attrs));
	}

	/**
	 * Return a reader which reads a list of elements.
	 *
	 * @param reader the basic element reader
	 * @param <T> the object type
	 * @return the reader for the given elements
	 */
	public static <T> XMLReader<List<T>> ofList(final XMLReader<T> reader) {
		return new XMLListReader<T>(reader);
	}

}

/**
 * The main XML reader implementation.
 *
 * @param <T> the object type
 */
final class XMLReaderImpl<T> extends XMLReader<T> {

	private final List<XMLReader<?>> _children;
	private final Map<String, XMLReader<?>> _childMap = new HashMap<>();
	private final XML.Function<Object[], T> _creator;

	XMLReaderImpl(
		final String name,
		final List<Attr> attrs,
		final List<XMLReader<?>> children,
		final XML.Function<Object[], T> creator
	) {
		super(name, attrs);
		_creator = requireNonNull(creator);

		_children = requireNonNull(children);
		for (XMLReader<?> child : children) {
			_childMap.put(child.name(), child);
		}
	}

	@Override
	public T read(final XMLStreamReader reader, final boolean lenient)
		throws XMLStreamException
	{
		final Map<String, Object> param = new HashMap<>();
		for (Attr attr : attrs()) {
			final Object value = reader.getAttributeValue(null, attr.name);
			param.put(attr.name, value);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final XMLReader<?> child = _childMap.get(reader.getLocalName());
					try {
						// Special handling for XML list readers.
						if (child instanceof XMLListReader<?>) {
							@SuppressWarnings("unchecked")
							final List<Object> result = (List<Object>)param
								.computeIfAbsent(
									child.name(), key -> new ArrayList<>());

							result.add(
								((XMLListReader<?>)child)
									.adoptee()
									.read(reader, lenient)
							);

						} else if (child != null) {
							param.put(child.name(), child.read(reader, lenient));
						}
					} catch (XMLStreamException e) {
						if (!lenient) {
							throw e;
						}
					}

					break;
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						final int size = attrs().size() + _children.size();
						final Object[] args = new Object[size];

						for (int i = 0; i < attrs().size(); ++i) {
							args[i] = param.get(attrs().get(i).name);
						}
						for (int i = 0; i < _children.size(); ++i) {
							args[attrs().size() + i] =
								param.get(_children.get(i).name());
						}

						return _creator.apply(args);
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

/**
 * Special reader implementation for reading text content of leaf nodes.
 */
final class XMLTextReader extends XMLReader<String> {

	XMLTextReader(final String name, final List<Attr> attrs) {
		super(name, attrs);
	}

	@Override
	public String read(final XMLStreamReader reader, final boolean lenient)
		throws XMLStreamException
	{
		final StringBuilder result = new StringBuilder();
		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					result.append(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						return result.toString();
					}
			}
		}

		throw new XMLStreamException(format(
			"Premature end of file while reading '%s'.", name()
		));
	}

}

/**
 * List element reader.
 *
 * @param <T> the object type.
 */
final class XMLListReader<T> extends XMLReader<List<T>>  {

	private final XMLReader<T> _adoptee;

	XMLListReader(final XMLReader<T> adoptee) {
		super(adoptee.name(), emptyList());
		_adoptee = requireNonNull(adoptee);
	}

	XMLReader<T> adoptee() {
		return _adoptee;
	}

	@Override
	public List<T> read(final XMLStreamReader reader, final boolean lenient)
		throws XMLStreamException
	{
		throw new UnsupportedOperationException();
	}

}
