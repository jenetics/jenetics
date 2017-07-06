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

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * In truncation selection individuals are sorted according to their fitness.
 * Only the n  best individuals are selected. The truncation selection is a very
 * basic selection algorithm. It has it's strength in fast selecting individuals
 * in large populations, but is not very often used in practice.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Truncation_selection">
 *          Wikipedia: Truncation selection
 *      </a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.8
 */
public final class TruncationSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	private final int _n;

	/**
	 * Create a new {@code TruncationSelector} object, where the worst selected
	 * individual has rank {@code n}. This means, if you want to select
	 * {@code count} individuals, the worst selected individual has rank
	 * {@code n}. If {@code count > n}, the selected population will contain
	 * <em>duplicate</em> individuals.
	 *
	 * @since 3.8
	 *
	 * @param n the worst rank of the selected individuals
	 * @throws IllegalArgumentException if {@code n < 1}
	 */
	public TruncationSelector(final int n) {
		if (n < 1) {
			throw new IllegalArgumentException(format(
				"n must be greater or equal 1, but was %d.", n
			));
		}

		_n = n;
	}

	/**
	 * Create a new TruncationSelector object.
	 */
	public TruncationSelector() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * This method sorts the population in descending order while calculating
	 * the selection probabilities. (The method
	 * {@link Population#sortWith(java.util.Comparator)} )} is called by this
	 * method.) If the selection size is greater the the population size, the
	 * whole population is duplicated until the desired sample size is reached.
	 *
	 * @throws NullPointerException if the {@code population} or {@code opt} is
	 *         {@code null}.
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

		final Population<G, C> selection = new Population<>(count);
		if (count > 0 && !population.isEmpty()) {
			final Population<G, C> copy = population.copy();
			copy.sortWith(opt.<C>descending());

			int size = count;
			do {
				final int length = min(min(copy.size(), size), _n);
				selection.addAll(copy.subList(0, length));
				size -= length;
			} while (size > 0);
		}

		return selection;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.ofType(this, obj);
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
