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

import java.io.IOException;
import java.io.OutputStreamWriter;

import com.google.gson.stream.JsonWriter;

import org.testng.annotations.Test;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Genotype;
import org.jenetics.json.Writers;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class WriterTest {

	@Test
	public void write() throws IOException {
		final Genotype<DoubleGene> genotype = Genotype.of(
			DoubleChromosome.of(0.0, 1.0, 2),
			2
		);

		final JsonWriter json = new JsonWriter(new OutputStreamWriter(System.out));
		Writers.Genotype.writer(Writers.DoubleChromosome.writer())
			.write(json, genotype);

		json.close();
	}

}
