/*
 * Java Genetic Algorithm Library (@!identifier!@).
 * Copyright (c) @!year!@ Franz Wilhelmstötter
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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 * 	 
 */
package org.jenetics;

import static java.lang.Math.round;
import static org.jenetics.util.arrays.foreach;
import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.checkProbability;
import static org.jenetics.util.object.nonNull;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javolution.context.ConcurrentContext;
import javolution.context.LogContext;
import javolution.lang.Configurable;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Predicate;
import org.jenetics.util.Timer;


/**
 * Main class. 
 * <p/>
 * 
 * A simple GeneticAlgorithm setup.
 * [code]
 *  //Defining a genotype factory.
 *  final Factory<Genotype<BitGene>> gt = Genotype.valueOf(
 *                    BitChromosome.valueOf(10, 0.5);
 *              );
 *   
 *  // Defining the fitness function.
 *   final FitnessFunction<BitGene, Float64> ff = ...;
 *   
 *   // The given fitness function will be maximized. By default 
 *   // the GA tries to maximize the fitness function.
 *   final Optimize opt = Optimize.MINIMUM;
 *   
 *   // Create the GA.
 *   final GeneticAlgorithm<BitGene, Float64> ga = GeneticAlgorithm.valueOf(gt, ff, opt);
 * [/code]
 * All other needed GA parameters are initialized with default values. Therefore
 * the GA is ready for use now.
 * [code]
 *   ga.setup();
 *   ga.evolve(100);
 *   System.out.println(ga.getStatistics());
 * [/code]
 * 
 * It is possible to set an initial population instead an random one. The 
 * fitness function and the fitness scaler is not initialized by the
 * {@link #setPopulation(List)} or {@link #setGenotypes(List)} function.
 * [code]
 *   final Population<BitGene, Float64> population = (Population<Bitgene, Float64>)
 *       IOUtils.readXML(Population.class, new FileInputStream("population.xml");
 *   ga.setPopulation(population);
 *   //ga.setGenotypes(genotypes); //Or initialize the GA with genotypes.
 *   ga.setup();
 *   ga.evolve(100);
 *   System.out.println(ga.getStatistics());
 * [/code]
 * 
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Genetic_algorithm">
 * 		  Wikipedia: Genetic algorithm
 * 	  </a>
 * 
 * @param <G> The gene type this GA evaluates,
 * @param <C> The result type (of the fitness function).
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
	private final FitnessFunction<G, C> _fitnessFunction;
	private FitnessScaler<C> _fitnessScaler;
	
	private double _offspringFraction = DEFAULT_OFFSPRING_FRACTION;
	
	// Alterers
	private Alterer<G> _alterer = new CompositeAlterer<G>(
			new SinglePointCrossover<G>(0.1),
			new Mutator<G>(0.05)
		);
	
	// Selectors
	private Selector<G, C> _survivorSelector = new TournamentSelector<G, C>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<G, C>(3);
	
	// Population
	private int _populationSize = DEFAULT_POPULATION_SIZE;
	private Population<G, C> _population = new Population<G, C>(_populationSize);
	private int _maximalPhenotypeAge = DEFAULT_MAXIMAL_PHENOTYPE_AGE;
	private volatile int _generation = 0;
	
	// Statistics
	private Statistics.Calculator<G, C> _calculator = new Statistics.Calculator<G, C>();
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
	 * Create a new genetic algorithm. By default the GA tries to maximize the
	 * fitness function.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory, 
		final FitnessFunction<G, C> fitnessFunction
	) {	 
		this(
				genotypeFactory, 
				fitnessFunction, 
				new IdentityScaler<C>(), 
				Optimize.MAXIMUM
			);
	}
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 * 		 fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory, 
		final FitnessFunction<G, C> fitnessFunction,
		final Optimize optimization
	) {	 
		this(
				genotypeFactory, 
				fitnessFunction, 
				new IdentityScaler<C>(), 
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
		final FitnessFunction<G, C> fitnessFunction, 
		final FitnessScaler<C> fitnessScaler
	) {	 
		this(
				genotypeFactory, 
				fitnessFunction, 
				fitnessScaler, 
				Optimize.MAXIMUM
			);
	}
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 * 		 fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory, 
		final FitnessFunction<G, C> fitnessFunction, 
		final FitnessScaler<C> fitnessScaler,
		final Optimize optimization
	) {	 
		_genotypeFactory = nonNull(genotypeFactory, "GenotypeFactory");
		_fitnessFunction = nonNull(fitnessFunction, "FitnessFunction");
		_fitnessScaler = nonNull(fitnessScaler, "FitnessScaler");
		_optimization = nonNull(optimization, "Optimization");
	}
	
	/**
	 * Setting up the <code>GeneticAlgorithm</code>. Subsequent calls to this 
	 * method throw IllegalStateException. If no initial population has been 
	 * set (with {@link #setPopulation(List)} or {@link #setGenotypes(List)}) a
	 * random population is generated.
	 * 
	 * @throws IllegalStateException if called more than once.
	 */
	public void setup() {
		_lock.lock();
		try {
			if (_generation > 0) {
				throw new IllegalStateException(
					"The method GeneticAlgorithm.setup() must be called only once."
				);
			}
			
			++_generation;
			
			_executionTimer.start();
			
			//Initializing/filling up the Population.
			for (int i = _population.size(); i < _populationSize; ++i) {
				final Phenotype<G, C> pt = Phenotype.valueOf(
					_genotypeFactory.newInstance(), 
					_fitnessFunction, 
					_fitnessScaler, 
					_generation
				);
				_population.add(pt);
			}
			
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
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * Evolve one generation.
	 * 
	 * @throws IllegalStateException if the {@link GeneticAlgorithm#setup()} 
	 * 		  method was not called first.
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
					_bestStatistics.getBestPhenotype(), 
					_statistics.getBestPhenotype()
				);
			
			if (comp < 0) {
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
		ConcurrentContext.enter();
		try  {
			for (int i = 0; i < _population.size(); ++i) {
				ConcurrentContext.execute(_population.get(i));
			}
		} finally {
			ConcurrentContext.exit();
			_evaluateTimer.stop();
		}
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
	 * Evolve the GA as long the given {@link Predicate} returns {@code true}.
	 * 
	 * @see Until
	 * 
	 * @param until the predicate which defines the termination condition.
	 * @throws NullPointerException if the given predicate is {@code null}.
	 */
	public void evolve(final Predicate<? super Statistics<G, C>> until) {
		nonNull(until, "Termination condition");
		while (until.evaluate(getStatistics())) {
			evolve();
		}
	}
	
	private Array<Population<G, C>> select() {
		final Array<Population<G, C>> selection = new Array<Population<G, C>>(2);
		final int numberOfSurvivors = getNumberOfSurvivors();
		final int numberOfOffspring = getNumberOfOffsprings();
		assert (numberOfSurvivors + numberOfOffspring == _populationSize);
		
		ConcurrentContext.enter();
		try {
			ConcurrentContext.execute(new Runnable() {
				@Override public void run() {
					final Population<G, C> survivors = _survivorSelector.select(
						_population, numberOfSurvivors, _optimization
					);
					
					assert (survivors.size() == numberOfSurvivors);
					selection.set(0, survivors);
				}
			});
			ConcurrentContext.execute(new Runnable() {
				@Override public void run() {
					final Population<G, C> offsprings = _offspringSelector.select(
						_population, numberOfOffspring, _optimization
					);	
					
					assert (offsprings.size() == numberOfOffspring);
					selection.set(1, offsprings);
				}
			});
		} finally {
			ConcurrentContext.exit();
		}
	
		return selection;
	}
	
	private Population<G, C> combine(
		final Population<G, C> survivors, 
		final Population<G, C> offsprings
	) {
		assert (survivors.size() + offsprings.size() == _populationSize);
		final Population<G, C> population = new Population<G, C>(_populationSize);
		
		ConcurrentContext.enter();
		try {
			// Kill survivors which are to old and replace it with new one.
			ConcurrentContext.execute(new Runnable() {
				@Override public void run() {
					for (int i = 0, n = survivors.size(); i < n; ++i) {
						final Phenotype<G, C> survivor = survivors.get(i);
						
						final boolean isTooOld = 
							survivor.getAge(_generation) > _maximalPhenotypeAge;
							
						final boolean isInvalid = isTooOld || !survivor.isValid();
						
						// Sorry, too old or not valid.
						if (isInvalid) {
							final Phenotype<G, C> newpt = Phenotype.valueOf(
									_genotypeFactory.newInstance(), 
									_fitnessFunction, 
									_fitnessScaler, 
									_generation
								);
							survivors.set(i, newpt);
						}
						
						if (isTooOld) {
							_killed.incrementAndGet();
						} else if (isInvalid) {
							_invalid.incrementAndGet();
						}
					}
				}
			});
			
			// In the mean time we can add the offsprings.
			ConcurrentContext.execute(new Runnable() {
				@Override public void run() {
					population.addAll(offsprings);
				}
			});
		} finally {
			ConcurrentContext.exit();
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
	 *  final GeneticAlgorithm<DoubleGene, Float64> ga = ...
	 *  final Predicate<GeneticAlgorithm<?, ?>> stopCondition = ...
	 *  
	 *  //Starting the GA in separate thread.
	 *  final Thread thread = new Thread(new Runnable() {
	 *      public void run() {
	 *          while (!Thread.currentThread().isInterrupted() && 
	 *                 !stopCondition.evaluate(ga)) 
	 *          {
	 *              if (ga.getGeneration() == 0) {
	 *                  ga.setup();
	 *              } else {
	 *                  ga.evolve();
	 *              }
	 *          }
	 *      }
	 *  });
	 *  thread.start();
	 *  
	 *   //Changing the GA parameters outside the evolving thread. All parameters
	 *   //are changed before the next evolve step.
	 *  ga.getLock().lock();
	 *  try {
	 *      ga.setAlterer(new Mutation(Probability.valueOf(0.02));
	 *      ga.setPopulationSize(55);
	 *      ga.setMaximalPhenotypeAge(30);
	 *  } finally {
	 *      ga.getLock().unlock();
	 *  }
	 * [/code]
	 * 
	 * You can use the same lock if you want get a consistent state of the used
	 * parameters, if they where changed within an other thread.
	 * 
	 * [code]
	 *   ga.getLock().lock();
	 *   try {
	 *       final Statistics<?, ?> statistics = ga.getStatistic();
	 *       final FitnessScaler<?> scaler = ga.getFitnessScaler();
	 *   } finally {
	 *       ga.getLock().unlock();
	 *   }
	 * [/code]
	 * 
	 * The code above ensures that the returned {@code statistics} and 
	 * {@code scaler} were used together within the same {@link #evolve()} step.
	 * 
	 * @return the lock acquired in the {@link #setup()} and the {@link #evolve()}
	 * 		  method.
	 */
	public Lock getLock() {
		return _lock;
	}
	
	/**
	 * Return the used genotype {@link Factory} of the GA. 
	 * 
	 * @return the used genotype {@link Factory} of the GA. 
	 */
	public Factory<Genotype<G>> getGenotypeFactory() {
		return _genotypeFactory;
	}
	
	/**
	 * Return the used {@link FitnessFunction} of the GA. 
	 * 
	 * @return the used {@link FitnessFunction} of the GA. 
	 */
	public FitnessFunction<G, C> getFitnessFunction() {
		return _fitnessFunction;
	}
	
	/**
	 * Set the currently used fitness scaler.
	 * 
	 * @param scaler The fitness scaler.
	 * @throws NullPointerException if the scaler is {@code null}.
	 */
	public void setFitnessScaler(final FitnessScaler<C> scaler) {
		_fitnessScaler = nonNull(scaler, "FitnessScaler");
	}
	
	/**
	 * Return the currently used {@link FitnessScaler} of the GA. 
	 * 
	 * @return the currently used {@link FitnessScaler} of the GA. 
	 */
	public FitnessScaler<C> getFitnessScaler() {
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
	 * 		  been initialized yet.
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _bestStatistics != null ? _bestStatistics.getBestPhenotype() : null;
	}
	
	/**
	 * Return the current {@link Population} {@link Statistics} or {@code null} 
	 * if the GA hasn't been initialized yet.
	 * 
	 * @return the current {@link Population} {@link Statistics} or {@code null} 
	 * 		  if the GA hasn't been initialized yet.
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
	 * Add a Alterer to the GeneticAlgorithm.
	 * 
	 * @param alterer the {@link Alterer} to add.
	 */
	public void addAlterer(final Alterer<G> alterer) {
		_alterer = CompositeAlterer.join(_alterer, nonNull(alterer, "Alterer"));
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
	 *		one.
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
	 * function and fitness scaler will not be changed.
	 * 
	 * @param population The list of phenotypes to set. The population size is 
	 * 		 set to <code>phenotype.size()</code>.
	 * @throws NullPointerException if the population, or one of its element, is 
	 * 		  {@code null}.
	 * @throws IllegalArgumentException it the population size is smaller than
	 * 		  one.
	 */
	public void setPopulation(final List<Phenotype<G, C>> population) {
		foreach(population, NonNull);
		if (population.size() < 1) {
			throw new IllegalArgumentException(String.format(
				"Population size must be greater than zero, but was %s.",
				population.size()
			));
		}
		
		final Population<G, C> pop = new Population<G, C>(population.size());
		for (Phenotype<G, C> phenotype : population) {
			pop.add(phenotype.newInstance(
					_fitnessFunction, _fitnessScaler, _generation
				));
		}
		_population = pop;
		_populationSize = population.size();
	}
	
	/**
	 * Set the (initial) population in form of a list of genotypes. The fitness
	 * function and fitness scaler will not be changed.
	 * 
	 * @param genotypes The list of genotypes to set. The population size is set 
	 * 		 to <code>genotypes.size()</code>.
	 * @throws NullPointerException if the population, or one of its elements, 
	 * 		  is {@code null}s.
	 * @throws IllegalArgumentException it the population size is smaller than
	 *			one.
	 */
	public void setGenotypes(final List<Genotype<G>> genotypes) {
		foreach(genotypes, NonNull);
		if (genotypes.size() < 1) {
			throw new IllegalArgumentException(
				"Genotype size must be greater than zero, but was " +
				genotypes.size() + ". "
			);
		}
		
		final Population<G, C> pop = new Population<G, C>(genotypes.size());
		for (Genotype<G> genotype : genotypes) {
			pop.add(Phenotype.valueOf(
				genotype, 
				_fitnessFunction,
				_fitnessScaler,
				_generation
			));
		}
		_population = pop;
		_populationSize = genotypes.size();
	}
	
	/**
	 * Return a copy of the current population.
	 * 
	 * @return The copy of the current population.
	 */
	public Population<G, C> getPopulation() {
		return new Population<G, C>(_population);
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
	 * 		  hashn't been initialized yet.
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
	 * 		  {@code null}.
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
	
	public static void setConcurrency(final int concurrency) {
		if (concurrency > ConcurrentContext.getConcurrency()) {
			LogContext.enter(LogContext.NULL);
			try {
				final Properties properties = new Properties();
				properties.put(
					"javolution.context.ConcurrentContext#MAXIMUM_CONCURRENCY", 
					concurrency
				);
				
				Configurable.read(properties);
			} finally {
				LogContext.exit();
			}
		}
		
		ConcurrentContext.setConcurrency(concurrency);
	}
	
	public static void setConcurrentContext(
		final Class<? extends ConcurrentContext> context
	) {
		LogContext.enter(LogContext.NULL);
		try {
			final Properties properties = new Properties();
			properties.put(
				"javolution.context.ConcurrentContext#DEFAULT", context
			);
			Configurable.read(properties);
		} finally {
			LogContext.exit();
		}
	}
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 * 		 fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final Factory<Genotype<SG>> genotypeFactory, 
		final FitnessFunction<SG, SC> fitnessFunction, 
		final FitnessScaler<SC> fitnessScaler,
		final Optimize optimization
	)
	{
		return new GeneticAlgorithm<SG, SC>(
			genotypeFactory, fitnessFunction, fitnessScaler, optimization
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
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final Factory<Genotype<SG>> genotypeFactory, 
		final FitnessFunction<SG, SC> fitnessFunction, 
		final FitnessScaler<SC> fitnessScaler
	)
	{
		return new GeneticAlgorithm<SG, SC>(
			genotypeFactory, fitnessFunction, fitnessScaler
		);
	}

	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param optimization Determine whether this GA maximize or minimize the
	 * 		 fitness function.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final Factory<Genotype<SG>> genotypeFactory, 
		final FitnessFunction<SG, SC> fitnessFunction,
		final Optimize optimization
	) 
	{
		return new GeneticAlgorithm<SG, SC>(
				genotypeFactory, fitnessFunction, optimization
			);
	}
	
	/**
	 * Create a new genetic algorithm. By default the GA tries to maximize the
	 * fitness function.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <SG extends Gene<?, SG>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final Factory<Genotype<SG>> genotypeFactory, 
		final FitnessFunction<SG, SC> fitnessFunction
	) 
	{
		return new GeneticAlgorithm<SG, SC>(genotypeFactory, fitnessFunction);
	}
}







