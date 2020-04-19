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
import io.jenetics.internal.math.Combinatorics;
import io.jenetics.util.MSeq;

/**
 * The reverse sequence mutation, two positions i and j are randomly chosen The
 * gene order in a chromosome will then be reversed between this two points.
 * This mutation operator can also be used for combinatorial problems, where no
 * duplicated genes within a chromosome are allowed, e.g. for the TSP.
 *
 * @see io.jenetics.SwapMutator
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
public class RSMutator<
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
	public RSMutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with default mutation probability
	 * ({@link AbstractAlterer#DEFAULT_ALTER_PROBABILITY}).
	 */
	public RSMutator() {
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
			final int[] points = Combinatorics.subset(chromosome.length() + 1, 2);
			final MSeq<G> genes = MSeq.of(chromosome);
			genes.subSeq(points[0], points[1]).reverse();

			result = MutatorResult.of(
				chromosome.newInstance(genes.toISeq()),
				points[1] - points[0] - 1
			);
		} else {
			result = MutatorResult.of(chromosome);
		}

		return result;
	}

}
