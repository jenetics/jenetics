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

import org.jenetics.stat.Histogram;
import org.jenetics.util.ArrayUtils;
import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class BoltzmannSelectorTest extends ProbabilitySelectorTest {

	private static class FF implements FitnessFunction<Float64Gene, Float64> {
		private static final long serialVersionUID = -5717330505575904303L;

		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			return genotype.getGene().getAllele();
		}
	}
	
	private static Population<Float64Gene, Float64> population(final int size) {
		final FF ff = new FF();
		
		final Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>(size);
		for (int i = 0; i < size; ++i) {
			population.add(Phenotype.valueOf(
					Genotype.valueOf(new Float64Chromosome(Float64Gene.valueOf(i, 0, size))),
					ff, 
					12
				));
		}
		ArrayUtils.shuffle(population, new Random(System.currentTimeMillis()));
		
		return population;
	}
	
	@Test
	public void probabilities() {
		final Population<Float64Gene, Float64> population = population(100);
		
		BoltzmannSelector<Float64Gene, Float64> selector = new BoltzmannSelector<Float64Gene, Float64>();
		double[] probs = selector.probabilities(population, 23);
		Assert.assertEquals(probs.length, population.size());
		Assert.assertEquals(sum(probs), 1.0, 0.000001);
		assertPositive(probs);
		
		ArrayUtils.shuffle(population, new Random(System.currentTimeMillis()));
		selector = new BoltzmannSelector<Float64Gene, Float64>(0.234234);
		probs = selector.probabilities(population, 23);
		Assert.assertEquals(probs.length, population.size());
		Assert.assertEquals(sum(probs), 1.0, 0.000001);
		assertPositive(probs);
		
		ArrayUtils.shuffle(population, new Random(System.currentTimeMillis()));
		selector = new BoltzmannSelector<Float64Gene, Float64>(1.878);
		probs = selector.probabilities(population, 23);
		Assert.assertEquals(probs.length, population.size());
		Assert.assertEquals(sum(probs), 1.0, 0.000001);
		assertPositive(probs);
	}
	
	@Test
	public void select() {
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1000.0, 20);
		final BoltzmannSelector<Float64Gene, Float64> 
		selector = new BoltzmannSelector<Float64Gene, Float64>(2);
		final Population<Float64Gene, Float64> population = population(1000);
		
		for (int i = 0; i < 1000; ++i) {
			final Population<Float64Gene, Float64> selected = selector.select(
					population, 10, Optimize.MAXIMUM
				);
			for (Phenotype<Float64Gene, Float64> pt : selected) {
				histogram.accumulate(pt.getFitness().doubleValue());
			}
		}
		
		System.out.println(histogram);
	}
	
}







