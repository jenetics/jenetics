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
package io.jenetics.ext;

import java.util.Random;

import io.jenetics.AbstractAlterer;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.comb;
import io.jenetics.internal.math.probability;
import io.jenetics.util.MSeq;

/**
 * The Hybridizing
 *  ing PSM and RSM Operator (HPRM)
 * constructs an offspring from a pair of parents by hybridizing
 * two mutation
 *  tion operators, PSM and RSM.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class HPRMutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Mutator<G, C>
{

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the crossover probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	public HPRMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public HPRMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final Random random
	) {
		final MutatorResult<Chromosome<G>> result;
		if (chromosome.length() > 1) {
			final int P = probability.toInt(p);
			final int[] points = comb.subset(chromosome.length() + 1, 2);
			final MSeq<G> genes = chromosome.toSeq().copy();

			int mutations = 0;
			for (int i = points[0], j = points[1]; i < j; ++i, --j) {
				genes.swap(i, j);
				++mutations;

				if (random.nextInt() < P) {
					genes.swap(i, random.nextInt(chromosome.length()));
					++mutations;
				}
			}

			result = MutatorResult.of(
				chromosome.newInstance(genes.toISeq()),
				mutations
			);
		} else {
			result = MutatorResult.of(chromosome);
		}

		return result;
	}

}