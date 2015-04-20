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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
	public int alter(final Population<G, C> population, final long generation) {
		final int altered = _adoptee.alter(population, generation);
		for (int i = 0, n = population.size(); i < n; ++i) {
			if (!population.get(i).isValid()) {
				population.set(i, repair(population.get(i)));
			}
		}
		return altered;
	}

	private Phenotype<G, C> repair(final Phenotype<G, C> pt) {
		return Phenotype.of(
			_repair.apply(pt.getGenotype()),
			pt.getGeneration(),
			pt.getFitnessFunction(),
			pt.getFitnessScaler()
		);
	}

}
