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

import org.jenetics.internal.util.Hash;

/**
 * <p>
 * An alternative to the "weak" {@code LinearRankSelector} is to assign
 * survival probabilities to the sorted individuals using an exponential
 * function.
 * </p>
 * <p><img
 *        src="doc-files/exponential-rank-selector.gif"
 *        alt="P(i)=\left(c-1\right)\frac{c^{i-1}}{c^{N}-1}"
 *     >,
 * </p>
 * where <i>c</i> must within the range {@code [0..1)}.
 *
 * <p>
 * A small value of <i>c</i> increases the probability of the best phenotypes to
 * be selected. If <i>c</i> is set to zero, the selection probability of the best
 * phenotype is set to one. The selection probability of all other phenotypes is
 * zero. A value near one equalizes the selection probabilities.
 * </p>
 * <p>
 * This selector sorts the population in descending order while calculating the
 * selection probabilities.
 * </p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
public final class ExponentialRankSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends ProbabilitySelector<G, C>
{

	private final double _c;

	/**
	 * Create a new exponential rank selector.
	 *
	 * @param c the <i>c</i> value.
	 * @throws IllegalArgumentException if {@code c} is not within the range
	 *         {@code [0..1)}.
	 */
	public ExponentialRankSelector(final double c) {
		super(true);

		if (c < 0.0 || c >= 1.0) {
			throw new IllegalArgumentException(format(
				"Value %s is out of range [0..1): ", c
			));
		}
		_c = c;
	}

	/**
	 * Create a new selector with default value of 0.975.
	 */
	public ExponentialRankSelector() {
		this(0.975);
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#populationSort()} is called
	 * by this method.)
	 */
	@Override
	protected double[] probabilities(
		final Population<G, C> population,
		final int count
	) {
		assert population != null : "Population must not be null. ";
		assert !population.isEmpty() : "Population is empty.";
		assert count > 0 : "Population to select must be greater than zero. ";

		final double N = population.size();
		final double[] probabilities = new double[population.size()];

		final double b = (_c - 1.0)/(pow(_c, N) - 1.0);
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = pow(_c, i)*b;
		}

		return probabilities;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_c).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof ExponentialRankSelector &&
			eq(((ExponentialRankSelector)obj)._c, _c);
	}

	@Override
	public String toString() {
		return format("%s[c=%f]", getClass().getSimpleName(), _c);
	}

}
