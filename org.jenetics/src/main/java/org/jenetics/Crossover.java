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

import java.util.Random;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * <p>
 * Performs a <a href="http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">
 * Crossover</a> of two {@link Chromosome}.
 * </p>
 * <p>
 * The order ({@link #getOrder()}) of this Recombination implementation is two.
 * </p>
 *
 * @param <G> the gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public abstract class Crossover<G extends Gene<?, G>> extends Recombinator<G> {

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}.
	 */
	protected Crossover(final double probability) {
		super(probability, 2);
	}

	@Override
	protected final <C extends Comparable<? super C>> int recombine(
		final Population<G, C> population,
		final int[] individuals,
		final int generation
	) {
		final Random random = RandomRegistry.getRandom();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();

		//Choosing the Chromosome for crossover.
		final int chIndex = random.nextInt(gt1.length());

		final MSeq<Chromosome<G>> chromosomes1 = gt1.toSeq().copy();
		final MSeq<Chromosome<G>> chromosomes2 = gt2.toSeq().copy();
		final MSeq<G> genes1 = chromosomes1.get(chIndex).toSeq().copy();
		final MSeq<G> genes2 = chromosomes2.get(chIndex).toSeq().copy();

		crossover(genes1, genes2);

		chromosomes1.set(chIndex, chromosomes1.get(chIndex).newInstance(genes1.toISeq()));
		chromosomes2.set(chIndex, chromosomes2.get(chIndex).newInstance(genes2.toISeq()));

		//Creating two new Phenotypes and exchanging it with the old.
		population.set(
			individuals[0],
			pt1.newInstance(Genotype.valueOf(chromosomes1.toISeq()), generation)
		);
		population.set(
			individuals[1],
			pt2.newInstance(Genotype.valueOf(chromosomes2.toISeq()), generation)
		);

		return getOrder();
	}


	/**
	 * Template method which performs the crossover. The arguments given are
	 * mutable non null arrays of the same length.
	 */
	protected abstract int crossover(final MSeq<G> that, final MSeq<G> other);


}




