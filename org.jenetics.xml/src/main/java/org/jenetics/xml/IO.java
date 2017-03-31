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

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.jenetics.DoubleChromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.LongChromosome;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class IO<T> {

	public static final IO<IntegerChromosome> INTEGER_CHROMOSOME = of(
		Readers.INTEGER_CHROMOSOME,
		Writers.INTEGER_CHROMOSOME_WRITER
	);

	public static final IO<LongChromosome> LONG_CHROMOSOME = of(
		Readers.LONG_CHROMOSOME,
		Writers.LONG_CHROMOSOME_WRITER
	);

	public static final IO<DoubleChromosome> DOUBLE_CHROMOSOME = of(
		Readers.DOUBLE_CHROMOSOME,
		Writers.DOUBLE_CHROMOSOME_WRITER
	);

	private final Reader<T> _reader;
	private final Writer<T> _writer;

	private IO(final Reader<T> reader, final Writer<T> writer) {
		_reader = requireNonNull(reader);
		_writer = requireNonNull(writer);
	}

	public Reader<T> reader() {
		return _reader;
	}

	public Writer<T> writer() {
		return _writer;
	}

	public void write(final T value, final OutputStream out, final String indent)
		throws XMLStreamException
	{
		try (AutoCloseableXMLStreamWriter writer = XML.writer(out, indent)) {
			Writer.doc(_writer).write(value, writer);
		}
	}

	public T read(final InputStream in) throws XMLStreamException {
		try (AutoCloseableXMLStreamReader reader = XML.reader(in)) {
			if (reader.hasNext()) {
				reader.next();
				return _reader.read(reader);
			} else {
				throw new XMLStreamException("Couldn't read root element.");
			}
		}
	}

	public static <T> IO<T> of(final Reader<T> reader, final Writer<T> writer) {
		return new IO<>(reader, writer);
	}

}
