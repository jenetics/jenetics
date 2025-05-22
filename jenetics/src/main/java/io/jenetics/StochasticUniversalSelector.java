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

import static java.util.Objects.requireNonNull;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * {@code StochasticUniversalSelector} is a method for selecting a
 * population according to some given probability in a way that minimizes chance
 * fluctuations. It can be viewed as a type of roulette game where now we have
 * P equally spaced points which we spin.
 *
 * <p>
 * <img src="doc-files/StochasticUniversalSelection.svg" width="400"
 *      alt="Selector">
 * </p>
 *
 * The figure above shows how the stochastic-universal selection works; <i>n</i>
 * is the number of individuals to select.
 *
 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Stochastic_universal_sampling">
 *           Wikipedia: Stochastic universal sampling
 *      </a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.0
 */
public class StochasticUniversalSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends RouletteWheelSelector<G, N>
{

	public StochasticUniversalSelector() {
		super(false);
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities.
	 */
	@Override
	public ISeq<Phenotype<G, N>> select(
		final Seq<Phenotype<G, N>> population,
		final int count,
		final Optimize opt
	) {
		requireNonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " +
				count
			);
		}

		if (count == 0 || population.isEmpty()) {
			return ISeq.empty();
		}

		final MSeq<Phenotype<G, N>> selection = MSeq.ofLength(count);

		final double[] probabilities = probabilities(population, count, opt);
		assert population.size() == probabilities.length;

		//Calculating the equal spaces of random points.
		final double delta = 1.0/count;
		final double[] points = new double[count];
		points[0] = RandomRegistry.random().nextDouble()*delta;
		for (int i = 1; i < count; ++i) {
			points[i] = points[i - 1] + delta;
		}

		int j = 0;
		double cumProb = probabilities[0];
		for (int i = 0; i < count; ++i) {
			while (points[i] > cumProb) {
				++j;
				cumProb += probabilities[j%population.size()];
			}

			selection.set(i, population.get(j%population.size()));
		}

		return selection.toISeq();
	}

}
