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

import static java.lang.Math.pow;
import static java.lang.String.format;

import java.util.Random;

import io.jenetics.internal.math.Probabilities;
import io.jenetics.util.ISeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

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
 * <img src="doc-files/mutator-N_G.svg" alt="N_P N_{g}=N_P \sum_{i=0}^{N_{G}-1}N_{C[i]}" >
 * </p>
 * where <i>N<sub>P</sub></i>  is the population size, <i>N<sub>g</sub></i> the
 * number of genes of a genotype. So the (average) number of genes
 * mutated by the mutation is
 * <p>
 * <img src="doc-files/mutator-mean_m.svg" alt="\hat{\mu}=N_{P}N_{g}\cdot P(m)" >
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
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
	 *          valid range of {@code [0, 1]}.
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
	 * Concrete implementation of the alter method. It uses the following
	 * mutation methods: {@link #mutate(Phenotype, long, double, Random)},
	 * {@link #mutate(Genotype, double, Random)},
	 * {@link #mutate(Chromosome, double, Random)}, {@link #mutate(Gene, Random)},
	 * in this specific order.
	 *
	 * @see #mutate(Phenotype, long, double, Random)
	 * @see #mutate(Genotype, double, Random)
	 * @see #mutate(Chromosome, double, Random)
	 * @see #mutate(Gene, Random)
	 */
	@Override
	public AltererResult<G, C> alter(
		final Seq<Phenotype<G, C>> population,
		final long generation
	) {
		assert population != null : "Not null is guaranteed from base class.";

		final Random random = RandomRegistry.random();
		final double p = pow(_probability, 1.0/3.0);
		final int P = Probabilities.toInt(p);

		final Seq<MutatorResult<Phenotype<G, C>>> result = population
			.map(pt -> random.nextInt() < P
				? mutate(pt, generation, p, random)
				: MutatorResult.of(pt));

		return AltererResult.of(
			result.map(MutatorResult::result).asISeq(),
			result.stream().mapToInt(MutatorResult::mutations).sum()
		);
	}

	/**
	 * Mutates the given phenotype.
	 *
	 * @see #mutate(Genotype, double, Random)
	 * @see #mutate(Chromosome, double, Random)
	 * @see #mutate(Gene, Random)
	 *
	 * @param phenotype the phenotype to mutate
	 * @param generation the actual generation
	 * @param p the mutation probability for the underlying genetic objects
	 * @param random the random engine used for the phenotype mutation
	 * @return the mutation result
	 */
	protected MutatorResult<Phenotype<G, C>> mutate(
		final Phenotype<G, C> phenotype,
		final long generation,
		final double p,
		final Random random
	) {
		return mutate(phenotype.genotype(), p, random)
			.map(gt -> Phenotype.of(gt, generation));
	}

	/**
	 * Mutates the given genotype.
	 *
	 * @see #mutate(Chromosome, double, Random)
	 * @see #mutate(Gene, Random)
	 *
	 * @param genotype the genotype to mutate
	 * @param p the mutation probability for the underlying genetic objects
	 * @param random the random engine used for the genotype mutation
	 * @return the mutation result
	 */
	protected MutatorResult<Genotype<G>> mutate(
		final Genotype<G> genotype,
		final double p,
		final Random random
	) {
		final int P = Probabilities.toInt(p);
		final ISeq<MutatorResult<Chromosome<G>>> result = genotype.stream()
			.map(gt -> random.nextInt() < P
				? mutate(gt, p, random)
				: MutatorResult.of(gt))
			.collect(ISeq.toISeq());

		return MutatorResult.of(
			Genotype.of(result.map(MutatorResult::result)),
			result.stream().mapToInt(MutatorResult::mutations).sum()
		);
	}

	/**
	 * Mutates the given chromosome.
	 *
	 * @see #mutate(Gene, Random)
	 *
	 * @param chromosome the chromosome to mutate
	 * @param p the mutation probability for the underlying genetic objects
	 * @param random the random engine used for the genotype mutation
	 * @return the mutation result
	 */
	protected MutatorResult<Chromosome<G>> mutate(
		final Chromosome<G> chromosome,
		final double p,
		final Random random
	) {
		final int P = Probabilities.toInt(p);
		final ISeq<MutatorResult<G>> result = chromosome.stream()
			.map(gene -> random.nextInt() < P
				? MutatorResult.of(mutate(gene, random), 1)
				: MutatorResult.of(gene))
			.collect(ISeq.toISeq());

		return MutatorResult.of(
			chromosome.newInstance(result.map(MutatorResult::result)),
			result.stream().mapToInt(MutatorResult::mutations).sum()
		);
	}

	/**
	 * Mutates the given gene.
	 *
	 * @param gene the gene to mutate
	 * @param random the random engine used for the genotype mutation
	 * @return the mutation result
	 */
	protected G mutate(final G gene, final Random random) {
		return gene.newInstance();
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
