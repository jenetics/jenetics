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
package org.jenetics.util;

import static org.jenetics.util.arrays.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.accumulator.MinMax;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class AccumulatorsMinMaxTest 
	extends AbstractAccumulatorTester<accumulator.MinMax<Double>> 
{
	
	final Factory<accumulator.MinMax<Double>> 
	_factory = new Factory<accumulator.MinMax<Double>>() {
		@Override
		public accumulator.MinMax<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();
			
			final MinMax<Double> minMax = new MinMax<Double>();
			for (int i = 0; i < 1000; ++i) {
				minMax.accumulate(random.nextGaussian());
			}
			
			return minMax;
		}
	};
	@Override
	protected Factory<accumulator.MinMax<Double>> getFactory() {
		return _factory;
	}
	
	@Test
	public void minMax() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		shuffle(array);
		
		final accumulator.MinMax<Integer> minMax = new accumulator.MinMax<Integer>();
		accumulator.accumulate(Arrays.asList(array), minMax);
		Assert.assertEquals(minMax.getMin(), new Integer(0));
		Assert.assertEquals(minMax.getMax(), new Integer(19));
	}
	
}
