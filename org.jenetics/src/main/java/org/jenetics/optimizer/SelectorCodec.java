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

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.codecs.ofScalar;
import static org.jenetics.internal.collection.seq.concat;

import java.util.function.Function;

import org.jenetics.BoltzmannSelector;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.ExponentialRankSelector;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.LinearRankSelector;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.Selector;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
 * Selector codec of all given selectors.
 *
 * @param <G> the gene type of the problem encoding
 * @param <C> the fitness function return type of the problem encoding
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SelectorCodec<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Codec<Selector<G, C>, DoubleGene>
{

	private static final DoubleRange
		BOLTZMANN_SELECTOR_PARAM = DoubleRange.of(0, 3);

	private static final DoubleRange
		EXPONENTIAL_RANK_SELECTOR_PARAM = DoubleRange.of(0, 1);

	private static final DoubleRange
		LINEAR_RANK_SELECTOR_PARAM = DoubleRange.of(0, 5);

	private static final IntRange TOURNAMENT_SIZE = IntRange.of(2, 15);

	private final ISeq<Codec<Selector<G, C>, DoubleGene>> _codecs;
	private final ISeq<Selector<G, C>> _selectors;
	private final Codec<Selector<G, C>, DoubleGene> _codec;

	/**
	 * Create a selector {@code Codec} with the given set of {@code Selectors}.
	 *
	 * @param codecs the available selector codecs to choose from
	 * @param selectors the available selectors to choose from
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	private SelectorCodec(
		final ISeq<Codec<Selector<G, C>, DoubleGene>> codecs,
		final ISeq<Selector<G, C>> selectors
	) {
		_codecs = requireNonNull(codecs);
		_selectors = requireNonNull(selectors);

		final int selectorCount = codecs.length() + selectors.length();
		final Codec<Double, DoubleGene> selectorIndexCodec =
			ofScalar(DoubleRange.of(0, selectorCount));

		_codec = Codec.of(
			concat(ISeq.of(selectorIndexCodec), codecs),
			x -> {
				final int selectorIndex =
					min(((Double)x[0]).intValue(), selectorCount - 1);

				return selectorIndex < codecs.length()
					? (Selector<G, C>)x[selectorIndex + 1]
					: selectors.get(selectorIndex - codecs.length());
			}
		);
	}

	/**
	 * Return the list of available selector codecs.
	 *
	 * @return the list of available selector codecs
	 */
	public ISeq<Codec<Selector<G, C>, DoubleGene>> getCodecs() {
		return _codecs;
	}

	/**
	 * Return the list of available selectors.
	 *
	 * @return the list of available selectors
	 */
	public ISeq<Selector<G, C>> getSelectors() {
		return _selectors;
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, Selector<G, C>> decoder() {
		return _codec.decoder();
	}

	/**
	 * Return a selector codec which is suitable for arbitrary problem encodings.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 *
	 * @return a selector codec which is suitable for arbitrary problem
	 *         encodings
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> general() {
		final ISeq<Codec<Selector<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(EXPONENTIAL_RANK_SELECTOR_PARAM)),
				gt -> new ExponentialRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(LINEAR_RANK_SELECTOR_PARAM)),
				gt -> new LinearRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(TOURNAMENT_SIZE.doubleRange())),
				gt -> new TournamentSelector<>(gt.getGene().intValue())
			)
		);

		final ISeq<Selector<G, C>> selectors = ISeq.of(
			new TruncationSelector<>()
		);

		return new SelectorCodec<>(codecs, selectors);
	}

	/**
	 * Return a selector codec which is suitable for numerical problem encodings.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <N> the fitness function return type of the problem encoding
	 *
	 * @return a selector codec which is suitable for numerical problem
	 *         encodings
	 */
	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	SelectorCodec<G, N> numeric() {
		final ISeq<Codec<Selector<G, N>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(BOLTZMANN_SELECTOR_PARAM)),
				gt -> new BoltzmannSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(EXPONENTIAL_RANK_SELECTOR_PARAM)),
				gt -> new ExponentialRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(LINEAR_RANK_SELECTOR_PARAM)),
				gt -> new LinearRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(TOURNAMENT_SIZE.doubleRange())),
				gt -> new TournamentSelector<>(gt.getGene().intValue())
			)
		);

		final ISeq<Selector<G, N>> selectors = ISeq.of(
			new RouletteWheelSelector<G, N>(),
			new StochasticUniversalSelector<G, N>(),
			new TruncationSelector<G, N>()
		);

		return new SelectorCodec<>(codecs, selectors);
	}

}
