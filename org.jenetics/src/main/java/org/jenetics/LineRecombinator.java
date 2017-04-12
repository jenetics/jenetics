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
import static java.lang.String.format;

import java.util.Random;

import org.jenetics.internal.util.Hash;

import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Mean;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Seq;

/**
 * This alterer takes two chromosome (treating it as vectors) and creates a
 * linear combination of this vectors as result.
 *
 * @see <a href="https://cs.gmu.edu/~sean/book/metaheuristics/"><em>
 *       Essentials of Metaheuristic, page 42</em></a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinearRecombinator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
	>
	extends Recombinator<G, C>
{

	/**
	 * Creates a new linear-recombinator with the given recombination
	 * probability.
	 *
	 * @param probability the recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}.
	 */
	public LinearRecombinator(final double probability) {
		super(probability, 2);
	}

	@Override
	protected int recombine(
		final Population<G, C> population,
		final int[] individuals,
		final long generation
	) {
		assert individuals.length == 2;
		final Random random = RandomRegistry.getRandom();

		final Phenotype<G, C> pt1 = population.get(individuals[0]);
		final Phenotype<G, C> pt2 = population.get(individuals[1]);
		final Genotype<G> gt1 = pt1.getGenotype();
		final Genotype<G> gt2 = pt2.getGenotype();

		//Choosing the Chromosome index for crossover.
		final int cindex = random.nextInt(min(gt1.length(), gt2.length()));

		final MSeq<Chromosome<G>> c1 = gt1.toSeq().copy();
		final MSeq<Chromosome<G>> c2 = gt2.toSeq().copy();

		final MSeq<G> v = c1.get(cindex).toSeq().copy();
		final MSeq<G> w = c2.get(cindex).toSeq().copy();

		mean(v, w, random);

		c1.set(cindex, c1.get(cindex).newInstance(v.toISeq()));
		c2.set(cindex, c2.get(cindex).newInstance(w.toISeq()));

		population.set(
			individuals[0],
			pt1.newInstance(gt1.newInstance(c1.toISeq()), generation)
		);
		population.set(
			individuals[1],
			pt2.newInstance(gt2.newInstance(c2.toISeq()), generation)
		);

		return 2;
	}

	private void mean(final MSeq<G> v, final MSeq<G> w, final Random random) {
		for (int i = 0, n = min(v.length(), w.length()); i < n; ++i) {

		}
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LinearRecombinator && super.equals(obj);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
