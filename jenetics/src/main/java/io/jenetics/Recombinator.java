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

import static java.lang.String.format;
import static io.jenetics.internal.math.base.subset;
import static io.jenetics.internal.math.random.indexes;

import java.util.Random;
import java.util.function.IntFunction;

import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * <p>
 * An enhanced genetic algorithm (EGA) combine elements of existing solutions in
 * order to create a new solution, with some of the properties of each parent.
 * Recombination creates a new chromosome by combining parts of two (or more)
 * parent chromosomes. This combination of chromosomes can be made by selecting
 * one or more crossover points, splitting these chromosomes on the selected
 * points, and merge those portions of different chromosomes to form new ones.
 * </p>
 * <p>
 * The recombination probability <i>P(r)</i> determines the probability that a
 * given individual (genotype, not gene) of a population is selected for
 * recombination. The (<i>mean</i>) number of changed individuals depend on the
 * concrete implementation and can be vary from
 * <i>P(r)</i>&middot;<i>N<sub>G</sub></i> to
 * <i>P(r)</i>&middot;<i>N<sub>G</sub></i>&middot;<i>O<sub>R</sub></i>, where
 * <i>O<sub>R</sub></i> is the order of the recombination, which is the number
 * of individuals involved int the {@link #recombine} method.
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 4.0
 */
public abstract class Recombinator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends AbstractAlterer<G, C>
{

	private final int _order;

	/**
	 * Constructs an alterer with a given recombination probability.
	 *
	 * @param probability The recombination probability.
	 * @param order the number of individuals involved in the
	 *        {@link #recombine(MSeq, int[], long)} step
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]} or the given {@code order} is
	 *         smaller than two.
	 */
	protected Recombinator(final double probability, final int order) {
		super(probability);
		if (order < 2) {
			throw new IllegalArgumentException(format(
				"Order must be greater than one, but was %d.", order
			));
		}
		_order = order;
	}

	/**
	 * Return the number of individuals involved in the
	 * {@link #recombine(MSeq, int[], long)} step.
	 *
	 * @return the number of individuals involved in the recombination step.
	 */
	public int getOrder() {
		return _order;
	}

	@Override
	public final AlterResult<G, C> alter(
		final Seq<Phenotype<G, C>> population,
		final long generation
	) {
		final AlterResult<G, C> result;
		if (population.size() >= 2) {
			final Random random = RandomRegistry.getRandom();
			final int order = Math.min(_order, population.size());

			// Selection of the individuals for recombination.
			final IntFunction<int[]> individuals = i -> {
				final int[] ind = subset(population.size(), order, random);
				ind[0] = i;
				return ind;
			};

			final MSeq<Phenotype<G, C>> pop = MSeq.of(population);
			final int count = indexes(random, population.size(), _probability)
				.mapToObj(individuals)
				.mapToInt(i -> recombine(pop, i, generation))
				.sum();

			result = AlterResult.of(pop.toISeq(), count);
		} else {
			result = AlterResult.of(population.asISeq());
		}

		return result;
	}

	/**
	 * Recombination template method. This method is called 0 to n times. It is
	 * guaranteed that this method is only called by one thread.
	 *
	 * @param population the population to recombine
	 * @param individuals the array with the indexes of the individuals which
	 *        are involved in the <i>recombination</i> step. The length of the
	 *        array is {@link #getOrder()}. The first individual is the
	 *        <i>primary</i> individual.
	 * @param generation the current generation.
	 * @return the number of genes that has been altered.
	 */
	protected abstract int recombine(
		final MSeq<Phenotype<G, C>> population,
		final int[] individuals,
		final long generation
	);

}
