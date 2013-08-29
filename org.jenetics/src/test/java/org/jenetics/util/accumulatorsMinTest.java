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

import org.jenetics.util.accumulators.Min;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class accumulatorsMinTest
	extends MappedAccumulatorTester<accumulators.Min<Double>>
{

	final Factory<accumulators.Min<Double>>
	_factory = new Factory<accumulators.Min<Double>>() {
		@Override
		public Min<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Min<Double> min = new Min<>();
			for (int i = 0; i < 1000; ++i) {
				min.accumulate(random.nextGaussian());
			}

			return min;
		}
	};
	@Override
	protected Factory<accumulators.Min<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void min() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		shuffle(array);

		final accumulators.Min<Integer> min = new accumulators.Min<>();
		accumulators.accumulate(Arrays.asList(array), min);
		Assert.assertEquals(min.getMin(), new Integer(0));
	}

}




