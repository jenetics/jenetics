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
import java.util.stream.Stream;

import org.jenetics.internal.util.require;

import org.jenetics.Gene;
import org.jenetics.Optimize;

/**
 * The {@code EvolutionStream} class extends the Java {@link Stream} and adds a
 * method for limiting the evolution by a given predicate.
 *
 * @see java.util.stream.Stream
 * @see Engine
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-21 $</em>
 */
public interface EvolutionStream<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	extends Stream<EvolutionResult<G, C>>
{

	/**
	 * Returns a stream consisting of the elements of this stream, truncated
	 * when the given {@code proceed} predicate returns {@code false}.
	 * <p>
	 * <i>General usage example:</i>
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @see EvolutionStream.Limit
	 *
	 * @param proceed the predicate which determines whether the stream is
	 *        truncated or not. <i>If the predicate returns {@code false}, the
	 *        evolution stream is truncated.</i>
	 * @return the new stream
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public EvolutionStream<G, C>
	limit(final Predicate<? super EvolutionResult<G, C>> proceed);


	/**
	 * This class contains factory methods for creating predicates, which can be
	 * used for limiting the evolution stream.
	 *
	 * @see EvolutionStream#limit(Predicate)
	 */
	public static final class Limit {
		private Limit() {require.noInstance();}

		/**
		 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
		 * @since 3.0
		 * @version 3.0 &mdash; <em>$Date: 2014-10-21 $</em>
		 */
		private static final class SteadyFitness<C extends Comparable<? super C>>
			implements Predicate<EvolutionResult<?, C>>
		{
			private final int _generations;
			private int _stableGenerations = 0;
			private C _fitness;

			private SteadyFitness(final int generations) {
				_generations = generations;
			}

			@Override
			public boolean test(final EvolutionResult<?, C> result) {
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
			if (generations < 1) {
				throw new IllegalArgumentException(
					"Generations must be greater than zero, but was " +
					generations
				);
			}
			return new SteadyFitness<>(generations);
		}

	}

}
