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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.IntSummaryStatistics;
import java.util.function.Consumer;
import java.util.stream.Collector;

import org.jenetics.Gene;
import org.jenetics.Phenotype;
import org.jenetics.stat.IntSummary;
import org.jenetics.stat.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-23 $</em>
 */
public class EvolutionSummaryStatistics<
	G extends Gene<?, G>,
	C extends Comparable<? super C>,
    PS
>
	implements Consumer<EvolutionResult<G, C>>
{

    private final Collector<Phenotype<G, C>, ?, PS> _phenotypeCollector;

	private IntSummaryStatistics _killedStatistics = new IntSummaryStatistics();
	private IntSummaryStatistics _invalidStatistics = new IntSummaryStatistics();
	private IntSummaryStatistics _alterStatistics = new IntSummaryStatistics();

	private MinMax<PopulationSummary<G, C>> _populationSummary =
		MinMax.of((a, b) -> a.getBest().compareTo(b.getBest()));

    public EvolutionSummaryStatistics(
        final Collector<Phenotype<G, C>, ?, PS> phenotypeCollector
    ) {
        _phenotypeCollector = requireNonNull(phenotypeCollector);
    }

	@Override
	public void accept(final EvolutionResult<G, C> result) {
		_killedStatistics.accept(result.getKillCount());
		_invalidStatistics.accept(result.getInvalidCount());
		_alterStatistics.accept(result.getAlterCount());

		final PopulationSummaryStatistics<G, C> populationStatistics =
			new PopulationSummaryStatistics<>(
				result.getOptimize(),
				result.getGeneration()
			);
		result.getPopulation().forEach(populationStatistics);
		_populationSummary.accept(PopulationSummary.of(populationStatistics));

        result.getPopulation().stream().collect(_phenotypeCollector);
	}

	public IntSummary getKilledMoments() {
		return IntSummary.of(_killedStatistics);
	}

	public IntSummary getInvalidMoments() {
		return IntSummary.of(_invalidStatistics);
	}

	public IntSummary getAlterMoments() {
		return IntSummary.of(_alterStatistics);
	}

	public PopulationSummary<G, C> getBestPopulationSummary() {
		return _populationSummary.getMax();
	}

}
