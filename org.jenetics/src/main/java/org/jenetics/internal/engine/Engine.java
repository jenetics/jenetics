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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.TimedExecutor;
import org.jenetics.internal.util.TimedResult;

import org.jenetics.Alterer;
import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// GA context.
	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final int _offspringCount;
	private final int _survivorCount;
	private final Alterer<G, C> _alterer;
	private final Optimize _optimize;

	private final int _maxAge;
	private final Factory<Phenotype<G, C>> _phenotypeFactory;

	// Execution context.
	private final TimedExecutor _executor;

	public Engine(
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Optimize optimize,
		final int populationCount,
		final int offspringFraction,
		final int maxAge,
		final Factory<Phenotype<G, C>> phenotypeFactory,
		final Executor executor
	) {
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_optimize = requireNonNull(optimize);

		_offspringCount = round(offspringFraction*populationCount);
		_survivorCount = populationCount - _offspringCount;
		_maxAge = maxAge;
		_phenotypeFactory = phenotypeFactory;

		_executor = new TimedExecutor(requireNonNull(executor));
	}

	public State<G, C> evolve(final State<G, C> state) {
		// Select the offspring population.
		final CompletableFuture<TimedResult<Population<G, C>>>
		offspring = _executor.async(() ->
			selectOffspring(state.getPopulation())
		);

		// Select the survivor population.
		final CompletableFuture<TimedResult<Population<G, C>>>
		survivors = _executor.async(() ->
			selectSurvivors(state.getPopulation())
		);

		// Altering the offspring population.
		final CompletableFuture<TimedResult<AlterResult<G, C>>>
		alteredOffspring = _executor.thenApply(offspring, p ->
				alter(p.get(), state.getGeneration())
		);

		// Filter and replace invalid and to old survivor individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>>
		filteredSurvivors = _executor.thenApply(survivors, pop ->
				filter(pop.get(), state.getGeneration())
		);

		// Filter and replace invalid and to old offspring individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>>
		filteredOffspring = _executor.thenApply(alteredOffspring, pop ->
			filter(pop.get().population, state.getGeneration())
		);

		// Combining survivors and offspring to the new population.
		final CompletableFuture<Population<G, C>>
		population = filteredSurvivors.thenCombineAsync(filteredOffspring, (s, o) -> {
				s.get().population.addAll(o.get().population);
				return s.get().population;
			},
			_executor.get()
		);

		// Evaluate the fitness-function and wait for result.
		final Population<G, C> result = population
			.thenApply(this::evaluate)
			.join();

		return state.next(result);
	}

	private Population<G, C> selectSurvivors(final Population<G, C> population) {
		return _survivorsSelector.select(population, _survivorCount, _optimize);
	}

	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _offspringSelector.select(population, _offspringCount, _optimize);
	}

	private FilterResult<G, C> filter(final Population<G, C> population, final int generation) {
		int killed = 0;
		int invalid = 0;

		for (int i = 0, n = population.size(); i < n; ++i) {
			final Phenotype<G, C> survivor = population.get(i);

			final boolean isTooOld =
				survivor.getAge(generation) > _maxAge;

			final boolean isInvalid = isTooOld || !survivor.isValid();

			// Sorry, too old or not valid.
			if (isInvalid) {
				population.set(i, _phenotypeFactory.newInstance());
			}

			if (isTooOld) {
				++killed;
			} else if (isInvalid) {
				++invalid;
			}
		}

		return new FilterResult<>(
			population, killed, invalid
		);
	}

	private AlterResult<G, C> alter(final Population<G,C> population, final int generation) {
		return new AlterResult<>(
			population,
			_alterer.alter(population, generation)
		);
	}

	private Population<G, C> evaluate(final Population<G, C> population) {
		try (Concurrency c = Concurrency.with(_executor.get())) {
			c.execute(population);
		}
		return population;
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

		public State<G, C> next(final Population<G, C> population) {
			return new State<>(population, _generation + 1);
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

	private static final class FilterResult<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {
		public final Population<G, C> population;
		public final int killed;
		public final int invalid;

		FilterResult(
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
