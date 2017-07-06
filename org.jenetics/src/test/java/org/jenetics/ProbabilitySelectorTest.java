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
package org.jenetics;

import static org.jenetics.internal.math.arithmetic.normalize;
import static org.jenetics.internal.util.array.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class ProbabilitySelectorTest {

	private static double[] array(final int size, final Random random) {
		final double[] array = new double[size];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}

		shuffle(array, random);
		return array;
	}

	@DataProvider(name = "arraySize")
	public Object[][] arraySize() {
		return new Object[][]{
			{6}, {100}, {1000}, {10_000}, {100_000}, {500_000}
		};
	}

	@Test(dataProvider = "arraySize")
	public void revert(final Integer size) {
		final double[] probabilities = array(size, new Random());
		final double[] reverted = ProbabilitySelector.sortAndRevert(probabilities);

		//System.out.println(Arrays.toString(probabilities));
		//System.out.println(Arrays.toString(reverted));

		for (int i = 0; i < size; ++i) {
			Assert.assertEquals(
				probabilities[i] + reverted[i],
				size - 1.0
			);
		}
	}

	@Test(dataProvider = "arraySize")
	public void revertSortedArray(final Integer size) {
		final double[] values = array(size, new Random());
		Arrays.sort(values);

		final double[] reverted = ProbabilitySelector.sortAndRevert(values);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(reverted[i], (double)(values.length - i - 1));
		}
	}

	@Test(dataProvider = "arraySize")
	public void indexOfSerialEqualBinary(final Integer size) {
		final double[] probabilities = array(size, new Random(12));
		normalize(probabilities);
		ProbabilitySelector.incremental(probabilities);

		Assert.assertEquals(
			ProbabilitySelector.indexOfSerial(probabilities, 0.5),
			ProbabilitySelector.indexOfBinary(probabilities, 0.5)
		);
	}

}
