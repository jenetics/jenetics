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

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class XML {

	private XML() {
	}


	public static XMLStreamReader reader(final InputStream input)
		throws XMLStreamException
	{
		requireNonNull(input);

		final XMLInputFactory factory = XMLInputFactory.newFactory();
		return factory.createXMLStreamReader(input);
	}

	/**
	 * Create a new {@code XMLStreamWriter} from the given output stream.
	 * <em>
	 * The caller is responsible for closing the returned {@code XMLStreamWriter}
	 * <b>and</b> the given output stream.
	 * </em>
	 *
	 * @param out the underlying output stream
	 * @param indent the element indent used for the XML output
	 * @return a new {@code XMLStreamWriter} instance
	 * @throws XMLStreamException if an error occurs while creating the XML
	 *         stream writer
	 */
	public static AutoClosableXMLStreamWriter writer(
		final OutputStream out,
		final String indent
	)
		throws XMLStreamException
	{
		requireNonNull(out);

		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		return indent != null
			? new IndentingXMLWriter(factory.createXMLStreamWriter(out), indent)
			: new XMLWriterProxy(factory.createXMLStreamWriter(out));
	}

	/**
	 * Create a new {@code XMLStreamWriter} from the given output stream.
	 * <em>
	 * The caller is responsible for closing the returned {@code XMLStreamWriter}
	 * <b>and</b> the given output stream.
	 * </em>
	 *
	 * @param out the underlying output stream
	 * @return a new {@code XMLStreamWriter} instance
	 * @throws XMLStreamException if an error occurs while creating the XML
	 *         stream writer
	 */
	public static AutoClosableXMLStreamWriter writer(final OutputStream out)
		throws XMLStreamException
	{
		return writer(out, null);
	}

}
