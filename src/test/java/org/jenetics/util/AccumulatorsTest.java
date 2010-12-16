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

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class AccumulatorsTest {
	
	@Test
	public void min() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		ArrayUtils.shuffle(array);
		
		final Accumulators.Min<Integer> min = new Accumulators.Min<Integer>();
		Accumulators.accumulate(Arrays.asList(array), min);
		Assert.assertEquals(min.getMin(), new Integer(0));
	}
	
	@Test
	public void max() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		ArrayUtils.shuffle(array);
		
		final Accumulators.Max<Integer> max = new Accumulators.Max<Integer>();
		Accumulators.accumulate(Arrays.asList(array), max);
		Assert.assertEquals(max.getMax(), new Integer(19));
	}
	
	@Test
	public void minMax() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		ArrayUtils.shuffle(array);
		
		final Accumulators.MinMax<Integer> minMax = new Accumulators.MinMax<Integer>();
		Accumulators.accumulate(Arrays.asList(array), minMax);
		Assert.assertEquals(minMax.getMin(), new Integer(0));
		Assert.assertEquals(minMax.getMax(), new Integer(19));
	}
	
//	@Test
//	public void sum() {
//		final Integer64[] array = new Integer64[20];
//		for (int i = 0; i < array.length; ++i) {
//			array[i] = Integer64.valueOf(i);
//		}
//		
//		final Accumulators.Sum<Integer64> sum = new Accumulators.Sum<Integer64>();
//		Accumulators.accumulate(Arrays.asList(array), sum);
//		Assert.assertEquals(sum.getSum(), Integer64.valueOf((20*19/2)));
//	}
	
}








