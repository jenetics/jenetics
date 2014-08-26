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

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.IndexSorter;
import org.jenetics.internal.util.IndexedArray;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-26 $</em>
 */
public class ProbabilitySelectorTest {

	private static double[] array(final int size, final Random random) {
		final double[] array = new double[size];
		for (int i = 0; i < array.length; ++i) {
			array[i] = random.nextInt(100);
		}

		return array;
	}

	@Test(dataProvider = "arraySize")
	public void sort(final Integer size) {
		final double[] probabilities = array(size, new Random());

		final int[] indexes = IndexSorter.sort(probabilities);
		final double[] sorted = probabilities.clone();
		Arrays.sort(sorted);

		for (int i = 0; i < indexes.length; ++i) {
			Assert.assertEquals(probabilities[indexes[i]], sorted[i]);
		}
	}

	@DataProvider(name = "arraySize")
	public Object[][] arraySize() {
		return new Object[][]{
			{5}
			//{100}, {1000}, {10_000}, {100_000}, {500_000}
		};
	}

	@Test(dataProvider = "arraySize")
	public void revert(final Integer size) {
		final double[] probabilities = array(size, new Random(12));
		normalize(probabilities);
		final int[] indexes = IndexSorter.sort(probabilities);

		final double[] reverted = ProbabilitySelector.sortAndRevert(probabilities.clone());
		final int[] invertedIndexes = IndexSorter.sort(reverted);

		IndexedArray a1 = new IndexedArray(reverted);
		IndexedArray a2 = new IndexedArray(probabilities, invertedIndexes);

		System.out.println(a1);
		System.out.println(a2);

		for (int i = 0; i < indexes.length; ++i) {
			Assert.assertEquals(invertedIndexes[i], indexes[indexes.length - i - 1]);
		}
	}

	@Test(dataProvider = "arraySize")
	public void revertSortedArray(final Integer size) {
		final double[] values = new double[100];
		for (int i = 0; i < values.length; ++i) {
			values[i] = i;
		}

		final double[] reverted = ProbabilitySelector.sortAndRevert(values);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(reverted[i], (double)(values.length - i - 1));
		}
	}

	@Test
	public void revertSingularArray() {
		final double[] values = new double[9];
		values[0] = 1;
		System.out.println(Arrays.toString(values));

		double[] reverted = ProbabilitySelector.sortAndRevert(values.clone());
		System.out.println(Arrays.toString(reverted));

		reverted = ProbabilitySelector.sortAndRevert(reverted);
		System.out.println(Arrays.toString(reverted));
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
