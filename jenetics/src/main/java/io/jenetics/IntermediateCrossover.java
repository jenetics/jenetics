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
import static io.jenetics.internal.math.Randoms.nextDouble;

import java.util.Random;

import io.jenetics.internal.util.Requires;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

/**
 * This alterer takes two chromosome (treating it as vectors) and creates a
 * linear combination of this vectors as result. The  line-recombination depends
 * on a variable <em>p</em> which determines how far out along the line (defined
 * by the two multidimensional points/vectors) the children are allowed to be.
 * If <em>p</em> = 0 then the children will be located along the line within the
 * hypercube between the two points. If <em>p</em> &gt; 0 then the children may
 * be located anywhere on the line, even somewhat outside of the hypercube.
 * <p>
 * Points outside of the allowed numeric range are rejected and a new points are
 * generated, until they lie in the valid range. The strategy on how
 * out-of-range points are handled, is the difference to the very similar
 * {@link LineCrossover}.
 *
 * @see <a href="https://cs.gmu.edu/~sean/book/metaheuristics/"><em>
 *       Essentials of Metaheuristic, page 42</em></a>
 * @see LineCrossover
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.8
 * @since 3.8
 */
public class IntermediateCrossover<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends Crossover<G, C>
{

	private final double _p;

	/**
	 * Creates a new intermediate-crossover with the given recombination
	 * probability and the line-scaling factor <em>p</em>.
	 * <p>
	 * <b>When the value for <em>p</em> is greater then 0, the crossover point
	 * generation must be repeated until the points lie within the allowed
	 * range. Values greater then 10 are usually not recommended, since this
	 * leads to unnecessary crossover point generation.</b>
	 *
	 * @param probability the recombination probability.
	 * @param p defines the possible location of the recombined chromosomes. If
	 *        <em>p</em> = 0 then the children will be located along the line
	 *        within the hypercube between the two points. If <em>p</em> &gt; 0
	 *        then the children may be located anywhere on the line, even
	 *        somewhat outside of the hypercube.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]} or if {@code p} is smaller then zero
	 */
	public IntermediateCrossover(final double probability, final double p) {
		super(probability);
		_p = Requires.nonNegative(p, "p");
	}

	/**
	 * Creates a new intermediate-crossover with the given recombination
	 * probability. The parameter <em>p</em> is set to zero, which restricts the
	 * recombined chromosomes within the hypercube of the selected chromosomes
	 * (vectors).
	 *
	 * @param probability the recombination probability.
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *         valid range of {@code [0, 1]}
	 */
	public IntermediateCrossover(final double probability) {
		this(probability, 0);
	}

	/**
	 * Creates a new intermediate-crossover with default recombination
	 * probability ({@link #DEFAULT_ALTER_PROBABILITY}) and a <em>p</em> value
	 * of zero, which restricts the recombined chromosomes within the hypercube
	 * of the selected chromosomes (vectors).
	 */
	public IntermediateCrossover() {
		this(DEFAULT_ALTER_PROBABILITY, 0);
	}

	@Override
	protected int crossover(final MSeq<G> v, final MSeq<G> w) {
		final Random random = RandomRegistry.random();

		final double min = v.get(0).min().doubleValue();
		final double max = v.get(0).max().doubleValue();

		for (int i = 0, n = min(v.length(), w.length()); i < n; ++i) {
			final var g1 = v.get(i);
			final var g2 = w.get(i);

			if (g1.isValid() && g2.isValid()) {
				final double vi = g1.doubleValue();
				final double wi = g2.doubleValue();

				double t, s;
				do {
					final double a = nextDouble(-_p, 1 + _p, random);
					final double b = nextDouble(-_p, 1 + _p, random);

					t = a*vi + (1 - a)*wi;
					s = b*wi + (1 - b)*vi;
				} while (t < min || s < min || t >= max || s >= max);

				v.set(i, v.get(i).newInstance(t));
				w.set(i, w.get(i).newInstance(s));
			}
		}

		return 2;
	}

	@Override
	public String toString() {
		return format("%s[p=%f]", getClass().getSimpleName(), _probability);
	}

}
