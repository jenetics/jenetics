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

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import javolution.lang.Immutable;


/**
 * <p>
 * In linear-ranking selection the individuals are sorted according to their
 * fitness values. The rank <i>N</i> is assignee to the best individual and the
 * rank 1 to the worst individual. The selection probability <i>P(i)</i>  of
 * individual <i>i</i> is linearly assigned to the individuals according to
 * their rank.
 * </p>
 * <p/><img
 *        src="doc-files/linear-rank-selector.gif"
 *        alt="P(i)=\frac{1}{N}\left(n^{-}+\left(n^{+}-n^{-}\right)\frac{i-1}{N-1}\right)"
 *     />
 * </p>
 *
 * Here <i>n</i><sup><i>-</i></sup>/<i>N</i> is the probability of the worst
 * individual to be	selected and <i>n</i><sup><i>+</i></sup>/<i>N</i> the
 * probability of the best individual to be selected. As the population size is
 * held constant, the conditions <i>n</i><sup><i>+</i></sup> = 2 - <i>n</i><sup><i>-</i></sup>
 * and <i>n</i><sup><i>-</i></sup> >= 0 must be fulfilled. Note that all individuals
 * get a different rank, i.e., a different selection probability, even if the
 * have the same fitness value. <p/>
 *
 * <i>
 * T. Blickle, L. Thiele, A comparison of selection schemes used
 * in evolutionary algorithms, Technical Report, ETH Zurich, 1997, page 37.
 * <a href="http://citeseer.ist.psu.edu/blickle97comparison.html">
 *	http://citeseer.ist.psu.edu/blickle97comparison.html
 * </a>
 * </i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class LinearRankSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends ProbabilitySelector<G, C>
	implements Immutable
{
	private final double _nminus;
	private final double _nplus;

	/**
	 * Create a new LinearRankSelector with {@code nminus := 0.5}.
	 */
	public LinearRankSelector() {
		this(0.5);
	}

	/**
	 * Create a new LinearRankSelector with the given values for {@code nminus}.
	 *
	 * @param nminus {@code nminus/N} is the probability of the worst phenotype
	 *         to be selected.
	 * @throws IllegalArgumentException if {@code nminus < 0}.
	 */
	public LinearRankSelector(final double nminus) {
		if (nminus < 0) {
			throw new IllegalArgumentException(String.format(
					"nminus is smaller than zero: %s", nminus
				));
		}

		_nminus = nminus;
		_nplus = 2 - _nminus;
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#sort()} is called
	 * by this method.)
	 */
	@Override
	protected double[] probabilities(
		final Population<G, C> population,
		final int count
	) {
		assert(population != null) : "Population can not be null. ";
		assert(count > 0) : "Population to select must be greater than zero. ";

		//Sort the population.
		population.sort();

		final double N = population.size();
		final double[] probabilities = new double[population.size()];

		for (int i = probabilities.length; --i >= 0;) {
			probabilities[probabilities.length - i - 1] =
				(_nminus + ((_nplus - _nminus)*i)/(N - 1))/N;
		}

		assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";
		return probabilities;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_nminus).and(_nplus).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof LinearRankSelector<?, ?>)) {
			return false;
		}

		final LinearRankSelector<?, ?> selector = (LinearRankSelector<?, ?>)obj;
		return eq(_nminus, selector._nminus) && eq(_nplus, selector._nplus);
	}

	@Override
	public String toString() {
		return String.format(
				"%s[n-=%f, n+=%f]",
				getClass().getSimpleName(), _nminus, _nplus
			);
	}

}


