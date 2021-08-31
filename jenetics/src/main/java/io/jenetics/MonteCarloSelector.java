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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

/**
 * The Monte Carlo selector selects the individuals from a given population
 * randomly. This selector can be used to measure the performance of a other
 * selectors. In general, the performance of a selector should be better than
 * the selection performance of the Monte Carlo selector.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 5.0
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
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
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

		final MSeq<Phenotype<G, C>> selection;
		if (count > 0 && !population.isEmpty()) {
			selection = MSeq.ofLength(count);
			final var random = RandomRegistry.random();
			final int size = population.size();

			for (int i = 0; i < count; ++i) {
				final int pos = random.nextInt(size);
				selection.set(i, population.get(pos));
			}
		} else {
			selection = MSeq.empty();
		}

		return selection.toISeq();
	}

	@Override
	public String toString() {
		return format("%s", getClass().getSimpleName());
	}

}
