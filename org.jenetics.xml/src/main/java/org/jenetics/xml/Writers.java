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
import static org.jenetics.xml.stream.Writer.text;

import java.util.Collection;
import java.util.stream.Collectors;

import org.jenetics.BitChromosome;
import org.jenetics.BoundedChromosome;
import org.jenetics.BoundedGene;
import org.jenetics.CharacterChromosome;
import org.jenetics.Chromosome;
import org.jenetics.DoubleChromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.IntegerChromosome;
import org.jenetics.LongChromosome;
import org.jenetics.PermutationChromosome;
import org.jenetics.xml.stream.Writer;

/**
 * This class contains static fields and methods, which create chromosome- and
 * genotype writers for different gene types. Some writer creation examples:
 *
 * <pre>{@code
 * final Writer<Genotype<LongGene>> lgw = genotypeWriter(LONG_CHROMOSOME_WRITER);
 * final Writer<Genotype<DoubleGene>> dgw = genotypeWriter(DOUBLE_CHROMOSOME_WRITER);
 *
 * final Writer<PermutationChromosome<Integer>> ipc = permutationChromosomeWriter();
 * final Writer<Genotype<EnumGene<Double>>> pgw = genotypeWriter(permutationChromosomeWriter());
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Writers {

	private Writers() {
	}

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
	 * Create a writer for permutation-chromosomes. How to write the valid
	 * alleles is defined by the given {@link Writer}. The following writer
	 * allows to write permutation-chromosomes with double allele types, where
	 * the double values are written with a fixed precision:
	 * <pre>{@code
	 * final Writer<PermutationChromosome<Double> writer =
	 *     permutationChromosomeWriter(Writer.text(d -> String.format("%1.4f", d)));
	 * }</pre>
	 *
	 * Example output:
	 * <pre> {@code
	 * <permutation-chromosome length="15">
	 *     <valid-alleles>
	 *         <allele>0.2725</allele>
	 *         <allele>0.0031</allele>
	 *         <allele>0.4395</allele>
	 *         <allele>0.1065</allele>
	 *         <allele>0.1970</allele>
	 *         <allele>0.7450</allele>
	 *         <allele>0.5594</allele>
	 *         <allele>0.0282</allele>
	 *         <allele>0.5741</allele>
	 *         <allele>0.4534</allele>
	 *         <allele>0.8111</allele>
	 *         <allele>0.5710</allele>
	 *         <allele>0.3017</allele>
	 *         <allele>0.5455</allele>
	 *         <allele>0.2107</allele>
	 *     </valid-alleles>
	 *     <order>13 12 4 6 8 14 7 2 11 5 3 0 9 10 1</order>
	 * </permutation-chromosome>
	 * }</pre>
	 *
	 * @param writer the allele writer
	 * @param <A> the allele type
	 * @return a new permutation chromosome writer
	 * @throws NullPointerException if the given allele {@code writer} is
	 *         {@code null}
	 */
	public static <A> Writer<PermutationChromosome<A>>
	permutationChromosomeWriter(final Writer<? super A> writer) {
		return elem("permutation-chromosome",
			attr("length", PermutationChromosome::length),
			elem("valid-alleles",
				elems(
					"allele",
					PermutationChromosome::getValidAlleles,
					writer
				)
			),
			elem(
				"order",
				ch -> ch.stream()
					.map(g -> Integer.toString(g.getAlleleIndex()))
					.collect(Collectors.joining(" ")))
		);
	}

	/**
	 * Create a writer for permutation-chromosomes. The valid alleles are
	 * serialized by calling the {@link Object#toString()} method. Calling this
	 * method is equivalent with:
	 * <pre>{@code
	 * final Writer<PermutationChromosome<Double> writer =
	 *     permutationChromosomeWriter(Writer.text(Objects::toString));
	 * }</pre>
	 *
	 * Example output:
	 * <pre> {@code
	 * <permutation-chromosome length="15">
	 *     <valid-alleles>
	 *         <allele>0.27251556008507416</allele>
	 *         <allele>0.003140816229067145</allele>
	 *         <allele>0.43947528327497376</allele>
	 *         <allele>0.10654807463069327</allele>
	 *         <allele>0.19696530915810317</allele>
	 *         <allele>0.7450003838065538</allele>
	 *         <allele>0.5594416969271359</allele>
	 *         <allele>0.02823782430152355</allele>
	 *         <allele>0.5741102315010789</allele>
	 *         <allele>0.4533651041367144</allele>
	 *         <allele>0.811148141800367</allele>
	 *         <allele>0.5710456351848858</allele>
	 *         <allele>0.30166768355230955</allele>
	 *         <allele>0.5455492865240272</allele>
	 *         <allele>0.21068427527733102</allele>
	 *     </valid-alleles>
	 *     <order>13 12 4 6 8 14 7 2 11 5 3 0 9 10 1</order>
	 * </permutation-chromosome>
	 * }</pre>
	 *
	 * @param <A> the allele type
	 * @return a new permutation chromosome writer
	 */
	public static <A> Writer<PermutationChromosome<A>> permutationChromosomeWriter() {
		return permutationChromosomeWriter(text());
	}

	/**
	 * Create a writer for genotypes of arbitrary chromosomes. How to write the
	 * genotypes chromosomes is defined by the given {@link Writer}. The
	 * following writer allows to write double-gene chromosomes:
	 * <pre>{@code
	 * final Writer<Genotype<DoubleGene>> writer =
	 *     genotypeWriter(DOUBLE_CHROMOSOME_WRITER);
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
		A,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	Writer<Genotype<G>> genotypeWriter(final Writer<? super C> writer) {
		return elem("genotype",
			attr("length", Genotype<G>::length),
			attr("ngenes", Genotype<G>::getNumberOfGenes),
			elems(gt -> cast(gt.toSeq()), writer)
		);
	}

	/**
	 * Create a writer for genotypes of arbitrary chromosomes. How to write the
	 * genotypes chromosomes is defined by the given {@link Writer}. The
	 * following writer allows to write double-gene chromosomes:
	 * <pre>{@code
	 * final Writer<Genotype<DoubleGene>> writer =
	 *     genotypesWriter(DOUBLE_CHROMOSOME_WRITER);
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
		A,
		G extends Gene<A, G>,
		C extends Chromosome<G>
	>
	Writer<Collection<Genotype<G>>> genotypesWriter(final Writer<? super C> writer) {
		return elems("genotypes", genotypeWriter(writer));
	}

	@SuppressWarnings("unchecked")
	private static <A, B> B cast(final A value) {
		return (B)value;
	}

}
