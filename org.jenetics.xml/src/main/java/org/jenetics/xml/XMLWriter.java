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

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Helper class for simplifying XML stream writing.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
final class XMLWriter {

	/**
	 * Represents an XML attribute.
	 */
	static final class Attr {
		final String name;
		final String value;

		private Attr(final String name, final Object value) {
			this.name = requireNonNull(name);
			this.value = requireNonNull(value).toString();
		}

		@Override
		public int hashCode() {
			int hash = 37;
			hash += 17*Objects.hashCode(name) + 31;
			hash += 17*Objects.hashCode(value) + 31;
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			return object instanceof Attr &&
				((Attr)object).name.equals(name) &&
				((Attr)object).value.equals(value);
		}

		@Override
		public String toString() {
			return format("%s=%s", name, value);
		}
	}

	static final class NS {
		final String name;

		private NS(final String name) {
			this.name = requireNonNull(name);
		}
	}

	/**
	 * The element writer.
	 */
	@FunctionalInterface
	interface ElementWriter {
		void write() throws XMLStreamException;
	}

	/**
	 * Functional interface for writing a given data object to a given XML
	 * writer. The implementations have to handle {@code null} data elements
	 * accordingly.
	 *
	 * @param <T> the data type
	 */
	@FunctionalInterface
	interface DataWriter<T> {
		void write(final T data, final XMLStreamWriter writer)
			throws XMLStreamException;
	}


	private final XMLStreamWriter _writer;

	/**
	 * Create a new XML writer instance from the given XML stream writer.
	 *
	 * @param writer the underlying XML stream writer
	 * @throws NullPointerException if the given writer is {@code null}
	 */
	XMLWriter(final XMLStreamWriter writer) {
		_writer = requireNonNull(writer);
	}

	/**
	 * Create a new attribute with the given name and value.
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @return a new attribute with the given name and value
	 */
	Attr attr(final String name, final Object value) {
		return new Attr(name, value);
	}

	NS ns(final String name) {
		return new NS(name);
	}

	/* *************************************************************************
	 *  ElementWriter creation methods.
	 * ************************************************************************/

	/**
	 * Create a new XML element writer from the given data.
	 *
	 * @param name the element name
	 * @param text the element content.
	 * @return a new element writer
	 */
	ElementWriter elem(final String name, final Object text) {
		requireNonNull(name);

		return () -> {
			if (text != null) {
				_writer.writeStartElement(name);
				_writer.writeCharacters(text.toString());
				_writer.writeEndElement();
			}
		};
	}

	/**
	 * Create a new XML element writer, which allows an additional transformation
	 * of the data object before writing it to the XML stream.
	 *
	 * @param name the element name
	 * @param object the data object
	 * @param converter the data converter
	 * @param <T> the data type
	 * @return a new element writer
	 * @throws NullPointerException if the {@code name} or {@code converter} is
	 *         {@code null}
	 */
	<T> ElementWriter elem(
		final String name,
		final T object,
		final Function<T, Object> converter
	) {
		requireNonNull(name);
		requireNonNull(converter);

		return () -> {
			if (object != null) {
				_writer.writeStartElement(name);
				_writer.writeCharacters(converter.apply(object).toString());
				_writer.writeEndElement();
			}
		};

	}

	/**
	 * Create a new XML element writer for explicitly writing the given data to
	 * the given {@code writer}.
	 *
	 * @param data the data point
	 * @param writer the data writer
	 * @param <T> the data type
	 * @return a new element writer
	 * @throws NullPointerException if the {@code writer} is {@code null}
	 */
	<T> ElementWriter elem(final T data, final DataWriter<T> writer) {
		requireNonNull(writer);

		return () -> {
			if (data != null) {
				writer.write(data, _writer);
			}
		};
	}

	/**
	 * Create a element writer for writing a given collection of data.
	 *
	 * @param data the data points
	 * @param writer the writer used for writing one data point
	 * @param <T> the data type
	 * @return a new element writer
	 * @throws NullPointerException if the {@code writer} is {@code null}
	 */
	<T> ElementWriter elems(final Iterable<T> data, final DataWriter<T> writer) {
		requireNonNull(writer);

		return () -> {
			if (data != null) {
				for (T d : data) {
					if (d != null) {
						writer.write(d, _writer);
					}
				}
			}
		};
	}

	/* *************************************************************************
	 *  XML writer methods.
	 * ************************************************************************/

	/**
	 * Writes the element with the given name and its children.
	 *
	 * @param name the element name
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(final String name, final ElementWriter... children)
		throws XMLStreamException
	{
		write(name, emptyList(), asList(children));
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attr the element attribute
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(final String name, final Attr attr, final ElementWriter... children)
		throws XMLStreamException
	{
		write(name, singletonList(attr), asList(children));
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attr1 the first element attribute
	 * @param attr2 the second element attribute
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(
		final String name,
		final Attr attr1,
		final Attr attr2,
		final ElementWriter... children
	)
		throws XMLStreamException
	{
		write(name, asList(attr1, attr2), asList(children));
	}

	void write(
		final String name,
		final NS ns,
		final Attr attr1,
		final Attr attr2,
		final ElementWriter... children
	)
		throws XMLStreamException
	{
		write(name, ns, asList(attr1, attr2), asList(children));
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attr1 the first element attribute
	 * @param attr2 the second element attribute
	 * @param attr3 the third element attribute
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(
		final String name,
		final Attr attr1,
		final Attr attr2,
		final Attr attr3,
		final ElementWriter... children
	)
		throws XMLStreamException
	{
		write(name, asList(attr1, attr2, attr3), asList(children));
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attr1 the first element attribute
	 * @param attr2 the second element attribute
	 * @param attr3 the third element attribute
	 * @param attr4 the fourth element attribute
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	void write(
		final String name,
		final Attr attr1,
		final Attr attr2,
		final Attr attr3,
		final Attr attr4,
		final ElementWriter... children
	)
		throws XMLStreamException
	{
		write(name, asList(attr1, attr2, attr3, attr4), asList(children));
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attrs the element attributes
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private void write(
		final String name,
		final List<Attr> attrs,
		final List<ElementWriter> children
	)
		throws XMLStreamException
	{
		write(name, null, attrs, children);
	}

	/**
	 * Writes the element with the given name, attributes and its children.
	 *
	 * @param name the element name
	 * @param attrs the element attributes
	 * @param children the element children
	 * @throws XMLStreamException if an error occurs while writing
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	private void write(
		final String name,
		final NS ns,
		final List<Attr> attrs,
		final List<ElementWriter> children
	)
		throws XMLStreamException
	{
		requireNonNull(name);
		requireNonNull(attrs);
		requireNonNull(children);

		_writer.writeStartElement(name);
		for (Attr attr : attrs) {
			_writer.writeAttribute(attr.name, attr.value);
		}
		if (ns != null) {
			_writer.writeDefaultNamespace(ns.name);
		}

		for (ElementWriter child : children) {
			child.write();
		}
		_writer.writeEndElement();
	}

}
