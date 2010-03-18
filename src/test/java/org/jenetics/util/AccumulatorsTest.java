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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.jenetics.util.Accumulators.Mean;
import org.jenetics.util.Accumulators.Quantile;
import org.jenetics.util.Accumulators.Variance;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id: AccumulatorsTest.java 409 2010-03-09 18:34:42Z fwilhelm $
 */
public class AccumulatorsTest {
	private Double[][] _values = null;

	@BeforeTest
	public void setup() throws Exception {
		final InputStream in = getClass().getResourceAsStream("statistic-moments.txt");		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		final List<String> lines = new java.util.ArrayList<String>(1000);
		String line = null;
		while ((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		_values = new Double[lines.size()][10];
		
		for (int i = 0; i < lines.size(); ++i) {
			final String[] parts = lines.get(i).split("\\s");
			
			for (int j = 0; j < parts.length; ++j) {
				_values[i][j] = Double.valueOf(parts[j]);
			}
		}
	}
	
	@Test
	public void quantil() {
		final Quantile<Integer> quantile = new Quantile<Integer>(0.5);
		for (int i = 0; i < 1000; ++i) {
			quantile.accumulate(i);
//			System.out.println(quantile.getQuantile() + "--" + Math.floor(i/2.0));
			Assert.assertEquals(quantile.getQuantile(), Math.floor(i/2.0), 1.0);
		}
	}
	
	@Test
	public void mean() {
		final Mean<Double> moment = new Mean<Double>();
		
		for (int i = 0; i < _values.length; ++i) {
			moment.accumulate(_values[i][0]);
			Assert.assertEquals(moment.getMean(), _values[i][1]);
		}
	}
	
	@Test
	public void variance() {
		final Variance<Double> moment = new Variance<Double>();
		
		for (int i = 0; i < _values.length; ++i) {
			moment.accumulate(_values[i][0]);
			Assert.assertEquals(moment.getMean(), _values[i][1]);
			Assert.assertEquals(moment.getVariance(), _values[i][6], 0.0000001);
		}
	}
	
	@Test
	public void min() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		
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








