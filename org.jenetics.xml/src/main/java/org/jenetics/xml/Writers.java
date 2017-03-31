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

import static org.jenetics.xml.stream.Writer.attr;
import static org.jenetics.xml.stream.Writer.elem;
import static org.jenetics.xml.stream.Writer.elems;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jenetics.BitChromosome;
import org.jenetics.BoundedChromosome;
import org.jenetics.BoundedGene;
import org.jenetics.CharacterChromosome;
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.LongChromosome;
import org.jenetics.xml.stream.Writer;
import org.jenetics.xml.stream.XML;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Writers {

	/**
	 * Writer for bit-chromosomes.
	 * <p>
	 * {@code
	 * <bit-chromosome length="20" ones-probability="0.5">11100011101011001010</bit-chromosome>
	 * }
	 */
	public static final Writer<BitChromosome> BIT_CHROMOSOME_WRITER =
		elem("bit-chromosome",
			attr("length", BitChromosome::length),
			attr("ones-probability", BitChromosome::getOneProbability),
			elem(BitChromosome::toCanonicalString)
		);

	/**
	 * Writer for character-chromosomes.
	 * <pre> {@code
	 * <character-chromosome length="4">
	 *     <valid-alleles>ABCDEFGHIJKLMNOPQRSTUVWXYZ<valid-alleles>
	 *     <alleles>ASDF</alleles>
	 * </character-chromosome>
	 * }</pre>
	 */
	public static final Writer<CharacterChromosome> CHARACTER_CHROMOSOME_WRITER =
		elem("character-chromosome",
			attr("length", CharacterChromosome::length),
			elem("valid-alleles", ch -> ch.getGene().getValidCharacters()),
			elem("alleles", CharacterChromosome::toString)
		);

	/**
	 * Writer for int-chromosomes.
	 * <pre> {@code
	 * <int-chromosome length="3" min="-2147483648" max="2147483647">
	 *     <allele>-1878762439</allele>
	 *     <allele>-957346595</allele>
	 *     <allele>-88668137</allele>
	 * </int-chromosome>
	 * }</pre>
	 */
	public static final Writer<IntegerChromosome>
		INTEGER_CHROMOSOME_WRITER = boundedChromosome("int-chromosome");

	/**
	 * Writer for long-chromosomes.
	 * <pre> {@code
	 * <long-chromosome length="3" min="-9223372036854775808" max="9223372036854775807">
	 *     <allele>-1345217698116542402</allele>
	 *     <allele>-7144755673073475303</allele>
	 *     <allele>6053786736809578435</allele>
	 * </long-chromosome>
	 * }</pre>
	 */
	public static final Writer<LongChromosome>
		LONG_CHROMOSOME_WRITER = boundedChromosome("long-chromosome");

	/**
	 * Writer for double-chromosomes.
	 * <pre> {@code
	 * <double-chromosome length="3" min="0.0" max="1.0">
	 *     <allele>0.27251556008507416</allele>
	 *     <allele>0.003140816229067145</allele>
	 *     <allele>0.43947528327497376</allele>
	 * </double-chromosome>
	 * }</pre>
	 */
	public static final Writer<DoubleChromosome>
		DOUBLE_CHROMOSOME_WRITER = boundedChromosome("double-chromosome");

	private Writers() {
	}

	private static <
		A extends Comparable<? super A>,
		G extends BoundedGene<A, G>,
		C extends BoundedChromosome<A, G>
	>
	Writer<C> boundedChromosome(final String root) {
		return elem(root,
			attr("min", BoundedChromosome<A, G>::getMin),
			attr("max", BoundedChromosome<A, G>::getMax),
			attr("length", BoundedChromosome<A, G>::length),
			elems("allele", ch -> ch.toSeq().map(G::getAllele))
		);
	}

	/**
	 * Writer for genotypes of arbitrary chromosomes. How to write the genotypes
	 * chromosomes is defined by the given {@link Writer}. The following writer
	 * allows to write double-gene chromosomes:
	 * <pre>{@code
	 * final Writer<Genotype<DoubleGene>> writer = genotype(DOUBLE_CHROMOSOME_WRITER);
	 * }</pre>
	 *
	 * Example output:
	 * <pre> {@code
	 * <genotype length="2" ngenes="5">
	 *     <double-chromosome min="0.0" max="1.0" length="3">
	 *         <allele>0.27251556008507416</allele>
	 *         <allele>0.003140816229067145</allele>
	 *         <allele>0.43947528327497376</allele>
	 *     </double-chromosome>
	 *     <double-chromosome min="0.0" max="1.0" length="3">
	 *         <allele>0.18390258154466066</allele>
	 *         <allele>0.4026521545744768</allele>
	 *         <allele>0.36137605952663554</allele>
	 *     </double-chromosome>
	 * </genotype>
	 * }</pre>
	 *
	 * @param writer the chromosome writer
	 * @param <A> the allele type
	 * @param <G> the gene type
	 * @param <C> the chromosome type
	 * @return a new genotype writer
	 * @throws NullPointerException if the given chromosome {@code writer} is
	 *         {@code null}
	 */
	public static <
		A ,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	Writer<Genotype<G>> genotype(final Writer<C> writer) {
		return elem("genotype",
			attr("length", Genotype<G>::length),
			attr("ngenes", Genotype<G>::getNumberOfGenes),
			elems(gt -> gt.toSeq().map(Writers::cast), writer)
		);
	}

	/**
	 * Writer for genotypes of arbitrary chromosomes. How to write the genotypes
	 * chromosomes is defined by the given {@link Writer}. The following writer
	 * allows to write double-gene chromosomes:
	 * <pre>{@code
	 * final Writer<Genotype<DoubleGene>> writer = genotype(DOUBLE_CHROMOSOME_WRITER);
	 * }</pre>
	 *
	 * Example output:
	 * <pre> {@code
	 * <genotypes>
	 *     <genotype length="2" ngenes="5">
	 *         <double-chromosome min="0.0" max="1.0" length="3">
	 *             <allele>0.27251556008507416</allele>
	 *             <allele>0.003140816229067145</allele>
	 *             <allele>0.43947528327497376</allele>
	 *         </double-chromosome>
	 *         <double-chromosome min="0.0" max="1.0" length="3">
	 *             <allele>0.18390258154466066</allele>
	 *             <allele>0.4026521545744768</allele>
	 *             <allele>0.36137605952663554</allele>
	 *         </double-chromosome>
	 *     </genotype>
	 * </genotypes>
	 * }</pre>
	 *
	 * @param writer the chromosome writer
	 * @param <A> the allele type
	 * @param <G> the gene type
	 * @param <C> the chromosome type
	 * @return a new genotype writer
	 * @throws NullPointerException if the given chromosome {@code writer} is
	 *         {@code null}
	 */
	public static <
		A ,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	Writer<Collection<Genotype<G>>> genotypes(final Writer<C> writer) {
		return elems("genotypes", genotype(writer));
	}

	@SuppressWarnings("unchecked")
	private static <A, B> B cast(final A value) {
		return (B)value;
	}

	public static void main(final String[] args) throws Exception {
		final DoubleChromosome ch = DoubleChromosome.of(0, 1, 10);
		final Genotype<DoubleGene> gt = Genotype.of(ch, 5);

		//final Writer<Genotype<DoubleGene>> writer = genotype(DOUBLE_CHROMOSOME);
		//writer.write(gt, XML.writer(System.out, "    "));
		//System.out.flush();

		final List<Genotype<DoubleGene>> types = Stream
			.generate(gt::newInstance)
			.limit(10)
			.collect(Collectors.toList());

		final Writer<Collection<Genotype<DoubleGene>>> writers = genotypes(DOUBLE_CHROMOSOME_WRITER);
		writers.write(types, XML.writer(System.out, "    "));
		System.out.flush();
	}
}
