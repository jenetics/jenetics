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

import java.util.Iterator;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.Statistics.Calculator;
import org.jenetics.stat.Variance;
import org.jenetics.util.Accumulators;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class StatisticsCalculatorTest {

	public Calculator<Float64Gene, Float64> newCalculator() {
		return new Calculator<Float64Gene, Float64>();
	}
	
	public Iterable<Phenotype<Float64Gene, Float64>> population(final int size) {
		return new Iterable<Phenotype<Float64Gene,Float64>>() {
			@Override
			public Iterator<Phenotype<Float64Gene, Float64>> iterator() {
				return new Iterator<Phenotype<Float64Gene,Float64>>() {
					private final Float64 MIN = Float64.valueOf(0);
					private final Float64 MAX = Float64.valueOf(size);
					
					private int _pos = -1;
					
					@Override
					public boolean hasNext() {
						return _pos < size - 1;
					}
					
					@Override
					public Phenotype<Float64Gene, Float64> next() {
						++_pos;
						final Float64Gene gene = Float64Gene.valueOf(
									Float64.valueOf(_pos), MIN, MAX
								);
						return Phenotype.valueOf(
								Genotype.valueOf(new Float64Chromosome(gene)), 
								TestUtils.FF, 0
							);
					}
					
					@Override
					public void remove() {
					}
				};
			}
		};
	}
	
	@Test(dataProvider = "size_gen")
	public void evaluate(final Integer size, final Integer gen) {
		final Calculator<Float64Gene, Float64> calculator = newCalculator();
		final Statistics.Builder<Float64Gene, Float64> 
		builder = calculator.evaluate(population(size), gen, Optimize.MAXIMUM);
		final Statistics<Float64Gene, Float64> statistics = builder.build();

		final Variance<Integer> ageVariance = new Variance<Integer>();
		Accumulators.accumulate(population(size), ageVariance.adapt(Phenotype.Age(gen)));
		
		Assert.assertEquals(statistics.getAgeMean(), ageVariance.getMean());
		Assert.assertEquals(statistics.getAgeVariance(), ageVariance.getVariance());
		Assert.assertEquals(statistics.getSamples(), size.intValue());
		Assert.assertEquals(statistics.getGeneration(), gen.intValue());
		Assert.assertEquals(statistics.getBestFitness(), Float64.valueOf(size - 1));
		Assert.assertEquals(statistics.getWorstFitness(), Float64.ZERO);
	}
	
	@DataProvider(name = "size_gen")
	public Object[][] sizeGen() {
		return new Object[][] {
				{1000, 23},
				{5000, 100},
				{1000, 312}
		};
	}
	
}






