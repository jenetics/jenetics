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

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static org.jenetics.engine.codecs.ofVector;
import static org.jenetics.internal.collection.seq.concat;

import java.util.function.Function;
import java.util.stream.Stream;

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
import org.jenetics.PartiallyMatchedCrossover;
import org.jenetics.SwapMutator;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;
import org.jenetics.util.Mean;

/**
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

	private final Codec<Alterer<G, C>, DoubleGene> _codec;

	@SuppressWarnings("unchecked")
	private AltererCodec(
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs,
		final ISeq<Alterer<G, C>> alterers
	) {
		requireNonNull(codecs);
		requireNonNull(alterers);

		final int altererCount = codecs.length() + alterers.length();

		_codec = Codec.of(
			concat(
				ISeq.of(ofVector(DoubleRange.of(0, 1), altererCount)),
				codecs
			),
			x -> {
				final ISeq<Alterer<G, C>> a = Stream
					.concat(
						stream(x, 1, x.length).map(o -> (Alterer<G, C>) o),
						alterers.stream())
					.collect(ISeq.toISeq());

				final double[] index = (double[])x[0];
				Alterer<G, C> alterer = Alterer.empty();
				for (int i = 0; i < index.length; ++i) {
					if (index[i] > 0.5) {
						alterer = alterer.andThen(a.get(i));
					}
				}

				return alterer;
			}
		);
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, Alterer<G, C>> decoder() {
		return _codec.decoder();
	}

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	AltererCodec<G, C> general(final IntRange crossoverPoints) {
		final ISeq<Codec<Alterer<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new Mutator<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(
					DoubleChromosome.of(0, 1),
					DoubleChromosome.of(crossoverPoints.doubleRange())
				),
				gt -> new MultiPointCrossover<>(
					gt.getChromosome(0).getGene().doubleValue(),
					gt.getChromosome(1).getGene().intValue()
				)
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new SwapMutator<>(gt.getGene().doubleValue())
			)
		);

		return new AltererCodec<>(codecs, ISeq.empty());
	}

	public static <G extends NumericGene<?, G>, C extends Comparable<? super C>>
	Codec<GaussianMutator<G, C>, DoubleGene> GaussianMutator() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new GaussianMutator<>(gt.getGene().doubleValue())
		);
	}

	public static <G extends Gene<?, G> & Mean<G>, C extends Comparable<? super C>>
	Codec<MeanAlterer<G, C>, DoubleGene> MeanAlterer() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new MeanAlterer<>(gt.getGene().doubleValue())
		);
	}

	public static <A, C extends Comparable<? super C>>
	Codec<PartiallyMatchedCrossover<A, C>, DoubleGene>
	PartiallyMatchedCrossover() {
		return Codec.of(
			Genotype.of(DoubleChromosome.of(0, 1)),
			gt -> new PartiallyMatchedCrossover<>(gt.getGene().doubleValue())
		);
	}

}
