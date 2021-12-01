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

import java.util.random.RandomGenerator;

import io.jenetics.AbstractAlterer;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Mutator;
import io.jenetics.MutatorResult;
import io.jenetics.internal.math.Probabilities;
import io.jenetics.internal.math.Subset;
import io.jenetics.util.MSeq;

/**
 * The Hybridizing PSM and RSM Operator (HPRM) constructs an offspring from a
 * pair of parents by hybridizing two mutation operators, PSM and RSM.
 * <p>
 * This mutator is described in <a href="https://arxiv.org/abs/1203.5028">A New
 * Mutation Operator for Solving an NP-Complete Problem: Travelling Salesman
 * Problem</a>, by <em>Otman Abdoun, Chakir Tajani</em> and
 * <em>Jaafar Abouchabka</em>.
 *
 * @see RSMutator
 * @see io.jenetics.SwapMutator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
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
		final RandomGenerator random
	) {
		final MutatorResult<Chromosome<G>> result;
		if (chromosome.length() > 1) {
			final int P = Probabilities.toInt(p);
			final int[] points = Subset.next(chromosome.length(), 2, random);
			final MSeq<G> genes = MSeq.of(chromosome);

			int mutations = (points[1] - points[0] + 1)/2;
			for (int i = points[0], j = points[1]; i < j; ++i, --j) {
				genes.swap(i, j);
				if (random.nextInt() < P) {
					genes.swap(i, random.nextInt(chromosome.length()));
					++mutations;
				}
			}

			result = new MutatorResult<>(
				chromosome.newInstance(genes.toISeq()),
				mutations
			);
		} else {
			result = new MutatorResult<>(chromosome, 0);
		}

		return result;
	}

}
