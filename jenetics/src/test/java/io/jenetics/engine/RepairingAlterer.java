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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import io.jenetics.Alterer;
import io.jenetics.AltererResult;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Phenotype;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class RepairingAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Alterer<G, C>
{
	private final Alterer<G, C> _adoptee;
	private final Function<Genotype<G>, Genotype<G>> _repair;

	public RepairingAlterer(
		final Alterer<G, C> adoptee,
		final Function<Genotype<G>, Genotype<G>> repair
	) {
		_adoptee = requireNonNull(adoptee);
		_repair = requireNonNull(repair);
	}

	@Override
	public AltererResult<G, C> alter(
		final Seq<Phenotype<G, C>> population,
		final long generation
	) {
		final AltererResult<G, C> result = _adoptee.alter(population, generation);

		final MSeq<Phenotype<G, C>> pop = MSeq.of(population);
		for (int i = 0, n = pop.size(); i < n; ++i) {
			if (!pop.get(i).isValid()) {
				pop.set(i, repair(pop.get(i)));
			}
		}
		return new AltererResult<>(pop.toISeq(), result.alterations());
	}

	private Phenotype<G, C> repair(final Phenotype<G, C> pt) {
		return Phenotype.of(
			_repair.apply(pt.genotype()),
			pt.generation()
		);
	}

}
