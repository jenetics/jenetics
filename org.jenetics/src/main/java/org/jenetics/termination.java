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

import org.jenetics.util.Function;
import org.jenetics.util.StaticObject;

/**
 * Some default GA termination strategies.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public final class termination extends StaticObject {
	private termination() {}

	static class SteadyFitness<C extends Comparable<? super C>>
		implements Function<Statistics<?, C>, Boolean>
	{
		private final int _genenerations;

		private C _fitness;
		private int _stableGenerations = 0;

		public SteadyFitness(final int generations) {
			_genenerations = generations;
		}

		@Override
		public Boolean apply(final Statistics<?, C> statistics) {
			boolean proceed = true;

			if (_fitness == null) {
				_fitness = statistics.getBestFitness();
				_stableGenerations = 1;
			} else {
				final Optimize opt = statistics.getOptimize();
				if (opt.compare(_fitness, statistics.getBestFitness()) >= 0) {
					proceed = ++_stableGenerations <= _genenerations;
				} else {
					_fitness = statistics.getBestFitness();
					_stableGenerations = 1;
				}
			}

			return proceed ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	/**
	 * Create a <i>terminator</i> which returns {@code false} if the fitness
	 * hasn't improved for a given number of generations.
	 *
	 * @param <C> the fitness type.
	 * @param generation the number of generations the fitness don't have been
	 *         improved.
	 * @return the GA terminator.
	 */
	public static <C extends Comparable<? super C>>
	Function<Statistics<?, C>, Boolean> SteadyFitness(final int generation) {
		return new SteadyFitness<>(generation);
	}

	static class Generation implements Function<Statistics<?, ?>, Boolean> {
		private final int _generation;

		public Generation(final int generation) {
			_generation = generation;
		}

		@Override
		public Boolean apply(final Statistics<?, ?> statistics) {
			return statistics.getGeneration() < _generation ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	/**
	 * Return a <i>termination predicate</i> which returns {@code false} if the
	 * current GA generation is {@code >=} as the given {@code generation}.
	 *
	 * [code]
	 * final GeneticAlgortihm<Float64Gene, Float64> ga = ...
	 * ga.evolve(termination.Generation(100));
	 * [/code]
	 *
	 * @param generation the maximal GA generation.
	 * @return the termination predicate.
	 */
	public static Function<Statistics<?, ?>, Boolean> Generation(final int generation) {
		return new Generation(generation);
	}

}
