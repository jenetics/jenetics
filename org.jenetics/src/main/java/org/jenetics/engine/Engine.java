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

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.require.probability;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.require;

import org.jenetics.Alterer;
import org.jenetics.Chromosome;
import org.jenetics.Gene;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.Optimize;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.TournamentSelector;
import org.jenetics.engine.EvolutionResult.Durations;
import org.jenetics.util.Factory;

/**
 * Genetic algorithm engine, which performs the actual evolve steps. <i>The
 * engine itself has no mutable state.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	/**
	 * Collects the best evolution result of an evolution stream.
	 */
	public final Collector<EvolutionResult<G, C>, ?, EvolutionResult<G, C>>
		BestEvolutionResult;

	/**
	 * Collects the best phenotype of an evolution stream.
	 */
	public final Collector<EvolutionResult<G, C>, ?, Phenotype<G, C>>
		BestPhenotype;

	/**
	 * Collects the best genotype of an evolution stream.
	 */
	public final Collector<EvolutionResult<G, C>, ?, Genotype<G>>
		BestGenotype;


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
		BestGenotype = EvolutionResult.<G, C>bestGenotype(_optimize);
	}

	/**
	 * Create a new initial (random) population.
	 *
	 * @return a new, random population
	 */
	public Population<G, C> newPopulation() {
		final int generation = 1;
		final int size = _offspringCount + _survivorsCount;

		return new Population<G, C>(size)
			.fill(() -> newPhenotype(generation), size);
	}

	/**
	 * Perform one evolution step with the given {@code population} and
	 * {@code generation}. New phenotypes are created with the fitness function
	 * and fitness scaler defined by this <em>engine</em>.
	 *
	 * @param population the population to evolve
	 * @param generation the current generation; used for calculating the
	 *        phenotype age.
	 * @return the evolution result
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}
	 */
	public EvolutionResult<G, C> evolve(
		final Population<G, C> population,
		final int generation
	) {
		return evolve(EvolutionStart.of(population, generation));
	}

	/**
	 * Performs one generation step.
	 *
	 * @param start the evolution start state
	 * @return the resulting evolution state
	 */
	EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
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

		final Durations durations = Durations.of(
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

	// Selects the survivors population. A new population object is returned.
	private Population<G, C> selectSurvivors(final Population<G, C> population) {
		return _survivorsSelector.select(population, _survivorsCount, _optimize);
	}

	// Selects the offspring population. A new population object is returned.
	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _offspringSelector.select(population, _offspringCount, _optimize);
	}

	// Filters out invalid and to old individuals. Filtering is done in place.
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

	// Create a new phenotype
	private Phenotype<G, C> newPhenotype(final int generation) {
		return Phenotype.of(
			_genotypeFactory.newInstance(),
			_fitnessFunction,
			_fitnessScaler,
			generation
		);
	}

	// Alters the given population. The altering is done in place.
	private AlterResult<G, C> alter(
		final Population<G,C> population,
		final int generation
	) {
		return AlterResult.of(
			population,
			_alterer.alter(population, generation)
		);
	}

	// Evaluates the fitness function of the give population concurrently.
	private Population<G, C> evaluate(final Population<G, C> population) {
		try (Concurrency c = Concurrency.with(_executor.get())) {
			c.execute(population);
		}
		return population;
	}

	/**
	 * Create a new evolution stream with a newly created population.
	 *
	 * @return a new evolution stream.
	 */
	public EvolutionStream<G, C> stream() {
		return new EvolutionStreamImpl<>(
			this::evolve,
			() -> EvolutionStart.of(newPopulation(), 1)
		);
	}

	/**
	 * Create a new evolution stream with the given initial individuals. The
	 * given {@code genotypes} must contain at least one element.
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        removed.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws java.lang.IllegalArgumentException if the given {@code genotypes}
	 *         collection is empty.
	 */
	public EvolutionStream<G, C> stream(
		final Collection<Genotype<G>> genotypes
	) {
		requireNonNull(genotypes);
		if (genotypes.isEmpty()) {
			throw new IllegalArgumentException(
				"Given genotype collection is empty."
			);
		}

		// Lazy population evaluation.
		final Supplier<Population<G, C>> population = () -> Stream.concat(
			genotypes.stream(),
			Stream.generate(genotypes.iterator().next()::newInstance)
		).limit(_offspringCount + _survivorsCount)
			.map(gt -> Phenotype.of(gt, _fitnessFunction, _fitnessScaler, 1))
			.collect(Population.toPopulation());

		return new EvolutionStreamImpl<>(
			this::evolve,
			() -> EvolutionStart.of(population.get(), 1)
		);
	}

	/* *************************************************************************
	 * Property access methods.
	 **************************************************************************/

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


	/* *************************************************************************
	 * Builder methods.
	 **************************************************************************/

	/**
	 * Create a new evolution {@code EngineBuilder} with the given fitness
	 * function and genotype factory.
	 *
	 * @param fitnessFunction the fitness function
	 * @param genotypeFactory the genotype factory
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new engine builder
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> newBuilder(
		final Function<? super Genotype<G>, ? extends C> fitnessFunction,
		final Factory<Genotype<G>> genotypeFactory
	) {
		return new Builder<>(genotypeFactory, fitnessFunction);
	}

	/**
	 * Create a new evolution {@code EngineBuilder} with the given fitness
	 * function and chromosome templates.
	 *
	 * @param fitnessFunction the fitness function
	 * @param chromosomes the chromosome templates
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new engine builder
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> newBuilder(
		final Function<? super Genotype<G>, ? extends C> fitnessFunction,
		final Chromosome<G>... chromosomes
	) {
		return new Builder<>(Genotype.of(chromosomes), fitnessFunction);
	}




	/* *************************************************************************
	 * Inner classes
	 **************************************************************************/

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 3.0
	 * @version 3.0 &mdash; <em>$Date$</em>
	 */
	public static final class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	{

		// No default values for this properties.
		private Function<? super Genotype<G>, ? extends C> _fitnessFunction;
		private Factory<Genotype<G>> _genotypeFactory;

		// This are the properties which default values.
		private Function<? super C, ? extends C> _fitnessScaler = Function.identity();
		private Selector<G, C> _survivorsSelector = new TournamentSelector<>(3);
		private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
		private Alterer<G, C> _alterer = Alterer.of(
			new SinglePointCrossover<G, C>(0.2),
			new Mutator<G, C>(0.15)
		);
		private Optimize _optimize = Optimize.MAXIMUM;
		private double _offspringFraction = 0.6;
		private int _populationSize = 50;
		private int _maximalPhenotypeAge = 70;

		private Executor _executor = ForkJoinPool.commonPool();

		private Builder(
			final Factory<Genotype<G>> genotypeFactory,
			final Function<? super Genotype<G>, ? extends C> fitnessFunction
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			_fitnessFunction = requireNonNull(fitnessFunction);
		}

		public Builder<G, C> fitnessFunction(
			Function<? super Genotype<G>, ? extends C> function
		) {
			_fitnessFunction = requireNonNull(function);
			return this;
		}

		public Builder<G, C> fitnessScaler(
			final Function<? super C, ? extends C> scaler
		) {
			_fitnessScaler = requireNonNull(scaler);
			return this;
		}

		public Builder<G, C> genotypeFactory(
			final Factory<Genotype<G>> genotypeFactory
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			return this;
		}

		public Builder<G, C> offspringSelector(
			final Selector<G, C> selector
		) {
			_offspringSelector = requireNonNull(selector);
			return this;
		}

		public Builder<G, C> survivorsSelector(
			final Selector<G, C> selector
		) {
			_survivorsSelector = requireNonNull(selector);
			return this;
		}

		@SafeVarargs
		public final Builder<G, C> alterers(final Alterer<G, C>... alterers) {
			_alterer = Alterer.of(alterers);
			return this;
		}

		public Builder<G, C> optimize(final Optimize optimize) {
			_optimize = requireNonNull(optimize);
			return this;
		}

		public Builder<G, C> offspringFraction(final double fraction) {
			_offspringFraction = probability(fraction);
			return this;
		}

		public Builder<G, C> populationSize(final int size) {
			if (size < 1) {
				throw new IllegalArgumentException(format(
					"Population size must be greater than zero, but was %s.", size
				));
			}
			_populationSize = size;
			return this;
		}

		public Builder<G, C> maximalPhenotypeAge(final int age) {
			if (age < 1) {
				throw new IllegalArgumentException(format(
					"Phenotype age must be greater than one, but was %s.", age
				));
			}
			_maximalPhenotypeAge = age;
			return this;
		}

		public Builder<G, C> executor(final Executor executor) {
			_executor = requireNonNull(executor);
			return this;
		}

		public Engine<G, C> build() {
			return new Engine<>(
				_fitnessFunction,
				_fitnessScaler,
				_genotypeFactory,
				_survivorsSelector,
				_offspringSelector,
				_alterer,
				_optimize,
				getOffspringCount(),
				getSurvivorsCount(),
				_maximalPhenotypeAge,
				_executor
			);
		}

		private int getSurvivorsCount() {
			return _populationSize - getOffspringCount();
		}

		private int getOffspringCount() {
			return (int)round(_offspringFraction*_populationSize);
		}

	}
}
