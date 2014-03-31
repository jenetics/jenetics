/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.util;

import static org.jenetics.util.shuffling.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Accumulator.MinMax;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-12-18 $</em>
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
