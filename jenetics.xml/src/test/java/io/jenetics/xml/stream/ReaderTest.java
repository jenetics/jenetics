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

import static io.jenetics.xml.stream.Reader.attr;
import static io.jenetics.xml.stream.Reader.elem;
import static io.jenetics.xml.stream.Reader.elems;
import static io.jenetics.xml.stream.Reader.text;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ReaderTest {

	static Object fromBytes(final byte[] bytes, final Reader<?> reader)
		throws XMLStreamException
	{
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try (AutoCloseableXMLStreamReader xml = XML.reader(in)) {
			xml.next();
			return reader.read(xml);
		}
	}

	@Test(dataProvider = "readerExamples")
	public void reader(
		final String xml,
		final Reader<?> reader,
		final Object expected
	)
		throws Exception
	{
		Assert.assertEquals(fromBytes(xml.getBytes(), reader), expected);
	}

	@DataProvider(name = "readerExamples")
	public Object[][] readerExamples() {
		return new Object[][] {
			{
				"<element length=\"123\"/>",
				elem(
					v -> v[0],
					"element",
					attr("length").map(Integer::parseInt)
				),
				123
			},
			{
				"<element>123</element>",
				elem(
					v -> v[0],
					"element",
					text().map(Integer::parseInt)
				),
				123
			},
			{
				"<min><element>123</element></min>",
				elem("min",
					elem(
						v -> v[0],
						"element",
						text().map(Integer::parseInt)
					)
				),
				123
			},
			{
				"<properties length=\"3\">\n" +
				"    <property>1</property>   \n" +
				"    <property>2</property> \n" +
				"    <property>3</property>\n" +
				"</properties>",
				elem(
					v -> v[0],
					"properties",
					elems(elem("property", text().map(Integer::parseInt)))
				),
				Arrays.asList(1, 2, 3)
			}
		};
	}

}
