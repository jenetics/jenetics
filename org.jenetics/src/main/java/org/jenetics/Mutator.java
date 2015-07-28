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

import static java.lang.Math.pow;
import static java.lang.String.format;
import static org.jenetics.internal.math.random.indexes;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.IntRef;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * This class is for mutating a chromosomes of an given population. There are
 * two distinct roles mutation plays
 * <ul>
 *     <li>Exploring the search space. By making small moves mutation allows a
 *     population to explore the search space. This exploration is often slow
 *     compared to crossover, but in problems where crossover is disruptive this
 *     can be an important way to explore the landscape.
 *     </li>
 *     <li>Maintaining diversity. Mutation prevents a population from
 *     correlating. Even if most of the search is being performed by crossover,
 *     mutation can be vital to provide the diversity which crossover needs.
 *     </li>
 * </ul>
 *
 * <p>
 * The mutation probability is the parameter that must be optimized. The optimal
 * value of the mutation rate depends on the role mutation plays. If mutation is
 * the only source of exploration (if there is no crossover) then the mutation
 * rate should be set so that a reasonable neighborhood of solutions is explored.
 * </p>
 * The mutation probability <i>P(m)</i> is the probability that a specific gene
 * over the whole population is mutated. The number of available genes of an
 * population is
 * <p>
 * <img src="doc-files/mutator-N_G.gif" alt="N_P N_{g}=N_P \sum_{i=0}^{N_{G}-1}N_{C[i]}" >
 * </p>
 * where <i>N<sub>P</sub></i>  is the population size, <i>N<sub>g</sub></i> the
 * number of genes of a genotype. So the (average) number of genes
 * mutated by the mutation is
 * <p>
 * <img src="doc-files/mutator-mean_m.gif" alt="\hat{\mu}=N_{P}N_{g}\cdot P(m)" >
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0
 */
public class Mutator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractAlterer<G, C>
{

	/**
	 * Construct a Mutation object which a given mutation probability.
	 *
	 * @param probability Mutation probability. The given probability is
	 *         divided by the number of chromosomes of the genotype to form
	 *         the concrete mutation probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}..
	 */
	public Mutator(final double probability) {
		super(probability);
	}

	/**
	 * Default constructor, with probability = 0.01.
	 */
	public Mutator() {
		this(0.01);
	}

	/**
	 * Concrete implementation of the alter method.
	 */
	@Override
	public int alter(
		final Population<G, C> population,
		final long generation
	) {
		assert population != null : "Not null is guaranteed from base class.";

		final double p = pow(_probability, 1.0/3.0);
		final IntRef alterations = new IntRef(0);

		indexes(RandomRegistry.getRandom(), population.size(), p).forEach(i -> {
			final Phenotype<G, C> pt = population.get(i);

			final Genotype<G> gt = pt.getGenotype();
			final Genotype<G> mgt = mutate(gt, p, alterations);

			final Phenotype<G, C> mpt = pt.newInstance(mgt, generation);
			population.set(i, mpt);
		});

		return alterations.value;
	}

	private Genotype<G> mutate(
		final Genotype<G> genotype,
		final double p,
		final IntRef alterations
	) {
		final MSeq<Chromosome<G>> chromosomes = genotype.toSeq().copy();

		alterations.value +=
			indexes(RandomRegistry.getRandom(), genotype.length(), p)
				.map(i -> mutate(chromosomes, i, p))
				.sum();

		return genotype.newInstance(chromosomes.toISeq());
	}

	private int mutate(final MSeq<Chromosome<G>> c, final int i, final double p) {
		final Chromosome<G> chromosome = c.get(i);
		final MSeq<G> genes = chromosome.toSeq().copy();

		final int mutations = mutate(genes, p);
		if (mutations > 0) {
			c.set(i, chromosome.newInstance(genes.toISeq()));
		}
		return mutations;
	}

	/**
	 * <p>
	 * Template method which gives an (re)implementation of the mutation class
	 * the possibility to perform its own mutation operation, based on a
	 * writable gene array and the gene mutation probability <i>p</i>.
	 *
	 * @param genes the genes to mutate.
	 * @param p the gene mutation probability.
	 * @return the number of performed mutations
	 */
	protected int mutate(final MSeq<G> genes, final double p) {
		return (int)indexes(RandomRegistry.getRandom(), genes.length(), p)
			.peek(i -> genes.set(i, genes.get(i).newInstance()))
			.count();
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
