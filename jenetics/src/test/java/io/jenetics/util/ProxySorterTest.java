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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.util;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ProxySorterTest {

	/* *************************************************************************
	 * Test binary insertion sort.
	 * ************************************************************************/

	@Test(dataProvider = "arrays")
	public void binaryInsertionSortArrays(final int[] array) {
		final int[] indexes = BinaryInsertionSort.sort(
			array, array.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);

		Assert.assertEquals(sorted(array, indexes), expected(array));
	}

	@Test(dataProvider = "arrayLengths")
	public void binaryInsertionSortSize(final int size) {
		final int[] array = new Random().ints(size).toArray();

		final int[] indexes = BinaryInsertionSort.sort(
			array, array.length,
			(a, i, j) -> Integer.compare(a[i], a[j])
		);
		Assert.assertEquals(sorted(array, indexes), expected(array));
	}

	/* *************************************************************************
	 * Test tim sort.
	 * ************************************************************************/

	@Test(dataProvider = "arrays")
	public void timSortArrays(final int[] array) {
		final int[] indexes = ProxySorter.sort(array);
		Assert.assertEquals(sorted(array, indexes), expected(array));
	}

	@Test(dataProvider = "arrayLengths")
	public void timSortArraySize(final int size) {
		final int[] array = new Random().ints(size).toArray();

		final int[] indexes = ProxySorter.sort(array);
		Assert.assertEquals(sorted(array, indexes), expected(array));
	}

	@Test(dataProvider = "arrayRanges")
	public void binaryInsertionSortRange(
		final int size,
		final int from,
		final int to
	) {
		final int[] array = new Random().ints(size).toArray();

		final int[] indexes = ProxySorter.sort(array, from, to);
		Assert.assertEquals(sorted(array, indexes), expected(array, from, to));
	}

	@DataProvider(name = "arrays")
	public Object[][] arrays() {
		return new Object[][] {
			{1, 2, 3, 9, 8, 7, 4, 5, 6},
			{5, 4, 3, 2, 1},
			{1, 2, 3, 4, 56, 45, 34, 65, 34, 9, 8, 7, 6, 5}
		};
	}

	@DataProvider(name = "arrayLengths")
	public Object[][] arrayLengths() {
		return new Object[][] {
			{0},
			{1},
			{2},
			{3},
			{5},
			{11},
			{32},
			{33},
			{1_000},
			{10_000}
		};
	}

	@DataProvider(name = "arrayRanges")
	public Object[][] arrayRanges() {
		return new Object[][] {
			{3, 1, 2},
			{3, 1, 1},
			{5, 0, 2},
			{5, 1, 2},
			{11, 0, 5},
			{11, 5, 9},
			{11, 5, 11},
			{33, 0, 7},
			{33, 7, 25},
			{33, 0, 33},
			{33, 7, 33},
			{1_000, 0, 1_000},
			{1_000, 0, 600},
			{1_000, 500, 600},
			{1_000, 500, 1_000}
		};
	}

	private static int[] sorted(
		final int[] array,
		final int[] indexes
	) {
		final int[] result = new int[indexes.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = array[indexes[i]];
		}
		return result;
	}

	private static int[] expected(final int[] array, final int from, final int to) {
		final int[] result = Arrays.copyOfRange(array, from, to);
		Arrays.sort(result);
		return result;
	}

	private static int[] expected(final int[] array) {
		return expected(array,0, array.length);
	}

}
