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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.engine;

import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static io.jenetics.internal.util.require.probability;

import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import io.jenetics.Alterer;
import io.jenetics.AltererResult;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
import io.jenetics.SinglePointCrossover;
import io.jenetics.TournamentSelector;
import io.jenetics.internal.util.require;
import io.jenetics.util.Copyable;
import io.jenetics.util.Factory;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.NanoClock;
import io.jenetics.util.Seq;

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
 *
 * @implNote
 *     This class is thread safe:
 *     No mutable state is maintained by the engine. Therefore it is save to
 *     create multiple evolution streams with one engine, which may be actually
 *     used in different threads.
 *
 * @see Engine.Builder
 * @see EvolutionStart
 * @see EvolutionResult
 * @see EvolutionStream
 * @see EvolutionStatistics
 * @see Codec
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 5.1
 */
public final class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Evolution<G, C>,
		Function<EvolutionStart<G, C>, EvolutionResult<G, C>>,
		EvolutionStreamable<G, C>
{

	// Problem definition.
	private final Evaluator<G, C> _evaluator;
	private final Factory<Genotype<G>> _genotypeFactory;

	// Evolution parameters.
	private final Selector<G, C> _survivorsSelector;
	private final Selector<G, C> _offspringSelector;
	private final Alterer<G, C> _alterer;
	private final Optimize _optimize;
	private final int _offspringCount;
	private final int _survivorsCount;
	private final long _maximalPhenotypeAge;

	// Execution context for concurrent execution of evolving steps.
	private final Executor _executor;
	private final Clock _clock;

	// Additional parameters.
	private final Constraint<G, C> _constraint;
	private final UnaryOperator<EvolutionResult<G, C>> _mapper;


	/**
	 * Create a new GA engine with the given parameters.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param survivorsSelector the selector used for selecting the survivors
	 * @param offspringSelector the selector used for selecting the offspring
	 * @param alterer the alterer used for altering the offspring
	 * @param constraint phenotype constraint which can override the default
	 *        implementation the {@link Phenotype#isValid()} method and repairs
	 *        invalid phenotypes when needed.
	 * @param optimize the kind of optimization (minimize or maximize)
	 * @param offspringCount the number of the offspring individuals
	 * @param survivorsCount the number of the survivor individuals
	 * @param maximalPhenotypeAge the maximal age of an individual
	 * @param executor the executor used for executing the single evolve steps
	 * @param evaluator the population fitness evaluator
	 * @param clock the clock used for calculating the timing results
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given integer values are smaller
	 *         than one.
	 */
	Engine(
		final Evaluator<G, C> evaluator,
		final Factory<Genotype<G>> genotypeFactory,
		final Selector<G, C> survivorsSelector,
		final Selector<G, C> offspringSelector,
		final Alterer<G, C> alterer,
		final Constraint<G, C> constraint,
		final Optimize optimize,
		final int offspringCount,
		final int survivorsCount,
		final long maximalPhenotypeAge,
		final Executor executor,
		final Clock clock,
		final UnaryOperator<EvolutionResult<G, C>> mapper
	) {
		_evaluator = requireNonNull(evaluator);
		_genotypeFactory = requireNonNull(genotypeFactory);
		_survivorsSelector = requireNonNull(survivorsSelector);
		_offspringSelector = requireNonNull(offspringSelector);
		_alterer = requireNonNull(alterer);
		_constraint = requireNonNull(constraint);
		_optimize = requireNonNull(optimize);

		_offspringCount = require.nonNegative(offspringCount);
		_survivorsCount = require.nonNegative(survivorsCount);
		_maximalPhenotypeAge = require.positive(maximalPhenotypeAge);

		_executor = requireNonNull(executor);
		_clock = requireNonNull(clock);
		_mapper = requireNonNull(mapper);
	}

	/**
	 * This method is an <i>alias</i> for the {@link #evolve(EvolutionStart)}
	 * method.
	 *
	 * @since 3.1
	 *
	 * @see Evolution
	 *
	 * @deprecated Will be removed and superseded by {@link #evolve(EvolutionStart)}
	 */
	@Deprecated
	@Override
	public EvolutionResult<G, C> apply(final EvolutionStart<G, C> start) {
		return evolve(start);
	}

	@Override
	public EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
		// Create initial population if `start` is empty.
		final EvolutionStart<G, C> es = start.getPopulation().isEmpty()
			? evolutionStart(start)
			: start;

		final EvolutionTiming timing = new EvolutionTiming(_clock);
		timing.evolve.start();

		// Initial evaluation of the population.
		final ISeq<Phenotype<G, C>> evaluated = timing.evaluation.timing(() ->
			evaluate(es.getPopulation())
		);

		// Select the offspring population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> offspring =
			supplyAsync(() ->
				timing.offspringSelection.timing(() ->
					selectOffspring(evaluated)
				),
				_executor
			);

		// Select the survivor population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> survivors =
			supplyAsync(() ->
				timing.survivorsSelection.timing(() ->
					selectSurvivors(evaluated)
				),
				_executor
			);

		// Altering the offspring population.
		final CompletableFuture<AltererResult<G, C>> alteredOffspring =
			offspring.thenApplyAsync(off ->
				timing.offspringAlter.timing(() ->
					_alterer.alter(off, es.getGeneration())
				),
				_executor
			);

		// Filter and replace invalid and old survivor individuals.
		final CompletableFuture<FilterResult<G, C>> filteredSurvivors =
			survivors.thenApplyAsync(sur ->
				timing.survivorFilter.timing(() ->
					filter(sur, es.getGeneration())
				),
				_executor
			);

		// Filter and replace invalid and old offspring individuals.
		final CompletableFuture<FilterResult<G, C>> filteredOffspring =
			alteredOffspring.thenApplyAsync(off ->
				timing.offspringFilter.timing(() ->
					filter(off.getPopulation(), es.getGeneration())
				),
				_executor
			);

		// Combining survivors and offspring to the new population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> population =
			filteredSurvivors.thenCombineAsync(
				filteredOffspring,
				(s, o) -> ISeq.of(s.population.append(o.population)),
				_executor
			);

		// Evaluate the fitness-function and wait for result.
		final ISeq<Phenotype<G, C>> pop = population.join();
		final ISeq<Phenotype<G, C>> result = timing.evaluation.timing(() ->
			evaluate(pop)
		);

		final int killCount =
			filteredOffspring.join().killCount +
			filteredSurvivors.join().killCount;

		final int invalidCount =
			filteredOffspring.join().invalidCount +
			filteredSurvivors.join().invalidCount;

		final int alterationCount = alteredOffspring.join().getAlterations();

		EvolutionResult<G, C> er = EvolutionResult.of(
			_optimize,
			result,
			es.getGeneration(),
			timing.toDurations(),
			killCount,
			invalidCount,
			alterationCount
		);
		if (!UnaryOperator.identity().equals(_mapper)) {
			final EvolutionResult<G, C> mapped = _mapper.apply(er);
			er = er.with(timing.evaluation.timing(() ->
				evaluate(mapped.getPopulation())
			));
		}

		timing.evolve.stop();
		return er.with(timing.toDurations());
	}

	// Selects the survivors population. A new population object is returned.
	private ISeq<Phenotype<G, C>>
	selectSurvivors(final ISeq<Phenotype<G, C>> population) {
		return _survivorsCount > 0
			?_survivorsSelector.select(population, _survivorsCount, _optimize)
			: ISeq.empty();
	}

	// Selects the offspring population. A new population object is returned.
	private ISeq<Phenotype<G, C>>
	selectOffspring(final ISeq<Phenotype<G, C>> population) {
		return _offspringCount > 0
			? _offspringSelector.select(population, _offspringCount, _optimize)
			: ISeq.empty();
	}

	// Filters out invalid and old individuals. Filtering is done in place.
	private FilterResult<G, C> filter(
		final Seq<Phenotype<G, C>> population,
		final long generation
	) {
		int killCount = 0;
		int invalidCount = 0;

		final MSeq<Phenotype<G, C>> pop = MSeq.of(population);
		for (int i = 0, n = pop.size(); i < n; ++i) {
			final Phenotype<G, C> individual = pop.get(i);

			if (!_constraint.test(individual)) {
				pop.set(i, _constraint.repair(individual, generation));
				++invalidCount;
			} else if (individual.getAge(generation) > _maximalPhenotypeAge) {
				pop.set(i, Phenotype.of(_genotypeFactory.newInstance(), generation));
				++killCount;
			}
		}

		return new FilterResult<>(pop.toISeq(), killCount, invalidCount);
	}


	/* *************************************************************************
	 * Evaluation methods.
	 **************************************************************************/

	/**
	 * Evaluates the fitness function of the given population with the configured
	 * {@link Evaluator} of this engine and returns a new population
	 * with its fitness value assigned.
	 *
	 * @since 5.0
	 *
	 * @see Evaluator
	 * @see Evaluator#eval(Seq)
	 *
	 * @param population the population to evaluate
	 * @return a new population with assigned fitness values
	 * @throws IllegalStateException if the configured fitness function doesn't
	 *         return a population with the same size as the input population.
	 *         This exception is also thrown if one of the populations
	 *         phenotype has no fitness value assigned.
	 */
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
		final ISeq<Phenotype<G, C>> evaluated = _evaluator.eval(population);

		if (population.size() != evaluated.size()) {
			throw new IllegalStateException(format(
				"Expected %d individuals, but got %d. " +
					"Check your evaluator function.",
				population.size(), evaluated.size()
			));
		}
		if (!evaluated.forAll(Phenotype::isEvaluated)) {
			throw new IllegalStateException(
				"Some phenotypes have no assigned fitness value. " +
					"Check your evaluator function."
			);
		}

		return evaluated;
	}


	/* *************************************************************************
	 * Evolution Stream creation.
	 **************************************************************************/

	@Override
	public EvolutionStream<G, C>
	stream(final Supplier<EvolutionStart<G, C>> start) {
		return EvolutionStream.ofEvolution(
			() -> evolutionStart(start.get()),
			this
		);
	}

	@Override
	public EvolutionStream<G, C> stream(final EvolutionInit<G> init) {
		return stream(evolutionStart(init));
	}

	private EvolutionStart<G, C>
	evolutionStart(final EvolutionStart<G, C> start) {
		final ISeq<Phenotype<G, C>> population = start.getPopulation();
		final long gen = start.getGeneration();

		final Stream<Phenotype<G, C>> stream = Stream.concat(
			population.stream(),
			_genotypeFactory.instances()
				.map(gt -> Phenotype.of(gt, gen))
		);

		final ISeq<Phenotype<G, C>> pop = stream
			.limit(getPopulationSize())
			.collect(ISeq.toISeq());

		return EvolutionStart.of(pop, gen);
	}

	private EvolutionStart<G, C>
	evolutionStart(final EvolutionInit<G> init) {
		final ISeq<Genotype<G>> pop = init.getPopulation();
		final long gen = init.getGeneration();

		return evolutionStart(
			EvolutionStart.of(
				pop.map(gt -> Phenotype.of(gt, gen)),
				gen
			)
		);
	}

	/* *************************************************************************
	 * Property access methods.
	 **************************************************************************/

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
	 * Return the constraint of the evolution problem.
	 *
	 * @since 5.0
	 *
	 * @return the constraint of the evolution problem
	 */
	public Constraint<G, C> getConstraint() {
		return _constraint;
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
		return _executor;
	}

	/**
	 * Return the evolution result mapper.
	 *
	 * @since 4.0
	 *
	 * @return the evolution result mapper
	 */
	public UnaryOperator<EvolutionResult<G, C>> getMapper() {
		return _mapper;
	}

	/**
	 * Create a new evolution {@code Engine.Builder} initialized with the values
	 * of the current evolution {@code Engine}. With this method, the evolution
	 * engine can serve as a template for a new one.
	 *
	 * @return a new engine builder
	 */
	public Builder<G, C> builder() {
		return new Builder<>(_evaluator, _genotypeFactory)
			.alterers(_alterer)
			.clock(_clock)
			.executor(_executor)
			.maximalPhenotypeAge(_maximalPhenotypeAge)
			.offspringFraction((double)_offspringCount/(double)getPopulationSize())
			.offspringSelector(_offspringSelector)
			.optimize(_optimize)
			.constraint(_constraint)
			.populationSize(getPopulationSize())
			.survivorsSelector(_survivorsSelector)
			.mapping(_mapper);
	}


	/* *************************************************************************
	 * Static Builder methods.
	 **************************************************************************/

	/**
	 * Create a new evolution {@code Engine.Builder} with the given fitness
	 * function and genotype factory.
	 *
	 * @param ff the fitness function
	 * @param gtf the genotype factory
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 * @return a new engine builder
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}.
	 */
	public static <G extends Gene<?, G>, C extends Comparable<? super C>>
	Builder<G, C> builder(
		final Function<? super Genotype<G>, ? extends C> ff,
		final Factory<Genotype<G>> gtf
	) {
		return new Builder<>(Evaluators.concurrent(ff, commonPool()), gtf);
	}

	/**
	 * Create a new evolution {@code Engine.Builder} with the given fitness
	 * function and problem {@code codec}.
	 *
	 * @since 3.2
	 *
	 * @param ff the fitness evaluator
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
		return builder(ff, Genotype.of(chromosome, chromosomes));
	}


	/* *************************************************************************
	 * Inner classes
	 **************************************************************************/


	/**
	 * Builder class for building GA {@code Engine} instances.
	 *
	 * @see Engine
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness function result type
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @since 3.0
	 * @version 5.1
	 */
	public static final class Builder<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
		implements Copyable<Builder<G, C>>
	{

		// No default values for this properties.
		private final Evaluator<G, C> _evaluator;
		private final Factory<Genotype<G>> _genotypeFactory;

		// This are the properties which default values.
		private Selector<G, C> _survivorsSelector = new TournamentSelector<>(3);
		private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);
		private Alterer<G, C> _alterer = Alterer.of(
			new SinglePointCrossover<G, C>(0.2),
			new Mutator<>(0.15)
		);
		private Constraint<G, C> _constraint;
		private Optimize _optimize = Optimize.MAXIMUM;
		private double _offspringFraction = 0.6;
		private int _populationSize = 50;
		private long _maximalPhenotypeAge = 70;

		// Engine execution environment.
		private Executor _executor = commonPool();
		private Clock _clock = NanoClock.systemUTC();

		private UnaryOperator<EvolutionResult<G, C>> _mapper = UnaryOperator.identity();

		/**
		 * Create a new evolution {@code Engine.Builder} with the given fitness
		 * evaluator and genotype factory. This is the most general way for
		 * creating an engine builder.
		 *
		 * @since 5.0
		 *
		 * @see Engine#builder(Function, Codec)
		 * @see Engine#builder(Function, Factory)
		 * @see Engine#builder(Problem)
		 * @see Engine#builder(Function, Chromosome, Chromosome[])
		 *
		 * @param evaluator the fitness evaluator
		 * @param genotypeFactory the genotype factory
		 * @throws NullPointerException if one of the arguments is {@code null}.
		 */
		public Builder(
			final Evaluator<G, C> evaluator,
			final Factory<Genotype<G>> genotypeFactory
		) {
			_genotypeFactory = requireNonNull(genotypeFactory);
			_evaluator = requireNonNull(evaluator);
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

			_alterer = rest.length == 0
				? first
				: Alterer.of(rest).compose(first);

			return this;
		}

		/**
		 * The phenotype constraint is used for detecting invalid individuals
		 * and repairing them.
		 *
		 * <p><i>Default implementation uses {@code Phenotype::isValid} for
		 * validating the phenotype.</i></p>
		 *
		 * @since 5.0
		 *
		 * @param constraint phenotype constraint which can override the default
		 *        implementation the {@link Phenotype#isValid()} method and repairs
		 *        invalid phenotypes when needed.
		 * @return {@code this} builder, for command chaining
		 */
		public Builder<G, C> constraint(final Constraint<G, C> constraint) {
			_constraint = constraint;
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
		 * This method call is equivalent to
		 * {@code survivorsFraction(1 - offspringFraction)} and will override
		 * any previously set survivors-fraction.
		 *
		 * @see #survivorsFraction(double)
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
		 * The survivors fraction. <i>Default values is set to {@code 0.4}.</i>
		 * This method call is equivalent to
		 * {@code offspringFraction(1 - survivorsFraction)} and will override
		 * any previously set offspring-fraction.
		 *
		 * @since 3.8
		 *
		 * @see #offspringFraction(double)
		 *
		 * @param fraction the survivors fraction
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the fraction is not
		 *         within the range [0, 1].
		 */
		public Builder<G, C> survivorsFraction(final double fraction) {
			_offspringFraction = 1.0 - probability(fraction);
			return this;
		}

		/**
		 * The number of offspring individuals.
		 *
		 * @since 3.8
		 *
		 * @param size the number of offspring individuals.
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the size is not
		 *         within the range [0, population-size].
		 */
		public Builder<G, C> offspringSize(final int size) {
			if (size < 0) {
				throw new IllegalArgumentException(format(
					"Offspring size must be greater or equal zero, but was %s.",
					size
				));
			}

			return offspringFraction((double)size/(double)_populationSize);
		}

		/**
		 * The number of survivors.
		 *
		 * @since 3.8
		 *
		 * @param size the number of survivors.
		 * @return {@code this} builder, for command chaining
		 * @throws java.lang.IllegalArgumentException if the size is not
		 *         within the range [0, population-size].
		 */
		public Builder<G, C> survivorsSize(final int size) {
			if (size < 0) {
				throw new IllegalArgumentException(format(
					"Survivors must be greater or equal zero, but was %s.",
					size
				));
			}

			return survivorsFraction((double)size/(double)_populationSize);
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
					"Population size must be greater than zero, but was %s.",
					size
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
		 * The result mapper, which allows to change the evolution result after
		 * each generation.
		 *
		 * @since 4.0
		 * @see EvolutionResult#toUniquePopulation()
		 *
		 * @param mapper the evolution result mapper
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if the given {@code resultMapper} is
		 *         {@code null}
		 */
		public Builder<G, C> mapping(
			final Function<
				? super EvolutionResult<G, C>,
				EvolutionResult<G, C>
			> mapper
		) {
			_mapper = requireNonNull(mapper::apply);
			return this;
		}

		/**
		 * Builds an new {@code Engine} instance from the set properties.
		 *
		 * @return an new {@code Engine} instance from the set properties
		 */
		public Engine<G, C> build() {
			return new Engine<>(
				_evaluator instanceof ConcurrentEvaluator
					? ((ConcurrentEvaluator<G, C>)_evaluator).with(_executor)
					: _evaluator,
				_genotypeFactory,
				_survivorsSelector,
				_offspringSelector,
				_alterer,
				_constraint == null
					? RetryConstraint.of(_genotypeFactory)
					: _constraint,
				_optimize,
				getOffspringCount(),
				getSurvivorsCount(),
				_maximalPhenotypeAge,
				_executor,
				_clock,
				_mapper
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
		 * Return the constraint of the evolution problem.
		 *
		 * @since 5.0
		 *
		 * @return the constraint of the evolution problem
		 */
		public Constraint<G, C> getConstraint() {
			return _constraint;
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
		 * Return the evolution result mapper.
		 *
		 * @since 4.0
		 *
		 * @return the evolution result mapper
		 */
		public UnaryOperator<EvolutionResult<G, C>> getMapper() {
			return _mapper;
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
			return new Builder<G, C>(_evaluator, _genotypeFactory)
				.alterers(_alterer)
				.clock(_clock)
				.executor(_executor)
				.maximalPhenotypeAge(_maximalPhenotypeAge)
				.offspringFraction(_offspringFraction)
				.offspringSelector(_offspringSelector)
				.constraint(_constraint)
				.optimize(_optimize)
				.populationSize(_populationSize)
				.survivorsSelector(_survivorsSelector)
				.mapping(_mapper);
		}

	}

}
