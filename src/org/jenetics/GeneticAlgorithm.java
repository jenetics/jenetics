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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *     
 */
package org.jenetics;

import static java.lang.Math.round;
import static org.jenetics.util.EvaluatorRegistry.evaluate;
import static org.jenetics.util.Validator.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jenetics.util.Array;
import org.jenetics.util.ConcurrentEvaluator;
import org.jenetics.util.EvaluatorRegistry;
import org.jenetics.util.Factory;
import org.jenetics.util.Probability;
import org.jenetics.util.ThreadedEvaluator;
import org.jenetics.util.Timer;

/**
 * Main class. 
 * <p/>
 * 
 * A simple GeneticAlgorithm setup.
 * [code]
 *     //Defining a Genotype prototype.
 *     Genotype<BitGene> gt = Genotype.newGenotype(
 *         BitChromosome.valueOf(10, Probability.valueOf(0.5));
 *     );
 *     FitnessFunction<BitGene, Float64> ff = ...//FitnessFunction implementation
 *     GeneticAlgorithm<BitGene, Float64> ga = new GeneticAlgorithm.valueOf(gt, ff);
 * [/code]
 * All other needed GA parameters are initialized with default values. Therefore
 * the GA is ready for use now.
 * [code]
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getStatistics());
 * [/code]
 * 
 * It is possible to set an initial population instead an random one. The 
 * fitness function and the fitness scaler is not initialized by the
 * {@link #setPopulation(List)} or {@link #setGenotypes(List)} function.
 * [code]
 *     Population population = 
 *         (Population)XMLSerializer.read(new FileInputStream("population.xml");
 *     ga.setPopulation(population);
 *     //ga.setGenotypes(genotypes); //Or initialize the GA with genotypes.
 *     ga.setup();
 *     ga.evolve(100);
 *     System.out.println(ga.getStatistics());
 * [/code]
 * 
 * If you have a problem to solve which requires expensive fitness calculation
 * you can parallelize the fitness calculation by using the {@link ConcurrentEvaluator}
 * of {@link ThreadedEvaluator}.
 * [code]
 *     final int numberOfThreads = Runtime.getRuntime().availableProcessors() + 1;
 *     ga.setEvaluator(new ConcurrentEvaluator(numberOfThreads));
 * [/code]
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: GeneticAlgorithm.java,v 1.49 2010-01-14 14:53:10 fwilhelm Exp $
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Genetic_algorithm">
 *         Wikipedia: Genetic algorithm
 *      </a>
 * 
 * <G> 
 */
public class GeneticAlgorithm<G extends Gene<?, G>, C extends Comparable<C>> {
	private static final int POPULATION_SIZE = 50;
	private static final int MAXIMAL_PHENOTYPE_AGE = 70;
	
	private final Lock _lock = new ReentrantLock(true);	
	
	private final Factory<Genotype<G>> _genotypeFactory;
	private final FitnessFunction<G, C> _fitnessFunction;
	private FitnessScaler<C> _fitnessScaler;
	
	private Probability _offspringFraction = Probability.valueOf(0.6);
	
	private Alterer<G> _alterer = ( 
			new SinglePointCrossover<G>(Probability.valueOf(0.1))).append(
			new Mutation<G>(Probability.valueOf(0.05))
		);
	
	private Selector<G, C> _survivorSelector = new TournamentSelector<G, C>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<G, C>(3);
	
	private int _populationSize = POPULATION_SIZE;
	private Population<G, C> _population = new Population<G, C>(_populationSize);
	private int _maximalPhenotypeAge = MAXIMAL_PHENOTYPE_AGE;
	private int _generation = 0;
	
	private Statistics.Calculator<G, C> _calculator = new Statistics.Calculator<G, C>();
	private Statistics<G, C> _bestStatistic;
	private Statistics<G, C> _statistics;
	private Phenotype<G, C> _bestPhenotype;
	
	
	//Some performance measure.
	private final Timer _executionTimer = new Timer("Execution time");
	private final Timer _selectTimer = new Timer("Select time");
	private final Timer _alterTimer = new Timer("Alter time");
	private final Timer _statisticTimer = new Timer("Statistic time");
	private final Timer _evaluateTimer = new Timer("Evaluate time");
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotype factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final Factory<Genotype<G>> genotypeFactory, 
		final FitnessFunction<G, C> fitnessFunction
	) {	 
		this(genotypeFactory, fitnessFunction, new IdentityScaler<C>());
	}
	
	/**
	 * Create a new genetic algorithm.
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
		notNull(genotypeFactory, "GenotypeFactory");
		notNull(fitnessFunction, "FitnessFunction");
		notNull(fitnessScaler, "FitnessScaler");
		
		_genotypeFactory = genotypeFactory;
		_fitnessFunction = fitnessFunction;
		_fitnessScaler = fitnessScaler;
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
					_genotypeFactory.newInstance(), _fitnessFunction, 
					_fitnessScaler, _generation
				);
				_population.add(pt);
			}
			
			//Evaluate the fitness.
			_evaluateTimer.start();
			evaluate(_population);		
			_evaluateTimer.stop();
			
			//First valuation of the initial population.
			_statisticTimer.start();
			_statistics = _calculator.evaluate(_population);
			_bestPhenotype =_statistics.getBestPhenotype();
			_bestStatistic = _statistics;
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
	 *         method was not called first.
	 */
	public void evolve() {		
		_lock.lock();
		try {
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
			
			//Alter the offprings (Recombination, Mutation ...).
			_alterTimer.start();
			_alterer.alter(offsprings, _generation);
			_alterTimer.stop();
			
			//Combining the new population (containing the survivors and the altered
			//offsprings).
			_population = combine(survivors, offsprings);
			
			//Evaluate the fitness
			_evaluateTimer.start();
			evaluate(_population);		
			_evaluateTimer.stop();
			
			//Evaluate the statistic
			_statisticTimer.start();
			_statistics = _calculator.evaluate(_population);
			if (_bestPhenotype.getFitness().compareTo(_statistics.getBestFitness()) < 0) {
				_bestPhenotype = _statistics.getBestPhenotype();
				_bestStatistic = _statistics;
			}
			_statisticTimer.stop();
			
			_executionTimer.stop();
			
			setTimes(_statistics);
		} finally {
			_lock.unlock();
		}
	}
	
	private void setTimes(final Statistics<?, ?> statistic) {
		statistic.getTimes().execution.set(_executionTimer.getInterimTime());
		statistic.getTimes().selection.set(_selectTimer.getInterimTime());
		statistic.getTimes().alter.set(_alterTimer.getInterimTime());
		statistic.getTimes().evaluation.set(_evaluateTimer.getInterimTime());
		statistic.getTimes().statistics.set(_statisticTimer.getInterimTime());
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
	
	private Array<Population<G, C>> select() {
		final Array<Population<G, C>> selection = new Array<Population<G, C>>(2);
		
		final List<Runnable> selectors = new ArrayList<Runnable>(2);
		selectors.add(new Runnable() {
			@Override public void run() {
				final Population<G, C> survivors = _survivorSelector.select(
					_population, getNumberOfSurvivors()
				);
				selection.set(0, survivors);
			}
		});
		selectors.add(new Runnable() {
			@Override public void run() {
				final Population<G, C> offsprings = _offspringSelector.select(
					_population, getNumberOfOffsprings()
				);	
				selection.set(1, offsprings);
			}
		});
		EvaluatorRegistry.evaluate(selectors);
	
		return selection;
	}
	
	private Population<G, C> combine(
		final Population<G, C> survivors, 
		final Population<G, C> offsprings
	) {
		final Population<G, C> population = new Population<G, C>(_populationSize);
		
		for (int i = 0, n = survivors.size(); i < n; ++i) {
			final Phenotype<G, C> survivor = survivors.get(i);
			
			//Survivor is still alive and valid.
			if ((_generation - survivor.getGeneration()) <= _maximalPhenotypeAge && 
				survivor.isValid()) 
			{
				population.add(survivor);
				
			//Create new phenotypes for dead survivors.
			} else {
				final Phenotype<G, C> pt = Phenotype.valueOf(
					_genotypeFactory.newInstance(), _fitnessFunction, 
					_fitnessScaler, _generation
				);
				population.add(pt);
			}
		}
		population.addAll(offsprings);
		
		return population;
	}
	
	private int getNumberOfSurvivors() {
		return _populationSize - getNumberOfOffsprings();
	}
	
	private int getNumberOfOffsprings() {
		return (int)round(
			_offspringFraction.doubleValue()*_populationSize
		);
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
	 *     final GeneticAlgorithm<DoubleGene, Float64> ga = ...
	 *     final Predicate<GeneticAlgorithm<?, ?>> stopCondition = ...
	 *     
	 *     //Starting the GA in separate thread.
	 *     final Thread thread = new Thread(new Runnable() {
	 *         public void run() {
	 *             while (!Thread.currentThread().isInterrupted() && 
	 *                    !stopCondition.evaluate(ga)) 
	 *             {
	 *                 if (ga.getGeneration() == 0) {
	 *                     ga.setup();
	 *                 } else {
	 *                     ga.evolve();
	 *                 }
	 *             }
	 *         }
	 *     });
	 *     thread.start();
	 *     
	 *     //Changing the GA parameters outside the evolving thread. All parameters
	 *     //are changed before the next evolve step.
	 *     ga.getLock().lock();
	 *     try {
	 *         ga.setAlterer(new Mutation(Probability.valueOf(0.02));
	 *         ga.setPopulationSize(55);
	 *         ga.setMaximalPhenotypeAge(30);
	 *     } finally {
	 *         ga.getLock().unlock();
	 *     }
	 * [/code]
	 * 
	 * You can use the same lock if you want get a consistent state of the used
	 * parameters, if they where changed within an other thread.
	 * 
	 * [code]
	 *     ga.getLock().lock();
	 *     try {
	 *         final Statistics<?, ?> statistics = ga.getStatistic();
	 *         final FitnessScaler<?> scaler = ga.getFitnessScaler();
	 *     } finally {
	 *         ga.getLock().unlock();
	 *     }
	 * [/code]
	 * 
	 * The code above ensures that the returned {@code statistics} and 
	 * {@code scaler} were used together within the same {@link #evolve()} step.
	 * 
	 * @return the lock acquired in the {@link #setup()} and the {@link #evolve()}
	 *         method.
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
		notNull(scaler, "FitnessScaler");
		_fitnessScaler = scaler;
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
	public Probability getOffspringFraction() {
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
	 * Return the best {@link Phenotype} so far.
	 * 
	 * @return the best {@link Phenotype} so far.
	 */
	public Phenotype<G, C> getBestPhenotype() {
		return _bestPhenotype;
	}
	
	/**
	 * Return the current {@link Population} {@link Statistics}.
	 * 
	 * @return the current {@link Population} {@link Statistics}.
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
		notNull(selector, "Offspring selector");
		_offspringSelector = selector;
	}

	/**
	 * Set the survivor selector.
	 * 
	 * @param selector The survivor selector.
	 * @throws NullPointerException, if the given selector is null.
	 */
	public void setSurvivorSelector(final Selector<G, C> selector) {
		notNull(selector, "Survivor selector");
		_survivorSelector = selector;
	}
	
	/**
	 * Set both, the offspring selector and the survivor selector.
	 * 
	 * @param selector The selector for the offsprings and the survivors.
	 */
	public void setSelectors(final Selector<G, C> selector) {
		setOffspringSelector(selector);
		setSurvivorSelector(selector);
	}
	
	/**
	 * Set the offspring fraction.
	 * 
	 * @param offspringFraction The offspring fraction.
	 * @throws NullPointerException if the offspring fraction is null.
	 */
	public void setOffspringFraction(final Probability offspringFraction) {
		notNull(offspringFraction, "Offspring fraction");
		_offspringFraction = offspringFraction;
	}
	
	/**
	 * Set the alterer.
	 * 
	 * @param alterer The alterer.
	 * @throws NullPointerException if the alterer is null.
	 */
	public void setAlterer(final Alterer<G> alterer) {
		notNull(alterer, "Alterer");
		_alterer = alterer;
	}
	
	/**
	 * Add a Alterer to the GeneticAlgorithm.
	 * 
	 * @param alterer the {@link Alterer} to add.
	 */
	public void addAlterer(final Alterer<G> alterer) {
		notNull(alterer, "Alterer");
		_alterer.append(alterer);
	}
	
	/**
	 * Set the maximum age of the phenotypes in the population.
	 * 
	 * @param age Maximal phenotype age.
	 * @throws IllegalArgumentException if the age is smaller then one.
	 */
	public void setMaximalPhenotypeAge(final int age) {
		if (age < 1) {
			throw new IllegalArgumentException(
				"Phenotype age must be greater than one, but was " + age + ". "
			);
		}
		_maximalPhenotypeAge = age;
	}
	
	/**
	 * Set the desired population size.
	 * 
	 * @param size The population size.
	 * @throws IllegalArgumentException if the population size is smaller than
	 * 		one.
	 */
	public void setPopulationSize(final int size) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Population size must be greater than zero, but was " + size + ". "
			);
		}		 
		_populationSize = size;
	}
	
	/**
	 * Set the (initial) population in form of a list of phenotypes. The fitness
	 * function and fitness scaler will not be changed.
	 * 
	 * @param population The list of phenotypes to set. The population size is 
	 *        set to <code>phenotype.size()</code>.
	 * @throws NullPointerException if the population is null.
	 * @throws IllegalArgumentException it the population size is smaller than
	 *         one.
	 */
	public void setPopulation(final List<Phenotype<G, C>> population) {
		notNull(population, "Population");
		if (population.size() < 1) {
			throw new IllegalArgumentException(
				"Population size must be greater than zero, but was " +
				population.size() + ". "
			);
		}
		
		_population.clear();
		for (Phenotype<G, C> phenotype : population) {
			_population.add(phenotype.newInstance(
					_fitnessFunction, _fitnessScaler, _generation
				));
		}
		_populationSize = population.size();
	}
	
	/**
	 * Set the (initial) population in form of a list of genotypes. The fitness
	 * function and fitness scaler will not be changed.
	 * 
	 * @param genotypes The list of genotypes to set. The population size is set 
	 *        to <code>genotypes.size()</code>.
	 * @throws NullPointerException if the population is null.
	 * @throws IllegalArgumentException it the population size is smaller than
	 * 		one.
	 */
	public void setGenotypes(final List<Genotype<G>> genotypes) {
		notNull(genotypes, "Genotypes");
		if (genotypes.size() < 1) {
			throw new IllegalArgumentException(
				"Genotype size must be greater than zero, but was " +
				genotypes.size() + ". "
			);
		}
		
		_population.clear();
		for (Genotype<G> genotype : genotypes) {
			_population.add(Phenotype.valueOf(
				genotype, 
				_fitnessFunction,
				_fitnessScaler,
				_generation
			));
		}
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
	
	public Statistics<G, C> getBestStatistic() {
		return _bestStatistic;
	}
	
	/**
	 * Set the statistic calculator for this genetic algorithm instance.
	 * 
	 * @param calculator the new statistic calculator.
	 * @throws NullPointerException if the given {@code calculator} is {@code null}.
	 */
	public void setStatisticCalculator(final Statistics.Calculator<G, C> calculator) {
		notNull(calculator, "Statistic calculator");
		_calculator = calculator;
	}
	
	/**
	 * Return the current statistic calculator.
	 * 
	 * @return the current statistic calculator.
	 */
	public Statistics.Calculator<G, C> getStatisticCalculator() {
		return _calculator;
	}
	
	/**
	 * Return the current time statistics of the GA.
	 * 
	 * @return the current time statistics.
	 */
	public Statistics.Time getTimeStatistics() {
		_lock.lock();
		try {
			final Statistics.Time time = new Statistics.Time();
			time.alter.set(_alterTimer.getTime());
			time.evaluation.set(_evaluateTimer.getTime());
			time.execution.set(_executionTimer.getTime());
			time.selection.set(_selectTimer.getTime());
			time.statistics.set(_statisticTimer.getTime());
			return time;
		} finally {
			_lock.unlock();
		}
	}
	
	@Override
	public String toString() {
		_lock.lock();
		try {
			final StringBuilder out = new StringBuilder();
			out.append(String.format(
				"%4d: (best) %s", _generation, getStatistics().getBestPhenotype()
			));
			return out.toString();
		} finally {
			_lock.unlock();
		}
	}
	
	/**
	 * Create a new genetic algorithm.
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






