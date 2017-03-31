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
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.IntegerChromosome;
import org.jenetics.IntegerGene;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Readers {

	@FunctionalInterface
	private static interface GeneCreator<A, G> {
		G create(final A value, final A min, final A max);
	}

	@FunctionalInterface
	private static interface ChromosomeCreator<G, C> {
		C create(final G[] genes);
	}

	public static final Reader<IntegerChromosome> INTEGER_CHROMOSOME = chromosome(
		"integer-chromosome",
		Integer::parseInt,
		IntegerGene::of,
		IntegerGene[]::new,
		IntegerChromosome::of
	);

	public static final Reader<LongChromosome> LONG_CHROMOSOME = chromosome(
		"long-chromosome",
		Long::parseLong,
		LongGene::of,
		LongGene[]::new,
		LongChromosome::of
	);

	public static final Reader<DoubleChromosome> DOUBLE_CHROMOSOME = chromosome(
		"double-chromosome",
		Double::parseDouble,
		DoubleGene::of,
		DoubleGene[]::new,
		DoubleChromosome::of
	);

	private Readers() {
	}

	private static <A, G, C> Reader<C> chromosome(
		final String name,
		final Function<String, A> allele,
		final GeneCreator<A, G> gene,
		final IntFunction<G[]> genes,
		final ChromosomeCreator<G, C> chromosome
	) {
		requireNonNull(name);
		requireNonNull(allele);
		requireNonNull(gene);
		requireNonNull(genes);
		requireNonNull(chromosome);

		return Reader.of(
			p -> {
				final A min = allele.apply((String)p[0]);
				final A max = allele.apply((String)p[1]);
				final int length = Integer.parseInt((String)p[2]);

				@SuppressWarnings("unchecked")
				final List<A> alleles = (List<A>)p[3];
				if (alleles.size() != length) {
					throw new IllegalArgumentException(format(
						"Expected %d alleles, but got %d,",
						length, alleles.size()
					));
				}

				return chromosome.create(
					alleles.stream()
						.map(value -> gene.create(value, min, max))
						.toArray(genes)
				);
			},
			name,
			Reader.attrs("min", "max", "length"),
			Reader.ofList(Reader.of("allele").map(allele))
		);
	}


	public static void main(final String[] args) throws Exception {
		final DoubleChromosome ch = DoubleChromosome.of(0, 1, 10);
		System.out.println(ch);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		final XMLStreamWriter writer = XML.writer(out, "    ");
		Writer.doc(Writers.DOUBLE_CHROMOSOME_WRITER).write(ch, writer);
		writer.flush();

		Writer.doc(Writers.DOUBLE_CHROMOSOME_WRITER).write(ch, XML.writer(System.out, "    "));
		System.out.flush();
		System.out.println();

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final XMLStreamReader reader = XML.reader(in);
		reader.next();
		final DoubleChromosome dch = DOUBLE_CHROMOSOME.read(reader);
		System.out.println(dch);
	}

}
