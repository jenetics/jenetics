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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.BitChromosome;
import org.jenetics.CharacterChromosome;
import org.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import org.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class WritersReadersTests {

	static <T> byte[] toBytes(final T value, final Writer<T> writer)
		throws XMLStreamException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
			writer.write(value, xml);
		}

		return out.toByteArray();
	}

	static <T> T fromBytes(final byte[] bytes, final Reader<T> reader)
		throws XMLStreamException
	{
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
			xml.next();
			return reader.read(xml);
		}
	}

	@Test(dataProvider = "marshallings")
	public <T> void marshalling(
		final T expected,
		final Writer<T> writer,
		final Reader<T> reader
	)
		throws XMLStreamException
	{
		final byte[] bytes = toBytes(expected, writer);
		final T actual = fromBytes(bytes, reader);

		Assert.assertEquals(actual, expected);
	}

	@DataProvider
	public Object[][] marshallings() {
		return new Object[][] {
			{
				BitChromosome.of(10),
				Writers.BitChromosome.writer(),
				Readers.BitChromosome.reader()
			},
			{
				CharacterChromosome.of(15),
				Writers.CharacterChromosome.writer(),
				Readers.CharacterChromosome.reader()
			}
		};
	}

}
