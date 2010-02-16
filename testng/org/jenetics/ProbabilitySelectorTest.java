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

import java.util.Arrays;
import java.util.Random;

import org.jenetics.util.Validator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version $Id$
 */
public class ProbabilitySelectorTest {

	@Test
	public void nextIndex() {
		final Random random = new Random();
		
		final double[] props = new double[10];
		double divisor = props.length*(props.length + 1)/2.0;
		for (int i = 0; i < props.length; ++i) {
			props[i] = (i + 1)/divisor;
		}
		randomize(props, random);
		
		double samples = 1000000;
		double[] indices = new double[props.length];
		Arrays.fill(indices, 0);
		
		for (int i = 0; i < samples; ++i) {
			indices[ProbabilitySelector.nextIndex(props, random)] += 1;
		}
		
		for (int i = 0; i < props.length; ++i) {
			indices[i] /= samples;
		}
		
		System.out.println(toString(props) + String.format(": %6f", sum(props)));
		System.out.println(toString(indices) + String.format(": %6f", sum(indices)));
		
		for (int i = 0; i < props.length; ++i) {
			Assert.assertEquals(indices[i], props[i], 0.005);
		}
	}
	
	
	private static String toString(final double[] array) {
		StringBuilder out = new StringBuilder();
		
		out.append("[");
		if (array.length > 0) {
			out.append(String.format("%6f", array[0]));
		}
		for (int i = 1; i < array.length; ++i) {
			out.append(", ");
			out.append(String.format("%6f", array[i]));
		}
		out.append("]");
		
		return out.toString();
	}
	
	private double sum(final double[] array) {
		double sum = 0;
		for (int i = 0; i < array.length; ++i) {
			sum += array[i];
		}
		return sum;
	}
	
	private static void randomize(final double[] array, final Random random) {
		Validator.nonNull(array, "Array");
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}
	}
	
	private static void swap(final double[] array, int i, int j) {
		double temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
}
