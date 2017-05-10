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
package org.jenetics.json.stream;

import static org.jenetics.json.stream.Writer.array;
import static org.jenetics.json.stream.Writer.number;
import static org.jenetics.json.stream.Writer.obj;
import static org.jenetics.json.stream.Writer.text;

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.gson.stream.JsonWriter;

import org.testng.annotations.Test;

import org.jenetics.IntegerChromosome;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class WriterTest {

	@Test
	public void write() throws IOException {
		final Writer<IntegerChromosome> writer = obj(
			text("name", "int-chromosome"),
			number("min").map(IntegerChromosome::getMin),
			number("max").map(IntegerChromosome::getMax),
			array("alleles", number())
				.map(ch -> ch.toSeq().map(g -> g.getAllele()))
		);

		final IntegerChromosome chromosome = IntegerChromosome.of(0, 10, 10);

		final JsonWriter json = new JsonWriter(new OutputStreamWriter(System.out));
		writer.write(json, chromosome);
		json.close();
	}

}
