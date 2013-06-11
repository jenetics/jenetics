/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static java.lang.Math.round;
import static org.jenetics.util.arrays.forEach;
import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.checkProbability;
import static org.jenetics.util.object.nonNull;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.Concurrency;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.Timer;
import org.jenetics.util.functions;


/**
 * <h3>Getting started</h3>
 *
 * The minimum GA setup needs a genotype factory, {@code Factory<Genotype<?>>},
 * and a fitness {@link Function}. The {@link Genotype} implements the
 * {@link Factory} interface and can therefore be used as prototype for creating
 * the initial Population and for creating new random Genotypes.
 *
 * [code]
 * public static void main(final String[] args) {
 *     final Factory<Genotype<BitGene>> gtf = Genotype.valueOf(
 *         BitChromosome.valueOf(10, 0.5)
 *     );
 *     final Function<Genotype<BitGene> Float64> ff = ...
 *     final GeneticAlgorithm<BitGene, Float64>
 *     ga = new GeneticAlgorithm<>(gtf, ff, Optimize.MAXIMUM)
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * The genotype factory, {@code gtf}, in the example above will create genotypes
 * which consists of one {@link BitChromosome} with length 10. The one to zero
 * probability of the newly created genotypes is set to 0.5. The fitness function
 * is parameterized with a {@link BitGene} and a {@link Float64}. That means
 * that the fitness function is calculating the fitness value as {@link Float64}.
 * The return type of the fitness function must be at least a {@link Comparable}.
 * The {@code GeneticAlgorithm} object is then created with the genotype factory
 * and the fitness function. In this example the GA tries to maximize the fitness
 * function. If you want to find the minimal value you have to change the optimize
 * parameter from {@code Optimize.MAXIMUM} to {@code Optimize.MINIMUM}. The
 * {@code ga.setup()} call creates the initial population and calculates its
 * fitness value. Then the GA evolves 100 generations ({@code ga.evolve(100)})
 * an prints the best phenotype found so far onto the console.
 * <p/>
 * In a more advanced setup you may want to change the default mutation and/or
 * selection strategies.
 *
 * [code]
 * public static void main(final String[] args) {
 *     ...
 *     ga.setSelectors(new RouletteWheelSelector<BitGene>());
 *     ga.setAlterers(
 *         new SinglePointCrossover<BitGene>(0.1),
 *         new Mutator<BitGene>(0.01)
 *     );
 *
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getBestPhenotype());
 * }
 * [/code]
 *
 * The selection strategy for offspring and survivors are set to the
 * roulette-wheel selector. It is also possible to set the selector for
 * offspring and survivors independently with the {@code setOffspringSelector}
 * and {@code setSurvivorSelector} methods. The alterers are concatenated, at
 * first the crossover (with probability 0.1) is performed and then the
 * chromosomes are mutated (with probability 0.01).
 * <p/>
 *
 * <h3>Serialization</h3>
 *
 * With the serialization mechanism you can write a population to disk and load
 * it into an GA at a later time. It can also be used to transfer populations to
 * GAs, running on different hosts, over a network link. The IO class, located
 * in the {@code org.jenetics.util} package, supports native Java serialization
 * and XML serialization. For XML marshaling <em>Jenetics</em> internally uses
 * the XML support from the Javolution project.
 *
 * [code]
 * // Writing the population to disk.
 * final File file = new File("population.xml");
 * IO.xml.write(ga.getPopulation(), file);
 *
 * // Reading the population from disk.
 * Population<Float64Gene,Float64> population =
 *     (Population<Float64Gene, Float64)IO.xml.read(file);
 * ga.setPopulation(population);
 * [/code]
 *
 * @see <a href="http://en.wikipedia.org/wiki/Genetic_algorithm">
 *          Wikipedia: Genetic algorithm
 *      </a>
 * @see Alterer
 * @see Selector
 *
 * @param <G> The gene type this GA evaluates,
 * @param <C> The result type (of the fitness function).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class GeneticAlgorithm<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	/**
	 * The default population size used by this GA.
	 */
	public static final int DEFAULT_POPULATION_SIZE = 50;

	/**
	 * The default maximal phenotype age of this GA:
	 */
	public static final int DEFAULT_MAXIMAL_PHENOTYPE_AGE = 70;

	/**
	 * The default offspring fraction used by this GA.
	 */
	public static final double DEFAULT_OFFSPRING_FRACTION = 0.6;


	private final Lock _lock = new ReentrantLock(true);

	private final Optimize _optimization;

	private final Factory<Genotype<G>> _genotypeFactory;
	private final Factory<Phenotype<G, C>> _phenotypeFactory;
	private final Function<Genotype<G>, C> _fitnessFunction;
	private Function<C, C> _fitnessScaler;

	private double _offspringFraction = DEFAULT_OFFSPRING_FRACTION;

	// Alterers
	private Alterer<G> _alterer = new CompositeAlterer<>(
		new SinglePointCrossover<G>(0.1),
		new Mutator<G>(0.05)
	);

	// Selectors
	private Selector<G, C> _survivorSelector = new TournamentSelector<>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<>(3);

	// Population
	private int _populationSize = DEFAULT_POPULATION_SIZE;
	private Population<G, C> _population = new Population<>(_populationSize);
	private int _maximalPhenotypeAge = DEFAULT_MAXIMAL_PHENOTYPE_AGE;
	private volatile int _generation = 0;

	// Statistics
	private Statistics.Calculator<G, C> _calculator = new Statistics.Calculator<>();
	private Statistics<G, C> _bestStatistics = null;
	private Statistics<G, C> _statistics = null;
	private final AtomicInteger _killed = new AtomicInteger(0);
	private final AtomicInteger _invalid = new AtomicInteger(0);

	//Some performance measure.
	private final Timer _executionTimer = new Timer("Execution time");
	private final Timer _selectTimer = new Timer("Select time");
	private final Timer _alterTimer = new Timer("Alter time");
	private final Timer _combineTimer = new Timer("Combine survivors and offsprings time");
	private final Timer _statisticTimer = new Timer("Statistic time");
	private final Timer _evaluateTimer = new Timer("Evaluate time");


	/**
	 * Create a new genetic algorithm.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 *        fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<Genotype<G>, C> fitnessFunction,
		final Function<C, C> fitnessScaler,
		final Optimize optimization
	) {
		_genotypeFactory = nonNull(genotypeFactory, "GenotypeFactory");
		_fitnessFunction = nonNull(fitnessFunction, "FitnessFunction");
		_fitnessScaler = nonNull(fitnessScaler, "FitnessScaler");
		_optimization = nonNull(optimization, "Optimization");

		_phenotypeFactory = new Factory<Phenotype<G, C>>() {
			@Override public Phenotype<G, C> newInstance() {
				return Phenotype.valueOf(
					_genotypeFactory.newInstance(),
					_fitnessFunction,
					_fitnessScaler,
					_generation
				);
			}
		};
	}

	/**
	 * Create a new genetic algorithm. By default the GA tries to maximize the
	 * fitness function.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<Genotype<G>, C> fitnessFunction
	) {
		this(
			genotypeFactory,
			fitnessFunction,
			functions.<C>Identity(),
			Optimize.MAXIMUM
		);
	}

	/**
	 * Create a new genetic algorithm.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 *        fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<Genotype<G>, C> fitnessFunction,
		final Optimize optimization
	) {
		this(
			genotypeFactory,
			fitnessFunction,
			functions.<C>Identity(),
			optimization
		);
	}

	/**
	 * Create a new genetic algorithm. By default the GA tries to maximize the
	 * fitness function.
	 *
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory,
		final Function<Genotype<G>, C> fitnessFunction,
		final Function<C, C> fitnessScaler
	) {
		this(
			genotypeFactory,
			fitnessFunction,
			fitnessScaler,
			Optimize.MAXIMUM
		);
	}

	/**
	 * Create the initial population of the GA. Subsequent calls to this
	 * method throw IllegalStateException. If no initial population has been
	 * set (with {@link #setPopulation(Collection)} or
	 * {@link #setGenotypes(Collection)}) a random population is generated.
	 *
	 * @throws IllegalStateException if called more than once.
	 */
	public void setup() {
		_lock.lock();
		try {
			prepareSetup();
			_population.fill(_phenotypeFactory, _populationSize - _population.size());
			finishSetup();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * Setting up the <code>GeneticAlgorithm</code> with the given initial
	 * population. Subsequent calls to this method throw an IllegalStateException.
	 * This method is similar to the {@link #setGenotypes(Collection)} and
	 * {@link #setPopulation(Collection)} methods, but this method is required
	 * to be called only once and before starting evaluation. It also calculates
	 * the timing statistics when (calculating the fitness values for the given
	 * genotypes.
	 *
	 * @see #setGenotypes(Collection)
	 * @see #setPopulation(Collection)
	 * @param genotypes the initial population.
	 * @throws IllegalStateException if called more than once.
	 */
	public void setup(final Collection<Genotype<G>> genotypes) {
		_lock.lock();
		try {
			prepareSetup();
			setGenotypes(genotypes);
			finishSetup();
		} finally {
			_lock.unlock();
		}
	}

	private void prepareSetup() {
		if (_generation > 0) {
			throw new IllegalStateException(
				"The method GeneticAlgorithm.setup() must be called only once."
			);
		}

		++_generation;
		_executionTimer.start();
	}

	private void finishSetup() {
		//Evaluate the fitness.
		evaluate();

		//First valuation of the initial population.
		_statisticTimer.start();
		_statistics = _calculator.evaluate(
			_population, _generation, _optimization
		).build();

		_bestStatistics = _statistics;
		_statisticTimer.stop();

		_executionTimer.stop();

		setTimes(_statistics);
	}

	/**
	 * Evolve one generation.
	 *
	 * @throws IllegalStateException if the {@link GeneticAlgorithm#setup()}
	 *         method was not called first.
	 */
	public void evolve() {
		_lock.lock();
		try {
			// Check the setup state.
			if (_generation == 0) {
				throw new IllegalStateException(
					"Call the GeneticAlgorithm.setup() method before " +
					"calling GeneticAlgorithm.evolve()."
				);
			}

			//Start the overall execution timer.s
			_executionTimer.start();

			//Increment the generation and the generation.
			++_generation;

			//Select the survivors and the offsprings.
			_selectTimer.start();
			final Array<Population<G, C>> selection = select();
			final Population<G, C> survivors = selection.get(0);
			final Population<G, C> offsprings = selection.get(1);
			_selectTimer.stop();

			//Alter the offsprings (Recombination, Mutation ...).
			_alterTimer.start();
			_alterer.alter(offsprings, _generation);
			_alterTimer.stop();

			// Combining the new population (containing the survivors and the
			// altered offsprings).
			_combineTimer.start();
			final int killed = _killed.get();
			final int invalid = _invalid.get();
			_population = combine(survivors, offsprings);
			_combineTimer.stop();

			//Evaluate the fitness
			evaluate();

			//Evaluate the statistic
			_statisticTimer.start();
			final Statistics.Builder<G, C> builder = _calculator.evaluate(
					_population, _generation, _optimization
				);
			builder.killed(_killed.get() - killed);
			builder.invalid(_invalid.get() - invalid);
			_statistics = builder.build();

			final int comp = _optimization.compare(
				_statistics.getBestPhenotype(),
				_bestStatistics.getBestPhenotype()
			);

			if (comp > 0) {
				_bestStatistics = _statistics;
			}

			_statisticTimer.stop();

			_executionTimer.stop();

			setTimes(_statistics);
		} finally {
			_lock.unlock();
		}
	}

	private void setTimes(final Statistics<?, ?> statistic) {
		statistic.getTime().execution.set(_executionTimer.getInterimTime());
		statistic.getTime().selection.set(_selectTimer.getInterimTime());
		statistic.getTime().alter.set(_alterTimer.getInterimTime());
		statistic.getTime().combine.set(_combineTimer.getInterimTime());
		statistic.getTime().evaluation.set(_evaluateTimer.getInterimTime());
		statistic.getTime().statistics.set(_statisticTimer.getInterimTime());
	}

	private void evaluate() {
		_evaluateTimer.start();
		try (Concurrency c = Concurrency.start()) {
			for (int i =  _population.size(); --i >= 0;) {
				c.execute(_population.get(i));
			}
		}
		_evaluateTimer.stop();
	}

	/**
	 * Evolve the given number of {@code generations}
	 *
	 * @param generations the number of {@code generations} to evolve.
	 */
	public void evolve(final int generations) {
		for (int i = 0; i < generations; ++i) {
			evolve();
		}
	}

	/**
	 * Evolve the GA as long the given {@link Function} returns {@code true}.
	 *
	 * @see termination
	 *
	 * @param until the predicate which defines the termination condition.
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public void evolve(final Function<? super Statistics<G, C>, Boolean> until) {
		nonNull(until, "Termination condition");
		while (until.apply(getStatistics())) {
			evolve();
		}
	}

	private Array<Population<G, C>> select() {
		final Array<Population<G, C>> selection = new Array<>(2);
		final int numberOfSurvivors = getNumberOfSurvivors();
		final int numberOfOffspring = getNumberOfOffsprings();
		assert (numberOfSurvivors + numberOfOffspring == _populationSize);

		try (Concurrency c = Concurrency.start()) {
			c.execute(new Runnable() { @Override public void run() {
				final Population<G, C> survivors = _survivorSelector.select(
					_population, numberOfSurvivors, _optimization
				);

				assert (survivors.size() == numberOfSurvivors);
				selection.set(0, survivors);
			}});

			final Population<G, C> offsprings = _offspringSelector.select(
				_population, numberOfOffspring, _optimization
			);

			assert (offsprings.size() == numberOfOffspring);
			selection.set(1, offsprings);
		}

		return selection;
	}

	private Population<G, C> combine(
		final Population<G, C> survivors,
		final Population<G, C> offsprings
	) {
		assert (survivors.size() + offsprings.size() == _populationSize);
		final Population<G, C> population = new Population<>(_populationSize);

		try (final Concurrency concurrency = Concurrency.start()) {
			// Kill survivors which are to old and replace it with new one.
			concurrency.execute(new Runnable() { @Override public void run() {
				for (int i = 0, n = survivors.size(); i < n; ++i) {
					final Phenotype<G, C> survivor = survivors.get(i);

					final boolean isTooOld =
						survivor.getAge(_generation) > _maximalPhenotypeAge;

					final boolean isInvalid = isTooOld || !survivor.isValid();

					// Sorry, too old or not valid.
					if (isInvalid) {
						survivors.set(i, _phenotypeFactory.newInstance());
					}

					if (isTooOld) {
						_killed.incrementAndGet();
					} else if (isInvalid) {
						_invalid.incrementAndGet();
					}
				}
			}});

			// In the mean time we can add the offsprings.
			population.addAll(offsprings);
		}

		population.addAll(survivors);

		return population;
	}

	private int getNumberOfSurvivors() {
		return _populationSize - getNumberOfOffsprings();
	}

	private int getNumberOfOffsprings() {
		return (int)round(
			_offspringFraction*_populationSize
		);
	}

	/**
	 * Return {@code true} if the {@link #setup()} method has already been called,
	 * {@code false} otherwise.
	 *
	 * @return {@code true} if the {@link #setup()} method has already been called,
	 *         {@code false} otherwise.
	 */
	public boolean isInitialized() {
		_lock.lock();
		try {
			return _generation > 0;
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * If you are using the {@code GeneticAlgorithm} in an threaded environment
	 * and you want to change some of the GAs parameters you can use the returned
	 * {@link Lock} to synchronize your parameter changes. The GA acquires the
	 * lock at the begin of the {@link #setup()} and the {@link #evolve()}
	 * methods and releases it at the end of these methods.
	 * <p/>
	 * To set one ore more GA parameter you will write code like this:
	 * [code]
	 * final GeneticAlgorithm<Float64Gene, Float64> ga = ...
	 * final Function<GeneticAlgorithm<?, ?>, Boolean> until = ...
	 *
	 * //Starting the GA in separate thread.
	 * final Thread thread = new Thread(new Runnable() {
	 *     public void run() {
	 *         while (!Thread.currentThread().isInterrupted() &&
	 *                !until.apply(ga))
	 *         {
	 *             if (ga.getGeneration() == 0) {
	 *                 ga.setup();
	 *             } else {
	 *                 ga.evolve();
	 *             }
	 *         }
	 *     }
	 * });
	 * thread.start();
	 *
	 *  //Changing the GA parameters outside the evolving thread. All parameters
	 *  //are changed before the next evolve step.
	 * ga.getLock().lock();
	 * try {
	 *     ga.setAlterer(new Mutation(0.02);
	 *     ga.setPopulationSize(55);
	 *     ga.setMaximalPhenotypeAge(30);
	 * } finally {
	 *     ga.getLock().unlock();
	 * }
	 * [/code]
	 *
	 * You can use the same lock if you want get a consistent state of the used
	 * parameters, if they where changed within an other thread.
	 *
	 * [code]
	 * ga.getLock().lock();
	 * try {
	 *     final Statistics<?, ?> statistics = ga.getStatistic();
	 *     final Function<?, ?> scaler = ga.getFitnessScaler();
	 * } finally {
	 *     ga.getLock().unlock();
	 * }
	 * [/code]
	 *
	 * The code above ensures that the returned {@code statistics} and
	 * {@code scaler} where used together within the same {@link #evolve()} step.
	 *
	 * @return the lock acquired in the {@link #setup()} and the {@link #evolve()}
	 * 		  method.
	 */
	public Lock getLock() {
		return _lock;
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
	 * Return the used fitness {@link Function} of the GA. The fitness function
	 * is also an important part when modeling the GA. It takes a genotype as
	 * argument and returns, at least, a Comparable object as result---the
	 * fitness value. This allows the GA, respectively the selection operators,
	 * to select the offspring- and survivor population. Some selectors have
	 * stronger requirements to the fitness value than a Comparable, but this
	 * constraints is checked by the Java type system at compile time.
	 * <p/>
	 * The following example shows the simplest possible fitness function. It's
	 * the identity function and returns the allele of an 1x1  float genotype.
	 * [code]
	 * class Id implements Function<Genotype<Float64Gene>, Float64> {
	 *     public Float64 apply(final Genotype<Float64Gene> genotype) {
	 *         return genotype.getGene().getAllele();
	 *     }
	 * }
	 * [/code]
	 * The first type parameter of the {@link Function} defines the kind of
	 * genotype from which the fitness value is calculated and the second type
	 * parameter determines the return type. As already mentioned, the return
	 * type must implement the {@link Comparable} interface.
	 *
	 * @return the used fitness {@link Function} of the GA.
	 */
	public Function<Genotype<G>, C> getFitnessFunction() {
		return _fitnessFunction;
	}

	/**
	 * Set the currently used fitness scaler. The fitness value, calculated by
	 * the fitness function, is the raw-fitness of an individual. The
	 * <em>Jenetics</em> library allows you to apply an additional scaling
	 * function on the raw-fitness to form the fitness value which is used by
	 * the selectors. This can be useful when using probability selectors, where
	 * the actual amount of the fitness value influences the selection
	 * probability. In such cases, the fitness scaler gives you additional
	 * flexibility when selecting offspring and survivors. In the default
	 * configuration the raw-fitness is equal to the actual fitness value, that
	 * means, the used fitness scaler is the identity function.
	 * [code]
	 * class Sqrt extends Function<Float64, Float64> {
	 *     public Float64 apply(final Float64 value) {
	 *         return Float64.valueOf(sqrt(value.doubleValue()));
	 *     }
	 * }
	 * [/code]
	 * The listing above shows a fitness scaler which reduces the the raw-fitness
	 * to its square root. This gives weaker individuals a greater changes being
	 * selected and weakens the influence of super-individuals.
	 * <p/>
	 * <b>When using a fitness scaler you have to take care, that your scaler
	 * doesn't destroy your fitness value. This can be the case when your
	 * fitness value is negative and your fitness scaler squares the value.
	 * Trying to find the minimum will not work in this configuration.</b>
	 *
	 * @param scaler The fitness scaler.
	 * @throws NullPointerException if the scaler is {@code null}.
	 */
	public void setFitnessScaler(final Function<C, C> scaler) {
		_fitnessScaler = nonNull(scaler, "FitnessScaler");
	}

	/**
	 * Return the currently used fitness scaler {@link Function} of the GA.
	 *
	 * @return the currently used fitness scaler {@link Function} of the GA.
	 */
	public Function<C, C> getFitnessScaler() {
		return _fitnessScaler;
	}

	/**
	 * Return the currently used offspring fraction of the GA.
	 *
	 * @return the currently used offspring fraction of the GA.
	 */
	public double getOffspringFraction() {
		return _offspringFraction;
	}

	/**
	 * Return the currently used offspring {@link Selector} of the GA.
	 *
	 * @return the currently used offspring {@link Selector} of the GA.
	 */
	public Selector<G, C> getOffspringSelector() {
		return _offspringSelector;
	}

	/**
	 * Return the currently used survivor {@link Selector} of the GA.
	 *
	 * @return the currently used survivor {@link Selector} of the GA.
	 */
	public Selector<G, C> getSurvivorSelector() {
		return _survivorSelector;
	}

	/**
	 * Return the currently used {@link Alterer} of the GA.
	 *
	 * @return the currently used {@link Alterer} of the GA.
	 */
	public Alterer<G> getAlterer() {
		return _alterer;
	}

	/**
	 * Return the current overall generation.
	 *
	 * @return the current overall generation.
	 */
	public int getGeneration() {
		return _generation;
	}

	/**
	 * Return the maximal age of the {@link Phenotype}s.
	 *
	 * @return the maximal age of the {@link Phenotype}s.
	 */
	public int getMaximalPhenotypeAge() {
		return _maximalPhenotypeAge;
	}

	/**
	 * Return the best {@link Phenotype} so far or {@code null} if the GA hasn't
	 * been initialized yet.
	 *
	 * @return the best {@link Phenotype} so far or {@code null} if the GA hasn't
	 *         been initialized yet.
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _bestStatistics != null ? _bestStatistics.getBestPhenotype() : null;
	}

	/**
	 * Return the current {@link Population} {@link Statistics} or {@code null}
	 * if the GA hasn't been initialized yet.
	 *
	 * @return the current {@link Population} {@link Statistics} or {@code null}
	 *         if the GA hasn't been initialized yet.
	 */
	public Statistics<G, C> getStatistics() {
		return _statistics;
	}

	/**
	 * Set the offspring selector.
	 *
	 * @param selector The offspring selector.
	 * @throws NullPointerException, if the given selector is null.
	 */
	public void setOffspringSelector(final Selector<G, C> selector) {
		_offspringSelector = nonNull(selector, "Offspring selector");
	}

	/**
	 * Set the survivor selector.
	 *
	 * @param selector The survivor selector.
	 * @throws NullPointerException, if the given selector is null.
	 */
	public void setSurvivorSelector(final Selector<G, C> selector) {
		_survivorSelector = nonNull(selector, "Survivor selector");
	}

	/**
	 * Set both, the offspring selector and the survivor selector.
	 *
	 * @param selector The selector for the offsprings and the survivors.
	 * @throws NullPointerException if the {@code selector} is {@code null}
	 */
	public void setSelectors(final Selector<G, C> selector) {
		setOffspringSelector(selector);
		setSurvivorSelector(selector);
	}

	/**
	 * Set the offspring fraction.
	 *
	 * @param offspringFraction The offspring fraction.
	 * @throws IllegalArgumentException if the offspring fraction is out of range.
	 */
	public void setOffspringFraction(final double offspringFraction) {
		_offspringFraction = checkProbability(offspringFraction);
	}

	/**
	 * Set the alterer.
	 *
	 * @param alterer The alterer.
	 * @throws NullPointerException if the alterer is null.
	 */
	public void setAlterer(final Alterer<G> alterer) {
		_alterer = nonNull(alterer, "Alterer");
	}

	/**
	 * Set the given alterers.
	 *
	 * @param alterers the alterers to set.
	 * @throws NullPointerException if the alterers are null.
	 */
	@SafeVarargs
	public final void setAlterers(final Alterer<G>... alterers) {
		setAlterer(new CompositeAlterer<>(alterers));
	}

	/**
	 * Set the maximum age of the phenotypes in the population.
	 *
	 * @param age Maximal phenotype age.
	 * @throws IllegalArgumentException if the age is smaller then one.
	 */
	public void setMaximalPhenotypeAge(final int age) {
		if (age < 1) {
			throw new IllegalArgumentException(String.format(
				"Phenotype age must be greater than one, but was %s.", age
			));
		}
		_maximalPhenotypeAge = age;
	}

	/**
	 * Set the desired population size.
	 *
	 * @param size The population size.
	 * @throws IllegalArgumentException if the population size is smaller than
	 *         one.
	 */
	public void setPopulationSize(final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(String.format(
				"Population size must be greater than zero, but was %s.", size
			));
		}
		_populationSize = size;
	}

	/**
	 * Set the (initial) population in form of a list of phenotypes. The fitness
	 * function and fitness scaler of the phenotypes will be changed with the
	 * current one of this GA. The fitness values are calculated as needed by
	 * the next <i>evolve</i> step. <em>This method doesn't acquire the GA lock.
	 * When used from another thread, the lock must be acquired from outside.</em>
	 *
	 * @see #setGenotypes(Collection)
	 * @see #setup(Collection)
	 * @param population The list of phenotypes to set. The population size is
	 *        set to <code>phenotype.size()</code>.
	 * @throws NullPointerException if the population, or one of its element, is
	 *         {@code null}.
	 * @throws IllegalArgumentException it the population size is smaller than
	 *         one.
	 */
	public void setPopulation(final Collection<Phenotype<G, C>> population) {
		forEach(population, NonNull);
		if (population.size() < 1) {
			throw new IllegalArgumentException(String.format(
				"Population size must be greater than zero, but was %s.",
				population.size()
			));
		}

		final Population<G, C> pop = new Population<>(population.size());
		for (Phenotype<G, C> phenotype : population) {
			pop.add(phenotype.newInstance(
					_fitnessFunction, _fitnessScaler, _generation
				));
		}

		_population = pop;
		_populationSize = population.size();
	}

	/**
	 * Set/change the population in form of a list of genotypes. The fitness
	 * function and fitness scaler will not be changed. The fitness values are
	 * calculated as needed by the next <i>evolve</i> step. <em>This method
	 * doesn't acquire the GA lock. When used from another thread, the lock must
	 * be acquired from outside.</em>
	 *
	 * @see #setPopulation(Collection)
	 * @see #setup(Collection)
	 * @param genotypes The list of genotypes to set. The population size is set
	 *        to <code>genotypes.size()</code>.
	 * @throws NullPointerException if the population, or one of its elements,
	 *         is {@code null}s.
	 * @throws IllegalArgumentException it the population size is smaller than
	 *         one.
	 */
	public void setGenotypes(final Collection<Genotype<G>> genotypes) {
		forEach(genotypes, NonNull);
		if (genotypes.size() < 1) {
			throw new IllegalArgumentException(
				"Genotype size must be greater than zero, but was " +
				genotypes.size() + ". "
			);
		}

		final Population<G, C> population = new Population<>(genotypes.size());
		for (Genotype<G> genotype : genotypes) {
			population.add(Phenotype.valueOf(
				genotype,
				_fitnessFunction,
				_fitnessScaler,
				_generation
			));
		}

		_population = population;
		_populationSize = genotypes.size();
	}

	/**
	 * Return a copy of the current population.
	 *
	 * @return The copy of the current population.
	 */
	public Population<G, C> getPopulation() {
		return new Population<>(_population);
	}

	/**
	 * Return the desired population size of the GA.
	 *
	 * @return the desired population size of the GA.
	 */
	public int getPopulationSize() {
		return _populationSize;
	}

	/**
	 * Return the statistics of the best phenotype. The returned statistics is
	 * {@code null} if the algorithms hasn't been initialized.
	 *
	 * @return the statistics of the best phenotype, or {@code null} if the GA
	 *         hasn't been initialized yet.
	 */
	public Statistics<G, C> getBestStatistics() {
		return _bestStatistics;
	}

	/**
	 * Return the number of killed phenotypes, so far.
	 *
	 * @return the number of killed phenotypes
	 */
	public int getNumberOfKilledPhenotypes() {
		return _killed.get();
	}

	/**
	 * Return the number of invalid phenotypes, so far.
	 *
	 * @return the number of invalid phenotypes
	 */
	public int getNumberOfInvalidPhenotypes() {
		return _invalid.get();
	}

	/**
	 * Set the statistic calculator for this genetic algorithm instance.
	 *
	 * @param calculator the new statistic calculator.
	 * @throws NullPointerException if the given {@code calculator} is
	 *         {@code null}.
	 */
	public void setStatisticsCalculator(final Statistics.Calculator<G, C> calculator) {
		_calculator = nonNull(calculator, "Statistic calculator");
	}

	/**
	 * Return the current statistics calculator.
	 *
	 * @return the current statistics calculator.
	 */
	public Statistics.Calculator<G, C> getStatisticsCalculator() {
		return _calculator;
	}

	/**
	 * Return the current time statistics of the GA. This method acquires the
	 * lock to ensure that the returned values are consistent.
	 *
	 * @return the current time statistics.
	 */
	public Statistics.Time getTimeStatistics() {
		_lock.lock();
		try {
			final Statistics.Time time = new Statistics.Time();
			time.alter.set(_alterTimer.getTime());
			time.combine.set(_combineTimer.getTime());
			time.evaluation.set(_evaluateTimer.getTime());
			time.execution.set(_executionTimer.getTime());
			time.selection.set(_selectTimer.getTime());
			time.statistics.set(_statisticTimer.getTime());
			return time;
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * This method acquires the lock to ensure that the returned value is
	 * consistent.
	 */
	@Override
	public String toString() {
		Phenotype<G, C> phenotype = null;
		int generation = 0;

		_lock.lock();
		try {
			generation = _generation;
			phenotype = getStatistics().getBestPhenotype();
		} finally {
			_lock.unlock();
		}

		return String.format("%4d: (best) %s", generation, phenotype);
	}

}







