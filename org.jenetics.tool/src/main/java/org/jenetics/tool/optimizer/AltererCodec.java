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

import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.AbstractAlterer;
import org.jenetics.Alterer;
import org.jenetics.CompositeAlterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.EnumGene;
import org.jenetics.GaussianMutator;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.MultiPointCrossover;
import org.jenetics.Mutator;
import org.jenetics.NumericGene;
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.SinglePointCrossover;
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

	private final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> _codecs;
	private final ISeq<? extends Alterer<G, C>> _alterers;
	private final Codec<Alterer<G, C>, DoubleGene> _codec;

	// Primary constructor.
	private AltererCodec(
		final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Alterer<G, C>> alterers,
		final Codec<Alterer<G, C>, DoubleGene> codec
	) {
		_codecs = requireNonNull(codecs);
		_alterers = requireNonNull(alterers);
		_codec = requireNonNull(codec);
	}

	/**
	 * Return all alterer codecs this {@code AltererCodec} consists of.
	 *
	 * @return all alterer codecs this {@code AltererCodec} consists of
	 */
	public ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> getCodecs() {
		return _codecs;
	}

	/**
	 * Return all <i>parameter less</i> alterers this {@code AltererCodec}
	 * consists of
	 *
	 * @return all <i>parameter less</i> alterers
	 */
	public ISeq<? extends Alterer<G, C>> getAlterers() {
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
	 * Return a new {@code AltererCodec} with the given {@code codec} appended.
	 *
	 * @param codec the alterer codec to append
	 * @return a new {@code AltererCodec} with the given {@code codec} appended
	 * @throws NullPointerException if the given {@code codec} is {@code null}
	 */
	public AltererCodec<G, C>
	append(final Codec<? extends Alterer<G, C>, DoubleGene> codec) {
		return append(ISeq.of(codec), ISeq.empty());
	}

	/**
	 * Return a new {@code AltererCodec} with the given {@code codecs} and
	 * <i>parameter less</i> {@code alterers} appended.
	 *
	 * @param codecs the alterer codecs to append
	 * @param alterers the <i>parameter less</i> alterers to append
	 * @return a new {@code AltererCodec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public AltererCodec<G, C> append(
		final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Alterer<G, C>> alterers
	) {
		return of(ISeq.concat(_codec, codecs), ISeq.concat(_alterers, alterers));
	}

	/* *************************************************************************
	 *  Static factory methods
	 * ************************************************************************/

	/**
	 * Return a new {@code AltererCodec} with the given {@code codecs} and
	 * <i>parameter less</i> {@code alterers}.
	 *
	 * @param codecs the alterer codecs
	 * @param alterers the <i>parameter less</i> alterers
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return a new {@code AltererCodec}
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> of(
		final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<? extends Alterer<G, C>> alterers
	) {
		final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> c =
			codecs.stream()
				.flatMap(AltererCodec::flatten)
				.collect(ISeq.toISeq());

		final ISeq<? extends Alterer<G, C>> a =
			alterers.stream()
				.flatMap(AltererCodec::flatten)
				.collect(ISeq.toISeq());

		final int altererCount = c.length() + a.length();
		final Codec<double[], DoubleGene> altererIndexesCodec =
			ofVector(DoubleRange.of(0, 1), altererCount);

		final Codec<Alterer<G, C>, DoubleGene> cc = Codec.of(
			ISeq.concat(ISeq.of(altererIndexesCodec), c),
			x -> {
				final double[] index = (double[])x[0];

				Alterer<G, C> alterer = Alterer.empty();
				for (int i = 0; i < c.length(); ++i) {
					if (index[i] >= 0.5) {
						alterer = alterer
							.andThen(normalize((Alterer<G, C>)x[i + 1]));
					}
				}
				for (int i = 0; i < a.length(); ++i) {
					if (index[c.length() + i] >= 0.5) {
						alterer = alterer
							.andThen(normalize(a.get(i)));
					}
				}

				return alterer;
			}
		);

		return new AltererCodec<>(c, a, cc);
	}

	@SuppressWarnings("unchecked")
	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Stream<? extends Codec<? extends Alterer<G, C>, DoubleGene>>
	flatten(final Codec<? extends Alterer<G, C>, DoubleGene> codec) {
		return codec instanceof AltererCodec<?, ?>
			? ((AltererCodec<G, C>)codec)._codecs.stream()
			: Stream.of(codec);
	}

	@SuppressWarnings("unchecked")
	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Stream<? extends Alterer<G, C>>
	flatten(final Alterer<G, C> alterer) {
		return alterer instanceof CompositeAlterer<?, ?>
			? ((CompositeAlterer<G, C>)alterer).getAlterers().stream()
			: Stream.of(alterer);
	}

	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Alterer<G, C> normalize(final Alterer<G, C> alterer) {
		return alterer instanceof AbstractAlterer<?, ?>
			? ((AbstractAlterer<?, ?>)alterer).getProbability() < 0.0000001
			? Alterer.empty() : alterer
			: alterer;
	}

	/**
	 * Return a new {@code AltererCodec} with the given {@code codecs}.
	 *
	 * @param codecs the alterer codecs
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return a new {@code AltererCodec}
	 * @throws NullPointerException if the {@code codecs} {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> of(
		final ISeq<? extends Codec<? extends Alterer<G, C>, DoubleGene>> codecs
	) {
		return of(codecs, ISeq.empty());
	}

	/**
	 * Return a new {@code AltererCodec} with the given {@code codec}.
	 *
	 * @param codec the alterer codec
	 * @param <G> the gene type of the problem encoding
	 * @param <C> the fitness function return type of the problem encoding
	 * @return a new {@code AltererCodec}
	 * @throws NullPointerException if the {@code codec} {@code null}
	 */
	private static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> of(final Codec<? extends Alterer<G, C>, DoubleGene> codec) {
		return of(ISeq.of(codec), ISeq.empty());
	}


	/* *************************************************************************
	 *  Static factory methods for creating known alterer codecs.
	 * ************************************************************************/

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link SinglePointCrossover}.
	 *
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code SinglePointCrossover}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofSinglePointCrossover() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SinglePointCrossover<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link MultiPointCrossover}.
	 *
	 * @param points the range of the desired crossover points
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code MultiPointCrossover}
	 * @throws NullPointerException if the {@code points} range is {@code null}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofMultiPointCrossover(final IntRange points) {
		requireNonNull(points);

		return of(
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(points.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.get(0, 0).doubleValue(),
					gt.get(1, 0).intValue()
				)
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link Mutator}.
	 *
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code Mutator}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofMutator() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link SwapMutator}.
	 *
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code SwapMutator}
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofSwapMutator() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link GaussianMutator}.
	 *
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code GaussianMutator}
	 */
	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofGaussianMutator() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new GaussianMutator<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the {@link MeanAlterer}.
	 *
	 * @param <G> the gene type of the problem encoding the alterer is working on
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code MeanAlterer}
	 */
	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	AltererCodec<G, C> ofMeanAlterer() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new MeanAlterer<>(gt.getGene().doubleValue())
			)
		);
	}

	/**
	 * Return the <i>default</i> {@link Codec} of the
	 * {@link PartiallyMatchedCrossover}.
	 *
	 * @param <T> the generic type of the {@link EnumGene}
	 * @param <C> the fitness function result type of the problem
	 * @return the {@code Codec} of the {@code PartiallyMatchedCrossover}
	 */
	public static <T, C extends Comparable<? super C>>
	AltererCodec<EnumGene<T>, C> ofPartiallyMatchedCrossover() {
		return of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new PartiallyMatchedCrossover<>(gt.getGene().doubleValue())
			)
		);
	}

}
