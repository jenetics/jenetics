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
import static org.jenetics.internal.math.random.nextDouble;

import java.util.Random;

import org.jenetics.internal.util.Hash;
import org.jenetics.internal.util.require;

import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * This alterer takes two chromosome (treating it as vectors) and creates a
 * linear combination of this vectors as result. The  line-recombination depends
 * on a variable <em>p</em> which determines how far out along the line (defined
 * by the two multidimensional points/vectors) the children are allowed to be.
 * If <em>p</em> = 0 then the children will be located along the line within the
 * hypercube between the two points. If <em>p</em> > 0 then the children may be
 * located anywhere on the line, even somewhat outside of the hypercube.
 *
 * @see <a href="https://cs.gmu.edu/~sean/book/metaheuristics/"><em>
 *       Essentials of Metaheuristic, page 42</em></a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LineRecombinator<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
	>
	extends Recombinator<G, C>
{

	private final double _p;

	/**
	 * Creates a new linear-recombinator with the given recombination
	 * probability and the line-scaling factor <em>p</em>.
	 *
	 * @param probability the recombination probability.
	 * @param p defines the possible location of the recombined chromosomes. If
	 *        <em>p</em> = 0 then the children will be located along the line
	 *        within the hypercube between the two points. If <em>p</em> > 0
	 *        then the children may be located anywhere on the line, even
	 *        somewhat outside of the hypercube.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]} or if {@code p} is smaller then zero
	 */
	public LineRecombinator(final double probability, final double p) {
		super(probability, 2);
		_p = require.nonNegative(p, "p");
	}

	/**
	 * Creates a new linear-recombinator with the given recombination
	 * probability. The parameter <em>p</em> is set to zero, which restricts the
	 * recombined chromosomes within the hypercube of the selected chromosomes
	 * (vectors).
	 *
	 * @param probability the recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}
	 */
	public LineRecombinator(final double probability) {
		this(probability, 0);
	}

	/**
	 * Creates a new linear-recombinator with default recombination
	 * probability ({@link #DEFAULT_ALTER_PROBABILITY}) and a <em>p</em> value
	 * of zero, which restricts the recombined chromosomes within the hypercube
	 * of the selected chromosomes (vectors).
	 */
	public LineRecombinator() {
		this(DEFAULT_ALTER_PROBABILITY, 0);
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

		recombine(v, w, random);

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

	void recombine(final MSeq<G> v, final MSeq<G> w, final Random random) {
		final double min = v.get(0).getMin().doubleValue();
		final double max = v.get(0).getMax().doubleValue();

		final double a = nextDouble(random, -_p, 1 + _p);
		final double b = nextDouble(random, -_p, 1 + _p);

		for (int i = 0, n = min(v.length(), w.length()); i < n; ++i) {
			final double vi = v.get(i).doubleValue();
			final double wi = w.get(i).doubleValue();

			final double t = a*vi + (1 - a)*wi;
			final double s = b*wi + (1 - b)*vi;

			if (t >= min && s >= min && t < max && s < max) {
				v.set(i, v.get(i).newInstance(t));
				w.set(i, w.get(i).newInstance(s));
			}
		}
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LineRecombinator && super.equals(obj);
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
