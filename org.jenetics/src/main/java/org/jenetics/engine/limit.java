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

import java.time.Clock;
import java.time.Duration;
import java.util.function.Predicate;

import org.jenetics.internal.util.require;

import org.jenetics.util.NanoClock;

/**
 * This class contains factory methods for creating predicates, which can be
 * used for limiting the evolution stream.
 *
 * @see EvolutionStream#limit(Predicate)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version !__version__!
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

	/**
	 * Return a predicate, which will truncate the evolution stream if the GA
	 * execution exceeds a given time duration. This predicate is (normally)
	 * used as safety net, for guaranteed stream truncation.
	 *
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 500 ms.
	 *     .limit(byExecutionTime(Duration.ofMillis(500), Clock.systemUTC())
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @since !__version__!
	 *
	 * @param duration the duration after the evolution stream will be truncated
	 * @param clock the clock used for measure the execution time
	 * @return a predicate, which will truncate the evolution stream, based on
	 *         the exceeded execution time
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Predicate<Object>
	byExecutionTime(final Duration duration, final Clock clock) {
		return new ExecutionTimeLimit(duration, clock);
	}

	/**
	 * Return a predicate, which will truncate the evolution stream if the GA
	 * execution exceeds a given time duration. This predicate is (normally)
	 * used as safety net, for guaranteed stream truncation.
	 *
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 500 ms.
	 *     .limit(byExecutionTime(Duration.ofMillis(500))
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @since !__version__!
	 *
	 * @param duration the duration after the evolution stream will be truncated
	 * @return a predicate, which will truncate the evolution stream, based on
	 *         the exceeded execution time
	 * @throws NullPointerException if the evolution {@code duration} is
	 *         {@code null}
	 */
	public static Predicate<Object>
	byExecutionTime(final Duration duration) {
		return byExecutionTime(duration, NanoClock.systemUTC());
	}

	/**
	 * Return a predicate, which will truncated the evolution stream if the
	 * best fitness of the current population becomes less than the specified
	 * threshold and the objective is set to minimize the fitness. This
	 * predicate also stops the evolution if the best fitness in the current
	 * population becomes greater than the user-specified fitness threshold when
	 * the objective is to maximize the fitness.
	 *
	 * [code]
	 * final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
	 *      // Truncate the evolution stream if the best fitness is higher than
	 *      // the given threshold of '2.3'.      .
	 *     .limit(byFitnessThreshold(2.3))
	 *      // The evolution will stop after maximal 250 generations; guarantees
	 *      // the termination (truncation) of the evolution stream.
	 *     .limit(250)
	 *     .collect(toBestPhenotype());
	 * [/code]
	 *
	 * @since !__version__!
	 *
	 * @param threshold the desired threshold
	 * @param <C> the fitness type
	 * @return the predicate which truncates the evolution stream based on the
	 *         given {@code threshold}.
	 * @throws NullPointerException if the given {@code threshold} is
	 *        {@code null}.
	 */
	public static <C extends Comparable<? super C>>
	Predicate<EvolutionResult<?, C>> byFitnessThreshold(final C threshold) {
		return new FitnessThresholdLimit<>(threshold);
	}


}
