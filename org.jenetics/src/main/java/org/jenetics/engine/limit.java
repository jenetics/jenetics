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

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

import java.time.Clock;
import java.time.Duration;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.jenetics.internal.util.require;

import org.jenetics.stat.DoubleMoments;
import org.jenetics.util.NanoClock;

/**
 * This class contains factory methods for creating predicates, which can be
 * used for limiting the evolution stream.
 *
 * @see EvolutionStream#limit(Predicate)
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.7
 */
public final class limit {
	private limit() {require.noInstance();}

	/**
	 * Return a predicate, which will truncate the evolution stream after the
	 * given number of generations. The returned predicate behaves like a call
	 * of the {@link java.util.stream.Stream#limit(long)} and exists for
	 * <i>completeness</i> reasons.
	 *
	 * @since 3.1
	 *
	 * @param generation the number of generations after the evolution stream is
	 *        truncated
	 * @return a predicate which truncates the evolution stream after the given
	 *        number of generations
	 * @throws java.lang.IllegalArgumentException if the given {@code generation}
	 *         is smaller than zero.
	 */
	public static Predicate<Object> byFixedGeneration(final long generation) {
		if (generation < 0) {
			throw new IllegalArgumentException(format(
				"The number of generations must greater than one, but was %d",
				generation
			));
		}

		return new Predicate<Object>() {
			private long _current;
			@Override
			public boolean test(final Object o) {
				return ++_current < generation;
			}
		};
	}

	/**
	 * Return a predicate, which will truncate the evolution stream if no
	 * better phenotype could be found after the given number of
	 * {@code generations}.
	 *
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 100 generations.
	 *     .limit(100)
	 *     .collect(toBestPhenotype());
	 * }</pre>
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
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 500 ms.
	 *     .limit(byExecutionTime(Duration.ofMillis(500), Clock.systemUTC())
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * @since 3.1
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
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream after 5 "steady" generations.
	 *     .limit(bySteadyFitness(5))
	 *      // The evolution will stop after maximal 500 ms.
	 *     .limit(byExecutionTime(Duration.ofMillis(500))
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * @since 3.1
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
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *      // Truncate the evolution stream if the best fitness is higher than
	 *      // the given threshold of '2.3'.
	 *     .limit(byFitnessThreshold(2.3))
	 *      // The evolution will stop after maximal 250 generations; guarantees
	 *      // the termination (truncation) of the evolution stream.
	 *     .limit(250)
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * @since 3.1
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

	/**
	 * Return a predicate, which will truncate the evolution stream if the
	 * fitness is converging. Two filters of different lengths are used to
	 * smooth the best fitness across the generations.
	 *
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *     .limit(byFitnessConvergence(5, 15, (s, l) -> {
	 *          final double div = max(abs(s.getMean()), abs(l.getMean()));
	 *          final eps = abs(s.getMean() - l.getMean())/(div <= 10E-20 ? 1.0 : div);
	 *          return esp >= 10E-5
	 *     }))
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * In the example above, the moving average of the short- and long filter
	 * is used for determining the fitness convergence.
	 *
	 * @since 3.7
	 *
	 * @param shortFilterSize the size of the short filter
	 * @param longFilterSize the size of the long filter. The long filter size
	 *        also determines the minimum number of generations of the evolution
	 *        stream.
	 * @param proceed the predicate which determines when the evolution stream
	 *        is truncated. The first parameter of the predicate contains the
	 *        double statistics of the short filter and the second parameter
	 *        contains the statistics of the long filter
	 * @param <N> the fitness type
	 * @return a new fitness convergence strategy
	 * @throws NullPointerException if the {@code proceed} predicate is
	 *         {@code null}
	 */
	public static <N extends Number & Comparable<? super N>>
	Predicate<EvolutionResult<?, N>> byFitnessConvergence(
		final int shortFilterSize,
		final int longFilterSize,
		final BiPredicate<DoubleMoments, DoubleMoments> proceed
	) {
		return new FitnessConvergenceLimit<>(
			shortFilterSize,
			longFilterSize,
			proceed
		);
	}

	/**
	 * Return a predicate, which will truncate the evolution stream if the
	 * fitness is converging. Two filters of different lengths are used to
	 * smooth the best fitness across the generations. When the smoothed best
	 * fitness from the long filter is less than a user-specified percentage
	 * away from the smoothed best fitness from the short filter, the fitness is
	 * deemed as converged and the evolution terminates.
	 *
	 * <pre>{@code
	 * final Phenotype<DoubleGene, Double> result = engine.stream()
	 *     .limit(byFitnessConvergence(5, 15, 10E-4))
	 *     .collect(toBestPhenotype());
	 * }</pre>
	 *
	 * In the given example, the evolution stream stops, if the difference of the
	 * mean values of the long and short filter is less than 1%. The short
	 * filter calculates the mean of the best fitness values of the last 5
	 * generations. The long filter uses the best fitness values of the last 15
	 * generations.
	 *
	 * @since 3.7
	 *
	 * @param shortFilterSize the size of the short filter
	 * @param longFilterSize the size of the long filter. The long filter size
	 *        also determines the minimum number of generations of the evolution
	 *        stream.
	 * @param epsilon the maximal relative distance of the mean value between
	 *        the short and the long filter. The {@code epsilon} must within the
	 *        range of {@code [0..1]}.
	 * @param <N> the fitness type
	 * @return a new fitness convergence strategy
	 * @throws IllegalArgumentException if {@code shortFilterSize < 1} ||
	 *         {@code longFilterSize < 2} ||
	 *         {@code shortFilterSize >= longFilterSize}
	 * @throws IllegalArgumentException if {@code epsilon} is not in the range
	 *         of {@code [0..1]}
	 */
	public static <N extends Number & Comparable<? super N>>
	Predicate<EvolutionResult<?, N>> byFitnessConvergence(
		final int shortFilterSize,
		final int longFilterSize,
		final double epsilon
	) {
		if (epsilon < 0.0 || epsilon > 1.0) {
			throw new IllegalArgumentException(format(
				"The given epsilon is not in the range [0, 1]: %f", epsilon
			));
		}

		return new FitnessConvergenceLimit<>(
			shortFilterSize,
			longFilterSize,
			(s, l) -> eps(s, l) >= epsilon
		);
	}

	// Calculate the relative mean difference between short and long filter.
	private static double eps(final DoubleMoments s, final DoubleMoments l) {
		final double div = max(abs(s.getMean()), abs(l.getMean()));
		return abs(s.getMean() - l.getMean())/(div <= 10E-20 ? 1.0 : div);
	}

}
