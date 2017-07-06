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

import static java.util.Objects.requireNonNull;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.RandomRegistry;

/**
 * {@code StochasticUniversalSelector} is a method for selecting a
 * population according to some given probability in a way that minimize chance
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
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.2
 */
public class StochasticUniversalSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends RouletteWheelSelector<G, N>
{

	public StochasticUniversalSelector() {
		super(true);
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#populationSort()} is called
	 * by this method.)
	 */
	@Override
	public Population<G, N> select(
		final Population<G, N> population,
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

		final Population<G, N> selection = new Population<>(count);
		if (count == 0 || population.isEmpty()) {
			return selection;
		}

		final Population<G, N> pop = copy(population);
		final double[] probabilities = probabilities(pop, count, opt);
		assert  pop.size() == probabilities.length;

		//Calculating the equally spaces random points.
		final double delta = 1.0/count;
		final double[] points = new double[count];
		points[0] = RandomRegistry.getRandom().nextDouble()*delta;
		for (int i = 1; i < count; ++i) {
			points[i] = delta*i;
		}

		int j = 0;
		double prop = 0;
		for (int i = 0; i < count; ++i) {
			while (points[i] > prop) {
				prop += probabilities[j];
				++j;
			}
			selection.add(pop.get(j%pop.size()));
		}

		return selection;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(super::equals);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
