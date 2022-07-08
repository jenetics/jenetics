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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.ext.grammar;

import java.util.function.Function;

import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Cfg.Rule;

/**
 * This class defines factories for different CFG &harr; Chromosome mappings
 * (encodings). The classical mapping codec, with a bit-chromosome can be created
 * in the following way.
 * <pre>{@code
 * final Cfg<String> cfg = ...;
 * final Codec<List<Terminal<String>>, BitGene> codec = singleBitChromosomeMapper(
 *     cfg,
 *     1000,
 *     index -> new SentenceGenerator<>(index, 1000)
 * );
 * }</pre>
 * This codec creates a mapping for the given grammar {@code cfg} and uses
 * bit-chromosomes with length {@code 1000}. The result of the mapping will be a
 * list of terminal symbols which has been created by the given
 * {@link SentenceGenerator}. The sentence generator creates sentences with a
 * maximal length of {@code 1000}. If no sentence could be created within this
 * limit, an empty list of terminal symbols is returned.
 *
 * @see <a href="https://www.brinckerhoff.org/tmp/grammatica_evolution_ieee_tec_2001.pdf">
 * 	 Grammatical Evolution</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
public final class Mappers {
	private Mappers() {
	}

	/**
	 * Return a classic mapping codec. It uses a bit-chromosome for creating the
	 * grammar results. The codons are created by dividing the chromosome in
	 * 8-bit junks, as described in <a href="https://www.brinckerhoff.org/tmp/grammatica_evolution_ieee_tec_2001.pdf">
	 * Grammatical Evolution</a> by Michael O’Neill and Conor Ryan.
	 *
	 * <pre>{@code
	 * final Cfg<String> cfg = ...;
	 * final Codec<List<Terminal<String>>, BitGene> codec = singleBitChromosomeMapper(
	 *     cfg,
	 *     1000,
	 *     index -> new SentenceGenerator<>(index, 1000)
	 * );
	 * }</pre>
	 *
	 * @see #singleIntegerChromosomeMapper(Cfg, IntRange, IntRange, Function)
	 *
	 * @param cfg the encoding grammar
	 * @param length the length of the bit-chromosome
	 * @param generator sentence generator function from a given
	 *        {@link SymbolIndex}
	 * @param <T> the terminal token type of the grammar
	 * @param <R> the result type of the mapper
	 * @return a new mapping codec for the given {@code cfg}
	 */
	public static <T, R> Codec<R, BitGene>
	singleBitChromosomeMapper(
		final Cfg<? extends T> cfg,
		final int length,
		final Function<? super SymbolIndex, ? extends Generator<T, R>> generator
	) {
		return Codec.of(
			Genotype.of(BitChromosome.of(length)),
			gt -> generator
				.apply(Codons.ofBitGenes(gt.chromosome()))
				.generate(cfg)
		);
	}

	/**
	 * Create a mapping codec, similar as in {@link #singleBitChromosomeMapper(Cfg, int, Function)}.
	 * The only difference is that the codons are encoded directly, via an
	 * integer-chromosome, so that no gene split is necessary.
	 *
	 * <pre>{@code
	 * final Cfg<String> cfg = ...;
	 * final Codec<List<Terminal<String>>, IntegerGene> codec = singleIntegerChromosomeMapper(
	 *     cfg,
	 *     IntRange.of(0, 256), // Value range of chromosomes.
	 *     IntRange.of(100),   // Length (range) ot the chromosome.
	 *     index -> new SentenceGenerator<>(index, 1000)
	 * );
	 * }</pre>
	 *
	 * @param cfg the encoding grammar
	 * @param range the value range of the integer genes
	 * @param length the length range of the integer-chromosome
	 * @param generator sentence generator function from a given
	 *        {@link SymbolIndex}
	 * @param <T> the terminal token type of the grammar
	 * @param <R> the result type of the mapper
	 * @return a new mapping codec for the given {@code cfg}
	 */
	public static <T, R> Codec<R, IntegerGene>
	singleIntegerChromosomeMapper(
		final Cfg<? extends T> cfg,
		final IntRange range,
		final IntRange length,
		final Function<? super SymbolIndex, ? extends Generator<T, R>> generator
	) {
		return Codec.of(
			Genotype.of(IntegerChromosome.of(range, length)),
			gt -> generator
				.apply(Codons.ofIntegerGenes(gt.chromosome()))
				.generate(cfg)
		);
	}

	/**
	 * Create a mapping codec, similar as in {@link #singleBitChromosomeMapper(Cfg, int, Function)}.
	 * The only difference is that the codons are encoded directly, via an
	 * integer-chromosome, so that no gene split is necessary.
	 *
	 * <pre>{@code
	 * final Cfg<String> cfg = ...;
	 * final Codec<List<Terminal<String>>, IntegerGene> codec = singleIntegerChromosomeMapper(
	 *     cfg,
	 *     IntRange.of(0, 256), // Value range of chromosomes.
	 *     100,                 // Length (range) ot the chromosome.
	 *     index -> new SentenceGenerator<>(index, 1000)
	 * );
	 * }</pre>
	 *
	 * @param cfg the encoding grammar
	 * @param range the value range of the integer genes
	 * @param length the length range of the integer-chromosome
	 * @param generator sentence generator function from a given
	 *        {@link SymbolIndex}
	 * @param <T> the terminal token type of the grammar
	 * @param <R> the result type of the mapper
	 * @return a new mapping codec for the given {@code cfg}
	 */
	public static <T, R> Codec<R, IntegerGene>
	singleIntegerChromosomeMapper(
		final Cfg<? extends T> cfg,
		final IntRange range,
		final int length,
		final Function<? super SymbolIndex, ? extends Generator<T, R>> generator
	) {
		return singleIntegerChromosomeMapper(
			cfg, range, IntRange.of(length), generator
		);
	}

	/**
	 * Codec for creating <em>results</em> from a given grammar. The creation of
	 * the grammar result is controlled by a given genotype. This encoding uses
	 * separate <em>codons</em>, backed up by a {@link IntegerChromosome}, for
	 * every rule. The length of the chromosome is defined as a function of the
	 * encoded rules. This means that the following CFG,
	 *
	 * <pre>{@code
	 *                       (0)            (1)
	 * (0) <expr> ::= (<expr><op><expr>) | <var>
	 *               (0) (1) (2) (3)
	 * (1) <op>   ::= + | - | * | /
	 *               (0) (1) (2) (3) (4)
	 * (2) <var>  ::= x | 1 | 2 | 3 | 4
	 * }</pre>
	 *
	 * will be represented by the following {@link Genotype}
	 * <pre>{@code
	 * Genotype.of(
	 *     IntegerChromosome.of(IntRange.of(0, 2), length.apply(cfg.rules().get(0))),
	 *     IntegerChromosome.of(IntRange.of(0, 4), length.apply(cfg.rules().get(1))),
	 *     IntegerChromosome.of(IntRange.of(0, 5), length.apply(cfg.rules().get(2)))
	 * )
	 * }</pre>
	 *
	 * The {@code length} function lets you defining the number of codons as
	 * function of the rule the chromosome is encoding.
	 *
	 * <pre>{@code
	 * final Cfg<String> cfg = Bnf.parse(...);
	 * final Codec<List<Terminal<String>>, IntegerGene> codec = multiIntegerChromosomeMapper(
	 *     cfg,
	 *     // The chromosome length is 25 times the
	 *     // number of rule alternatives.
	 *     rule -> IntRange.of(rule.alternatives().size()*25),
	 *     // Using the standard sentence generator
	 *     // with a maximal sentence length of 500.
	 *     index -> new SentenceGenerator<>(index, 500)
	 * );
	 * }</pre>
	 *
	 * @param cfg the encoding grammar
	 * @param length the length of the chromosome which is used for selecting
	 *        rules and symbols. The input parameter for this function is the
	 *        actual rule. This way it is possible to define the chromosome
	 *        length dependent on the selectable alternatives.
	 * @param generator sentence generator function from a given
	 *        {@link SymbolIndex}
	 * @param <T> the terminal token type of the grammar
	 * @param <R> the result type of the mapper
	 * @return a new mapping codec for the given {@code cfg}
	 */
	public static <T, R> Codec<R, IntegerGene>
	multiIntegerChromosomeMapper(
		final Cfg<? extends T> cfg,
		final Function<? super Rule<?>, IntRange> length,
		final Function<? super SymbolIndex, ? extends Generator<T, R>> generator
	) {
		return new MultiIntegerChromosomeMapper<>(cfg, length, generator);
	}


}
