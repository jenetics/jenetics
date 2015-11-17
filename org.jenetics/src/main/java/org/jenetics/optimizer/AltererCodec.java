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
import static org.jenetics.internal.collection.seq.concat;

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
			concat(ISeq.of(altererIndexesCodec), codecs),
			x -> {
				final double[] index = (double[])x[0];

				Alterer<G, C> alterer = Alterer.empty();
				for (int i = 0; i < codecs.length(); ++i) {
					if (index[i] >= 0.5) {
						alterer = alterer
							.andThen(alterer((Alterer<G, C>)x[i + 1]));
					}
				}
				for (int i = 0; i < alterers.length(); ++i) {
					if (index[codecs.length() + i] >= 0.5) {
						alterer = alterer
							.andThen(alterer(alterers.get(i)));
					}
				}

				return alterer;
			}
		);
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

	/**
	 * Return the generically applicable alterer {@code Codec}.
	 *
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return the generically applicable alterer {@code Codec}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> general() {
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(CROSSOVER_POINTS.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.get(1, 0).intValue()
				)
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);

		return new AltererCodec<>(codecs, ISeq.empty());
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> alterer(final Alterer<G, C> alterer) {
		return alterer instanceof AbstractAlterer<?, ?>
			? ((AbstractAlterer<?, ?>)alterer).getProbability() < 0.0000001
				? Alterer.empty() : alterer
			: alterer;
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
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new GaussianMutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(CROSSOVER_POINTS.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.getChromosome(1).getGene().intValue()
				)
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);

		return new AltererCodec<>(codecs, ISeq.empty());
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
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new MeanAlterer<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(CROSSOVER_POINTS.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.getChromosome(1).getGene().intValue()
				)
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);

		return new AltererCodec<>(codecs, ISeq.empty());
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
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new GaussianMutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new MeanAlterer<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(CROSSOVER_POINTS.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.getChromosome(1).getGene().intValue()
				)
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);

		return new AltererCodec<>(codecs, ISeq.empty());
	}

}
