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

import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import org.jenetics.util.RandomRegistry;


/**
 * <code>StochasticUniversalSelector</code> is a method for selecting a
 * population according to some given probability in a way that minimize chance
 * fluctuations. It can be viewed as a type of roulette game where now we have
 * P equally spaced points which we spin.
 *
 * <p><div align="center">
 * <img src="doc-files/StochasticUniversalSelection.svg" width="400" />
 * </p></div>
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
 * @version 2.0 &mdash; <em>$Date: 2013-05-25 $</em>
 */
public class StochasticUniversalSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends RouletteWheelSelector<G, N>
{

	public StochasticUniversalSelector() {
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#sort()} is called
	 * by this method.)
	 */
	@Override
	public Population<G, N> select(
		final Population<G, N> population,
		final int count,
		final Optimize opt
	) {
		nonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " + count
			);
		}

		final Population<G, N> selection = new Population<>(count);
		if (count == 0) {
			return selection;
		}

		final double[] probabilities = probabilities(population, count, opt);
		assert (population.size() == probabilities.length);

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
			selection.add(population.get(j));
		}

		return selection;
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population,
		final int count
	) {
		population.sort();
		return super.probabilities(population, count);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return String.format("%s", getClass().getSimpleName());
	}

}






