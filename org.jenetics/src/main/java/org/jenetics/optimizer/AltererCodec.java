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
import static org.jenetics.engine.codecs.ofVector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jenetics.AbstractAlterer;
import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.MultiPointCrossover;
import org.jenetics.Mutator;
import org.jenetics.NumericGene;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.Mean;

/**
 * Alterer codec for all given alterers
 *
 * @param <G> the gene type of the problem encoding
 * @param <C> the fitness function return type of the problem encoding
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class AltererCodec<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Codec<Alterer<G, C>, DoubleGene>
{

	private static final IntRange CROSSOVER_POINTS = IntRange.of(1, 15);

	private final ISeq<Codec<Alterer<G, C>, DoubleGene>> _codecs;
	private final ISeq<Alterer<G, C>> _alterers;
	private final Codec<Alterer<G, C>, DoubleGene> _codec;

	@SuppressWarnings("unchecked")
	private AltererCodec(
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<Alterer<G, C>> alterers
	) {
		_codecs = requireNonNull(codecs);
		_alterers = requireNonNull(alterers);

		final int altererCount = codecs.length() + alterers.length();
		final Codec<double[], DoubleGene> altererIndexesCodec =
			ofVector(DoubleRange.of(0, 1), altererCount);

		_codec = Codec.of(
			ISeq.concat(ISeq.of(altererIndexesCodec), codecs),
			x -> {
				final double[] index = (double[])x[0];

				Alterer<G, C> alterer = Alterer.empty();
				for (int i = 0; i < codecs.length(); ++i) {
					if (index[i] >= 0.5) {
						alterer = alterer
							.andThen(normalize((Alterer<G, C>)x[i + 1]));
					}
				}
				for (int i = 0; i < alterers.length(); ++i) {
					if (index[codecs.length() + i] >= 0.5) {
						alterer = alterer
							.andThen(normalize(alterers.get(i)));
					}
				}

				return alterer;
			}
		);
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> normalize(final Alterer<G, C> alterer) {
		return alterer instanceof AbstractAlterer<?, ?>
			? ((AbstractAlterer<?, ?>)alterer).getProbability() < 0.0000001
			? Alterer.empty() : alterer
			: alterer;
	}

	public ISeq<Codec<Alterer<G, C>, DoubleGene>> getCodecs() {
		return _codecs;
	}

	public ISeq<Alterer<G, C>> getAlterers() {
		return _alterers;
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, Alterer<G, C>> decoder() {
		return _codec.decoder();
	}

	public AltererCodec<G, C> append(
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<Alterer<G, C>> alterers
	) {
		return of(_codecs.append(codecs), _alterers.append(alterers));
	}

	public AltererCodec<G, C> append(
		final Codec<Alterer<G, C>, DoubleGene> codec
	) {
		return of(_codecs.append(ISeq.of(codec)), _alterers);
	}

	/* *************************************************************************
	 *  Static factory methods
	 * ************************************************************************/

	/**
	 *
	 *
	 * @param codecs
	 * @param alterers
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> of(
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<Alterer<G, C>> alterers
	) {
		return new AltererCodec<>(codecs, alterers);
	}

	/**
	 * Return the generically applicable alterer {@code Codec}.
	 *
	 * @param crossoverPoints the allowed crossover points for the
	 *        {@link MultiPointCrossover} alterer.
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the generically applicable alterer {@code Codec}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> general(final IntRange crossoverPoints) {
		return AltererCodec.<G, C>of(
			ISeq.of(
				multiPointCrossover(crossoverPoints),
				mutator(),
				swapMutator()
			),
			ISeq.empty()
		);
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code NumericGene} instances.
	 *
	 * @param crossoverPoints the allowed crossover points for the
	 *        {@link MultiPointCrossover} alterer.
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the numeric alterer {@code Codec}
	 */
	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> numeric(final IntRange crossoverPoints) {
		return AltererCodec.<G, C>general(crossoverPoints)
			.append(gaussianMutator());
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code NumericGene} instances.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the numeric alterer {@code Codec}
	 */
	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> numeric() {
		return numeric(CROSSOVER_POINTS);
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code Mean} gene instances.
	 *
	 * @param crossoverPoints the allowed crossover points for the
	 *        {@link MultiPointCrossover} alterer.
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the {@code Mean} gene alterer {@code Codec}
	 */
	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	AltererCodec<G, C> mean(final IntRange crossoverPoints) {
		return AltererCodec.<G, C>general(crossoverPoints)
			.append(meanAlterer());
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code Mean} gene instances.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the {@code Mean} gene alterer {@code Codec}
	 */
	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	AltererCodec<G, C> mean() {
		return mean(CROSSOVER_POINTS);
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code Mean} gene and {@code NumericGene} instances.
	 *
	 * @param crossoverPoints the allowed crossover points for the
	 *        {@link MultiPointCrossover} alterer.
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the {@code Mean} gene  and {@code NumericGene} alterer
	 *         {@code Codec}
	 */
	public static <G extends NumericGene<?, G> & Mean<G>, C extends Comparable<? super C>>
	AltererCodec<G, C> numericMean(final IntRange crossoverPoints) {
		return AltererCodec.<G, C>numeric(crossoverPoints)
			.append(mean(crossoverPoints));
	}

	/**
	 * Return the alterer {@code Codec} for which contains alterers for
	 * {@code Mean} gene and {@code NumericGene} instances.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the {@code Mean} gene  and {@code NumericGene} alterer
	 *         {@code Codec}
	 */
	public static <G extends NumericGene<?, G> & Mean<G>, C extends Comparable<? super C>>
	AltererCodec<G, C> numericMean() {
		return numericMean(CROSSOVER_POINTS);
	}

	/* *************************************************************************
	 *  Static factory methods for creating alterer codecs.
	 * ************************************************************************/

	/**
	 *
	 * @param points
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> multiPointCrossover(final IntRange points) {
		return Codec.of(
			Genotype.of(
				DoubleChromosome.of(0, 1),
				DoubleChromosome.of(points.doubleRange())
			),
			gt -> new MultiPointCrossover<>(
				gt.get(0, 0).doubleValue(),
				gt.get(1, 0).intValue()
			)
		);
	}

	/**
	 *
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> mutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new Mutator<>(gt.getGene().doubleValue())
		);
	}

	/**
	 *
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> swapMutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new SwapMutator<>(gt.getGene().doubleValue())
		);
	}

	/**
	 *
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> gaussianMutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new GaussianMutator<>(gt.getGene().doubleValue())
		);
	}

	/**
	 *
	 * @param <G>
	 * @param <C>
	 * @return
	 */
	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	Codec<Alterer<G, C>, DoubleGene> meanAlterer() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new MeanAlterer<>(gt.getGene().doubleValue())
		);
	}

}
