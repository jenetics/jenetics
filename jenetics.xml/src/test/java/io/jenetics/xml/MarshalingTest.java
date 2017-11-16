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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.xml.stream.AutoCloseableXMLStreamReader;
import io.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class MarshalingTest {
	private static final String RESOURCE_PATTERN = "/serialization/%s.xml";

	@Test(dataProvider = "persistentObjectMarshallings")
	public void marshallingCompatibility(final PersistentObject<?> object)
		throws IOException, XMLStreamException
	{
		final String resource = String.format(RESOURCE_PATTERN, object.getName());

		try (InputStream in = getClass().getResourceAsStream(resource);
			 AutoCloseableXMLStreamReader xml = XML.reader(in))
		{
			xml.next();
			final Object o = object.getReader().read(xml);
			Assert.assertEquals(o, object.getValue());
		}
	}

	@DataProvider(name = "persistentObjectMarshallings")
	public Object[][] getPersistentObjects() {
		final Object[][] result = new Object[PersistentObject.VALUES.size()][1];
		for (int i = 0; i < PersistentObject.VALUES.size(); ++i) {
			result[i][0] = PersistentObject.VALUES.get(i);
			System.out.println(PersistentObject.VALUES.get(i).getName());
		}

		return result;
	}
}
