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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IndexedAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Alterer<G, C>
{

	private final ISeq<Component<G, C>> _alterers;

	private IndexedAlterer(final ISeq<Component<G, C>> alterers) {
		_alterers = requireNonNull(alterers);
	}

	@Override
	public AltererResult<G, C>
	alter(final Seq<Phenotype<G, C>> population, final long generation) {
		final Map<Integer, Seq<Phenotype<G, C>>> slices = split(population);

		int alterations = 0;
		for (int i = 0; i < _alterers.size(); ++i) {
			final int index = _alterers.get(i).index;
			final Alterer<G, C> alterer = _alterers.get(i).alterer;

			final Seq<Phenotype<G, C>> pop = slices.get(index);
			final AltererResult<G, C> result = alterer.alter(pop, generation);

			alterations += result.getAlterations();
			slices.put(i, result.getPopulation());
		}

		return AltererResult.of(merge(population, slices), alterations);
	}

	private Map<Integer, Seq<Phenotype<G, C>>>
	split(final Seq<Phenotype<G, C>> population) {
		final Map<Integer, Seq<Phenotype<G, C>>> split = new HashMap<>();
		return split;
	}

	private ISeq<Phenotype<G, C>> merge(
		final Seq<Phenotype<G, C>> population,
		Map<Integer, Seq<Phenotype<G, C>>> slices
	) {
		return population.asISeq();
	}


	private static final class Component<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		final int index;
		final Alterer<G, C> alterer;

		private Component(final int index, final Alterer<G, C> alterer) {
			this.index = index;
			this.alterer = requireNonNull(alterer);
		}
	}

	


}
