/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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

import org.jenetics.util.Accumulator.MinMax;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-03-26 $</em>
 */
public class accumulatorsMinMaxTest
	extends MappedAccumulatorTester<Accumulator.MinMax<Double>>
{

	final Factory<Accumulator.MinMax<Double>> _factory = () -> {
		final Random random = RandomRegistry.getRandom();

		final MinMax<Double> minMax = new MinMax<>();
		for (int i = 0; i < 1000; ++i) {
			minMax.accumulate(random.nextGaussian());
		}

		return minMax;
	};
	@Override
	protected Factory<Accumulator.MinMax<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void minMax() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		shuffle(array);

		final Accumulator.MinMax<Integer> minMax = new Accumulator.MinMax<>();
		Accumulator.accumulate(Arrays.asList(array), minMax);
		Assert.assertEquals(minMax.getMin(), new Integer(0));
		Assert.assertEquals(minMax.getMax(), new Integer(19));
	}

}
