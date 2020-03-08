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
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.BinaryOperator;

import io.jenetics.util.BaseSeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * Alters a chromosome by replacing two genes by the result of a given
 * <em>combiner</em> function.
 *
 * <p>
 * The order ({@link #order()}) of this recombination implementation is two.
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.0
 * @version 6.0
 */
public class CombineAlterer<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Recombinator<G, C>
{

	private final BinaryOperator<G> _combiner;

	/**
	 * Create a new combiner alterer with the given arguments.
	 *
	 * @param combiner the function used for combining two genes
	 * @param probability The recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}
	 * @throws NullPointerException if the given {@code combiner} is {@code null}
	 */
	public CombineAlterer(
		final BinaryOperator<G> combiner,
		final double probability
	) {
		super(probability, 2);
		_combiner = requireNonNull(combiner);
	}

	/**
	 * Create a new combiner alterer with the given arguments.
	 *
	 * @param combiner the function used for combining two genes
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}
	 * @throws NullPointerException if the given {@code combiner} is {@code null}
	 */
	public CombineAlterer(final BinaryOperator<G> combiner) {
		this(combiner, DEFAULT_ALTER_PROBABILITY);
	}

	/**
	 * Return the combiner function, used by {@code this} alterer.
	 *
	 * @return the combiner function, used by {@code this} alterer
	 */
	public BinaryOperator<G> combiner() {
		return _combiner;
	}

	@Override
	protected int recombine(
		final MSeq<Phenotype<G, C>> population,
		final int[] individuals,
		final long generation
	) {
		final Random random = RandomRegistry.random();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.genotype();
		final Genotype<G> gt2 = pt2.genotype();

		//Choosing the Chromosome index for crossover.
		final int ci = random.nextInt(min(gt1.length(), gt2.length()));

		final MSeq<Chromosome<G>> c1 = MSeq.of(gt1);

		// Calculate the mean value of the gene array.
		final MSeq<G> mean = combine(c1.get(ci), gt2.get(ci), _combiner);

		c1.set(ci, c1.get(ci).newInstance(mean.toISeq()));
		population.set(individuals[0], Phenotype.of(Genotype.of(c1), generation));

		return 1;
	}

	private static <G extends Gene<?, G>>
	MSeq<G> combine(
		final BaseSeq<G> a,
		final BaseSeq<G> b,
		final BinaryOperator<G> combiner
	) {
		final MSeq<G> result = MSeq.ofLength(a.length());
		for (int i = a.length(); --i >= 0;) {
			result.set(i, combiner.apply(a.get(i), b.get(i)));
		}
		return result;
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}
}
