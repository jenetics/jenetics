/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Random;

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
 * @version 1.0 &mdash; <em>$Date$</em>
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
	 *          population size or {@code count} is greater the the population
	 *          size or the _sampleSize is greater the the population size.
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
		if (count == 0) {
			return pop;
		}

		Phenotype<G, C> winner = null;

		final int N = population.size();
		final Random random = RandomRegistry.getRandom();

		for (int i = 0; i < count; ++i) {
			winner = population.get(random.nextInt(N));

			for (int j = 0; j < _sampleSize; ++j) {
				final Phenotype<G, C> selection = population.get(random.nextInt(N));
				if (opt.compare(selection, winner) > 0) {
					winner = selection;
				}
			}

			assert (winner != null);
			pop.add(winner);
		}

		return pop;
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





