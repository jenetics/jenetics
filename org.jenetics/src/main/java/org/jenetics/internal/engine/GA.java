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
package org.jenetics.internal.engine;

import static java.lang.Math.round;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenetics.internal.engine.CombineStage.CombineResult;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-08-06 $</em>
 */
public class GA<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// GA context.
	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final double _offspringFraction;
	private final int _populationCount;
	private final int _offspringCount;
	private final int _survivorCount;
	private final Alterer<G, C> _alterer;
	private final Optimize _optimize;

	// Execution context.
	private final Executor _executor;

	public GA(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Optimize optimize,
		final int populationCount,
		final int offspringFraction,
		final Executor executor
	) {
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_optimize = requireNonNull(optimize);

		_populationCount = populationCount;
		_offspringFraction = offspringFraction;
		_offspringCount = (int)round(_offspringFraction*_populationCount);
		_survivorCount = _populationCount - _offspringCount;

		_executor = requireNonNull(executor);
	}

	public State<G, C> evolve(final State<G, C> state) {
		final CompletionStage<TimedResult<Population<G, C>>> offspring =
			async(() -> selectOffspring(state.getPopulation()));

		final CompletionStage<TimedResult<Population<G, C>>> survivors =
			async(() -> selectSurvivors(state.getPopulation()));

		final CompletionStage<TimedResult<AlterResult<G, C>>> alter =
			then(offspring, p -> alter(p.get(), state.getGeneration()));

		final CompletionStage<CombineResult<G, C>> combine =
			survivors.thenCombineAsync(alter, (s, o) -> combine(s.get(), o.get().population), _executor);

		

		return null;
	}

	private Population<G, C> selectSurvivors(final Population<G, C> population) {
		return _survivorsSelector.select(population, _survivorCount, _optimize);
	}

	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _offspringSelector.select(population, _offspringCount, _optimize);
	}

	private AlterResult<G, C> alter(final Population<G,C> population, final int generation) {
		return new AlterResult<>(
			population,
			_alterer.alter(population, generation)
		);
	}

	private CombineResult<G, C> combine(final Population<G, C> survivors, final Population<G, C> offspring) {
		return null;
	}

	private <T> CompletionStage<TimedResult<T>> async(final Supplier<T> supplier) {
		return supplyAsync(TimedResult.of(supplier), _executor);
	}

	private <U, T> CompletionStage<TimedResult<T>> then(
		final CompletionStage<U> result,
		final Function<U, T> fn
	) {
		return result.thenApplyAsync(TimedResult.of(fn), _executor);
	}


	/**
	 * Represents a state of the GA.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 */
	public static final class State<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{
		private final Population<G, C> _population;
		private final int _generation;

		public State(final Population<G, C> population, final int generation) {
			_population = requireNonNull(population);
			_generation = generation;
		}

		public Population<G, C> getPopulation() {
			return _population;
		}

		public int getGeneration() {
			return _generation;
		}
	}

	private static final class AlterResult<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		public final Population<G, C> population;
		public final int altered;

		AlterResult(final Population<G, C> population, final int altered) {
			this.population = population;
			this.altered = altered;
		}
	}

	private static final class CombineResult<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		public final Population<G, C> population;
		public final int killed;
		public final int invalid;

		CombineResult(
			final Population<G, C> population,
			final int killed,
			final int invalid
		) {
			this.population = population;
			this.killed = killed;
			this.invalid = invalid;
		}
	}

	/**
	 * Represents a configuration of the GA.
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness type
	 */
	public static final class Config<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{

	}

}
