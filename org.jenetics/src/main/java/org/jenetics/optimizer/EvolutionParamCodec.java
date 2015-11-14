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
package org.jenetics.optimizer;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.internal.util.require;

import org.jenetics.Alterer;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Selector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.codecs;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.LongRange;

/**
 * The {@code Codec} for the evolution parameter of the GA.
 *
 * @param <G> the gene type of the problem encoding
 * @param <C> the fitness function return type of the problem encoding
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamCodec<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Codec<EvolutionParam<G, C>, DoubleGene>
{

	private static final IntRange POPULATION_SIZE = IntRange.of(10, 1000);
	private static final DoubleRange OFFSPRING_FRACTION = DoubleRange.of(0, 1);
	private static final LongRange MAXIMAL_PHENOTYPE_AGE = LongRange.of(5, 1000);

	private final Codec<Selector<G, C>, DoubleGene> _selector;
	private final Codec<Alterer<G, C>, DoubleGene> _alterer;
	private final IntRange _populationSize;
	private final DoubleRange _offspringFraction;
	private final LongRange _maximalPhenotypeAge;

	private final Codec<EvolutionParam<G, C>, DoubleGene> _codec;

	/**
	 * Create a new evolution parameter {@code Codec} with the given parameters.
	 *
	 * @param selector the selector codec used for survivor and offspring
	 *        selectors
	 * @param alterer the alterer codec
	 * @param populationSize the population size range
	 * @param offspringFraction the offspring fraction range
	 * @param maximalPhenotypeAge the maximal phenotype age range
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the population size or the maximal
	 *         phenotype age is smaller than one.
	 * @throws IllegalArgumentException if the offspring fraction is not within
	 *         the range [0..1].
	 */
	@SuppressWarnings("unchecked")
	private EvolutionParamCodec(
		final Codec<Selector<G, C>, DoubleGene> selector,
		final Codec<Alterer<G, C>, DoubleGene> alterer,
		final IntRange populationSize,
		final DoubleRange offspringFraction,
		final LongRange maximalPhenotypeAge
	) {
		_selector = requireNonNull(selector);
		_alterer = requireNonNull(alterer);
		_populationSize = requireNonNull(populationSize);
		_offspringFraction = requireNonNull(offspringFraction);
		_maximalPhenotypeAge = requireNonNull(maximalPhenotypeAge);

		require.positive(populationSize.getMin());
		require.positive(populationSize.getMax());
		require.probability(offspringFraction.getMin());
		require.probability(offspringFraction.getMax());
		require.positive(maximalPhenotypeAge.getMin());
		require.positive(maximalPhenotypeAge.getMax());

		_codec = Codec.of(
			ISeq.of(
				selector,
				selector,
				alterer,
				codecs.ofScalar(populationSize.doubleRange()),
				codecs.ofScalar(offspringFraction),
				codecs.ofScalar(maximalPhenotypeAge.doubleRange())
			),
			x -> EvolutionParam.of(
					(Selector<G, C>)x[0],
					(Selector<G, C>)x[1],
					(Alterer<G, C>)x[2],
					((Double)x[3]).intValue(),
					(Double)x[4],
					((Double)x[5]).longValue()
				)
		);
	}

	/**
	 * Return the selector codec for survivor and offspring codec.
	 *
	 * @return the selector codec for survivor and offspring codec
	 */
	public Codec<Selector<G, C>, DoubleGene> getSelector() {
		return _selector;
	}

	/**
	 * Return the alterer codec.
	 *
	 * @return the alterer codec
	 */
	public Codec<Alterer<G, C>, DoubleGene> getAlterer() {
		return _alterer;
	}

	/**
	 * Return the allowed population size range.
	 *
	 * @return the allowed population size range
	 */
	public IntRange getPopulationSize() {
		return _populationSize;
	}

	/**
	 * Return the allowed offspring fraction range.
	 *
	 * @return the allowed offspring fraction range
	 */
	public DoubleRange getOffspringFraction() {
		return _offspringFraction;
	}

	/**
	 * Return the allowed maximal phenotype age range.
	 *
	 * @return the allowed maximal phenotype age range
	 */
	public LongRange getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, EvolutionParam<G, C>> decoder() {
		return _codec.decoder();
	}

	/**
	 * Create a new evolution parameter {@code Codec} with the given parameters.
	 *
	 * @param selector the selector codec used for survivor and offspring
	 *        selectors
	 * @param alterer the alterer codec
	 * @param populationSize the population size range
	 * @param offspringFraction the offspring fraction range
	 * @param maximalPhenotypeAge the maximal phenotype age range
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the population size or the maximal
	 *         phenotype age is smaller than one.
	 * @throws IllegalArgumentException if the offspring fraction is not within
	 *         the range [0..1].
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionParamCodec<G, C> of(
		final Codec<Selector<G, C>, DoubleGene> selector,
		final Codec<Alterer<G, C>, DoubleGene> alterer,
		final IntRange populationSize,
		final DoubleRange offspringFraction,
		final LongRange maximalPhenotypeAge
	) {
		return new EvolutionParamCodec<G, C>(
			SelectorCodec.general(),
			AltererCodec.general(),
			populationSize,
			offspringFraction,
			maximalPhenotypeAge
		);
	}

	/**
	 * Create a new evolution parameter {@code Codec} with the given parameters.
	 *
	 * @param selector the selector codec used for survivor and offspring
	 *        selectors
	 * @param alterer the alterer codec
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EvolutionParamCodec<G, C> of(
		final Codec<Selector<G, C>, DoubleGene> selector,
		final Codec<Alterer<G, C>, DoubleGene> alterer
	) {
		return of(
			selector,
			alterer,
			POPULATION_SIZE,
			OFFSPRING_FRACTION,
			MAXIMAL_PHENOTYPE_AGE
		);
	}

}
