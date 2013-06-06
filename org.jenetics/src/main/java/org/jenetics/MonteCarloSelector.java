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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.String.format;
import static org.jenetics.util.object.hashCodeOf;
import static org.jenetics.util.object.nonNull;

import java.util.Random;

import javolution.lang.Immutable;

import org.jenetics.util.RandomRegistry;

/**
 * The Monte Carlo selector selects the individuals from a given population
 * randomly. This selector can be used to measure the performance of a other
 * selectors. In general, the performance of a selector should be better than
 * the selection performance of the Monte Carlo selector.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class MonteCarloSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Selector<G, C>,
		Immutable
{

	public MonteCarloSelector() {
	}

	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		nonNull(population, "Population");
		nonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(format(
				"Selection count must be greater or equal then zero, but was %d.",
				count
			));
		}

		final Population<G, C> selection = new Population<>(count);

		if (count > 0) {
			final Random random = RandomRegistry.getRandom();
			final int size = population.size();
			for (int i = 0; i < count; ++i) {
				final int pos = random.nextInt(size);
				selection.add(population.get(pos));
			}
		}

		return selection;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof MonteCarloSelector<?, ?>;
	}

	@Override
	public String toString() {
		return format("%s", getClass().getSimpleName());
	}

}
