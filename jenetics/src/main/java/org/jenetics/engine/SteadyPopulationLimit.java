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

import static org.jenetics.stat.DoubleMoments.toDoubleMoments;

import java.util.function.Predicate;

import org.jenetics.Optimize;
import org.jenetics.stat.DoubleMoments;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
final class SteadyPopulationLimit<
	N extends Number & Comparable<? super N>
>
	implements Predicate<EvolutionResult<?, N>>
{
	private final int _generations;
	private int _stableGenerations = 0;
	private N _fitness;

	private SteadyPopulationLimit(final int generations) {
		_generations = generations;
	}

	@Override
	public boolean test(final EvolutionResult<?, N> result) {
		final DoubleMoments moments = result.getPopulation().stream()
			.collect(toDoubleMoments(pt -> pt.getFitness().doubleValue()));

		boolean proceed = true;

		if (_fitness == null) {
			_fitness = result.getBestFitness();
			_stableGenerations = 1;
		} else {
			final Optimize opt = result.getOptimize();
			if (opt.compare(_fitness, result.getBestFitness()) >= 0) {
				proceed = ++_stableGenerations <= _generations;
			} else {
				_fitness = result.getBestFitness();
				_stableGenerations = 1;
			}
		}

		return proceed;
	}
}
