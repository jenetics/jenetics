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
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
import org.jenetics.util.Copyable;
import org.jenetics.util.Factory;
import org.jenetics.util.NanoClock;

/**
 * Genetic algorithm <em>engine</em> which is the main class. The following
 * example shows the main steps in initializing and executing the GA.
 *
 * <pre>{@code
 * public class RealFunction {
 *    // Definition of the fitness function.
 *    private static Double eval(final Genotype<DoubleGene> gt) {
 *        final double x = gt.getGene().doubleValue();
 *        return cos(0.5 + sin(x))*cos(x);
 *    }
 *
 *    public static void main(String[] args) {
 *        // Create/configuring the engine via its builder.
 *        final Engine<DoubleGene, Double> engine = Engine
 *            .builder(
 *                RealFunction::eval,
 *                DoubleChromosome.of(0.0, 2.0*PI))
 *            .populationSize(500)
 *            .optimize(Optimize.MINIMUM)
 *            .alterers(
 *                new Mutator<>(0.03),
 *                new MeanAlterer<>(0.6))
 *            .build();
 *
 *        // Execute the GA (engine).
 *        final Phenotype<DoubleGene, Double> result = engine.stream()
 *             // Truncate the evolution stream if no better individual could
 *             // be found after 5 consecutive generations.
 *            .limit(bySteadyFitness(5))
 *             // Terminate the evolution after maximal 100 generations.
 *            .limit(100)
 *            .collect(toBestPhenotype());
 *     }
 * }
 * }</pre>
 *
 * The architecture allows to decouple the configuration of the engine from the
 * execution. The {@code Engine} is configured via the {@code Engine.Builder}
 * class and can't be changed after creation. The actual <i>evolution</i> is
 * performed by the {@link EvolutionStream}, which is created by the
 * {@code Engine}.
 * <p>
 * <em>
 *     <b>This class is thread safe:</b>
 *     No mutable state is maintained by the engine. Therefore it is save to
 *     create multiple evolution streams with one engine, which may be actually
 *     used in different threads.
 * </em>
 *
 * @see Engine.Builder
 * @see EvolutionStart
 * @see EvolutionResult
 * @see EvolutionStream
 * @see EvolutionStatistics
 * @see Codec
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.7
 */
public final class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Function<EvolutionStart<G, C>, EvolutionResult<G, C>>
{

	// Needed context for population evolving.
	private final Function<? super Genotype<G>, ? extends C> _fitnessFunction;
	private final Function<? super C, ? extends C> _fitnessScaler;
	private final Factory<Genotype<G>> _genotypeFactory;
	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final Predicate<? super Phenotype<G, C>> _validator;
	private final Optimize _optimize;
	private final int _offspringCount;
	private final int _survivorsCount;
	private final long _maximalPhenotypeAge;

	// Execution context for concurrent execution of evolving steps.
	private final TimedExecutor _executor;
	private final Clock _clock;

	// Additional parameters.
	private final int _individualCreationRetries;


	/**
	 * Create a new GA engine with the given parameters.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @param survivorsSelector the selector used for selecting the survivors
	 * @param offspringSelector the selector used for selecting the offspring
	 * @param alterer the alterer used for altering the offspring
	 * @param validator phenotype validator which can override the default
	 *        implementation the {@link Phenotype#isValid()} method.
	 * @param optimize the kind of optimization (minimize or maximize)
	 * @param offspringCount the number of the offspring individuals
	 * @param survivorsCount the number of the survivor individuals
	 * @param maximalPhenotypeAge the maximal age of an individual
	 * @param executor the executor used for executing the single evolve steps
	 * @param clock the clock used for calculating the timing results
	 * @param individualCreationRetries the maximal number of attempts for
	 *        creating a valid individual.
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
		final Predicate<? super Phenotype<G, C>> validator,
		final Optimize optimize,
		final int offspringCount,
		final int survivorsCount,
		final long maximalPhenotypeAge,
		final Executor executor,
		final Clock clock,
		final int individualCreationRetries
	) {
		_fitnessFunction = requireNonNull(fitnessFunction);
		_fitnessScaler = requireNonNull(fitnessScaler);
		_genotypeFactory = requireNonNull(genotypeFactory);
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_validator = requireNonNull(validator);
		_optimize = requireNonNull(optimize);

		_offspringCount = require.nonNegative(offspringCount);
		_survivorsCount = require.nonNegative(survivorsCount);
		_maximalPhenotypeAge = require.positive(maximalPhenotypeAge);

		_executor = new TimedExecutor(requireNonNull(executor));
		_clock = requireNonNull(clock);

		if (individualCreationRetries < 0) {
			throw new IllegalArgumentException(format(
				"Retry count must not be negative: %d",
				individualCreationRetries
			));
		}
		_individualCreationRetries = individualCreationRetries;
	}

	/**
	 * Perform one evolution step with the given {@code population} and
	 * {@code generation}. New phenotypes are created with the fitness function
	 * and fitness scaler defined by this <em>engine</em>
	 * <p>
	 * <em>This method is thread-safe.</em>
	 *
	 * @see #evolve(EvolutionStart)
	 *
	 * @param population the population to evolve
	 * @param generation the current generation; used for calculating the
	 *        phenotype age.
	 * @return the evolution result
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public EvolutionResult<G, C> evolve(
		final Population<G, C> population,
		final long generation
	) {
		return evolve(EvolutionStart.of(population, generation));
	}

	/**
	 * Perform one evolution step with the given evolution {@code start} object
	 * New phenotypes are created with the fitness function and fitness scaler
	 * defined by this <em>engine</em>
	 * <p>
	 * <em>This method is thread-safe.</em>
	 *
	 * @since 3.1
	 * @see #evolve(org.jenetics.Population, long)
	 *
	 * @param start the evolution start object
	 * @return the evolution result
	 * @throws java.lang.NullPointerException if the given evolution
	 *         {@code start} is {@code null}
	 */
	public EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
		final Timer timer = Timer.of().start();

		final Population<G, C> startPopulation = start.getPopulation();

		// Initial evaluation of the population.
		final Timer evaluateTimer = Timer.of(_clock).start();
		evaluate(startPopulation);
		evaluateTimer.stop();

		// Select the offspring population.
		final CompletableFuture<TimedResult<Population<G, C>>> offspring =
			_executor.async(() ->
				selectOffspring(startPopulation),
				_clock
			);

		// Select the survivor population.
		final CompletableFuture<TimedResult<Population<G, C>>> survivors =
			_executor.async(() ->
				selectSurvivors(startPopulation),
				_clock
			);

		// Altering the offspring population.
		final CompletableFuture<TimedResult<AlterResult<G, C>>> alteredOffspring =
			_executor.thenApply(offspring, p ->
				alter(p.result, start.getGeneration()),
				_clock
			);

		// Filter and replace invalid and to old survivor individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredSurvivors =
			_executor.thenApply(survivors, pop ->
				filter(pop.result, start.getGeneration()),
				_clock
			);

		// Filter and replace invalid and to old offspring individuals.
		final CompletableFuture<TimedResult<FilterResult<G, C>>> filteredOffspring =
			_executor.thenApply(alteredOffspring, pop ->
				filter(pop.result.population, start.getGeneration()),
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
		final Population<G, C> pop = population.join();
		final TimedResult<Population<G, C>> result = TimedResult
			.of(() -> evaluate(pop), _clock)
			.get();


		final EvolutionDurations durations = EvolutionDurations.of(
			offspring.join().duration,
			survivors.join().duration,
			alteredOffspring.join().duration,
			filteredOffspring.join().duration,
			filteredSurvivors.join().duration,
			result.duration.plus(evaluateTimer.getTime()),
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
			start.getGeneration(),
			durations,
			killCount,
			invalidCount,
			alteredOffspring.join().result.alterCount
		);
	}

	/**
	 * This method is an <i>alias</i> for the {@link #evolve(EvolutionStart)}
	 * method.
	 *
	 * @since 3.1
	 */
	@Override
	public EvolutionResult<G, C> apply(final EvolutionStart<G, C> start) {
		return evolve(start);
	}

	// Selects the survivors population. A new population object is returned.
	private Population<G, C> selectSurvivors(final Population<G, C> population) {
		return _survivorsCount > 0
			?_survivorsSelector.select(population, _survivorsCount, _optimize)
			: Population.empty();
	}

	// Selects the offspring population. A new population object is returned.
	private Population<G, C> selectOffspring(final Population<G, C> population) {
		return _offspringCount > 0
			? _offspringSelector.select(population, _offspringCount, _optimize)
			: Population.empty();
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

			if (!_validator.test(individual)) {
				population.set(i, newPhenotype(generation));
				++invalidCount;
			} else if (individual.getAge(generation) > _maximalPhenotypeAge) {
				population.set(i, newPhenotype(generation));
				++killCount;
			}
		}

		return new FilterResult<>(population, killCount, invalidCount);
	}

	// Create a new and valid phenotype
	private Phenotype<G, C> newPhenotype(final long generation) {
		int count = 0;
		Phenotype<G, C> phenotype;
		do {
			phenotype = Phenotype.of(
				_genotypeFactory.newInstance(),
				generation,
				_fitnessFunction,
				_fitnessScaler
			);
		} while (++count < _individualCreationRetries &&
				!_validator.test(phenotype));

		return phenotype;
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


	/* *************************************************************************
	 * Evolution Stream/Iterator creation.
	 **************************************************************************/

	/**
	 * Create a new <b>infinite</b> evolution iterator with a newly created
	 * population. This is an alternative way for evolution. It lets the user
	 * start, stop and resume the evolution process whenever desired.
	 *
	 * @return a new <b>infinite</b> evolution iterator
	 */
	public Iterator<EvolutionResult<G, C>> iterator() {
		return new EvolutionIterator<>(
			this::evolutionStart,
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with a newly created
	 * population.
	 *
	 * @return a new evolution stream.
	 */
	public EvolutionStream<G, C> stream() {
		return EvolutionStream.of(this::evolutionStart, this::evolve);
	}

	private EvolutionStart<G, C> evolutionStart() {
		final int generation = 1;
		final int size = _offspringCount + _survivorsCount;

		final Population<G, C> population = new Population<G, C>(size)
			.fill(() -> newPhenotype(generation), size);

		return EvolutionStart.of(population, generation);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @param genotypes the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 */
	public Iterator<EvolutionResult<G, C>> iterator(
		final Iterable<Genotype<G>> genotypes
	) {
		requireNonNull(genotypes);

		return new EvolutionIterator<>(
			() -> evolutionStart(genotypes, 1),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @since 3.7
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 */
	public EvolutionStream<G, C> stream(final Iterable<Genotype<G>> genotypes) {
		requireNonNull(genotypes);

		return EvolutionStream.of(
			() -> evolutionStart(genotypes, 1),
			this::evolve
		);
	}

	private EvolutionStart<G, C> evolutionStart(
		final Iterable<Genotype<G>> genotypes,
		final long generation
	) {
		final Stream<Phenotype<G, C>> stream = Stream.concat(
			StreamSupport.stream(genotypes.spliterator(), false)
				.map(gt -> Phenotype.of(
					gt, generation, _fitnessFunction, _fitnessScaler)),
			Stream.generate(() -> newPhenotype(generation))
		);

		final Population<G, C> population = stream
			.limit(getPopulationSize())
			.collect(toPopulation());

		return EvolutionStart.of(population, generation);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @since 3.7
	 *
	 * @param genotypes the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public Iterator<EvolutionResult<G, C>> iterator(
		final Iterable<Genotype<G>> genotypes,
		final long generation
	) {
		requireNonNull(genotypes);
		require.positive(generation);

		return new EvolutionIterator<>(
			() -> evolutionStart(genotypes, generation),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * individuals. If an empty {@code Iterable} is given, the engines genotype
	 * factory is used for creating the population.
	 *
	 * @since 3.7
	 *
	 * @param genotypes the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code genotypes} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public EvolutionStream<G, C> stream(
		final Iterable<Genotype<G>> genotypes,
		final long generation
	) {
		requireNonNull(genotypes);

		return EvolutionStream.of(
			() -> evolutionStart(genotypes, generation),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @since 3.7
	 *
	 * @param population the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 */
	public Iterator<EvolutionResult<G, C>> iterator(
		final Population<G, C> population
	) {
		requireNonNull(population);

		return new EvolutionIterator<>(
			() -> evolutionStart(population, 1),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 */
	public EvolutionStream<G, C> stream(
		final Population<G, C> population
	) {
		requireNonNull(population);

		return EvolutionStream.of(
			() -> evolutionStart(population, 1),
			this::evolve
		);
	}

	private EvolutionStart<G, C> evolutionStart(
		final Population<G, C> population,
		final long generation
	) {
		final Stream<Phenotype<G, C>> stream = Stream.concat(
			population.stream()
				.map(p -> p.newInstance(
					p.getGeneration(),
					_fitnessFunction,
					_fitnessScaler)),
			Stream.generate(() -> newPhenotype(generation))
		);

		final Population<G, C> pop = stream
			.limit(getPopulationSize())
			.collect(toPopulation());

		return EvolutionStart.of(pop, generation);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution iterator.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the iterator starts from; must be greater
	 *        than zero.
	 * @return a new <b>infinite</b> evolution iterator
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is smaller
	 *        then one
	 */
	public Iterator<EvolutionResult<G, C>> iterator(
		final Population<G, C> population,
		final long generation
	) {
		requireNonNull(population);
		require.positive(generation);

		return new EvolutionIterator<>(
			() -> evolutionStart(population, generation),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution stream with the given initial
	 * population. If an empty {@code Population} is given, the engines genotype
	 * factory is used for creating the population. The given population might
	 * be the result of an other engine and this method allows to start the
	 * evolution with the outcome of an different engine. The fitness function
	 * and the fitness scaler are replaced by the one defined for this engine.
	 *
	 * @param population the initial individuals used for the evolution stream.
	 *        Missing individuals are created and individuals not needed are
	 *        skipped.
	 * @param generation the generation the stream starts from; must be greater
	 *        than zero.
	 * @return a new evolution stream.
	 * @throws java.lang.NullPointerException if the given {@code population} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the given {@code generation} is
	 *         smaller then one
	 */
	public EvolutionStream<G, C> stream(
		final Population<G, C> population,
		final long generation
	) {
		requireNonNull(population);
		require.positive(generation);

		return EvolutionStream.of(
			() -> evolutionStart(population, generation),
			this::evolve
		);
	}

	/**
	 * Create a new <b>infinite</b> evolution iterator starting with a
	 * previously evolved {@link EvolutionResult}. The iterator is initialized
	 * with the population of the given {@code result} and its total generation
	 * {@link EvolutionResult#getTotalGenerations()}.
	 *
	 * @since 3.7
	 *
	 * @param result the previously evolved {@code EvolutionResult}
	 * @return a new evolution stream, which continues a previous one
	 * @throws NullPointerException if the given evolution {@code result} is
	 *         {@code null}
	 */
	public Iterator<EvolutionResult<G, C>> iterator(
		final EvolutionResult<G, C> result
	) {
		return iterator(result.getPopulation(), result.getTotalGenerations());
	}

	/**
	 * Create a new {@code EvolutionStream} starting with a previously evolved
	 * {@link EvolutionResult}. The stream is initialized with the population
	 * of the given {@code result} and its total generation
	 * {@link EvolutionResult#getTotalGenerations()}.
	 *
	 * <pre>{@code
	 * private static final Problem<Double, DoubleGene, Double>
	 * PROBLEM = Problem.of(
	 *     x -> cos(0.5 + sin(x))*cos(x),
	 *     codecs.ofScalar(DoubleRange.of(0.0, 2.0*PI))
	 * );
	 *
	 * private static final Engine<DoubleGene, Double>
	 * ENGINE = Engine.builder(PROBLEM)
	 *     .optimize(Optimize.MINIMUM)
	 *     .offspringSelector(new RouletteWheelSelector<>())
	 *     .build();
	 *
	 * public static void main(final String[] args) throws IOException {
	 *     // Result of the first evolution run.
	 *     final EvolutionResult<DoubleGene, Double> rescue = ENGINE.stream()
	 *         .limit(limit.bySteadyFitness(10))
	 *         .collect(EvolutionResult.toBestEvolutionResult());
	 *
	 *     // Save the result of the first run into a file.
	 *     final Path path = Paths.get("result.bin");
	 *     IO.object.write(rescue, path);
	 *
	 *     // Load the previous result and continue evolution.
	 *     \@SuppressWarnings("unchecked")
	 *     final EvolutionResult<DoubleGene, Double> result = ENGINE
	 *         .stream((EvolutionResult<DoubleGene, Double>)IO.object.read(path))
	 *         .limit(limit.bySteadyFitness(20))
	 *         .collect(EvolutionResult.toBestEvolutionResult());
	 *
	 *     System.out.println(result.getBestPhenotype());
	 * }
	 * }</pre>
	 *
	 * The example above shows how to save an {@link EvolutionResult} from a
	 * first run, save it to disk and continue the evolution.
	 *
	 * @since 3.7
	 *
	 * @param result the previously evolved {@code EvolutionResult}
	 * @return a new evolution stream, which continues a previous one
	 * @throws NullPointerException if the given evolution {@code result} is
	 *         {@code null}
	 */
	public EvolutionStream<G, C> stream(final EvolutionResult<G, C> result) {
		return stream(result.getPopulation(), result.getTotalGenerations());
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
	public long getMaximalPhenotypeAge() {
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
	 * Return the {@link Clock} the engine is using for measuring the execution
	 * time.
	 *
	 * @return the clock used for measuring the execution time
	 */
	public Clock getClock() {
		return _clock;
	}

	/**
	 * Return the {@link Executor} the engine is using for executing the
	 * evolution steps.
	 *
	 * @return the executor used for performing the evolution steps
	 */
	public Executor getExecutor() {
		return _executor.get();
	}


	/* *************************************************************************
	 * Builder methods.
	 **************************************************************************/

	/**
	 * Create a new evolution {@code Engine.Builder} initialized with the values
	 * of the current evolution {@code Engine}. With this method, the evolution
	 * engine can serve as a template for a new one.
	 *
	 * @return a new engine builder
	 */
	public Builder<G, C> builder() {
		return new Builder<G, C>(_genotypeFactory, _fitnessFunction)
			.alterers(_alterer)
			.clock(_clock)
			.executor(_executor.get())
			.fitnessScaler(_fitnessScaler)
			.maximalPhenotypeAge(_maximalPhenotypeAge)
			.offspringFraction((double)_offspringCount/(double)getPopulationSize())
			.offspringSelector(_offspringSelector)
			.optimize(_optimize)
			.phenotypeValidator(_validator)
			.populationSize(getPopulationSize())
			.survivorsSelector(_survivorsSelector)
			.individualCreationRetries(_individualCreationRetries);
	}

	/**
	 * Create a new evolution {@code Engine.Builder} for the given
	 * {@link Problem}.
	 *
	 * @since 3.4
	 *
	 * @param problem the problem to be solved by the evolution {@code Engine}
	 * @param <T> the (<i>native</i>) argument type of the problem fitness function
	 * @param <G> the gene type the evolution engine is working with
	 * @param <C> the result type of the fitness function
	 * @return Create a new evolution {@code Engine.Builder}
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(final Problem<T, G, C> problem) {
		return builder(problem.fitness(), problem.codec());
	}

	/**
	 * Create a new evolution {@code Engine.Builder} with the given fitness
	 * function and genotype factory.
	 *
	 * @param ff the fitness function
	 * @param genotypeFactory the genotype factory
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new engine builder
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(
		final Function<? super Genotype<G>, ? extends C> ff,
		final Factory<Genotype<G>> genotypeFactory
	) {
		return new Builder<>(genotypeFactory, ff);
	}

	/**
	 * Create a new evolution {@code Engine.Builder} with the given fitness
	 * function and chromosome templates.
	 *
	 * @param ff the fitness function
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
		final Function<? super Genotype<G>, ? extends C> ff,
		final Chromosome<G> chromosome,
		final Chromosome<G>... chromosomes
	) {
		return new Builder<>(Genotype.of(chromosome, chromosomes), ff);
	}

	/**
	 * Create a new evolution {@code Engine.Builder} with the given fitness
	 * function and problem {@code codec}.
	 *
	 * @since 3.2
	 *
	 * @param ff the fitness function
	 * @param codec the problem codec
	 * @param <T> the fitness function input type
	 * @param <C> the fitness function result type
	 * @param <G> the gene type
	 * @return a new engine builder
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}.
	 */
	public static <T, G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(
		final Function<? super T, ? extends C> ff,
		final Codec<T, G> codec
	) {
		return builder(ff.compose(codec.decoder()), codec.encoding());
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
	 * @version 3.0
	 */
	public static final class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Copyable<Builder<G, C>>
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
		private Predicate<? super Phenotype<G, C>> _validator = Phenotype::isValid;
		private Optimize _optimize = Optimize.MAXIMUM;
		private double _offspringFraction = 0.6;
		private int _populationSize = 50;
		private long _maximalPhenotypeAge = 70;

		private Executor _executor = ForkJoinPool.commonPool();
		private Clock _clock = NanoClock.systemUTC();

		private int _individualCreationRetries = 10;

		private Builder(
			final Factory<Genotype<G>> genotypeFactory,
			final Function<? super Genotype<G>, ? extends C> fitnessFunction
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			_fitnessFunction = requireNonNull(fitnessFunction);
		}

		/**
		 * Set the fitness function of the evolution {@code Engine}.
		 *
		 * @param function the fitness function to use in the GA {@code Engine}
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> fitnessFunction(
			Function<? super Genotype<G>, ? extends C> function
		) {
			_fitnessFunction = requireNonNull(function);
			return this;
		}

		/**
		 * Set the fitness scaler of the evolution {@code Engine}. <i>Default
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

		/**
		 * The genotype factory used for creating new individuals.
		 *
		 * @param genotypeFactory the genotype factory for creating new
		 *        individuals.
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> genotypeFactory(
			final Factory<Genotype<G>> genotypeFactory
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			return this;
		}

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
		 * The phenotype validator used for detecting invalid individuals.
		 * Alternatively it is also possible to set the genotype validator with
		 * {@link #genotypeFactory(Factory)}, which will replace any
		 * previously set phenotype validators.
		 *
		 * <p><i>Default value is set to {@code Phenotype::isValid}.</i></p>
		 *
		 * @since 3.1
		 *
		 * @see #genotypeValidator(Predicate)
		 *
		 * @param validator the {@code validator} used for validating the
		 *        individuals (phenotypes).
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.NullPointerException if the {@code validator} is
		 *         {@code null}.
		 */
		public Builder<G, C> phenotypeValidator(
			final Predicate<? super Phenotype<G, C>> validator
		) {
			_validator = requireNonNull(validator);
			return this;
		}

		/**
		 * The genotype validator used for detecting invalid individuals.
		 * Alternatively it is also possible to set the phenotype validator with
		 * {@link #phenotypeValidator(Predicate)}, which will replace any
		 * previously set genotype validators.
		 *
		 * <p><i>Default value is set to {@code Genotype::isValid}.</i></p>
		 *
		 * @since 3.1
		 *
		 * @see #phenotypeValidator(Predicate)
		 *
		 * @param validator the {@code validator} used for validating the
		 *        individuals (genotypes).
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.NullPointerException if the {@code validator} is
		 *         {@code null}.
		 */
		public Builder<G, C> genotypeValidator(
			final Predicate<? super Genotype<G>> validator
		) {
			requireNonNull(validator);

			_validator = pt -> validator.test(pt.getGenotype());
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
		 * Set to a fitness maximizing strategy.
		 *
		 * @since 3.4
		 *
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> maximizing() {
			return optimize(Optimize.MAXIMUM);
		}

		/**
		 * Set to a fitness minimizing strategy.
		 *
		 * @since 3.4
		 *
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> minimizing() {
			return optimize(Optimize.MINIMUM);
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
		public Builder<G, C> maximalPhenotypeAge(final long age) {
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
			return this;
		}

		/**
		 * The maximal number of attempt before the {@code Engine} gives up
		 * creating a valid individual ({@code Phenotype}). <i>Default values is
		 * set to {@code 10}.</i>
		 *
		 * @since 3.1
		 *
		 * @param retries the maximal retry count
		 * @throws IllegalArgumentException if the given retry {@code count} is
		 *         smaller than zero.
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> individualCreationRetries(final int retries) {
			if (retries < 0) {
				throw new IllegalArgumentException(format(
					"Retry count must not be negative: %d",
					retries
				));
			}
			_individualCreationRetries = retries;
			return this;
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
				_validator,
				_optimize,
				getOffspringCount(),
				getSurvivorsCount(),
				_maximalPhenotypeAge,
				_executor,
				_clock,
				_individualCreationRetries
			);
		}

		private int getSurvivorsCount() {
			return _populationSize - getOffspringCount();
		}

		private int getOffspringCount() {
			return (int)round(_offspringFraction*_populationSize);
		}

		/**
		 * Return the used {@link Alterer} of the GA.
		 *
		 * @return the used {@link Alterer} of the GA.
		 */
		public Alterer<G, C> getAlterers() {
			return _alterer;
		}

		/**
		 * Return the {@link Clock} the engine is using for measuring the execution
		 * time.
		 *
		 * @since 3.1
		 *
		 * @return the clock used for measuring the execution time
		 */
		public Clock getClock() {
			return _clock;
		}

		/**
		 * Return the {@link Executor} the engine is using for executing the
		 * evolution steps.
		 *
		 * @since 3.1
		 *
		 * @return the executor used for performing the evolution steps
		 */
		public Executor getExecutor() {
			return _executor;
		}

		/**
		 * Return the fitness function of the GA engine.
		 *
		 * @since 3.1
		 *
		 * @return the fitness function
		 */
		public Function<? super Genotype<G>, ? extends C> getFitnessFunction() {
			return _fitnessFunction;
		}

		/**
		 * Return the fitness scaler of the GA engine.
		 *
		 * @since 3.1
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
		 * @since 3.1
		 *
		 * @return the used genotype {@link Factory} of the GA.
		 */
		public Factory<Genotype<G>> getGenotypeFactory() {
			return _genotypeFactory;
		}

		/**
		 * Return the maximal allowed phenotype age.
		 *
		 * @since 3.1
		 *
		 * @return the maximal allowed phenotype age
		 */
		public long getMaximalPhenotypeAge() {
			return _maximalPhenotypeAge;
		}

		/**
		 * Return the offspring fraction.
		 *
		 * @return the offspring fraction.
		 */
		public double getOffspringFraction() {
			return _offspringFraction;
		}

		/**
		 * Return the used offspring {@link Selector} of the GA.
		 *
		 * @since 3.1
		 *
		 * @return the used offspring {@link Selector} of the GA.
		 */
		public Selector<G, C> getOffspringSelector() {
			return _offspringSelector;
		}

		/**
		 * Return the used survivor {@link Selector} of the GA.
		 *
		 * @since 3.1
		 *
		 * @return the used survivor {@link Selector} of the GA.
		 */
		public Selector<G, C> getSurvivorsSelector() {
			return _survivorsSelector;
		}

		/**
		 * Return the optimization strategy.
		 *
		 * @since 3.1
		 *
		 * @return the optimization strategy
		 */
		public Optimize getOptimize() {
			return _optimize;
		}

		/**
		 * Return the number of individuals of a population.
		 *
		 * @since 3.1
		 *
		 * @return the number of individuals of a population
		 */
		public int getPopulationSize() {
			return _populationSize;
		}

		/**
		 * Return the maximal number of attempt before the {@code Engine} gives
		 * up creating a valid individual ({@code Phenotype}).
		 *
		 * @since 3.1
		 *
		 * @return the maximal number of {@code Phenotype} creation attempts
		 */
		public int getIndividualCreationRetries() {
			return _individualCreationRetries;
		}

		/**
		 * Create a new builder, with the current configuration.
		 *
		 * @since 3.1
		 *
		 * @return a new builder, with the current configuration
		 */
		@Override
		public Builder<G, C> copy() {
			return new Builder<G, C>(_genotypeFactory, _fitnessFunction)
				.alterers(_alterer)
				.clock(_clock)
				.executor(_executor)
				.fitnessScaler(_fitnessScaler)
				.maximalPhenotypeAge(_maximalPhenotypeAge)
				.offspringFraction(_offspringFraction)
				.offspringSelector(_offspringSelector)
				.phenotypeValidator(_validator)
				.optimize(_optimize)
				.populationSize(_populationSize)
				.survivorsSelector(_survivorsSelector)
				.individualCreationRetries(_individualCreationRetries);
		}

	}
}
