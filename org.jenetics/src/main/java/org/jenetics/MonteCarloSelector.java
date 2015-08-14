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
import static java.util.Objects.requireNonNull;

import java.util.Random;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.util.RandomRegistry;

/**
 * The Monte Carlo selector selects the individuals from a given population
 * randomly. This selector can be used to measure the performance of a other
 * selectors. In general, the performance of a selector should be better than
 * the selection performance of the Monte Carlo selector.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0
 */
public final class MonteCarloSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	public MonteCarloSelector() {
	}

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
				"Selection count must be greater or equal then zero, but was %d.",
				count
			));
		}

		final Population<G, C> selection = new Population<>(count);
		if (count > 0 && !population.isEmpty()) {
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
		return Hash.of(getClass()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.ofType(this, obj);
	}

	@Override
	public String toString() {
		return format("%s", getClass().getSimpleName());
	}

}
