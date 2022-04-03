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

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Cfg.Rule;

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
 * final Codec<List<Terminal<String>>, IntegerGene> codec = new Mapper<>(
 *     cfg,
 *     // The chromosome length is 10 times the
 *     // number of rule alternatives.
 *     rule -> IntRange.of(rule.alternatives().size()*10),
 *     // Using the standard sentence generator
 *     // with a maximal sentence length of 5,000.
 *     index -> new SentenceGenerator<>(index, 5_000)
 * );
 * }</pre>
 *
 * @param <T> the terminal token type of the grammar
 * @param <R> the result type of the mapper
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.1
 * @version 7.1
 */
final class MultiIntegerChromosomeMapper<T, R> implements Codec<R, IntegerGene> {

	private final Factory<Genotype<IntegerGene>> _encoding;
	private final Function<Genotype<IntegerGene>, R> _decoder;

	/**
	 * Create a new sentence (list of terminal symbols) codec.
	 *
	 * @param cfg the encoding grammar
	 * @param length the length of the chromosome which is used for selecting
	 *        rules and symbols. The input parameter for this function is the
	 *        actual rule. This way it is possible to define the chromosome
	 *        length dependent on the selectable alternatives.
	 * @param generator sentence generator function from a given
	 *        {@link SymbolIndex}
	 */
	public MultiIntegerChromosomeMapper(
		final Cfg<? extends T> cfg,
		final Function<? super Rule<?>, IntRange> length,
		final Function<? super SymbolIndex, ? extends Generator<T, R>> generator
	) {
		// Every rule gets its own codons. The ranges of the chromosomes
		// will fit exactly the number of rule alternatives.
		_encoding = Genotype.of(
			cfg.rules().stream()
				.map(rule ->
					IntegerChromosome.of(
						IntRange.of(0, rule.alternatives().size()),
						length.apply(rule)
					))
				.collect(ISeq.toISeq())
		);

		final var codons = new CodonsFactory(cfg);
		_decoder = gt -> generator.apply(codons.get(gt)).generate(cfg);
	}

	@Override
	public Factory<Genotype<IntegerGene>> encoding() {
		return _encoding;
	}

	@Override
	public Function<Genotype<IntegerGene>, R> decoder() {
		return _decoder;
	}

	/**
	 * Helper class for generating codons for a given genotype.
	 */
	private static final class CodonsFactory {
		private final Map<String, Integer> _rulesIndex;

		CodonsFactory(final Cfg<?> cfg) {
			_rulesIndex = IntStream
				.range(0, cfg.rules().size())
				.mapToObj(i -> Map.entry(cfg.rules().get(i).start().name(), i))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}

		SymbolIndex get(final Genotype<IntegerGene> gt) {
			final var codons = new CodonsCache(gt);

			return (rule, bound) -> codons
				.get(_rulesIndex.get(rule.start().name()))
				.next(rule, bound);
		}

		/**
		 * Caching and lazy creation of codons.
		 */
		private static final class CodonsCache {
			private final Genotype<IntegerGene> _genotype;
			private final Codons[] _codons;

			CodonsCache(final Genotype<IntegerGene> genotype) {
				_genotype = requireNonNull(genotype);
				_codons = new Codons[genotype.length()];
			}

			Codons get(final int index) {
				Codons result = _codons[index];
				if (result == null) {
					result = Codons.ofIntegerGenes(_genotype.get(index));
					_codons[index] = result;
				}

				return result;
			}
		}

	}

}
