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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.xml.stream;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * This class contains helper methods for creating
 * {@link javax.xml.stream.XMLStreamReader} and
 * {@link javax.xml.stream.XMLStreamWriter} objects.
 * <p>
 * Creating a new XML stream reader:
 * {@snippet lang="java":
 * try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
 *     // Move XML stream to first element.
 *     xml.next();
 *     return reader.read(xml);
 * }
 * }
 *
 * Create a new XML stream reader:
 * {@snippet lang="java":
 * try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
 *     writer.write(value, xml);
 * }
 * }
 *
 * Create a new XML stream reader with pretty-print-indentation:
 * {@snippet lang="java":
 * final String indent = "    ";
 * try (AutoCloseableXMLStreamWriter xml = XML.writer(out, indent)) {
 *     writer.write(value, xml);
 * }
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.9
 * @since 3.9
 */
public final class XML {
	private XML() {}

	/**
	 * Create a new XML stream reader from the given {@code input} stream.
	 * <em>
	 * The caller is responsible for closing the returned {@code XMLStreamReader}.
	 * </em>
	 *
	 * {@snippet lang="java":
	 * try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
	 *     // Move XML stream to first element.
	 *     xml.next();
	 *     return reader.read(xml);
	 * }
	 * }
	 *
	 * @param input the input stream
	 * @return a new {@code Closeable} XML stream reader
	 * @throws XMLStreamException if the creation of the XML stream reader fails
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static AutoCloseableXMLStreamReader reader(final InputStream input)
		throws XMLStreamException
	{
		requireNonNull(input);

		//final XMLInputFactory factory = XMLInputFactory
		//	.newFactory(XMLInputFactory.class.getName(), null);
		final XMLInputFactory factory = XMLInputFactory.newFactory();
		return new XMLReaderProxy(
			factory.createXMLStreamReader(input, "UTF-8"));
	}

	/**
	 * Create a new {@code XMLStreamWriter} from the given output stream.
	 * <em>
	 * The caller is responsible for closing the returned {@code XMLStreamWriter}.
	 * </em>
	 *
	 * {@snippet lang="java":
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(out, "    ")) {
	 *     writer.write(value, xml);
	 * }
	 * }
	 *
	 * @param output the underlying output stream
	 * @param indent the element indent used for the XML output
	 * @return a new {@code XMLStreamWriter} instance
	 * @throws XMLStreamException if an error occurs while creating the XML
	 *         stream writer
	 * @throws NullPointerException if the given {@code output} stream is
	 *         {@code null}
	 */
	public static AutoCloseableXMLStreamWriter writer(
		final OutputStream output,
		final String indent
	)
		throws XMLStreamException
	{
		requireNonNull(output);

		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		return indent != null
			? new IndentingXMLWriter(
				factory.createXMLStreamWriter(output, "UTF-8"), indent)
			: new XMLWriterProxy(
				factory.createXMLStreamWriter(output, "UTF-8"));
	}

	/**
	 * Create a new {@code XMLStreamWriter} from the given output stream.
	 * <em>
	 * The caller is responsible for closing the returned {@code XMLStreamWriter}.
	 * </em>
	 *
	 * {@snippet lang="java":
	 * try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
	 *     writer.write(value, xml);
	 * }
	 * }
	 *
	 * @param output the underlying output stream
	 * @return a new {@code XMLStreamWriter} instance
	 * @throws XMLStreamException if an error occurs while creating the XML
	 *         stream writer
	 * @throws NullPointerException if the given {@code output} stream is
	 *         {@code null}
	 */
	public static AutoCloseableXMLStreamWriter writer(final OutputStream output)
		throws XMLStreamException
	{
		return writer(output, null);
	}

}
