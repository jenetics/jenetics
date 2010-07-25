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

import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.BitUtils;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StatisticsTest {

	//@Test
	public void equals() {
		double a = 123.0;
		double b = a;
		
		assert Statistics.equals(a, b, 0);
		
		b = Math.nextUp(a);
		assert a != b;
		assert Statistics.equals(a, b, 1);
		assert Statistics.equals(a, b, 2);
		assert !Statistics.equals(a, b, 0);
		
		b = Math.nextUp(a);
		b = Math.nextUp(b);
		assert a != b;
		assert !Statistics.equals(a, b, 0);
		assert !Statistics.equals(a, b, 1);
		assert Statistics.equals(a, b, 2);
		
		a = Math.nextAfter(0.0, Double.POSITIVE_INFINITY);
		b = Math.nextAfter(0.0, Double.NEGATIVE_INFINITY);
		b = Math.nextAfter(b, Double.NEGATIVE_INFINITY);
		b = Math.nextAfter(b, Double.NEGATIVE_INFINITY);
		assert a != b;
		assert !Statistics.equals(a, b, 0);
		assert !Statistics.equals(a, b, 1);
		assert !Statistics.equals(a, b, 3);
		assert Statistics.equals(a, b, 4);
		
		a = 0.0;
		for (int i = 0; i < 10; ++i) {
			a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
		}
		
		for (int i = 0; i < 19; ++i) {
			a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
			Reporter.log(
				a + "\t" + 
				BitUtils.ulpPosition(a) + "\t" + 
				BitUtils.ulpDistance(0.0, a)
			);
		}
	}
	
	private static Population<Float64Gene, Float64> newPopulation(final int size) {
		Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>(size);
		FitnessFunction<Float64Gene, Float64> ff = new FitnessFunction<Float64Gene, Float64>() {
			private static final long serialVersionUID = -2089185336380721853L;

			@Override
			public Float64 evaluate(Genotype<Float64Gene> genotype) {
				return genotype.getChromosome().getGene().getAllele();
			}
		};
		
		for (int i = 1; i <= size; ++i) {
			Float64Gene gene = Float64Gene.valueOf(i, 0, Integer.MAX_VALUE);
			Float64Chromosome chromosome = new Float64Chromosome(gene);
			Genotype<Float64Gene> gt = Genotype.valueOf(chromosome);
			Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(gt, ff, i);
			
			population.add(pt);
		}
		
		return population;
	}
	
	static final double EPSILON = 0.00000001;
	
	@Test
	public void calculation() {
		int size = 2;
		Population<Float64Gene, Float64> population = newPopulation(size);
		Statistics.Calculator<Float64Gene, Float64> calculator = new Statistics.Calculator<Float64Gene, Float64>();
		
		Statistics<Float64Gene, Float64> statistics = calculator.evaluate(population, size + 1, Optimize.MAXIMUM);
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
		Statistics.Calculator<Float64Gene, Float64> calculator = new Statistics.Calculator<Float64Gene, Float64>();
		
		Statistics<Float64Gene, Float64> statistics = calculator.evaluate(population, size + 1, Optimize.MAXIMUM);
		Assert.assertEquals(statistics.getSamples(), 10);
		Assert.assertEquals(statistics.getAgeMean(), 5.5, EPSILON);
		Assert.assertEquals(statistics.getAgeVariance(), 9.1666666666666, EPSILON);
		Assert.assertEquals(statistics.getBestFitness().doubleValue(), 10.0, EPSILON);
		Assert.assertEquals(statistics.getWorstFitness().doubleValue(), 1.0, EPSILON);
		Assert.assertEquals(statistics.getBestPhenotype().getFitness().doubleValue(), 10.0, EPSILON);
		Assert.assertEquals(statistics.getWorstPhenotype().getFitness().doubleValue(), 1.0, EPSILON);
		
		Reporter.log(statistics.toString());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void serialize() throws XMLStreamException {
		SerializeUtils.testSerialization(new Statistics(123, null, null, 0, 0, 0));
	}

}





