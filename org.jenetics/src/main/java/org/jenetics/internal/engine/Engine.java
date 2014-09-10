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
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.require;

import org.jenetics.Alterer;
import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.util.Factory;

/**
 * Genetic algorithm engine, which performs the actual evolve steps. <i>The
 * engine itself has no mutable state.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-09-10 $</em>
 */
public final class Engine<
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

	// Some collectors.
	public final Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
		BestEvolutionResult;

	public final Collector<EvolutionResult<G, C>, ?, Phenotype<G, C>>
		BestPhenotype;

	public final Collector<EvolutionResult<G, C>, ?, Genotype<G>>
		BestGenotype;


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
	Engine(
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

		BestEvolutionResult = EvolutionResult.<G, C>best(_optimize);
		BestPhenotype = EvolutionResult.<G, C>bestPhenotype(_optimize);
		BestGenotype = EvolutionResult.<G, C>bestGenotype(optimize);
	}

	/**
	 * Return the fitness function of the GA engine.
	 *
	 * @return the fitness function
	 */
	public Function<? super Genotype<G>, ? extends C> getFitnessFunction() {
		return _fitnessFunction;
	}

	/**
	 * Return the fitness scaler of the GA engine.
	 *
	 * @return the fitness scaler
	 */
	public Function<? super C, ? extends C> getFitnessScaler() {
		return _fitnessScaler;
	}

	/**
	 * Return the used genotype {@link Factory} of the GA. The genotype factory
	 * is used for creating the initial population and new, random individuals
	 * when needed (as replacement for invalid and/or died genotypes).
	 *
	 * @return the used genotype {@link Factory} of the GA.
	 */
	public Factory<Genotype<G>> getGenotypeFactory() {
		return _genotypeFactory;
	}

	/**
	 * Return the used survivor {@link Selector} of the GA.
	 *
	 * @return the used survivor {@link Selector} of the GA.
	 */
	public Selector<G, C> getSurvivorsSelector() {
		return _survivorsSelector;
	}

	/**
	 * Return the used offspring {@link Selector} of the GA.
	 *
	 * @return the used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> getOffspringSelectors() {
		return _offspringSelector;
	}

	/**
	 * Return the used {@link Alterer} of the GA.
	 *
	 * @return the used {@link Alterer} of the GA.
	 */
	public Alterer<G, C> getAlterer() {
		return _alterer;
	}

	/**
	 * Return the number of selected offsprings.
	 *
	 * @return the number of selected offsprings
	 */
	public int getOffspringCount() {
		return _offspringCount;
	}

	/**
	 * The number of selected survivors.
	 *
	 * @return the number of selected survivors
	 */
	public int getSurvivorsCount() {
		return _survivorsCount;
	}

	/**
	 * Return the maximal allowed phenotype age.
	 *
	 * @return the maximal allowed phenotype age
	 */
	public int getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	/**
	 * Return the optimization strategy.
	 *
	 * @return the optimization strategy
	 */
	public Optimize getOptimize() {
		return _optimize;
	}

	/**
	 * Create a new start generation.
	 *
	 * @return a new evolution start object
	 */
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
		return StreamSupport.stream(
			new LimitedEvolutionSpliterator<>(
				this::evolve,
				evolutionStart(),
				generations
			),
			false
		);
	}

	public Stream<EvolutionResult<G, C>> stream() {
		return StreamSupport.stream(
			new UnlimitedEvolutionSpliterator<>(
				this::evolve,
				this::evolutionStart
			),
			false
		);
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


	public static <C extends Comparable<? super C>>
	EngineBuilder<DoubleGene, C> newBuilder(
		final DoubleFunction<? extends C> fitnessFunction,
		final double min, final double max
	) {
		final Function<? super Genotype<DoubleGene>, ? extends C> ff = gt ->
			fitnessFunction.apply(gt.getGene().doubleValue());

		final Factory<Genotype<DoubleGene>> gtf = Genotype.of(
			DoubleChromosome.of(min, max)
		);

		return new EngineBuilder<>(gtf, ff);
	}

}
