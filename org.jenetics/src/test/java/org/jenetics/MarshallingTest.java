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
package org.jenetics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.PersistentObject.Marshalling;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class MarshallingTest {

	private static final String RESOURCE_PATTERN = "/org/jenetics/serialization/%s.%s";

	@Test(dataProvider = "persistentObjectMarshallings")
	public void marshallingCompatibility(
		final PersistentObject<?> object,
		final Marshalling marshalling
	)
		throws IOException
	{
		final String resource = String.format(
			RESOURCE_PATTERN, object.getName(), marshalling.name
		);

		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final Object o = marshalling.io.read(in);

			Assert.assertEquals(o, object.getValue());
		}
	}

	@DataProvider(name = "persistentObjectMarshallings")
	public Object[][] getPersistentObjects() {
		final List<Object[]> combinations = new ArrayList<>();
		for (PersistentObject<?> po : PersistentObject.VALUES) {
			for (Marshalling m : po.getMarshallings()) {
				combinations.add(new Object[]{po, m});
			}
		}

		final Object[][] result = new Object[combinations.size()][];
		for (int i = 0; i < result.length; ++i) {
			result[i] = combinations.get(i);
		}

		return result;
	}
}
