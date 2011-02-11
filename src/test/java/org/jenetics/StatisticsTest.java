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

import java.util.Random;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.ObjectTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StatisticsTest extends ObjectTester<Statistics<Float64Gene, Float64>> {

	final Factory<Statistics<Float64Gene, Float64>>
	_factory = new Factory<Statistics<Float64Gene,Float64>>() {
		private final Phenotype<Float64Gene, Float64> _best = TestUtils.newFloat64Phenotype();
		private final Phenotype<Float64Gene, Float64> _worst = _best;
		
		@Override
		public Statistics<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			final int generation = random.nextInt(1000);

			final int samples = random.nextInt(1000);
			final double ageMean = random.nextDouble();
			final double ageVariance = random.nextDouble();
			final int killed = random.nextInt(1000);
			final int invalid = random.nextInt(10000);
			
			return new Statistics<Float64Gene, Float64>(
					Optimize.MAXIMUM, generation, _best, _worst, 
					samples, ageMean, ageVariance, killed, invalid
				);
		}
	};
	@Override
	protected Factory<Statistics<Float64Gene, Float64>> getFactory() {
		return _factory;
	}
	
	private static Population<Float64Gene, Float64> newPopulation(final int size) {
		Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>(size);
		
		for (int i = 1; i <= size; ++i) {
			Float64Gene gene = Float64Gene.valueOf(i, 0, Integer.MAX_VALUE);
			Float64Chromosome chromosome = new Float64Chromosome(gene);
			Genotype<Float64Gene> gt = Genotype.valueOf(chromosome);
			Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(gt, TestUtils.FF, i);
			
			population.add(pt);
		}
		
		return population;
	}
	
	static final double EPSILON = 0.00000001;
	
	@Test
	public void calculation() {
		int size = 2;
		final Population<Float64Gene, Float64> population = newPopulation(size);
		final Statistics.Calculator<Float64Gene, Float64> 
		calculator = new Statistics.Calculator<Float64Gene, Float64>();
		
		final Statistics<Float64Gene, Float64> statistics = 
			calculator.evaluate(population, size + 1, Optimize.MAXIMUM).build();
		Assert.assertEquals(statistics.getSamples(), 2);
		Assert.assertEquals(statistics.getAgeMean(), 1.5, EPSILON);
		Assert.assertEquals(statistics.getAgeVariance(), 0.5, EPSILON);
		Assert.assertEquals(statistics.getBestFitness().doubleValue(), 2.0, EPSILON);
		Assert.assertEquals(statistics.getWorstFitness().doubleValue(), 1.0, EPSILON);
		Assert.assertEquals(statistics.getBestPhenotype().getFitness().doubleValue(), 2.0, EPSILON);
		Assert.assertEquals(statistics.getWorstPhenotype().getFitness().doubleValue(), 1.0, EPSILON);		
	}
	
	@Test
	public void calculation2() {
		int size = 10;
		Population<Float64Gene, Float64> population = newPopulation(size);
		Statistics.Calculator<Float64Gene, Float64> calculator = 
			new Statistics.Calculator<Float64Gene, Float64>();
		
		Statistics<Float64Gene, Float64> statistics = 
			calculator.evaluate(population, size + 1, Optimize.MAXIMUM).build();
		Assert.assertEquals(statistics.getSamples(), 10);
		Assert.assertEquals(statistics.getAgeMean(), 5.5, EPSILON);
		Assert.assertEquals(statistics.getAgeVariance(), 9.1666666666666, EPSILON);
		Assert.assertEquals(statistics.getBestFitness().doubleValue(), 10.0, EPSILON);
		Assert.assertEquals(statistics.getWorstFitness().doubleValue(), 1.0, EPSILON);
		Assert.assertEquals(statistics.getBestPhenotype().getFitness().doubleValue(), 10.0, EPSILON);
		Assert.assertEquals(statistics.getWorstPhenotype().getFitness().doubleValue(), 1.0, EPSILON);
	}

}





