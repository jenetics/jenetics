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
import static org.jenetics.Population.toPopulation;
import static org.jenetics.internal.util.require.probability;

import java.time.Clock;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.NanoClock;
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
import org.jenetics.util.Factory;

/**
 * Genetic algorithm <em>engine</em> which is the main class. The following
 * example shows the main steps in initializing and executing the GA.
 *
 * [code]
 * public class RealFunction {
 *    // Definition of the fitness function.
 *    private static Double evaluate(final Genotype&lt;DoubleGene&gt; gt) {
 *        final double x = gt.getGene().doubleValue();
 *        return cos(0.5 + sin(x)) * cos(x);
 *    }
 *
 *    public static void main(String[] args) {
 *        // Create/configuring the engine via its builder.
 *        final Engine&lt;DoubleGene, Double&gt; engine = Engine
 *            .builder(
 *                RealFunction::evaluate,
 *                DoubleChromosome.of(0.0, 2.0*PI))
 *            .populationSize(500)
 *            .optimize(Optimize.MINIMUM)
 *            .alterers(
 *                new Mutator&lt;&gt;(0.03),
 *                new MeanAlterer&lt;&gt;(0.6))
 *            .build();
 *
 *        // Execute the GA (engine).
 *        final Phenotype&lt;DoubleGene, Double&gt; result = engine.stream()
 *             // Truncate the evolution stream if no better individual could
 *             // be found after 5 consecutive generations.
 *            .limit(bySteadyFitness(5)
 *             // Terminate the evolution after maximal 100 generations.
 *            .limit(100)
 *            .collect(toBestPhenotype());
 *     }
 * }
 * [/code]
 *
 * The architecture allows to decouple the configuration of the engine from the
 * execution. The {@code Engine} is configured via the {@code Engine.Builder}
 * class and can't be changed after creation. The actual <i>evolution</i> is
 * performed by the {@link EvolutionStream}, which is created by the
 * {@code Engine}.
 * <p>
 * <em>No mutable state is maintained by the engine.</em>
 *
 * @see Engine.Builder
 * @see EvolutionResult
 * @see EvolutionStream
 * @see EvolutionStatistics
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date: 2014-10-28 $</em>
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
	private final Clock _clock;


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
	 * @param clock the clock used for calculating the timing results
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
		final Executor executor,
		final Clock clock
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
		_clock = requireNonNull(clock);
	}

	/**
	 * Perform one evolution step with the given {@code population} and
	 * {@code generation}. New phenotypes are created with the fitness function
	 * and fitness scaler defined by this <em>engine</em>.
     * <p>
     * <em>This method is thread-safe.</em>
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
		return evolve(new EvolutionStart<>(population, generation));
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
				selectOffspring(start.population),
				_clock
			);

		// Select the survivor population.
		final CompletableFuture<TimedResult<Population<G, C>>> survivors =
			_executor.async(() ->
				selectSurvivors(start.population),
				_clock
			);

		// Altering the offspring population.
		final CompletableFuture<TimedResult<AlterResult<G, C>>> alteredOffspring =
			_executor.thenApply(offspring, p ->
				alter(p.result, start.generation),
				_clock
			);

		// Filter and replace invalid and to old survivor individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredSurvivors =
			_executor.thenApply(survivors, pop ->
				filter(pop.result, start.generation),
				_clock
			);

		// Filter and replace invalid and to old offspring individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredOffspring =
			_executor.thenApply(alteredOffspring, pop ->
				filter(pop.result.population, start.generation),
				_clock
			);

		// Combining survivors and offspring to the new population.
		final CompletableFuture<Population<G, C>> population =
			filteredSurvivors.thenCombineAsync(filteredOffspring, (s, o) -> {
					final Population<G, C> pop = s.result.population;
					pop.addAll(o.result.population);
					return pop;
				},
				_executor.get()
			);

		// Evaluate the fitness-function and wait for result.
		final TimedResult<Population<G, C>> result = population
			.thenApply(TimedResult.of(this::evaluate, _clock))
			.join();

		final EvolutionDurations durations = EvolutionDurations.of(
			offspring.join().duration,
			survivors.join().duration,
			alteredOffspring.join().duration,
			filteredOffspring.join().duration,
			filteredSurvivors.join().duration,
			result.duration,
			timer.stop().getTime()
		);

		final int killCount =
			filteredOffspring.join().result.killCount +
			filteredSurvivors.join().result.killCount;

		final int invalidCount =
			filteredOffspring.join().result.invalidCount +
			filteredSurvivors.join().result.invalidCount;

		return EvolutionResult.of(
			_optimize,
			result.result,
			start.generation,
			durations,
			killCount,
			invalidCount,
			alteredOffspring.join().result.alterCount
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
		final long generation
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

		return new FilterResult<>(population, killCount, invalidCount);
	}

	// Create a new phenotype
	private Phenotype<G, C> newPhenotype(final long generation) {
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
		final long generation
	) {
		return new AlterResult<>(
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
	 * Create a new <b>infinite</b> evolution iterator. This is an alternative
	 * way for evolution. It lets the user start, stop and resume the evolution
	 * process whenever desired.
	 *
	 * @return a new <b>infinite</b> evolution iterator
	 */
	public Iterator<EvolutionResult<G, C>> iterator() {
		return new EvolutionIterator<>(
			this::evolve,
			this::evolutionStart
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with a newly created
	 * population.
	 *
	 * @return a new evolution stream.
	 */
	public EvolutionStream<G, C> stream() {
		return new EvolutionStreamImpl<>(
			this::evolve,
			this::evolutionStart
		);
	}

	private EvolutionStart<G, C> evolutionStart() {
		final int generation = 1;
		final int size = _offspringCount + _survivorsCount;

		final Population<G, C> population = new Population<G, C>(size)
			.fill(() -> newPhenotype(generation), size);

		return new EvolutionStart<>(population, generation);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 */
	public EvolutionStream<G, C> stream(
		final Collection<Genotype<G>> genotypes
	) {
		requireNonNull(genotypes);

		return new EvolutionStreamImpl<>(
			this::evolve,
			() -> evolutionStart(genotypes)
		);
	}

	private EvolutionStart<G, C> evolutionStart(
		final Collection<Genotype<G>> genotypes
	) {
		final Stream<Genotype<G>> stream = Stream.concat(
			genotypes.stream(),
			genotypes.stream().findFirst()
				.map(Factory::instances)
				.orElse(_genotypeFactory.instances())
		);

		final int generation = 1;
		final int size = _offspringCount + _survivorsCount;
		final Population<G, C> pop = stream.limit(size)
			.map(gt -> Phenotype.of(
				gt, _fitnessFunction, _fitnessScaler, generation))
			.collect(toPopulation());

		return new EvolutionStart<>(pop, generation);
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
	public Selector<G, C> getOffspringSelector() {
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
	 * Return the number of individuals of a population.
	 *
	 * @return the number of individuals of a population
	 */
	public int getPopulationSize() {
		return _offspringCount + _survivorsCount;
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
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(
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
	 * @param chromosome the first chromosome
	 * @param chromosomes the chromosome templates
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new engine builder
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(
		final Function<? super Genotype<G>, ? extends C> fitnessFunction,
		final Chromosome<G> chromosome,
		final Chromosome<G>... chromosomes
	) {
		return new Builder<>(
			Genotype.of(chromosome, chromosomes),
			fitnessFunction
		);
	}



	/* *************************************************************************
	 * Inner classes
	 **************************************************************************/

	/**
	 * Builder class for building GA {@code Engine} instances.
	 *
	 * @see Engine
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 3.0
	 * @version 3.0 &mdash; <em>$Date: 2014-10-28 $</em>
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
		private Function<? super C, ? extends C> _fitnessScaler = a -> a;
		private Selector<G, C> _survivorsSelector = new TournamentSelector<>(3);
		private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
		private Alterer<G, C> _alterer = Alterer.of(
			new SinglePointCrossover<G, C>(0.2),
			new Mutator<>(0.15)
		);
		private Optimize _optimize = Optimize.MAXIMUM;
		private double _offspringFraction = 0.6;
		private int _populationSize = 50;
		private int _maximalPhenotypeAge = 70;

		private Executor _executor = ForkJoinPool.commonPool();
		private Clock _clock = NanoClock.INSTANCE;

		private Builder(
			final Factory<Genotype<G>> genotypeFactory,
			final Function<? super Genotype<G>, ? extends C> fitnessFunction
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			_fitnessFunction = requireNonNull(fitnessFunction);
		}

//		/**
//		 * Set the fitness function of the build GA {@code Engine}.
//		 *
//		 * @param function the fitness function to use in the GA {@code Engine}
//		 * @return {@code this} builder, for command chaining
//		 */
//		public Builder<G, C> fitnessFunction(
//			Function<? super Genotype<G>, ? extends C> function
//		) {
//			_fitnessFunction = requireNonNull(function);
//			return this;
//		}

		/**
		 * Set the fitness scaler of the build GA {@code Engine}. <i>Default
		 * value is set to the identity function.</i>
		 *
		 * @param scaler the fitness scale to use in the GA {@code Engine}
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> fitnessScaler(
			final Function<? super C, ? extends C> scaler
		) {
			_fitnessScaler = requireNonNull(scaler);
			return this;
		}

//		/**
//		 * The genotype factory used for creating new individuals.
//		 *
//		 * @param genotypeFactory the genotype factory for creating new
//		 *        individuals.
//		 * @return {@code this} builder, for command chaining
//		 */
//		public Builder<G, C> genotypeFactory(
//			final Factory<Genotype<G>> genotypeFactory
//		) {
//			_genotypeFactory = requireNonNull(genotypeFactory);
//			return this;
//		}

		/**
		 * The selector used for selecting the offspring population. <i>Default
		 * values is set to {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting the offspring population
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> offspringSelector(
			final Selector<G, C> selector
		) {
			_offspringSelector = requireNonNull(selector);
			return this;
		}

		/**
		 * The selector used for selecting the survivors population. <i>Default
		 * values is set to {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting survivors population
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> survivorsSelector(
			final Selector<G, C> selector
		) {
			_survivorsSelector = requireNonNull(selector);
			return this;
		}

		/**
		 * The selector used for selecting the survivors and offspring
		 * population. <i>Default values is set to
		 * {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting survivors and offspring population
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> selector(final Selector<G, C> selector) {
			_offspringSelector = requireNonNull(selector);
			_survivorsSelector = requireNonNull(selector);
			return this;
		}

		/**
		 * The alterers used for alter the offspring population. <i>Default
		 * values is set to {@code new SinglePointCrossover<>(0.2)} followed by
		 * {@code new Mutator<>(0.15)}.</i>
		 *
		 * @param first the first alterer used for alter the offspring
		 *        population
		 * @param rest the rest of the alterers used for alter the offspring
		 *        population
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.NullPointerException if one of the alterers is
		 *         {@code null}.
		 */
		@SafeVarargs
		public final Builder<G, C> alterers(
			final Alterer<G, C> first,
			final Alterer<G, C>... rest
		) {
			requireNonNull(first);
			Stream.of(rest).forEach(Objects::requireNonNull);

			_alterer = rest.length == 0 ?
				first :
				Alterer.of(rest).compose(first);

			return this;
		}

		/**
		 * The optimization strategy used by the engine. <i>Default values is
		 * set to {@code Optimize.MAXIMUM}.</i>
		 *
		 * @param optimize the optimization strategy used by the engine
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> optimize(final Optimize optimize) {
			_optimize = requireNonNull(optimize);
			return this;
		}

		/**
		 * The offspring fraction. <i>Default values is set to {@code 0.6}.</i>
		 *
		 * @param fraction the offspring fraction
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the fraction is not
		 *         within the range [0, 1].
		 */
		public Builder<G, C> offspringFraction(final double fraction) {
			_offspringFraction = probability(fraction);
			return this;
		}

		/**
		 * The number of individuals which form the population. <i>Default
		 * values is set to {@code 50}.</i>
		 *
		 * @param size the number of individuals of a population
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if {@code size < 1}
		 */
		public Builder<G, C> populationSize(final int size) {
			if (size < 1) {
				throw new IllegalArgumentException(format(
					"Population size must be greater than zero, but was %s.", size
				));
			}
			_populationSize = size;
			return this;
		}

		/**
		 * The maximal allowed age of a phenotype. <i>Default values is set to
		 * {@code 70}.</i>
		 *
		 * @param age the maximal phenotype age
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if {@code age < 1}
		 */
		public Builder<G, C> maximalPhenotypeAge(final int age) {
			if (age < 1) {
				throw new IllegalArgumentException(format(
					"Phenotype age must be greater than one, but was %s.", age
				));
			}
			_maximalPhenotypeAge = age;
			return this;
		}

		/**
		 * The executor used by the engine.
		 *
		 * @param executor the executor used by the engine
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> executor(final Executor executor) {
			_executor = requireNonNull(executor);
			return this;
		}

		/**
		 * The clock used for calculating the execution durations.
		 *
		 * @param clock the clock used for calculating the execution durations
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> clock(final Clock clock) {
			_clock = requireNonNull(clock);
			return null;
		}

		/**
		 * Builds an new {@code Engine} instance from the set properties.
		 *
		 * @return an new {@code Engine} instance from the set properties
		 */
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
				_executor,
				_clock
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
