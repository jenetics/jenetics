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
package io.jenetics.xml;

import static java.util.Collections.emptyList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.BitChromosome;
import io.jenetics.CharacterChromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.LongChromosome;
import io.jenetics.PermutationChromosome;
import io.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import io.jenetics.xml.stream.AutoCloseableXMLStreamWriter;
import io.jenetics.xml.stream.Reader;
import io.jenetics.xml.stream.Writer;
import io.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class WritersReadersTests {

	static <T> byte[] toBytes(final T value, final Writer<T> writer)
		throws XMLStreamException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (AutoCloseableXMLStreamWriter xml = XML.writer(out)) {
			writer.write(xml, value);
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
		//System.out.println(new String(bytes));
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
				CharacterChromosome.of(5),
				Writers.CharacterChromosome.writer(),
				Readers.CharacterChromosome.reader()
			},
			{
				IntegerChromosome.of(0, 1_000_000, 20),
				Writers.IntegerChromosome.writer(),
				Readers.IntegerChromosome.reader()
			},
			{
				LongChromosome.of(0, 1_000_000, 20),
				Writers.LongChromosome.writer(),
				Readers.LongChromosome.reader()
			},
			{
				DoubleChromosome.of(0, 1_000_000, 20),
				Writers.DoubleChromosome.writer(),
				Readers.DoubleChromosome.reader()
			},
			{
				PermutationChromosome.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
				Writers.PermutationChromosome.writer(Writers.IntegerChromosome.alleleWriter()),
				Readers.PermutationChromosome.reader(Readers.IntegerChromosome.alleleReader())
			},
			{
				Genotype.of(DoubleChromosome.of(0, 1, 2), 20),
				Writers.Genotype.writer(Writers.DoubleChromosome.writer()),
				Readers.Genotype.reader(Readers.DoubleChromosome.reader())
			},
			{
				Genotype.of(DoubleChromosome.of(0, 1, 10), 10)
					.instances()
					.limit(20)
					.toList(),
				Writers.Genotypes.writer(Writers.DoubleChromosome.writer()),
				Readers.Genotypes.reader(Readers.DoubleChromosome.reader())
			},
			{
				emptyList(),
				Writers.Genotypes.writer(Writers.DoubleChromosome.writer()),
				Readers.Genotypes.reader(Readers.DoubleChromosome.reader())
			}
		};
	}

}
