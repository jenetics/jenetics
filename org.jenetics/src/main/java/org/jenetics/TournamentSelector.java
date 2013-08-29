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

import java.util.Random;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * In tournament selection the best individual from a random sample of <i>s</i>
 * individuals is chosen from the population <i>P<sub>g</sub></i>. The samples
 * are drawn with replacement. An individual will win a tournament only if its
 * fitness is greater than the fitness of the other <i>s-1</i>  competitors.
 * Note that the worst individual never survives, and the best individual wins
 * in all the tournaments it participates. The selection pressure can be varied
 * by changing the tournament size <i>s</i> . For large values of <i>s</i>, weak
 * individuals have less chance being selected.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Tournament_selection">Tournament selection</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-04-26 $</em>
 */
public class TournamentSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	private final int _sampleSize;

	/**
	 * Create a tournament selector with sample size two.
	 */
	public TournamentSelector() {
		this(2);
	}

	/**
	 * Create a tournament selector with the give sample size. The sample size
	 * must be greater than one.
	 *
	 * @throws IllegalArgumentException if the sample size is smaller than two.
	 */
	public TournamentSelector(final int sampleSize) {
		if (sampleSize < 2) {
			throw new IllegalArgumentException(
				"Sample size must be greater than one, but was " + sampleSize
			);
		}
		_sampleSize = sampleSize;
	}

	/**
	 * @throws IllegalArgumentException if the sample size is greater than the
	 *         population size or {@code count} is greater the the population
	 *         size or the _sampleSize is greater the the population size.
	 * @throws NullPointerException if the {@code population} is {@code null}.
	 */
	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		nonNull(population, "Population");
		nonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(String.format(
				"Selection count must be greater or equal then zero, but was %s",
				count
			));
		}
		if (count > population.size()) {
			throw new IllegalArgumentException(String.format(
				"Selection size greater than population size: %s > %s",
				count, population.size()
			));
		}
		if (_sampleSize > population.size()) {
			throw new IllegalArgumentException(String.format(
				"Tournament size is greater than the population size! %d > %d.",
				 _sampleSize, population.size()
			));
		}

		final Population<G, C> pop = new Population<>(count);
		final Factory<Phenotype<G, C>> factory = factory(
			population, opt, _sampleSize, RandomRegistry.getRandom()
		);

		return pop.fill(factory, count);
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Factory<Phenotype<G, C>> factory(
		final Population<G, C> population,
		final Optimize opt,
		final int sampleSize,
		final Random random
	) {
		return new Factory<Phenotype<G, C>>() {
			@Override
			public Phenotype<G, C> newInstance() {
				return select(population, opt, sampleSize, random);
			}
		};
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Phenotype<G, C> select(
		final Population<G, C> population,
		final Optimize opt,
		final int sampleSize,
		final Random random
	) {
		final int N = population.size();
		Phenotype<G, C> winner = population.get(random.nextInt(N));

		for (int j = 0; j < sampleSize; ++j) {
			final Phenotype<G, C> selection = population.get(random.nextInt(N));
			if (opt.compare(selection, winner) > 0) {
				winner = selection;
			}
		}
		assert (winner != null);

		return winner;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_sampleSize).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final TournamentSelector<?, ?> selector = (TournamentSelector<?, ?>)obj;
		return _sampleSize == selector._sampleSize;
	}

	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	TournamentSelector<SG, SC> valueOf(final int sampleSize) {
		return new TournamentSelector<>(sampleSize);
	}

	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	TournamentSelector<SG, SC> valueOf() {
		return new TournamentSelector<>();
	}

	@Override
	public String toString() {
		return String.format("%s[s=%d]", getClass().getSimpleName(), _sampleSize);
	}

}





