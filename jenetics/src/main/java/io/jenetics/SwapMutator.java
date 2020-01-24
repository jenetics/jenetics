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

import static io.jenetics.internal.math.Randoms.indexes;

import java.util.Random;

import io.jenetics.util.MSeq;

/**
 * The {@code SwapMutation} changes the order of genes in a chromosome, with the
 * hope of bringing related genes closer together, thereby facilitating the
 * production of building blocks. This mutation operator can also be used for
 * combinatorial problems, where no duplicated genes within a chromosome are
 * allowed, e.g. for the TSP.
 * <p>
 * This mutator is also known as <em>Partial Shuffle Mutator</em> (PSM).
 *
 * @see <a href="https://arxiv.org/ftp/arxiv/papers/1203/1203.3099.pdf">
 *     Analyzing the Performance of Mutation Operators to Solve the Travelling
 *     Salesman Problem</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.0
 */
public class SwapMutator<
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
	public SwapMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public SwapMutator() {
		this(DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Swaps the genes in the given array, with the mutation probability of this
	 * mutation.
	 */
	@Override
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final Random random
	) {
		final MutatorResult<Chromosome<G>> result;
		if (chromosome.length() > 1) {
			final MSeq<G> genes = MSeq.of(chromosome);
			final int mutations = (int)indexes(random, genes.length(), p)
				.peek(i -> genes.swap(i, random.nextInt(genes.length())))
				.count();
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
