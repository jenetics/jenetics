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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.hashCodeOf;

/**
 * In truncation selection individuals are sorted according to their fitness.
 * Only the n  best individuals are selected. The truncation selection is a very
 * basic selection algorithm. It has it's strength in fast selecting individuals
 * in large populations, but is not very often used in practice.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Truncation_selection">
 * 			Wikipedia: Truncation selection
 *      </a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2013-06-11 $</em>
 */
public final class TruncationSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	/**
	 * Create a new TruncationSelector object.
	 */
	public TruncationSelector() {
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#sort()} is called
	 * by this method.)
	 *
	 * @throws IllegalArgumentException if the sample size is greater than the
	 *         population size or {@code count} is greater the the population
	 *         size.
	 * @throws NullPointerException if the {@code population} is {@code null}.
	 */
	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		requireNonNull(population, "Population");
		requireNonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(format(
				"Selection count must be greater or equal then zero, but was %s",
				count
			));
		}
		if (count > population.size()) {
			throw new IllegalArgumentException(format(
				"Selection size greater than population size: %s > %s",
				count, population.size()
			));
		}

		population.sort(opt.<C>descending());
		return new Population<>(population.subList(0, count));
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
		return obj instanceof TruncationSelector<?, ?>;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}











