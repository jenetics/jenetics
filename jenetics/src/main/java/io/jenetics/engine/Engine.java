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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.ForkJoinPool.commonPool;

import java.time.Clock;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.Alterer;
import io.jenetics.AltererResult;
import io.jenetics.Chromosome;
import io.jenetics.Gene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.Selector;
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
 *        final double x = gt.gene().doubleValue();
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
 * @see Constraint
 *
 * @param <G> the gene type
 * @param <C> the fitness result type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 6.0
 */
public final class Engine<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements
		Evolution<G, C>,
		EvolutionStreamable<G, C>,
		Evaluator<G, C>
{

	// Problem definition.
	private final Evaluator<G, C> _evaluator;
	private final Factory<Genotype<G>> _genotypeFactory;
	private final Constraint<G, C> _constraint;
	private final Optimize _optimize;

	// Evolution parameters.
	private final EvolutionParams<G, C> _evolutionParams;

	// Execution context for concurrent execution of evolving steps.
	private final Executor _executor;
	private final Clock _clock;
	private final EvolutionInterceptor<G, C> _interceptor;


	/**
	 * Create a new GA engine with the given parameters.
	 *
	 * @param evaluator the population fitness evaluator
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param constraint phenotype constraint which can override the default
	 *        implementation the {@link Phenotype#isValid()} method and repairs
	 *        invalid phenotypes when needed.
	 * @param optimize the kind of optimization (minimize or maximize)
	 * @param evolutionParams the evolution parameters, which influences the
	 *        evolution process
	 * @param executor the executor used for executing the single evolve steps
	 * @param clock the clock used for calculating the timing results
	 * @param interceptor the evolution interceptor, which gives additional
	 *        possibilities to influence the actual evolution
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IllegalArgumentException if the given integer values are smaller
	 *         than one.
	 */
	Engine(
		final Evaluator<G, C> evaluator,
		final Factory<Genotype<G>> genotypeFactory,
		final Constraint<G, C> constraint,
		final Optimize optimize,
		final EvolutionParams<G, C> evolutionParams,
		final Executor executor,
		final Clock clock,
		final EvolutionInterceptor<G, C> interceptor
	) {
		_evaluator = requireNonNull(evaluator);
		_genotypeFactory = requireNonNull(genotypeFactory);
		_constraint = requireNonNull(constraint);
		_optimize = requireNonNull(optimize);
		_evolutionParams = requireNonNull(evolutionParams);
		_executor = requireNonNull(executor);
		_clock = requireNonNull(clock);
		_interceptor = requireNonNull(interceptor);
	}

	@Override
	public EvolutionResult<G, C> evolve(final EvolutionStart<G, C> start) {
		final EvolutionTiming timing = new EvolutionTiming(_clock);
		timing.evolve.start();

		final EvolutionStart<G, C> interceptedStart = _interceptor.before(start);

		// Create initial population if `start` is empty.
		final EvolutionStart<G, C> es = interceptedStart.population().isEmpty()
			? evolutionStart(interceptedStart)
			: interceptedStart;

		// Initial evaluation of the population.
		final ISeq<Phenotype<G, C>> population = es.isDirty()
			? timing.evaluation.timing(() -> eval(es.population()))
			: es.population();

		// Select the offspring population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> offspring =
			supplyAsync(() ->
				timing.offspringSelection.timing(() ->
					selectOffspring(population)
				),
				_executor
			);

		// Select the survivor population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> survivors =
			supplyAsync(() ->
				timing.survivorsSelection.timing(() ->
					selectSurvivors(population)
				),
				_executor
			);

		// Altering the offspring population.
		final CompletableFuture<AltererResult<G, C>> alteredOffspring =
			offspring.thenApplyAsync(off ->
				timing.offspringAlter.timing(() ->
					_evolutionParams.alterer().alter(off, es.generation())
				),
				_executor
			);

		// Filter and replace invalid and old survivor individuals.
		final CompletableFuture<FilterResult<G, C>> filteredSurvivors =
			survivors.thenApplyAsync(sur ->
				timing.survivorFilter.timing(() ->
					filter(sur, es.generation())
				),
				_executor
			);

		// Filter and replace invalid and old offspring individuals.
		final CompletableFuture<FilterResult<G, C>> filteredOffspring =
			alteredOffspring.thenApplyAsync(off ->
				timing.offspringFilter.timing(() ->
					filter(off.population(), es.generation())
				),
				_executor
			);

		// Combining survivors and offspring to the new population.
		final CompletableFuture<ISeq<Phenotype<G, C>>> nextPopulation =
			filteredSurvivors.thenCombineAsync(
				filteredOffspring,
				(s, o) -> ISeq.of(s.population.append(o.population)),
				_executor
			);

		// Evaluate the fitness-function and wait for result.
		final ISeq<Phenotype<G, C>> pop = nextPopulation.join();
		final ISeq<Phenotype<G, C>> result = timing.evaluation.timing(() ->
			eval(pop)
		);

		final int killCount =
			filteredOffspring.join().killCount +
			filteredSurvivors.join().killCount;

		final int invalidCount =
			filteredOffspring.join().invalidCount +
			filteredSurvivors.join().invalidCount;

		final int alterationCount = alteredOffspring.join().alterations();

		EvolutionResult<G, C> er = EvolutionResult.of(
			_optimize,
			result,
			es.generation(),
			timing.toDurations(),
			killCount,
			invalidCount,
			alterationCount
		);

		final EvolutionResult<G, C> interceptedResult = _interceptor.after(er);
		if (er != interceptedResult) {
			er = interceptedResult.withPopulation(
				timing.evaluation.timing(() ->
					eval(interceptedResult.population())
			));
		}

		timing.evolve.stop();

		return er
			.withDurations(timing.toDurations())
			.clean();
	}

	// Selects the survivors population. A new population object is returned.
	private ISeq<Phenotype<G, C>>
	selectSurvivors(final ISeq<Phenotype<G, C>> population) {
		return _evolutionParams.survivorsSize() > 0
			? _evolutionParams.survivorsSelector()
				.select(population, _evolutionParams.survivorsSize(), _optimize)
			: ISeq.empty();
	}

	// Selects the offspring population. A new population object is returned.
	private ISeq<Phenotype<G, C>>
	selectOffspring(final ISeq<Phenotype<G, C>> population) {
		return _evolutionParams.offspringSize() > 0
			? _evolutionParams.offspringSelector()
				.select(population, _evolutionParams.offspringSize(), _optimize)
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
			} else if (individual.age(generation) >
						_evolutionParams.maximalPhenotypeAge())
			{
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
	@Override
	public ISeq<Phenotype<G, C>> eval(final Seq<Phenotype<G, C>> population) {
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
		final ISeq<Phenotype<G, C>> population = start.population();
		final long gen = start.generation();

		final Stream<Phenotype<G, C>> stream = Stream.concat(
			population.stream(),
			_genotypeFactory.instances()
				.map(gt -> Phenotype.of(gt, gen))
		);

		final ISeq<Phenotype<G, C>> pop = stream
			.limit(populationSize())
			.collect(ISeq.toISeq());

		return EvolutionStart.of(pop, gen);
	}

	private EvolutionStart<G, C>
	evolutionStart(final EvolutionInit<G> init) {
		final ISeq<Genotype<G>> pop = init.population();
		final long gen = init.generation();

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
	public Factory<Genotype<G>> genotypeFactory() {
		return _genotypeFactory;
	}

	/**
	 * Return the constraint of the evolution problem.
	 *
	 * @since 5.0
	 *
	 * @return the constraint of the evolution problem
	 */
	public Constraint<G, C> constraint() {
		return _constraint;
	}

	/**
	 * Return the used survivor {@link Selector} of the GA.
	 *
	 * @return the used survivor {@link Selector} of the GA.
	 */
	public Selector<G, C> survivorsSelector() {
		return _evolutionParams.survivorsSelector();
	}

	/**
	 * Return the used offspring {@link Selector} of the GA.
	 *
	 * @return the used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> offspringSelector() {
		return _evolutionParams.offspringSelector();
	}

	/**
	 * Return the used {@link Alterer} of the GA.
	 *
	 * @return the used {@link Alterer} of the GA.
	 */
	public Alterer<G, C> alterer() {
		return _evolutionParams.alterer();
	}

	/**
	 * Return the number of selected offspring.
	 *
	 * @return the number of selected offspring
	 */
	public int offspringSize() {
		return _evolutionParams.offspringSize();
	}

	/**
	 * The number of selected survivors.
	 *
	 * @return the number of selected survivors
	 */
	public int survivorsSize() {
		return _evolutionParams.survivorsSize();
	}

	/**
	 * Return the number of individuals of a population.
	 *
	 * @return the number of individuals of a population
	 */
	public int populationSize() {
		return _evolutionParams.populationSize();
	}

	/**
	 * Return the maximal allowed phenotype age.
	 *
	 * @return the maximal allowed phenotype age
	 */
	public long maximalPhenotypeAge() {
		return _evolutionParams.maximalPhenotypeAge();
	}

	/**
	 * Return the optimization strategy.
	 *
	 * @return the optimization strategy
	 */
	public Optimize optimize() {
		return _optimize;
	}

	/**
	 * Return the {@link Clock} the engine is using for measuring the execution
	 * time.
	 *
	 * @return the clock used for measuring the execution time
	 */
	public Clock clock() {
		return _clock;
	}

	/**
	 * Return the {@link Executor} the engine is using for executing the
	 * evolution steps.
	 *
	 * @return the executor used for performing the evolution steps
	 */
	public Executor executor() {
		return _executor;
	}

	/**
	 * Return the evolution interceptor.
	 *
	 * @since 6.0
	 *
	 * @return the evolution result mapper
	 */
	public EvolutionInterceptor<G, C> interceptor() {
		return _interceptor;
	}

	/**
	 * Create a new evolution {@code Engine.Builder} initialized with the values
	 * of the current evolution {@code Engine}. With this method, the evolution
	 * engine can serve as a template for a new one.
	 *
	 * @return a new engine builder
	 */
	public Builder<G, C> toBuilder() {
		return new Builder<>(_evaluator, _genotypeFactory)
			.clock(_clock)
			.executor(_executor)
			.optimize(_optimize)
			.constraint(_constraint)
			.evolutionParams(_evolutionParams)
			.interceptor(_interceptor);
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
		final var builder = builder(problem.fitness(), problem.codec());
		problem.constraint().ifPresent(builder::constraint);
		return builder;
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
	 * Engine builder
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
	 * @version 6.0
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
		private Constraint<G, C> _constraint;
		private Optimize _optimize = Optimize.MAXIMUM;

		// Evolution parameters.
		private final EvolutionParams.Builder<G, C> _evolutionParams =
			EvolutionParams.builder();


		// Engine execution environment.
		private Executor _executor = commonPool();
		private Clock _clock = NanoClock.systemUTC();

		private EvolutionInterceptor<G, C> _interceptor =
			EvolutionInterceptor.identity();

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
		 * Applies the given {@code setup} recipe to {@code this} engine builder.
		 *
		 * @since 6.0
		 *
		 * @param setup the setup recipe applying to {@code this} builder
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if the {@code setup} is {@code null}.
		 */
		public Builder<G, C> setup(final Setup<G, C> setup) {
			setup.apply(this);
			return this;
		}

		/**
		 * Set the evolution parameters used by the engine.
		 *
		 * @since 5.2
		 *
		 * @param params the evolution parameter
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if the {@code params} is {@code null}.
		 */
		public Builder<G, C> evolutionParams(final EvolutionParams<G, C> params) {
			_evolutionParams.evolutionParams(params);
			return this;
		}

		/**
		 * The selector used for selecting the offspring population. <i>Default
		 * values is set to {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting the offspring population
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if one of the {@code selector} is
		 *         {@code null}.
		 */
		public Builder<G, C> offspringSelector(final Selector<G, C> selector) {
			_evolutionParams.offspringSelector(selector);
			return this;
		}

		/**
		 * The selector used for selecting the survivors population. <i>Default
		 * values is set to {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting survivors population
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if one of the {@code selector} is
		 *         {@code null}.
		 */
		public Builder<G, C> survivorsSelector(final Selector<G, C> selector) {
			_evolutionParams.survivorsSelector(selector);
			return this;
		}

		/**
		 * The selector used for selecting the survivors and offspring
		 * population. <i>Default values is set to
		 * {@code TournamentSelector<>(3)}.</i>
		 *
		 * @param selector used for selecting survivors and offspring population
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if one of the {@code selector} is
		 *         {@code null}.
		 */
		public Builder<G, C> selector(final Selector<G, C> selector) {
			_evolutionParams.selector(selector);
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
		 * @throws NullPointerException if one of the alterers is {@code null}.
		 */
		@SafeVarargs
		public final Builder<G, C> alterers(
			final Alterer<G, C> first,
			final Alterer<G, C>... rest
		) {
			_evolutionParams.alterers(first, rest);
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
		 * @throws NullPointerException if one of the {@code constraint} is
		 *         {@code null}.
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
		 * @throws NullPointerException if one of the {@code optimize} is
		 *         {@code null}.
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
			_evolutionParams.offspringFraction(fraction);
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
			return offspringFraction(1 - fraction);
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

			return offspringFraction(size/(double)_evolutionParams.populationSize());
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

			return survivorsFraction(size/(double)_evolutionParams.populationSize());
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
			_evolutionParams.populationSize(size);
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
			_evolutionParams.maximalPhenotypeAge(age);
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
		 * The evolution interceptor, which allows to change the evolution start
		 * and result.
		 *
		 * @since 6.0
		 * @see EvolutionResult#toUniquePopulation()
		 *
		 * @param interceptor the evolution interceptor
		 * @return {@code this} builder, for command chaining
		 * @throws NullPointerException if the given {@code interceptor} is
		 *         {@code null}
		 */
		public Builder<G, C>
		interceptor(final EvolutionInterceptor<G, C> interceptor) {
			_interceptor = requireNonNull(interceptor);
			return this;
		}

		/**
		 * Builds an new {@code Engine} instance from the set properties.
		 *
		 * @return an new {@code Engine} instance from the set properties
		 */
		public Engine<G, C> build() {
			return new Engine<>(
				__evaluator(),
				_genotypeFactory,
				__constraint(),
				_optimize,
				_evolutionParams.build(),
				_executor,
				_clock,
				_interceptor
			);
		}

		private Evaluator<G, C> __evaluator() {
			return _evaluator instanceof ConcurrentEvaluator
				? ((ConcurrentEvaluator<G, C>)_evaluator).with(_executor)
				: _evaluator;
		}

		private Constraint<G, C> __constraint() {
			return _constraint == null
				? RetryConstraint.of(_genotypeFactory)
				: _constraint;
		}

		/* *********************************************************************
		 * Current properties
		 ***********************************************************************/

		/**
		 * Return the used {@link Alterer} of the GA.
		 *
		 * @return the used {@link Alterer} of the GA.
		 */
		public Alterer<G, C> alterer() {
			return _evolutionParams.alterer();
		}

		/**
		 * Return the {@link Clock} the engine is using for measuring the execution
		 * time.
		 *
		 * @since 3.1
		 *
		 * @return the clock used for measuring the execution time
		 */
		public Clock clock() {
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
		public Executor executor() {
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
		public Factory<Genotype<G>> genotypeFactory() {
			return _genotypeFactory;
		}

		/**
		 * Return the constraint of the evolution problem.
		 *
		 * @since 5.0
		 *
		 * @return the constraint of the evolution problem
		 */
		public Constraint<G, C> constraint() {
			return _constraint;
		}

		/**
		 * Return the currently set evolution parameters.
		 *
		 * @since 5.2
		 *
		 * @return the currently set evolution parameters
		 */
		public EvolutionParams<G, C> evolutionParams() {
			return _evolutionParams.build();
		}

		/**
		 * Return the maximal allowed phenotype age.
		 *
		 * @since 3.1
		 *
		 * @return the maximal allowed phenotype age
		 */
		public long maximalPhenotypeAge() {
			return _evolutionParams.maximalPhenotypeAge();
		}

		/**
		 * Return the offspring fraction.
		 *
		 * @return the offspring fraction.
		 */
		public double offspringFraction() {
			return _evolutionParams.offspringFraction();
		}

		/**
		 * Return the used offspring {@link Selector} of the GA.
		 *
		 * @since 3.1
		 *
		 * @return the used offspring {@link Selector} of the GA.
		 */
		public Selector<G, C> offspringSelector() {
			return _evolutionParams.offspringSelector();
		}

		/**
		 * Return the used survivor {@link Selector} of the GA.
		 *
		 * @since 3.1
		 *
		 * @return the used survivor {@link Selector} of the GA.
		 */
		public Selector<G, C> survivorsSelector() {
			return _evolutionParams.survivorsSelector();
		}

		/**
		 * Return the optimization strategy.
		 *
		 * @since 3.1
		 *
		 * @return the optimization strategy
		 */
		public Optimize optimize() {
			return _optimize;
		}

		/**
		 * Return the number of individuals of a population.
		 *
		 * @since 3.1
		 *
		 * @return the number of individuals of a population
		 */
		public int populationSize() {
			return _evolutionParams.populationSize();
		}

		/**
		 * Return the evolution interceptor.
		 *
		 * @since 6.0
		 *
		 * @return the evolution interceptor
		 */
		public EvolutionInterceptor<G, C> interceptor() {
			return _interceptor;
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
			return new Builder<>(_evaluator, _genotypeFactory)
				.clock(_clock)
				.executor(_executor)
				.constraint(_constraint)
				.optimize(_optimize)
				.evolutionParams(_evolutionParams.build())
				.interceptor(_interceptor);
		}

	}


	/* *************************************************************************
	 * Engine setup
	 **************************************************************************/


	/**
	 * This interface represents a recipe for configuring (setup) a given
	 * {@link Builder}. It is mainly used for grouping mutually dependent
	 * engine configurations. The following code snippet shows a possible usage
	 * example.
	 *
	 * <pre>{@code
	 * final Engine<CharacterGene, Integer> engine = Engine.builder(problem)
	 *     .setup(new WeaselProgram<>())
	 *     .build();
	 * }</pre>
	 *
	 * @see Builder#setup(Setup)
	 *
	 * @param <G> the gene type
	 * @param <C> the fitness result type
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 6.0
	 * @since 6.0
	 */
	@FunctionalInterface
	public interface Setup<
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	> {

		/**
		 * Applies {@code this} setup to the given engine {@code builder}.
		 *
		 * @param builder the engine builder to setup (configure)
		 */
		void apply(final Builder<G, C> builder);

	}
}



