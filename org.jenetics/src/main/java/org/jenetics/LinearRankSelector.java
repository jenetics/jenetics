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

import static java.lang.String.format;

import org.jenetics.internal.util.Hash;

/**
 * <p>
 * In linear-ranking selection the individuals are sorted according to their
 * fitness values. The rank <i>N</i> is assignee to the best individual and the
 * rank 1 to the worst individual. The selection probability <i>P(i)</i>  of
 * individual <i>i</i> is linearly assigned to the individuals according to
 * their rank.
 * </p>
 * <p><img
 *        src="doc-files/linear-rank-selector.gif"
 *        alt="P(i)=\frac{1}{N}\left(n^{-}+\left(n^{+}-n^{-}\right)\frac{i-1}{N-1}\right)"
 *     >
 * </p>
 *
 * Here <i>n</i><sup><i>-</i></sup>/<i>N</i> is the probability of the worst
 * individual to be	selected and <i>n</i><sup><i>+</i></sup>/<i>N</i> the
 * probability of the best individual to be selected. As the population size is
 * held constant, the conditions <i>n</i><sup><i>+</i></sup> = 2 - <i>n</i><sup><i>-</i></sup>
 * and <i>n</i><sup><i>-</i></sup> &gt;= 0 must be fulfilled. Note that all individuals
 * get a different rank, i.e., a different selection probability, even if the
 * have the same fitness value. <p>
 *
 * <i>
 * T. Blickle, L. Thiele, A comparison of selection schemes used
 * in evolutionary algorithms, Technical Report, ETH Zurich, 1997, page 37.
 * <a href="http://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.15.9584&rank=1">
 *	http://citeseer.ist.psu.edu/blickle97comparison.html
 * </a>
 * </i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
public final class LinearRankSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends ProbabilitySelector<G, C>
{
	private final double _nminus;
	private final double _nplus;

	/**
	 * Create a new LinearRankSelector with the given values for {@code nminus}.
	 *
	 * @param nminus {@code nminus/N} is the probability of the worst phenotype
	 *         to be selected.
	 * @throws IllegalArgumentException if {@code nminus < 0}.
	 */
	public LinearRankSelector(final double nminus) {
		super(true);

		if (nminus < 0) {
			throw new IllegalArgumentException(format(
				"nminus is smaller than zero: %s", nminus
			));
		}

		_nminus = nminus;
		_nplus = 2 - _nminus;
	}

	/**
	 * Create a new LinearRankSelector with {@code nminus := 0.5}.
	 */
	public LinearRankSelector() {
		this(0.5);
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

		if (N == 1) {
			probabilities[0] = 1;
		} else {
			for (int i = probabilities.length; --i >= 0; ) {
				probabilities[probabilities.length - i - 1] =
					(_nminus + (_nplus - _nminus)*i/(N - 1))/N;
			}
		}

		return probabilities;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_nminus).and(_nplus).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof LinearRankSelector &&
			eq(((LinearRankSelector)obj)._nminus, _nminus) &&
			eq(((LinearRankSelector)obj)._nplus, _nplus);
	}

	@Override
	public String toString() {
		return format(
			"%s[(n-)=%f, (n+)=%f]",
			getClass().getSimpleName(), _nminus, _nplus
		);
	}

}
