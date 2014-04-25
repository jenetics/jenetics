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

import java.util.function.Consumer;
import java.util.stream.Collector;

import org.jenetics.stat.IntMoments;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 3.0 &mdash; <em>$Date: 2014-04-25 $</em>
 * @since 3.0
 */
public class PopulationStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Consumer<Phenotype<G, C>>
{

	private final int _generation;
	private final Optimize _optimize;

	private Phenotype<G, C> _min = null;
	private Phenotype<G, C> _max = null;

	private final IntMoments _ageMoments = new IntMoments();

	public PopulationStatistics(final int generation, final Optimize optimize) {
		_generation = generation;
		_optimize = requireNonNull(optimize);
	}

	@Override
	public void accept(final Phenotype<G, C> phenotype) {
		_ageMoments.accept(phenotype.getAge(_generation));
		_min = min(_min, phenotype);
		_max = max(_max, phenotype);
	}

	public PopulationStatistics<G, C> combine(final PopulationStatistics<G, C> statistics) {
		final PopulationStatistics<G, C> result =
			new PopulationStatistics<>(_generation, _optimize);

		result._ageMoments.set(_ageMoments.combine(statistics._ageMoments));
		result._min = min(_min, statistics._min);
		result._max = max(_max, statistics._max);

		return result;
	}

	public IntMoments getAgeMoments() {
		return _ageMoments;
	}

	public Phenotype<G, C> getWorst() {
		return (_min != null && _max != null) ? _optimize.worst(_min, _max) : null;
	}

	public Phenotype<G, C> getBest() {
		return (_min != null && _max != null) ? _optimize.best(_min, _max) : null;
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Collector<Phenotype<G, C>, ?, PopulationStatistics<G, C>>
	collector(final int generation, final Optimize optimize) {
		return Collector.of(
			() -> new PopulationStatistics<>(generation, optimize),
			PopulationStatistics::accept,
			PopulationStatistics::combine
		);
	}
}
