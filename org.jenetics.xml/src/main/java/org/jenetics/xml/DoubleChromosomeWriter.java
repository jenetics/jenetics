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

import org.jenetics.BoundedChromosome;
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.NumericChromosome;
import org.jenetics.NumericGene;
import org.jenetics.xml.stream.Reader;
import org.jenetics.xml.stream.Writer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class DoubleChromosomeWriter {

	public static final Writer<Chromosome<DoubleGene>> WRITER = null;

		/*
		elem("double-chromosome",
			attr("min", DoubleChromosome::getMin),
			attr("max", DoubleChromosome::getMax),
			attr("length", DoubleChromosome::length),
			elems("allele", ch -> ch.toSeq().map(DoubleGene::getAllele))
		);
		*/

	public static
	<N extends Number & Comparable<? super N>, G extends NumericGene<N, G>>
	Writer<NumericChromosome<N, G>> writer(final String name) {
		return elem(name,
			attr("min", NumericChromosome::getMin),
			attr("max", NumericChromosome::getMax),
			attr("length", NumericChromosome::length),
			elems("allele", ch -> ch.toSeq().map(Gene<N, G>::getAllele))
		);
	}

	public static final Reader<DoubleChromosome> READER =
		Reader.of(
			values -> {
				final double min = Double.parseDouble((String)values[0]);
				final double max = Double.parseDouble((String)values[1]);

				return DoubleChromosome.of(
					((List<String>)values[3]).stream()
						.map(Double::parseDouble)
						.map(a -> DoubleGene.of(a, min, max))
						.toArray(DoubleGene[]::new)
				);
			},
			"double-chromosome",
			attrs("min", "max", "length"),
			Reader.ofList(Reader.of("allele"))
		);


	public static final Writer<Genotype<DoubleGene>> GT_WRITER =
		gt(WRITER);

		/*
		elem("genotype",
			attr("length", Genotype::length),
			attr("ngenes", Genotype::getNumberOfGenes),
			elems(writer("double-chromosome"), gt -> gt.toSeq().map(ch -> ch.as(DoubleChromosome.class)))
		);
		*/

	public static <G extends Gene<?, G>> Writer<Genotype<G>>
	gt(final Writer<? super Chromosome<G>> writer) {
		return elem("genotype",
			attr("length", Genotype::length),
			attr("ngenes", Genotype::getNumberOfGenes),
			elems(writer, Genotype::toSeq)
		);
	}

	public static void main(final String[] args) throws Exception {
		final DoubleChromosome ch = DoubleChromosome.of(0, 1, 10);

		final Genotype<DoubleGene> gt = Genotype.of(ch, 5);

		GT_WRITER.write(gt, System.out);
		System.out.flush();

		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		WRITER.write(ch, System.out);
		System.out.flush();
		WRITER.write(ch, out);

		final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		final DoubleChromosome dch = READER.read(in);
		System.out.println(dch);
	}
}
