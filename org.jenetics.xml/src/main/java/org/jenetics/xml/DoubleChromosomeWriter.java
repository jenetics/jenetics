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

import static org.jenetics.xml.stream.Reader.attrs;
import static org.jenetics.xml.stream.Writer.attr;
import static org.jenetics.xml.stream.Writer.elem;
import static org.jenetics.xml.stream.Writer.elems;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.xml.stream.Function;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleChromosomeWriter {

	public static final Writer<DoubleChromosome> WRITER =
		elem("double-chromosome",
			attr("min", DoubleChromosome::getMin),
			attr("max", DoubleChromosome::getMax),
			attr("length", DoubleChromosome::length),
			elems("allele", ch -> ch.toSeq().map(DoubleGene::getAllele))
		);


	private static final Function<Object[], DoubleChromosome> CREATOR = values -> {
		final double min = Double.parseDouble((String)values[0]);
		final double max = Double.parseDouble((String)values[1]);
		final int length = Integer.parseInt((String)values[2]);
		final List<String> objects = (List<String>)values[3];

		final DoubleGene[] genes = objects.stream()
			.map(Double::parseDouble)
			.map(a -> DoubleGene.of(a, min, max))
			.toArray(DoubleGene[]::new);

		return DoubleChromosome.of(genes);
	};

	public static final Reader<DoubleChromosome> READER =
		Reader.of(CREATOR, "double-chromosome",
			attrs("min", "max", "length"),
			Reader.ofList(Reader.of("allele"))
		);

	public static void main(final String[] args) throws Exception {
		final DoubleChromosome ch = DoubleChromosome.of(0, 1, 10);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		WRITER.write(ch, System.out);
		System.out.flush();
		WRITER.write(ch, out);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final DoubleChromosome dch = READER.read(in);
		System.out.println(dch);
	}
}
