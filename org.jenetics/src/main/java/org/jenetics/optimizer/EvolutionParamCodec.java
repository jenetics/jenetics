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

import org.jenetics.Alterer;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Selector;
import org.jenetics.engine.Codec;
import org.jenetics.engine.EvolutionParam;
import org.jenetics.engine.codecs;
import org.jenetics.util.ISeq;
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamCodec {

	@SuppressWarnings("unchecked")
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<EvolutionParam<G, C>, DoubleGene> General(
		final IntRange crossoverPoints,
		final IntRange tournamentSize,
		final IntRange populationSize,
		final IntRange maxPhenotypeAge,
		final int survivorCount,
		final int offspringCount
	) {
		return Codec.of(ISeq.of(
			AltererCodec.general(crossoverPoints),
			SelectorCodec.general(tournamentSize),
			SelectorCodec.general(tournamentSize),
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
