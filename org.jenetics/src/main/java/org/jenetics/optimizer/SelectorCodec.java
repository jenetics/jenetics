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

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.ExponentialRankSelector;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.LinearRankSelector;
import org.jenetics.Selector;
import org.jenetics.TournamentSelector;
import org.jenetics.TruncationSelector;
import org.jenetics.engine.Codec;
import org.jenetics.util.DoubleRange;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
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

	private final Codec<Selector<G, C>, DoubleGene> _codec;

	@SuppressWarnings("unchecked")
	private SelectorCodec(
		final ISeq<Codec<Selector<G, C>, DoubleGene>> codecs,
		final ISeq<Selector<G, C>> selectors
	) {
		requireNonNull(codecs);
		requireNonNull(selectors);

		final int selectorCount = codecs.length() + selectors.length();

		_codec = Codec.of(
			concat(
				ISeq.of(ofScalar(DoubleRange.of(0, selectorCount))),
				codecs
			),
			x -> {
				final int i = min(((Double)x[0]).intValue(), selectorCount);

				return i < codecs.length()
					? (Selector<G, C>)x[i]
					: selectors.get(i - codecs.length());
			}
		);
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, Selector<G, C>> decoder() {
		return _codec.decoder();
	}

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<Selector<G, C>, DoubleGene> general(final IntRange tournamentSize) {
		final ISeq<Codec<Selector<G, C>, DoubleGene>> codecs = ISeq.of(
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new ExponentialRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new LinearRankSelector<>(gt.getGene().doubleValue())
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(0, 1)),
				gt -> new TruncationSelector<>()
			),
			Codec.of(
				Genotype.of(DoubleChromosome.of(tournamentSize.doubleRange())),
				gt -> new TournamentSelector<>(gt.getGene().intValue())
			)
		);

		final ISeq<Selector<G, C>> selectors = ISeq.of(
			new TruncationSelector<>()
		);

		return new SelectorCodec<>(codecs, selectors);
	}

}
