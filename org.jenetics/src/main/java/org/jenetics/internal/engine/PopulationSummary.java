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
package org.jenetics.internal.engine;

import static java.lang.String.format;
import static org.jenetics.internal.util.Equality.eq;

import java.util.Objects;
import java.util.stream.Collector;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.stat.IntSummary;

/**
 * Contains statistical values about a given population.
 *
 * @param <G> the gene type
 * @param <C> the fitness value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class PopulationSummary<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{
	private final int _count;
	private final Phenotype<G, C> _best;
	private final Phenotype<G, C> _worst;
	private final IntSummary _ageSummary;

	/**
	 * Create a new population summary object.
	 *
	 * @param count the number of phenotypes this summary was build from
	 * @param best the best phenotype
	 * @param worst the worst phenotype
	 * @param ageSummary the summary of the phenotype ages
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}.
	 */
	private PopulationSummary(
		final int count,
		final Phenotype<G, C> best,
		final Phenotype<G, C> worst,
		final IntSummary ageSummary
	) {
		_count = count;
		_best = Objects.requireNonNull(best);
		_worst = Objects.requireNonNull(worst);
		_ageSummary = Objects.requireNonNull(ageSummary);
	}

	/**
	 * The phenotype count this summary was build from.
	 *
	 * @return the number of phenotypes this summary was build from
	 */
	public int getCount() {
		return _count;
	}

	/**
	 * The best phenotype.
	 *
	 * @return the best phenotype
	 */
	public Phenotype<G, C> getBest() {
		return _best;
	}

	/**
	 * The worst phenotype.
	 *
	 * @return the worst phenotype
	 */
	public Phenotype<G, C> getWorst() {
		return _worst;
	}

	/**
	 * The summary of the phenotype ages.
	 *
	 * @return the summary of the phenotype ages
	 */
	public IntSummary getAgeSummary() {
		return _ageSummary;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_count)
			.and(_best)
			.and(_worst)
			.and(_ageSummary).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(summary ->
			eq(_count, summary._count) &&
			eq(_best, summary._best) &&
			eq(_worst, summary._worst) &&
			eq(_ageSummary, summary._ageSummary)
		);
	}

	@Override
	public String toString() {
		return format(
			"PopulationSummary[count=%d, best=%s, worst=%s, age=%s]",
			_count, _best, _worst, _ageSummary
		);
	}

	/**
	 * Create a new population summary object.
	 *
	 * @param count the number of phenotypes this summary was build from
	 * @param best the best phenotype
	 * @param worst the worst phenotype
	 * @param ageSummary the summary of the phenotype ages
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new population summary object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	PopulationSummary<G, C> of(
		final int count,
		final Phenotype<G, C> best,
		final Phenotype<G, C> worst,
		final IntSummary ageSummary
	) {
		return new PopulationSummary<>(
			count,
			best,
			worst,
			ageSummary
		);
	}

	/**
	 * Return a new population summary object.
	 *
	 * @param statistics the creating (mutable) statistics class
	 * @param <G> the gene type
	 * @param <C> the fitness value type
	 * @return a new population summary object
	 * @throws java.lang.NullPointerException if one of the parameters is
	 *         {@code null}.
	 */
	static <G extends Gene<?, G>, C extends Comparable<? super C>>
	PopulationSummary<G, C> of(final org.jenetics.internal.engine.PopulationSummaryStatistics<G, C> statistics) {
		return of(
			(int)statistics.getAgeSummary().getCount(),
			statistics.getBest(),
			statistics.getWorst(),
			IntSummary.of(statistics.getAgeSummary())
		);
	}

	/**
	 * Return a collector, which creates an population summary object.
	 *
	 * @param optimize the optimization strategy used
	 * @param generation the current generation
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new population summary collector
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, PopulationSummary<G, C>> toSummary(
		final Optimize optimize,
		final int generation
	) {
		return Collector.<
			Phenotype<G, C>,
			org.jenetics.internal.engine.PopulationSummaryStatistics<G, C>,
			PopulationSummary<G, C>
		>of(
			() -> new PopulationSummaryStatistics<>(optimize, generation),
			PopulationSummaryStatistics::accept,
			PopulationSummaryStatistics::combine,
			PopulationSummary::of
		);
	}

}
