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

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jenetics.DoubleGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleGeneAdapter extends TypeAdapter<DoubleGene> {

	@Override
	public void write(final JsonWriter out, final DoubleGene value)
		throws IOException
	{
		out.beginObject();
		out.name("value").value(value.getAllele());
		out.name("min").value(value.getMin());
		out.name("max").value(value.getMax());
		out.endObject();
	}

	@Override
	public DoubleGene read(final JsonReader in) throws IOException {
		double value = NaN, min = NaN, max = NaN;

		in.beginObject();
		while (in.hasNext()) {
			switch (in.nextName()) {
				case "value": value = in.nextDouble(); break;
				case "min": min = in.nextDouble(); break;
				case "max": max = in.nextDouble(); break;
			}
		}
		in.endObject();

		return DoubleGene.of(value, min, max);
	}

}
