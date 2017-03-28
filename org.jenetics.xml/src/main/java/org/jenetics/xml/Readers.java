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

import static java.lang.String.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Readers {

	public static final Reader<DoubleChromosome> DOUBLE_CHROMOSOME =
		Reader.of(
			p -> {
				final double min = Double.parseDouble((String)p[0]);
				final double max = Double.parseDouble((String)p[1]);
				final int length = Integer.parseInt((String)p[2]);

				@SuppressWarnings("unchecked")
				final List<String> alleles = (List<String>)p[3];
				if (alleles.size() != length) {
					throw new IllegalArgumentException(format(
						"Expected %d alleles, but got %d,",
						length, alleles.size()
					));
				}

				return DoubleChromosome.of(
					alleles.stream()
						.map(s -> DoubleGene.of(Double.parseDouble(s), min, max))
						.toArray(DoubleGene[]::new)
				);
			},
			"double-chromosome",
			Reader.attrs("min", "max", "length"),
			Reader.ofList(Reader.of("allele"))
		);

	private Readers() {
	}

	public static void main(final String[] args) throws Exception {
		final DoubleChromosome ch = DoubleChromosome.of(0, 1, 10);
		System.out.println(ch);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final XMLStreamWriter writer = XML.writer(out, "    ");
		Writer.doc(Writers.DOUBLE_CHROMOSOME).write(ch, writer);
		writer.flush();

		Writer.doc(Writers.DOUBLE_CHROMOSOME).write(ch, XML.writer(System.out, "    "));
		System.out.flush();
		System.out.println();

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final XMLStreamReader reader = XML.reader(in);
		reader.next();
		final DoubleChromosome dch = DOUBLE_CHROMOSOME.read(reader);
		System.out.println(dch);
	}

}
