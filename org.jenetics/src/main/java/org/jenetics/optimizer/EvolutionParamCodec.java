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
import org.jenetics.util.IntRange;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class EvolutionParamCodec {

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Codec<EvolutionParam<G, C>, DoubleGene> General(
		final IntRange crossoverPoints,
		final IntRange tournamentSize,
		final IntRange populationSize,
		final IntRange maxPhenotypeAge,
		final int survivorCount,
		final int offspringCount
	) {
		return Codec.of(
			AltererCodecs.General(crossoverPoints),
			SelectorCodecs.Generic(tournamentSize),
			SelectorCodecs.Generic(tournamentSize),
			codecs.ofScalar(populationSize.doubleRange()),
			codecs.ofScalar(maxPhenotypeAge.doubleRange()),
			(final Alterer<G, C> alterer,
			final Selector<G, C> survivorSelector,
			final Selector<G, C> offspringSelector,
			final Double popSize,
			final Double maxPtAge) -> EvolutionParam.of(
				survivorSelector,
				offspringSelector,
				alterer,
				survivorCount,
				offspringCount,
				maxPtAge.intValue()
			)
		);
	}

}
