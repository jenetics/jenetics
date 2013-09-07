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

import static org.jenetics.util.arrays.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.accumulators.Max;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class accumulatorsMaxTest
	extends MappedAccumulatorTester<accumulators.Max<Double>>
{

	final Factory<accumulators.Max<Double>>
	_factory = new Factory<accumulators.Max<Double>>() {
		@Override
		public Max<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Max<Double> max = new Max<>();
			for (int i = 0; i < 1000; ++i) {
				max.accumulate(random.nextGaussian());
			}

			return max;
		}
	};
	@Override
	protected Factory<Max<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void max() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		shuffle(array);

		final accumulators.Max<Integer> max = new accumulators.Max<>();
		accumulators.accumulate(Arrays.asList(array), max);
		Assert.assertEquals(max.getMax(), new Integer(19));
	}

}
