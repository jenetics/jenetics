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

import static java.lang.Double.NaN;
import static java.lang.String.format;

import java.io.IOException;
import java.util.stream.DoubleStream;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleChromosomeAdapter extends TypeAdapter<DoubleChromosome> {

	@Override
	public void write(final JsonWriter out, final DoubleChromosome value)
		throws IOException
	{
		out.beginObject();
		out.name("length").value(value.length());
		out.name("min").value(value.getMin());
		out.name("max").value(value.getMax());
		out.name("alleles");

		out.beginArray();
		for (DoubleGene gene : value) {
			out.value(gene.getAllele());
		}
		out.endArray();

		out.endObject();
	}

	@Override
	public DoubleChromosome read(final JsonReader in) throws IOException {
		int length = -1;
		double min = NaN, max = NaN;
		final DoubleStream.Builder alleles = DoubleStream.builder();

		in.beginObject();
		while (in.hasNext()) {
			switch (in.nextName()) {
				case "length": length = in.nextInt(); break;
				case "min": min = in.nextDouble(); break;
				case "max": max = in.nextDouble(); break;
				case "alleles":
					in.beginArray();
					while (in.hasNext()) {
						alleles.accept(in.nextDouble());
					}
					in.endArray();
					break;
			}
		}
		in.endObject();

		final double fmin = min;
		final double fmax = max;
		final DoubleGene[] genes = alleles.build()
			.mapToObj(a -> DoubleGene.of(a, fmin, fmax))
			.toArray(DoubleGene[]::new);

		if (genes.length != length) {
			throw new IOException(format(
				"Expected %d genes but got %d.", length, genes.length
			));
		}

		return DoubleChromosome.of(genes);
	}
}
