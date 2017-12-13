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

import java.util.Random;
import java.util.stream.Stream;

import io.jenetics.internal.util.Equality;
import io.jenetics.internal.util.Hash;
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
 * @version 4.0
 */
public class TournamentSelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{

	private final int _sampleSize;

	/**
	 * Create a tournament selector with the give sample size. The sample size
	 * must be greater than one.
	 *
	 * @param sampleSize the number of individuals involved in one tournament
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
	 * Create a tournament selector with sample size two.
	 */
	public TournamentSelector() {
		this(2);
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

		final Random random = RandomRegistry.getRandom();
		return population.isEmpty()
			? ISeq.empty()
			: MSeq.<Phenotype<G, C>>ofLength(count)
				.fill(() -> select(population, opt, _sampleSize, random))
				.toISeq();
	}

	private Phenotype<G, C> select(
		final Seq<Phenotype<G, C>> population,
		final Optimize opt,
		final int sampleSize,
		final Random random
	) {
		final int N = population.size();
		return Stream.generate(() -> population.get(random.nextInt(N)))
			.limit(sampleSize)
			.max(opt.ascending())
			.orElseThrow(IllegalStateException::new);
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass()).and(_sampleSize).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(s -> _sampleSize == s._sampleSize);
	}

	@Override
	public String toString() {
		return format("%s[s=%d]", getClass().getSimpleName(), _sampleSize);
	}

}
