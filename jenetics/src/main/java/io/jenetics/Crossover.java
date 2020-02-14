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

import static java.lang.Math.min;

import java.util.Random;

import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * <p>
 * Performs a <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">
 * Crossover</a> of two {@link Chromosome}. This crossover implementation can
 * handle genotypes with different length (different number of chromosomes). It
 * is guaranteed that chromosomes with the the same (genotype) index are chosen
 * for <em>crossover</em>.
 * </p>
 * <p>
 * The order ({@link #order()}) of this Recombination implementation is two.
 * </p>
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public abstract class Crossover<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Recombinator<G, C>
{

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability the recombination probability
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}
	 */
	protected Crossover(final double probability) {
		super(probability, 2);
	}

	@Override
	protected final int recombine(
		final MSeq<Phenotype<G, C>> population,
		final int[] individuals,
		final long generation
	) {
		assert individuals.length == 2 : "Required order of 2";
		final Random random = RandomRegistry.random();

		final var pt1 = population.get(individuals[0]);
		final var pt2 = population.get(individuals[1]);
		final var gt1 = pt1.genotype();
		final var gt2 = pt2.genotype();

		//Choosing the Chromosome index for crossover.
		final int chIndex = random.nextInt(min(gt1.length(), gt2.length()));

		final var c1 = MSeq.of(gt1);
		final var c2 = MSeq.of(gt2);
		final var genes1 = MSeq.of(c1.get(chIndex));
		final var genes2 = MSeq.of(c2.get(chIndex));

		crossover(genes1, genes2);

		c1.set(chIndex, c1.get(chIndex).newInstance(genes1.toISeq()));
		c2.set(chIndex, c2.get(chIndex).newInstance(genes2.toISeq()));

		//Creating two new Phenotypes and exchanging it with the old.
		population.set(
			individuals[0],
			Phenotype.of(Genotype.of(c1), generation)
		);
		population.set(
			individuals[1],
			Phenotype.of(Genotype.of(c2), generation)
		);

		return order();
	}

	/**
	 * Template method which performs the crossover. The arguments given are
	 * mutable non null arrays of the same length.
	 *
	 * @param that the genes of the first chromosome
	 * @param other the genes of the other chromosome
	 * @return the number of altered genes
	 */
	protected abstract int crossover(final MSeq<G> that, final MSeq<G> other);

}
