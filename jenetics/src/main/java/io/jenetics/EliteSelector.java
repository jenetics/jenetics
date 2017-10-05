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

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import io.jenetics.internal.util.require;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * The {@code EliteSelector} guarantees the survival of the best individual(s)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.0
 * @since 3.4
 */
public class EliteSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	private final TruncationSelector<G, C>
	ELITE_SELECTOR = new TruncationSelector<>();

	private final Selector<G, C> _nonEliteSelector;
	private final int _eliteCount;

	/**
	 * Create a new elite selector with the desired number of elites to be
	 * selected and the selector used for selecting the rest of the population.
	 *
	 * @param eliteCount the desired number of elite individual to be selected
	 * @param nonEliteSelector the selector used for selecting the rest of the
	 *        population
	 * @throws IllegalArgumentException if {@code eliteCount < 1}
	 * @throws NullPointerException if the {@code nonEliteSelector} is
	 *         {@code null}
	 */
	public EliteSelector(
		final int eliteCount,
		final Selector<G, C> nonEliteSelector
	) {
		_eliteCount = require.positive(eliteCount);
		_nonEliteSelector = requireNonNull(nonEliteSelector);
	}

	/**
	 * Create a new elite selector with the desired number of elites to be
	 * selected. The selector for selecting the rest of the population is
	 * initialized with {@code TournamentSelector<>(3)}.
	 *
	 * @see TournamentSelector
	 *
	 * @param eliteCount the desired number of elite individual to be selected
	 * @throws IllegalArgumentException if {@code eliteCount < 1}
	 */
	public EliteSelector(final int eliteCount) {
		this(eliteCount, new TournamentSelector<>(3));
	}

	/**
	 * Create a new elite selector with selector used for selecting the rest of
	 * the population. The elite count is set to 1.
	 *
	 * @see TournamentSelector
	 *
	 * @param nonEliteSelector the selector used for selecting the rest of the
	 *        population
	 * @throws NullPointerException if the {@code nonEliteSelector} is
	 *         {@code null}
	 */
	public EliteSelector(final Selector<G, C> nonEliteSelector) {
		this(1, nonEliteSelector);
	}

	/**
	 * Create a new elite selector with elite count 1 and the selector for
	 * selecting the rest of the population is initialized with
	 * {@code TournamentSelector<>(3)}
	 */
	public EliteSelector() {
		this(1, new TournamentSelector<>(3));
	}

	@Override
	public ISeq<Phenotype<G, C>> select(
		final Seq<Phenotype<G, C>> population,
		final int count,
		final Optimize opt
	) {
		if (count < 0) {
			throw new IllegalArgumentException(format(
				"Selection count must be greater or equal then zero, but was %s.",
				count
			));
		}

		ISeq<Phenotype<G, C>> result;
		if (population.isEmpty() || count <= 0) {
			result = ISeq.empty();
		} else {
			final int ec = min(count, _eliteCount);
			result = ELITE_SELECTOR.select(population, ec, opt);
			result = result.append(
				_nonEliteSelector.select(population, max(0, count - ec), opt)
			);
		}

		return result;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 31*_eliteCount + 37;
		hash += 31*_nonEliteSelector.hashCode() + 37;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof EliteSelector<?, ?> &&
			((EliteSelector)obj)._eliteCount == _eliteCount &&
			((EliteSelector)obj)._nonEliteSelector.equals(_nonEliteSelector);
	}

	@Override
	public String toString() {
		return format("EliteSelector[%d, %s]", _eliteCount, _nonEliteSelector);
	}

}
