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

import java.util.function.Predicate;

import org.jenetics.Optimize;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
final class SteadyFitnessLimit<C extends Comparable<? super C>>
	implements Predicate<EvolutionResult<?, C>>
{
	private final int _generations;

	private boolean _proceed = true;
	private int _stable = 0;
	private C _fitness;

	public SteadyFitnessLimit(final int generations) {
		if (generations < 1) {
			throw new IllegalArgumentException("Generations < 1: " + generations);
		}
		_generations = generations;
	}

	@Override
	public boolean test(final EvolutionResult<?, C> result) {
		if (!_proceed) return false;

		if (_fitness == null) {
			_fitness = result.getBestFitness();
			_stable = 1;
		} else {
			final Optimize opt = result.getOptimize();
			if (opt.compare(_fitness, result.getBestFitness()) >= 0) {
				_proceed = ++_stable <= _generations;
			} else {
				_fitness = result.getBestFitness();
				_stable = 1;
			}
		}

		return _proceed;
	}
}
