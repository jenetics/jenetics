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
package org.jenetics;

import static java.lang.Math.min;

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * <p>
 * Performs a <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">
 * Crossover</a> of two {@link Chromosome}. This crossover implementation can
 * handle genotypes with different length (different number of chromosomes). It
 * is guaranteed that chromosomes with the the same (genotype) index are chosen
 * for <em>crossover</em>.
 * </p>
 * <p>
 * The order ({@link #getOrder()}) of this Recombination implementation is two.
 * </p>
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.6
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
		final Population<G, C> population,
		final int[] individuals,
		final long generation
	) {
		assert individuals.length == 2 : "Required order of 2";
		final Random random = RandomRegistry.getRandom();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();

		//Choosing the Chromosome index for crossover.
		final int chIndex = random.nextInt(min(gt1.length(), gt2.length()));

		final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
		final MSeq<Chromosome<G>> c2 = gt2.toSeq().copy();
		final MSeq<G> genes1 = c1.get(chIndex).toSeq().copy();
		final MSeq<G> genes2 = c2.get(chIndex).toSeq().copy();

		crossover(genes1, genes2);

		c1.set(chIndex, c1.get(chIndex).newInstance(genes1.toISeq()));
		c2.set(chIndex, c2.get(chIndex).newInstance(genes2.toISeq()));

		//Creating two new Phenotypes and exchanging it with the old.
		population.set(
			individuals[0],
			pt1.newInstance(gt1.newInstance(c1.toISeq()), generation)
		);
		population.set(
			individuals[1],
			pt2.newInstance(gt1.newInstance(c2.toISeq()), generation)
		);

		return getOrder();
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
