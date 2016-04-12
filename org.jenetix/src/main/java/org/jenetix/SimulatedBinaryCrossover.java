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
package org.jenetix;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static org.jenetics.internal.math.base.clamp;

import java.util.Random;

import org.jenetics.internal.math.random;
import org.jenetics.internal.util.require;

import org.jenetics.Crossover;
import org.jenetics.NumericGene;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;

/**
 * Performs the simulated binary crossover (SBX) on a {@code Chromosome} of
 * {@link NumericGene}s such that each position is either crossed contracted or
 * expanded with a certain probability. The probability distribution is designed
 * such that the children will lie closer to their parents as is the case with
 * the single point binary crossover.
 * <p>
 * It is implemented as described in Deb, K. and Agrawal, R. B. 1995. Simulated
 * binary crossover for continuous search space. Complex Systems, 9, pp. 115-148.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.5
 * @version 3.5
 */
public class SimulatedBinaryCrossover<
	G extends NumericGene<?, G>,
	C extends Comparable<? super C>
>
	extends Crossover<G, C>
{
	private final double _contiguity;

	/**
	 * Create a new <i>simulated binary crossover</i> alterer with the given
	 * parameters.
	 *
	 * @param probability the recombination probability
	 * @param contiguity the contiguity value that specifies how close a child
	 *       should be to its parents (larger value means closer). The value
	 *       must be greater or equal than 0. Typical values are in the range
	 *       [2..5].
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}
	 * @throws IllegalArgumentException if {@code contiguity} is smaller than
	 *         zero
	 */
	public SimulatedBinaryCrossover(
		final double probability,
		final double contiguity
	) {
		super(probability);
		_contiguity = require.nonNegative(contiguity);
	}

	/**
	 * Create a new <i>simulated binary crossover</i> alterer with the given
	 * parameters. The <i>contiguity</i> value is set to {@code 2.5}.
	 *
	 * @param probability the recombination probability
	 * @throws IllegalArgumentException if the {@code probability} is not in the
	 *          valid range of {@code [0, 1]}
	 * @throws IllegalArgumentException if {@code contiguity} is smaller than
	 *         zero
	 */
	public SimulatedBinaryCrossover(final double probability) {
		this(probability, 2.5);
	}

	/**
	 * Return the <i>contiguity</i> value of the crossover.
	 *
	 * @return the <i>contiguity</i> value of the crossover
	 */
	public double getContiguity() {
		return _contiguity;
	}

	@Override
	protected int crossover(final MSeq<G> that, final MSeq<G> other) {
		return (int)random.indexes(RandomRegistry.getRandom(), that.length(), 0.5)
			.peek(i -> crossover(that, other, i))
			.count();
	}

	private void crossover(final MSeq<G> that, final MSeq<G> other, final int i) {
		final Random random = RandomRegistry.getRandom();

		final double u = random.nextDouble();
		final double beta;
		if (u < 0.5) {
			// If u is smaller than 0.5 perform a contracting crossover.
			beta = pow(2*u, 1.0/(_contiguity + 1));
		} else if (u > 0.5) {
			// Otherwise perform an expanding crossover.
			beta = pow(0.5 / (1.0 - u), 1.0/(_contiguity + 1));
		} else if (u == 0.5) {
			beta = 1;
		} else {
			beta = 0;
		}

		final double v1 = that.get(i).doubleValue();
		final double v2 = other.get(i).doubleValue();
		final double v = random.nextBoolean()
			? ((v1 - v2)*0.5) - beta*0.5*abs(v1 - v2)
			: ((v1 - v2)*0.5) + beta*0.5*abs(v1 - v2);

		final double min = that.get(i).getMin().doubleValue();
		final double max = that.get(i).getMax().doubleValue();
		that.set(i, that.get(i).newInstance(clamp(v, min, max)));
	}

}
