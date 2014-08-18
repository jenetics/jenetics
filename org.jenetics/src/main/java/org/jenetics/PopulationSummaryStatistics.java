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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.math.statistics.max;
import static org.jenetics.internal.math.statistics.min;

import java.util.IntSummaryStatistics;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * @param <G> the gene type
 * @param <C> the fitness value type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-18 $</em>
 */
public class PopulationSummaryStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Consumer<Phenotype<G, C>>
{

	private final Optimize _optimize;
	private final int _currentGeneration;

	private Phenotype<G, C> _best = null;
	private Phenotype<G, C> _worst = null;
	private final IntSummaryStatistics _ageSummary = new IntSummaryStatistics();

	public PopulationSummaryStatistics(
		final Optimize optimize,
		final int currentGeneration
	) {
		_optimize = requireNonNull(optimize);
		_currentGeneration = currentGeneration;
	}

	@Override
	public void accept(final Phenotype<G, C> phenotype) {
		_best = max(_optimize::compare, _best, phenotype);
		_worst = min(_optimize::compare, _worst, phenotype);
		_ageSummary.accept(phenotype.getAge(_currentGeneration));
	}

	public void combine(final PopulationSummaryStatistics<G, C> other) {
		_best = max(_optimize::compare, _best, other._best);
		_worst = min(_optimize::compare, _worst, other._worst);
		_ageSummary.combine(other._ageSummary);
	}

	public Optimize getOptimize() {
		return _optimize;
	}

	public int getGeneration() {
		return _currentGeneration;
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
	public IntSummaryStatistics getAgeSummary() {
		return _ageSummary;
	}

	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<T, ?, PopulationSummaryStatistics<G, C>> collector(
		final Optimize optimize,
		final int currentGeneration,
		final Function<T, Phenotype<G, C>> mapper
	) {
		requireNonNull(mapper);
		return Collector.of(
			() -> new PopulationSummaryStatistics<>(optimize, currentGeneration),
			(r, t) -> r.accept(mapper.apply(t)),
			(a, b) -> {a.combine(b); return a;}
		);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, PopulationSummaryStatistics<G, C>, PopulationSummary<G, C>> collector(
		final Optimize optimize,
		final int currentGeneration
	) {
		return Collector.of(
			() -> new PopulationSummaryStatistics<>(optimize, currentGeneration),
			(r, t) -> r.accept(t),
			(a, b) -> {a.combine(b); return a;},
			s -> PopulationSummary.of(s)
		);
	}

}
