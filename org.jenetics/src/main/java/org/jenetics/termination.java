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

import java.util.function.Predicate;

import org.jenetics.internal.util.IntRef;
import org.jenetics.internal.util.ObjectRef;
import org.jenetics.internal.util.require;

/**
 * Some default GA termination strategies.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date: 2014-08-05 $</em>
 */
public final class termination {
	private termination() {require.singleton();}

	/**
	 * Create a <i>terminator</i> which returns {@code false} if the fitness
	 * hasn't improved for a given number of generations.
	 *
	 * @param <C> the fitness type.
	 * @param generations the number of generations the fitness don't have been
	 *         improved.
	 * @return the GA terminator.
	 */
	public static <C extends Comparable<? super C>>
	Predicate<Statistics<?, C>> SteadyFitness(final int generations) {
		final ObjectRef<C> fitness = new ObjectRef<>();
		final IntRef stableGenerations = new IntRef();

		return statistics -> {
			boolean proceed = true;

			if (fitness.value == null) {
				fitness.value = statistics.getBestFitness();
				stableGenerations.value = 1;
			} else {
				final Optimize opt = statistics.getOptimize();
				if (opt.compare(fitness.value, statistics.getBestFitness()) >= 0) {
					proceed = ++stableGenerations.value <= generations;
				} else {
					fitness.value = statistics.getBestFitness();
					stableGenerations.value = 1;
				}
			}

			return proceed;
		};
	}

	/**
	 * Return a <i>termination predicate</i> which returns {@code false} if the
	 * current GA generation is {@code >=} as the given {@code generation}.
	 *
	 * [code]
	 * final GeneticAlgorithm&lt;DoubleGene, Double&gt; ga = ...
	 * ga.evolve(termination.Generation(100));
	 * [/code]
	 *
	 * @param generation the maximal GA generation.
	 * @return the termination predicate.
	 */
	public static Predicate<Statistics<?, ?>> Generation(final int generation) {
		return statistics -> statistics.getGeneration() < generation;
	}

}
