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
package org.jenetix.json;

import java.text.DateFormat;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleChromosomeAdapterTest {
	private final static Gson GSON = new GsonBuilder()
		.registerTypeAdapter(DoubleChromosome.class, new DoubleChromosomeAdapter())
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setDateFormat(DateFormat.LONG)
		.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
		.setPrettyPrinting()
		.setVersion(1.0)
		.create();


	@Test
	public void marshalling() {
		final DoubleChromosome expected = DoubleChromosome.of(0.0, 1000.0, 10);

		final String json = GSON.toJson(expected);
		System.out.println(json);

		final DoubleChromosome actual = GSON.fromJson(json, DoubleChromosome.class);
		Assert.assertEquals(actual, expected);
	}

}
