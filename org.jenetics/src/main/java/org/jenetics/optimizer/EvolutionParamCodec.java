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

	private final Codec<Selector<G, C>, DoubleGene> _selector;
	private final Codec<Alterer<G, C>, DoubleGene> _alterer;
	private final IntRange _populationSize;
	private final LongRange _maxPhenotypeAge;
	private final DoubleRange _offspringFraction;

	private final Codec<EvolutionParam<G, C>, DoubleGene> _codec;

	private EvolutionParamCodec(
		final Codec<Selector<G, C>, DoubleGene> selector,
		final Codec<Alterer<G, C>, DoubleGene> alterer,
		final IntRange populationSize,
		final LongRange maxPhenotypeAge,
		final DoubleRange offspringFraction
	) {
		_selector = requireNonNull(selector);
		_alterer = requireNonNull(alterer);
		_populationSize = requireNonNull(populationSize);
		_maxPhenotypeAge = requireNonNull(maxPhenotypeAge);
		_offspringFraction = requireNonNull(offspringFraction);

		_codec = Codec.of(
			ISeq.of(
				alterer,
				selector,
				selector,
				codecs.ofScalar(populationSize.doubleRange()),
				codecs.ofScalar(maxPhenotypeAge.doubleRange()),
				codecs.ofScalar(offspringFraction)
			),
			x -> {
				return null;
			}
		);
	}

	@Override
	public Factory<Genotype<DoubleGene>> encoding() {
		return _codec.encoding();
	}

	@Override
	public Function<Genotype<DoubleGene>, EvolutionParam<G, C>> decoder() {
		return _codec.decoder();
	}

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<EvolutionParam<G, C>, DoubleGene> general(
		final IntRange populationSize,
		final IntRange maxPhenotypeAge,
		final int survivorCount,
		final int offspringCount
	) {
		return Codec.of(ISeq.of(
			AltererCodec.general(),
			SelectorCodec.general(),
			SelectorCodec.general(),
			codecs.ofScalar(populationSize.doubleRange()),
			codecs.ofScalar(maxPhenotypeAge.doubleRange())),
			data -> {
				final Alterer<G, C> alterer = (Alterer<G, C>)data[0];
				final Selector<G, C> survivorSelector = (Selector<G, C>)data[1];
				final Selector<G, C> offspringSelector = (Selector<G, C>)data[2];
				final Double popSize = (Double)data[3];
				final Double maxPtAge = (Double)data[4];
				return EvolutionParam.of(
					survivorSelector,
					offspringSelector,
					alterer,
					survivorCount,
					offspringCount,
					maxPtAge.intValue()
				);
			}
		);
	}

}
