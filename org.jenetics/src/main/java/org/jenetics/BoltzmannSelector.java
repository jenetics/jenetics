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

import static java.lang.Math.exp;
import static java.lang.String.format;
import static org.jenetics.internal.math.arithmetic.normalize;

import java.util.Arrays;

import org.jenetics.internal.util.Hash;

/**
 * <p>
 * In this {@code Selector}, the probability for selection is defined as.
 * </p>
 * <p><img
 *        src="doc-files/boltzmann-formula1.gif"
 *        alt="P(i)=\frac{\textup{e}^{b\cdot f_i}}{Z}"
 *     >
 * </p>
 * where <i>b</i> controls the selection intensity, and
 * <p><img
 *        src="doc-files/boltzmann-formula2.gif"
 *        alt="Z=\sum_{j=1}^{n}\textrm{e}^{f_j}"
 *     >.
 * </p>
 *
 * <i>f</i><sub><i>j</i></sub> denotes the fitness value of the
 * <i>j<sup>th</sup></i> individual.
 * <br>
 * Positive values of <i>b</i> increases the selection probability of the phenotype
 * with high fitness values. Negative values of <i>b</i> increases the selection
 * probability of phenotypes with low fitness values. If <i>b</i> is zero the
 * selection probability of all phenotypes is set to <sup>1</sup>/<sub>N</sub>.
 *
 * @param <G> the gene type.
 * @param <N> the BoltzmannSelector requires a number type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.2
 */
public final class BoltzmannSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends ProbabilitySelector<G, N>
{

	private final double _b;

	/**
	 * Create a new BoltzmannSelector with the given <i>b</i> value. <b>High
	 * absolute values of <i>b</i> can create numerical overflows while
	 * calculating the selection probabilities.</b>
	 *
	 * @param b the <i>b</i> value of this BoltzmannSelector
	 */
	public BoltzmannSelector(final double b) {
		_b = b;
	}

	/**
	 * Create a new BoltzmannSelector with a default beta of 4.0.
	 */
	public BoltzmannSelector() {
		this(4.0);
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population,
		final int count
	) {
		assert population != null : "Population must not be null. ";
		assert !population.isEmpty() : "Population is empty.";
		assert count > 0 : "Population to select must be greater than zero. ";

		// Copy the fitness values to probabilities arrays.
		final double[] fitness = new double[population.size()];

		fitness[0] = population.get(0).getFitness().doubleValue();
		double min = fitness[0];
		double max = fitness[0];
		for (int i = 1; i < fitness.length; ++i) {
			fitness[i] = population.get(i).getFitness().doubleValue();
			if (fitness[i] < min) min = fitness[i];
			else if (fitness[i] > max) max = fitness[i];
		}

		final double diff = max - min;
		if (eq(diff, 0.0)) {
			// Set equal probabilities if diff (almost) zero.
			Arrays.fill(fitness, 1.0/fitness.length);
		} else {
			// Scale fitness values to avoid overflow.
			for (int i = fitness.length; --i >= 0;) {
				fitness[i] = (fitness[i] - min)/diff;
			}

			// Apply the "Boltzmann" function.
			for (int i = fitness.length; --i >= 0;) {
				fitness[i] = exp(_b*fitness[i]);
			}
		}

		return normalize(fitness);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_b).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof BoltzmannSelector &&
			Double.compare(((BoltzmannSelector)obj)._b, _b) == 0;
	}

	@Override
	public String toString() {
		return format("BoltzmannSelector[b=%f]", _b);
	}

}
