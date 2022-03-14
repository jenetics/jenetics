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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Codec;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.IntRange;

import io.jenetics.ext.grammar.Cfg.Terminal;

/**
 * Codec for creating sentences (list of terminal symbols) from a given grammar.
 * The creation of the sentences is controlled by a given genotype.
 *
 * @param <T> the terminal value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class SentenceCodec<T> implements Codec<List<Terminal<T>>, IntegerGene> {

	private final Factory<Genotype<IntegerGene>> _encoding;
	private final Function<Genotype<IntegerGene>, List<Terminal<T>>> _decoder;

	SentenceCodec(
		final Cfg<? extends T> cfg,
		final IntUnaryOperator length,
		final Function<? super SymbolIndex, SentenceGenerator<T>> generator
	) {
		// Every rule gets its own codons. The ranges of the chromosomes
		// will fit exactly the number of rule alternatives.
		_encoding = Genotype.of(
			cfg.rules().stream()
				.map(rule ->
					IntegerChromosome.of(
						IntRange.of(0, rule.alternatives().size()),
						length.applyAsInt(rule.alternatives().size())
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
	public Function<Genotype<IntegerGene>, List<Terminal<T>>> decoder() {
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
