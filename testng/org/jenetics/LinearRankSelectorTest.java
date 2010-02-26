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

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class LinearRankSelectorTest extends ProbabilitySelectorTest {

	private static class FF implements FitnessFunction<Float64Gene, Float64> {
		private static final long serialVersionUID = -5717330505575904303L;

		@Override
		public Float64 evaluate(final Genotype<Float64Gene> genotype) {
			return genotype.getGene().getAllele();
		}
	}
	
	
	@Test
	public void probabilities() {
		final FF ff = new FF();
		
		final Population<Float64Gene, Float64> population = new Population<Float64Gene, Float64>(100);
		for (int i = 0; i < 100; ++i) {
			population.add(Phenotype.valueOf(
					Genotype.valueOf(new Float64Chromosome(Float64Gene.valueOf(i, 0, 1000))),
					ff, 
					12
				));
		}
		
		final LinearRankSelector<Float64Gene, Float64> selector = new LinearRankSelector<Float64Gene, Float64>();
		final double[] props = selector.probabilities(population, 23);
		
		assertSortedDescending(population);
		assertSortedDescending(props);
		Assert.assertEquals(sum(props), 1.0, 0.000001);
		
		double diff = props[0] - props[1];
		for (int i = 2; i < props.length; ++i) {
			Assert.assertEquals(props[i - 1] - props[i], diff, 0.000001);
		}
	}


	
}




