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

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;
import io.jenetics.util.Seq;

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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 6.0
 */
public class TournamentSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	private final Comparator<? super Phenotype<G, C>> _comparator;
	private final int _sampleSize;

	/**
	 * Create a tournament selector with the give {@code comparator} and
	 * sample size. The sample size must be greater than one.
	 *
	 * @since 6.0
	 *
	 * @param comparator the comparator use for comparing two individuals during
	 *        a tournament
	 * @param sampleSize the number of individuals involved in one tournament
	 * @throws IllegalArgumentException if the sample size is smaller than two
	 * @throws NullPointerException if the given {@code comparator} is
	 *         {@code null}
	 */
	public TournamentSelector(
		final Comparator<? super Phenotype<G, C>> comparator,
		final int sampleSize
	) {
		_comparator = requireNonNull(comparator);
		if (sampleSize < 2) {
			throw new IllegalArgumentException(
				"Sample size must be greater than one, but was " + sampleSize
			);
		}
		_sampleSize = sampleSize;
	}

	/**
	 * Create a tournament selector with the give sample size. The sample size
	 * must be greater than one.
	 *
	 * @param sampleSize the number of individuals involved in one tournament
	 * @throws IllegalArgumentException if the sample size is smaller than two.
	 */
	public TournamentSelector(final int sampleSize) {
		this(Phenotype::compareTo, sampleSize);
	}

	/**
	 * Create a tournament selector with sample size two.
	 */
	public TournamentSelector() {
		this(Phenotype::compareTo,2);
	}

	/**
	 * Return the sample size of the tournament selector.
	 *
	 * @since 5.0
	 *
	 * @return the sample size of the tournament selector
	 */
	public int sampleSize() {
		return _sampleSize;
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
				"Selection count must be greater or equal then zero, but was %s",
				count
			));
		}

		final Random random = RandomRegistry.random();
		return population.isEmpty()
			? ISeq.empty()
			: MSeq.<Phenotype<G, C>>ofLength(count)
				.fill(() -> select(population, opt, random))
				.toISeq();
	}

	private Phenotype<G, C> select(
		final Seq<Phenotype<G, C>> population,
		final Optimize opt,
		final Random random
	) {
		final int N = population.size();

		assert _sampleSize >= 2;
		assert N >= 1;

		final Comparator<? super Phenotype<G, C>> cmp = opt == Optimize.MAXIMUM
			? _comparator
			: _comparator.reversed();

		return Stream.generate(() -> population.get(random.nextInt(N)))
			.limit(_sampleSize)
			.max(cmp)
			.orElseThrow(AssertionError::new);
	}

	@Override
	public String toString() {
		return format("%s[s=%d]", getClass().getSimpleName(), _sampleSize);
	}

}
