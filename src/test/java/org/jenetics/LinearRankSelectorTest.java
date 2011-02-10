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

import org.jenetics.util.ArrayUtils;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class LinearRankSelectorTest	
	extends ProbabilitySelectorTest<LinearRankSelector<Float64Gene, Float64>> 
{
	
	@Override
	protected Factory<LinearRankSelector<Float64Gene, Float64>> getFactory() {
		return SelectorFactories.LinearRankSelector;
	}	
	
	@Test
	public void probabilities() {
		final Population<Float64Gene, Float64> population = TestUtils.newFloat64Population(100);
		ArrayUtils.shuffle(population, new Random(System.currentTimeMillis()));
		
		final LinearRankSelector<Float64Gene, Float64> selector = new LinearRankSelector<Float64Gene, Float64>();
		final double[] probs = selector.probabilities(population, 23);
		Assert.assertEquals(probs.length, population.size());
		
		assertSortedDescending(population);
		assertSortedDescending(probs);
		assertPositive(probs);
		Assert.assertEquals(sum(probs), 1.0, 0.000001);
		
		double diff = probs[0] - probs[1];
		for (int i = 2; i < probs.length; ++i) {
			Assert.assertEquals(probs[i - 1] - probs[i], diff, 0.000001);
		}
	}


	
}




