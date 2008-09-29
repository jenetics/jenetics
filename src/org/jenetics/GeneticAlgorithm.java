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
import static org.jenetics.util.Validator.notNull;

import java.util.List;
import java.util.Random;

import javax.measure.Measurable;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jenetics.util.Evaluator;
import org.jenetics.util.Probability;

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
 *     System.out.println(ga.getStatistic());
 * [/code]
 * 
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: GeneticAlgorithm.java,v 1.15 2008-09-29 20:39:31 fwilhelm Exp $
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Genetic_algorithm">Wikipedia: Genetic algorithm</a>
 * 
 * <G> 
 */
public class GeneticAlgorithm<G extends Gene<?>, C extends Comparable<C>> {
	
	private GenotypeFactory<G> _genotypeFactory = null;
	private FitnessFunction<G, C> _fitnessFunction = null;
	private FitnessScaler<C> _fitnessScaler = null;
	
	private Probability _survivorFraction = Probability.valueOf(0.4);
	private Probability _offspringFraction = Probability.valueOf(0.6);
	
	private Alterer<G> _alterer = ( 
		new SinglePointCrossover<G>(Probability.valueOf(0.1))).append(
		new Mutation<G>(Probability.valueOf(0.05))
	);
	private Selector<G, C> _survivorSelector = new TournamentSelector<G, C>(3);
	private Selector<G, C> _offspringSelector = new TournamentSelector<G, C>(3);
	
	private int _populationSize = 50;
	private Population<G, C> _population = new Population<G, C>();
	private int _maximalPhenotypeAge = 70;
	private int _generation = 0;
	
	private Phenotype<G, C> _bestPhenotype = null;
	private Statistic<G, C> _bestStatistic = null;
	
	private Statistic<G, C> _statistic = null;
	private StatisticCalculator<G, C> _calculator = new StatisticCalculator<G, C>();
	
	private Evaluator _evaluator = new SerialEvaluator();
	
	private long _startTime =  System.currentTimeMillis();
	private long _stopTime = System.currentTimeMillis();

	public final Timer _selectTimer = new Timer("Select");
	public final Timer _alterTimer = new Timer("Alter");
	public final Timer _evaluateTimer = new Timer("Evaluate");
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotyp factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final GenotypeFactory<G> genotypeFactory, 
		final FitnessFunction<G, C> fitnessFunction
	) {	 
		this(genotypeFactory, fitnessFunction, new IdentityScaler<C>());
	}
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotyp factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public GeneticAlgorithm(
		final GenotypeFactory<G> genotypeFactory, 
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
	 * method throw IllegalStateException.
	 * 
	 * @throws IllegalStateException if called more than once.
	 */
	public void setup() {
		if (_generation > 0) {
			throw new IllegalStateException(
				"The method GeneticAlgorithm.setup() must be called only once."
			);
		}
		
		_startTime = System.currentTimeMillis();
		
		//Initializing/filling up the Population 
		for (int i = _population.size(); i < _populationSize; ++i) {
			final Phenotype<G, C> pt = Phenotype.valueOf(
				_genotypeFactory.newGenotype(), _fitnessFunction, 
				_fitnessScaler, _generation
			);
			_population.add(pt);
		}
		
		//Evaluate the fitness.
		_evaluator.evaluate(_population);
		
		//First valuation of the initial population.
		_statistic = _calculator.evaluate(_population);
		_bestPhenotype = _statistic.getBestPhenotype();
		_bestStatistic = _statistic;
		++_generation;
	}
	
	/**
	 * Evolve one generation.
	 * 
	 * @throws IllegalStateException if the {@link GeneticAlgorithm#setup()} 
	 *         method was not called first.
	 */
	public void evolve() {
		if (_generation == 0) {
			throw new IllegalStateException(
				"Call the GeneticAlgorithm.setup() method before " +
				"calling GeneticAlgorithm.evolve()."
			);
		}
		
		//Increment the generation and the generation.
		++_generation;
		
		_selectTimer.start();
		//Select the survivors.
		final Population<G, C> survivors = _survivorSelector.select(
			_population, getNumberOfSurvivors()
		);

		//Generate the offspring.
		final Population<G, C> offspring = _offspringSelector.select(
			_population, getNumberOfOffsprings()
		);
		_selectTimer.stop();
		
		_alterTimer.start();
		//Altering the offpring (Recombination, Mutation ...).
		_alterer.alter(offspring);
		_alterTimer.stop();
		
		//Accepting the new population.
		_population = new Population<G, C>(_populationSize);
		for (int i = 0, n = survivors.size(); i < n; ++i) {
			final Phenotype<G, C> survivor = survivors.get(i);
			
			//Survivor is still alive and valid.
			if ((_generation - survivor.getGeneration()) <=
				_maximalPhenotypeAge && survivor.isValid()) 
			{
				_population.add(survivor);
				
			//Create new phenotypes for dead survivors.
			} else {
				final Phenotype<G, C> pt = Phenotype.valueOf(
					_genotypeFactory.newGenotype(), _fitnessFunction, 
					_fitnessScaler, _generation
				);
				_population.add(pt);
			}
		}
		_population.addAll(offspring);
		
		_evaluateTimer.start();
		//Evaluate the fitness
		_evaluator.evaluate(_population);		
		_evaluateTimer.stop();
		
		//Evaluate the statistic
		_statistic = _calculator.evaluate(_population);
		if (_bestPhenotype.getFitness().compareTo(_statistic.getBestFitness()) < 0) {
			_bestPhenotype = _statistic.getBestPhenotype();
			_bestStatistic = _statistic;
		}
		
		_stopTime = System.currentTimeMillis();
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
	
	private int getNumberOfSurvivors() {
		return (int)round(
			_survivorFraction.doubleValue()*_populationSize/*/
			(survivorFraction.doubleValue() + offspringFraction.doubleValue())*/
		);
	}
	
	private int getNumberOfOffsprings() {
		return (int)round(
			_offspringFraction.doubleValue()*_populationSize/*/
			(survivorFraction.doubleValue() + offspringFraction.doubleValue())*/
		);
	}
	
	/**
	 * Return the currently used {@link GenotypeFactory} of the GA. 
	 * 
	 * @return the currently used {@link GenotypeFactory} of the GA. 
	 */
	public GenotypeFactory<G> getGenotypeFactory() {
		return _genotypeFactory;
	}
	
	/**
	 * Return the currently used {@link FitnessFunction} of the GA. 
	 * 
	 * @return the currently used {@link FitnessFunction} of the GA. 
	 */
	public FitnessFunction<G, C> getFitnessFunction() {
		return _fitnessFunction;
	}
	
	/**
	 * Set the fitness scaler.
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
	 * Set a fitness evaluator.
	 * 
	 * @param evaluator the fitness evaluator.
	 * @throws NullPointerException if the evaluator is {@code null}.
	 */
	public void setFitnessEvaluator(final Evaluator evaluator) {
		notNull(evaluator, "Fitness evaluator");
		_evaluator = evaluator;
	}
	
	/**
	 * Return the fitness evaluator.
	 * 
	 * @return the fitness evaluator.
	 */
	public Evaluator getFitnessEvaluator() {
		return _evaluator;
	}
	
	/**
	 * Return the currently used survivor fraction of the GA. 
	 * 
	 * @return the currently used survivor fraction of the GA. 
	 */
	public Probability getSurvivorFraction() {
		return _survivorFraction;
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
	 * Return the current {@link Population} {@link Statistic}.
	 * 
	 * @return the current {@link Population} {@link Statistic}.
	 */
	public Statistic<G, C> getStatistic() {
		return _statistic;
	}
	
	/**
	 * Return the currently used random number generator.
	 * 
	 * @return the currently used random number generator.
	 */
	public static Random getRandom() {
		return RandomRegistry.getRandom();
	}

	/**
	 * Set the random engine.
	 * 
	 * @param random The random engine.
	 * @throws NullPointerException if the given random engine is null.
	 */
	public static void setRandom(final Random random) {
		notNull(random, "Random engine");
		RandomRegistry.setRandom(random);
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
	 * Set the survivor fraction.
	 * @param survivorFraction The survivor fraction.
	 * @throws NullPointerException if the survivor fraction is null.
	 */
	public void setSurvivorFraction(final Probability survivorFraction) {
		notNull(survivorFraction, "Survivor fraction");
		_survivorFraction = survivorFraction;
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
	 * Set the (initial) population in form of a list of phenootypes.
	 * 
	 * @param population The list of phenotypes to set. The population size is set to
	 * 	  <code>phenotype.size()</code>.
	 * @throws NullPointerException if the population is null.
	 * @throws IllegalArgumentException it the population size is smaller than
	 * 		one.
	 */
	public void setPopulation(final List<Phenotype<G, ?>> population) {
		notNull(population, "Population");
		if (population.size() < 1) {
			throw new IllegalArgumentException(
				"Population size must be greater than zero, but was " +
				population.size() + ". "
			);
		}
		
		_population.clear();
		for (Phenotype<G, ?> phenotype : population) {
			_population.add(Phenotype.valueOf(
				phenotype.getGenotype(), _fitnessFunction, _generation
			));
		}
		_populationSize = population.size();
	}
	
	/**
	 * Set the (initial) population in form of a list of genotypes.
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
				genotype, _fitnessFunction, _generation
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
	
	public Statistic<G, C> getBestStatistic() {
		return _bestStatistic;
	}
	
	/**
	 * Set the statistic calculator for this genetic algorithm instance.
	 * 
	 * @param calculator the new statistic calculator.
	 * @throws NullPointerException if the given {@code calculator} is {@code null}.
	 */
	public void setStatisticCalculator(final StatisticCalculator<G, C> calculator) {
		notNull(calculator, "Statistic calculator");
		_calculator = calculator;
	}
	
	/**
	 * Return the current statistic calculator.
	 * 
	 * @return the current statistic calculator.
	 */
	public StatisticCalculator<G, C> getStatisticCalculator() {
		return _calculator;
	}
	
	/**
	 * Return the execution time elapsed so far.
	 * 
	 * @return the execution time.
	 */
	public Measurable<Duration> getExecutionTime() {
		return Measure.valueOf(_stopTime - _startTime, SI.MILLI(SI.SECOND));
	}
	
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(String.format(
			"%4d: (best) %s", _generation, getStatistic().getBestPhenotype()
		));
		return out.toString();
	}
	
	/**
	 * Create a new genetic algorithm.
	 * 
	 * @param genotypeFactory the genotyp factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @param fitnessScaler the fitness scaler this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <SG extends Gene<?>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final GenotypeFactory<SG> genotypeFactory, 
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
	 * @param genotypeFactory the genotyp factory this GA is working with.
	 * @param fitnessFunction the fitness function this GA is using.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <SG extends Gene<?>, SC extends Comparable<SC>>
	GeneticAlgorithm<SG, SC> valueOf(
		final GenotypeFactory<SG> genotypeFactory, 
		final FitnessFunction<SG, SC> fitnessFunction
	) 
	{
		return new GeneticAlgorithm<SG, SC>(genotypeFactory, fitnessFunction);
	}
}






