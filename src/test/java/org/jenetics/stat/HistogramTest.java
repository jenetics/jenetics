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
package org.jenetics.stat;

import static org.jenetics.util.ArrayUtils.toDouble;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class HistogramTest {

	@Test
	public void histogramIndex() {
		final Random random = new Random();
		double[] parts = new double[10000];
		for (int i = 0; i < parts.length; ++i) {
			parts[i] = i;
		}
		
		Histogram<Double> histogram = Histogram.valueOf(toDouble(parts));
		Double[] classes = histogram.getClasses();
		for (int i = 0; i < 1000; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}
		
		
		// Performance tests.
//		final int runs = 10000000;
//		long start = System.nanoTime();
//		for (int i = 0; i < runs; ++i) {
//			final Double value = random.nextDouble()*(parts.length + 1);
//			histogram.index(value);
//		}
//		long end = System.nanoTime();
//		System.out.println("Index Time: " + (end - start)/1000000000.0);
//		
//		start = System.nanoTime();
//		for (int i = 0; i < runs; ++i) {
//			final Double value = random.nextDouble()*(parts.length + 1);
//			linearindex(classes, value);
//		}
//		end = System.nanoTime();
//		System.out.println("Linear Index Time: " + (end - start)/1000000000.0);
		
		parts = new double[]{1};
		histogram = Histogram.valueOf(toDouble(parts));
		classes = histogram.getClasses();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}
		
		parts = new double[]{1, 2};
		histogram = Histogram.valueOf(toDouble(parts));
		classes = histogram.getClasses();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}
		
		parts = new double[]{1, 2, 3};
		histogram = Histogram.valueOf(toDouble(parts));
		classes = histogram.getClasses();
		for (int i = 0; i < 10; ++i) {
			final Double value = random.nextDouble()*(parts.length + 1);
			Assert.assertEquals(histogram.index(value), linearindex(classes, value));
		}
	}
	
	// The 'brute force' variante to test the binsearch one.
	private static <C extends Comparable<C>> int linearindex(final C[] classes, final C value) {
		int index = classes.length;
		for (int i = 0; i < classes.length && index == classes.length; ++i) {
			if (value.compareTo(classes[i]) < 0) {
				index = i;
			}
		}
		return index;
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void histogramEmptyClasses() {
		Histogram.valueOf(toDouble());
	}
	
	@Test
	public void histogram() {
		final Random random = new Random();
		final Histogram<Double> histogram = Histogram.valueOf(toDouble(1, 2, 3, 4, 5));
		
		for (int i = 0; i < 600000; ++i) {
			histogram.accumulate(random.nextDouble()*6);
		}
		Assert.assertEquals(histogram.getSamples(), 600000);
		
		final long[] hist = histogram.getHistogram();
		for (int i = 0; i < hist.length; ++i) {
			Assert.assertEquals((double)hist[i], 100000.0, 1000.0);
		}
	}
	
}
