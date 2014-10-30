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

import org.jenetics.internal.util.require;

/**
 * This class contains factory methods for creating predicates, which can be
 * used for limiting the evolution stream.
 *
 * @see EvolutionStream#limit(Predicate)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-22 $</em>
 */
public final class limit {
	private limit() {require.noInstance();}


	/**
	 * Return a predicate, which will truncate the evolution stream if no
	 * better phenotype could be found after the given number of
	 * {@code generations}.
	 *
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @param generations the number of <i>steady</i> generations
	 * @param <C> the fitness type
	 * @return a predicate which truncate the evolution stream if no better
	 *         phenotype could be found after a give number of
	 *         {@code generations}
	 * @throws IllegalArgumentException if the generation is smaller than
	 *         one.
	 */
	public static <C extends Comparable<? super C>>
	Predicate<EvolutionResult<?, C>> bySteadyFitness(final int generations) {
		return new SteadyFitnessLimit<>(generations);
	}

}
