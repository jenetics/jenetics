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
package org.jenetics.tool.optimizer;

import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.codecs.ofScalar;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.BoltzmannSelector;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.ExponentialRankSelector;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.LinearRankSelector;
import org.jenetics.Selector;
import org.jenetics.TournamentSelector;
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

	private final ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> _codecs;
	private final ISeq<? extends Selector<G, C>> _selectors;
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
		final ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Selector<G, C>> selectors,
		final Codec<Selector<G, C>, DoubleGene> codec
	) {
		_codecs = requireNonNull(codecs);
		_selectors = requireNonNull(selectors);
		_codec = requireNonNull(codec);
	}

	/**
	 * Return the list of available selector codecs.
	 *
	 * @return the list of available selector codecs
	 */
	public ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> getCodecs() {
		return _codecs;
	}

	/**
	 * Return the list of available selectors.
	 *
	 * @return the list of available selectors
	 */
	public ISeq<? extends Selector<G, C>> getSelectors() {
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
	 * Return a new {@code SelectorCodec} with the given {@code codec} appended.
	 *
	 * @param codec the alterer codec to append
	 * @return a new {@code SelectorCodec} with the given {@code codec} appended
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	public SelectorCodec<G, C>
	append(final Codec<? extends Selector<G, C>, DoubleGene> codec) {
		return append(ISeq.of(codec), ISeq.empty());
	}

	/**
	 * Return a new {@code SelectorCodec} with the given {@code codecs} and
	 * <i>parameter less</i> {@code selectors} appended.
	 *
	 * @param codecs the selector codecs to append
	 * @param selectors the <i>parameter less</i> alterers to append
	 * @return a new {@code SelectorCodec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public SelectorCodec<G, C> append(
		final ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Selector<G, C>> selectors
	) {
		return of(ISeq.concat(_codec, codecs), ISeq.concat(_selectors, selectors));
	}

	/* *************************************************************************
	 *  Static factory methods
	 * ************************************************************************/

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> of(
		final ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Selector<G, C>> selectors
	) {
		final ISeq<? extends Codec<? extends Selector<G, C>, DoubleGene>> c =
			codecs.stream()
				.flatMap(SelectorCodec::flatten)
				.collect(ISeq.toISeq());

		final int selectorCount = c.length() + selectors.length();
		final Codec<Double, DoubleGene> selectorIndexCodec =
			ofScalar(DoubleRange.of(0, selectorCount));

		final Codec<Selector<G, C>, DoubleGene> cc = Codec.of(
			ISeq.concat(ISeq.of(selectorIndexCodec), c),
			x -> {
				final int selectorIndex =
					min(((Double)x[0]).intValue(), selectorCount - 1);

				return selectorIndex < c.length()
					? (Selector<G, C>)x[selectorIndex + 1]
					: selectors.get(selectorIndex - c.length());
			}
		);

		return new SelectorCodec<>(c, selectors, cc);
	}

	@SuppressWarnings("unchecked")
	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Stream<? extends Codec<? extends Selector<G, C>, DoubleGene>>
	flatten(final Codec<? extends Selector<G, C>, DoubleGene> codec) {
		return codec instanceof SelectorCodec<?, ?>
			? ((SelectorCodec<G, C>)codec)._codecs.stream()
			: Stream.of(codec);
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> of(final Codec<? extends Selector<G, C>, DoubleGene> codec) {
		return of(ISeq.of(codec), ISeq.empty());
	}

	/* *************************************************************************
	 *  Static factory methods for known selector codecs.
	 * ************************************************************************/

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link TournamentSelector}.
	 *
	 * @param size the desired tournament size range
	 * @param <G> the gene type of the problem encoding the selector is working
	 *            on
	 * @param <C> the fitness function result type of the problem
	 * @return the <i>default</i> {@link Codec} of the {@link TournamentSelector}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> ofTournamentSelector(final IntRange size){
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(size.doubleRange())),
				gt -> new TournamentSelector<>(gt.getGene().intValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link LinearRankSelector}.
	 *
	 * @param nminus {@code nminus/N} is the probability of the worst phenotype
	 *         to be selected.
	 * @param <G> the gene type of the problem encoding the selector is working
	 *            on
	 * @param <C> the fitness function result type of the problem
	 * @return the <i>default</i> {@link Codec} of the {@link LinearRankSelector}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> ofLinearRankSelector(final DoubleRange nminus){
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(nminus)),
				gt -> new LinearRankSelector<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the
	 * {@link ExponentialRankSelector}.
	 *
	 * @param c the parameter range of the selector parameter
	 * @param <G> the gene type of the problem encoding the selector is working
	 *            on
	 * @param <C> the fitness function result type of the problem
	 * @return the <i>default</i> {@link Codec} of the
	 *         {@link ExponentialRankSelector}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	SelectorCodec<G, C> ofExponentialRankSelector(final DoubleRange c){
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(c)),
				gt -> new ExponentialRankSelector<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link BoltzmannSelector}.
	 *
	 * @param b <i>b</i> value of this BoltzmanSelector
	 * @param <G> the gene type of the problem encoding the selector is working
	 *            on
	 * @param <N> the fitness function result type of the problem
	 * @return the <i>default</i> {@link Codec} of the {@link BoltzmannSelector}
	 */
	public static <G extends Gene<?, G>, N extends Number & Comparable<? super N>>
	SelectorCodec<G, N> ofBoltzmannSelector(final DoubleRange b){
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(b)),
				gt -> new BoltzmannSelector<>(gt.getGene().doubleValue())
			)
		);
	}

}
