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
package org.jenetics.xml.stream;

import static java.util.Objects.requireNonNull;

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Writers {

	private Writers() {
	}

	public static <T, P> Writer<T> attr(
		final String name,
		final Function<T, P> property
	) {
		requireNonNull(name);
		requireNonNull(property);

		return (data, writer) -> {
			if (data != null) {
				writer.writeAttribute(name, property.apply(data).toString());
			}
		};
	}

	public static <T, P> Writer<T> elem(
		final String name,
		final Function<T, P> property
	) {
		requireNonNull(name);

		return (data, writer) -> {
			if (data != null) {
				writer.writeStartElement(name);
				writer.writeCharacters(property.apply(data).toString());
				writer.writeEndElement();
			}
		};
	}

	public static <T, P> Writer<T> elems(
		final String name,
		final Function<T, ? extends Iterable<P>> properties
	) {
		requireNonNull(name);

		return (value, writer) -> {
			if (value != null) {
				final Iterable<P> data = properties.apply(value);
				for (P v : data) {
					writer.writeStartElement(name);
					writer.writeCharacters(v.toString());
					writer.writeEndElement();
				}
			}
		};
	}

	public static <T, P> Writer<T> elems(
		final Writer<P> children,
		final Function<T, ? extends Iterable<? extends P>> properties
	) {
		return (value, writer) -> {
			final Iterable<? extends P> data = properties.apply(value);
			for (P v : data) {
				children.write(v, writer);
			}
		};
	}

	@SafeVarargs
	public static <T> Writer<T> elem(
		final String name,
		final Writer<? super T>... children
	) {
		return (data, writer) -> {
			writer.writeStartElement(name);
			for (Writer<? super T> child : children) {
				child.write(data, writer);
			}
			writer.writeEndElement();
		};
	}




	public static XMLStreamWriter newXMLStreamWriter(
		final OutputStream out,
		final String indent
	)
		throws XMLStreamException
	{
		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		return indent != null
			? new IndentingXMLWriter(factory.createXMLStreamWriter(out), indent)
			: factory.createXMLStreamWriter(out);
	}

	public static XMLStreamWriter newXMLStreamWriter(final OutputStream out)
		throws XMLStreamException
	{
		return newXMLStreamWriter(out, null);
	}

	public static <T> void write(
		final T value,
		final Writer<T> writer,
		final OutputStream out,
		final String indent
	)
		throws XMLStreamException
	{
		try (AutoCloseableAdapter adapter =
				 new AutoCloseableAdapter(newXMLStreamWriter(out, indent)))
		{
			adapter.writer().writeStartDocument("UTF-8", "1.0");
			writer.write(value, adapter.writer());
			adapter.writer().writeEndDocument();
		}
	}


	public static <T> void write(final T value, final Writer<T> writer, final OutputStream out)
		throws XMLStreamException
	{
		write(value, writer, out, null);
	}



}
