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

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.ObjectRef;
import org.jenetics.internal.util.require;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.PopulationSummary;
import org.jenetics.Selector;
import org.jenetics.util.Factory;

/**
 * Genetic algorithm engine, which performs the actual evolve steps. <i>The
 * engine itself has not mutable state.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-05 $</em>
 */
public class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	// Needed context for population evolving.
	private final Function<? super Genotype<G>, ? extends C> _fitnessFunction;
	private final Function<? super C, ? extends C> _fitnessScaler;
	private final Factory<Genotype<G>> _genotypeFactory;
	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final Optimize _optimize;
	private final int _offspringCount;
	private final int _survivorsCount;
	private final int _maximalPhenotypeAge;

	// Execution context for concurrent execution of evolving steps.
	private final TimedExecutor _executor;

	/**
	 * Create a new GA engine with the given parameters.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @param survivorsSelector the selector used for selecting the survivors
	 * @param offspringSelector the selector used for selecting the offspring
	 * @param alterer the alterer used for altering the offspring
	 * @param optimize the kind of optimization (minimize or maximize)
	 * @param offspringCount the number of the offspring individuals
	 * @param survivorsCount the number of the survivor individuals
	 * @param maximalPhenotypeAge the maximal age of an individual
	 * @param executor the executor used for executing the single evolve steps
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given integer values are smaller
	 *         than one.
	 */
	public Engine(
		final Function<? super Genotype<G>, ? extends C> fitnessFunction,
		final Function<? super C, ? extends C> fitnessScaler,
        final Factory<Genotype<G>> genotypeFactory,
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Optimize optimize,
        final int offspringCount,
        final int survivorsCount,
		final int maximalPhenotypeAge,
		final Executor executor
	) {
		_fitnessFunction = requireNonNull(fitnessFunction);
		_fitnessScaler = requireNonNull(fitnessScaler);
		_genotypeFactory = requireNonNull(genotypeFactory);
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_optimize = requireNonNull(optimize);

		_offspringCount = require.positive(offspringCount);
		_survivorsCount = require.positive(survivorsCount);
		_maximalPhenotypeAge = require.positive(maximalPhenotypeAge);


		_executor = new TimedExecutor(requireNonNull(executor));
	}

	public Optimize getOptimize() {
		return _optimize;
	}

	public EvolutionStart<G, C> evolutionStart() {
		final int generation = 1;
		final int size = _offspringCount + _survivorsCount;
		final Population<G, C> population = new Population<>(size);
		population.fill(() -> newPhenotype(generation), size);

		return EvolutionStart.of(evaluate(population), generation);
	}

	/**
	 * Performs one generation step.
	 *
	 * @param start the evolution start state
	 * @return the resulting evolution state
	 */
	public EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
		final Timer timer = Timer.of().start();

		// Select the offspring population.
		final CompletableFuture<TimedResult<Population<G, C>>> offspring =
			_executor.async(() ->
				selectOffspring(start.getPopulation())
			);

		// Select the survivor population.
		final CompletableFuture<TimedResult<Population<G, C>>> survivors =
			_executor.async(() ->
				selectSurvivors(start.getPopulation())
			);

		// Altering the offspring population.
		final CompletableFuture<TimedResult<AlterResult<G, C>>> alteredOffspring =
			_executor.thenApply(offspring, p ->
				alter(p.get(), start.getGeneration())
			);

		// Filter and replace invalid and to old survivor individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredSurvivors =
			_executor.thenApply(survivors, pop ->
				filter(pop.get(), start.getGeneration())
			);

		// Filter and replace invalid and to old offspring individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredOffspring =
			_executor.thenApply(alteredOffspring, pop ->
				filter(pop.get().getPopulation(), start.getGeneration())
			);

		// Combining survivors and offspring to the new population.
		final CompletableFuture<Population<G, C>> population =
			filteredSurvivors.thenCombineAsync(filteredOffspring, (s, o) -> {
					final Population<G, C> pop = s.get().getPopulation();
					pop.addAll(o.get().getPopulation());
					return pop;
				},
				_executor.get()
			);

		// Evaluate the fitness-function and wait for result.
		final TimedResult<Population<G, C>> result = population
			.thenApply(TimedResult.of(this::evaluate))
			.join();

		final EvolutionDurations durations = EvolutionDurations.of(
			offspring.join().getDuration(),
			survivors.join().getDuration(),
			alteredOffspring.join().getDuration(),
			filteredOffspring.join().getDuration(),
			filteredSurvivors.join().getDuration(),
			result.getDuration(),
			timer.stop().getTime()
		);

		final int killCount = filteredOffspring.join().get().getKillCount() +
			filteredSurvivors.join().get().getKillCount();

		final int invalidCount = filteredOffspring.join().get().getInvalidCount() +
			filteredOffspring.join().get().getInvalidCount();

		return EvolutionResult.of(
			_optimize,
			result.get(),
			start.getGeneration(),
			durations,
			killCount,
			invalidCount,
			alteredOffspring.join().get().getAlterCount()
		);
	}

	private Population<G, C> selectSurvivors(final Population<G, C> population) {
		return _survivorsSelector.select(population, _survivorsCount, _optimize);
	}

	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _offspringSelector.select(population, _offspringCount, _optimize);
	}

	private FilterResult<G, C> filter(
		final Population<G, C> population,
		final int generation
	) {
		int killCount = 0;
		int invalidCount = 0;

		for (int i = 0, n = population.size(); i < n; ++i) {
			final Phenotype<G, C> individual = population.get(i);

			if (!individual.isValid()) {
				population.set(i, newPhenotype(generation));
				++invalidCount;
			} else if (individual.getAge(generation) > _maximalPhenotypeAge) {
				population.set(i, newPhenotype(generation));
				++killCount;
			}
		}

		return FilterResult.of(population, killCount, invalidCount);
	}

	private Phenotype<G, C> newPhenotype(final int generation) {
		return Phenotype.of(
			_genotypeFactory.newInstance(),
			_fitnessFunction,
			_fitnessScaler,
			generation
		);
	}

	private AlterResult<G, C> alter(
		final Population<G,C> population,
		final int generation
	) {
		return AlterResult.of(
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

	public Stream<EvolutionResult<G, C>> stream(final int generations) {
		final ObjectRef<EvolutionStart<G, C>> start = new ObjectRef<>(evolutionStart());

		return IntStream.range(0, generations).mapToObj(i -> {
			final EvolutionResult<G, C> result = evolve(start.value);
			start.value = result.next();
			return result;
		});
	}

	public Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>> best() {
		return EvolutionResult.<G, C>best(_optimize);
	}

	public Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>> worst() {
		return EvolutionResult.<G, C>worst(_optimize);
	}

	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	EngineBuilder<G, C> newBuilder(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<? super Genotype<G>, ? extends C> fitnessFunction
	) {
		return new EngineBuilder<>(genotypeFactory, fitnessFunction);
	}

	public static void main(final String[] args) {
		final Engine<DoubleGene, Double> engine = Engine
			.newBuilder(
				Genotype.of(DoubleChromosome.of(0.0, 1.0)),
				gt -> gt.getGene().getAllele())
			.build();

		EvolutionStart<DoubleGene, Double> start = engine.evolutionStart();
		for (int i = 0; i < 10; ++i) {
			final EvolutionResult<DoubleGene, Double> result = engine.evolve(start);
			final PopulationSummary<DoubleGene, Double> summary =
				result.getPopulation().stream()
					.collect(PopulationSummary
						.collector(engine._optimize, result.getGeneration()));

			System.out.println(summary);

			start = result.next();
		}
	}

}
