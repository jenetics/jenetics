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
public class IndexSorterTest {


	@Test(dataProvider = "sorters")
	public void sort(final IndexSorter sorter, final int size) {
		final int[] array = new Random().ints(1000).toArray();

		final int[] indexes = sorter.sort(array);
		Assert.assertEquals(sorted(array, indexes), expected(array));
	}

	private static int[] sorted(final int[] array, final int[] indexes) {
		final int[] result = array.clone();
		for (int i = 0; i < array.length; ++i) {
			result[i] = array[indexes[i]];
		}
		return result;
	}

	private static int[] expected(final int[] array) {
		final int[] result = array.clone();
		Arrays.sort(result);
		return result;
	}

	@DataProvider(name = "sorters")
	public Object[][] sorters() {
		return new Object[][] {
			{InsertionIndexSorter.INSTANCE, 1},
			{InsertionIndexSorter.INSTANCE, 2},
			{InsertionIndexSorter.INSTANCE, 3},
			{InsertionIndexSorter.INSTANCE, 5},
			{InsertionIndexSorter.INSTANCE, 33},
			{InsertionIndexSorter.INSTANCE, 1_000},
			{InsertionIndexSorter.INSTANCE, 10_000},
			{HeapIndexSorter.INSTANCE, 1},
			{HeapIndexSorter.INSTANCE, 2},
			{HeapIndexSorter.INSTANCE, 3},
			{HeapIndexSorter.INSTANCE, 5},
			{HeapIndexSorter.INSTANCE, 11},
			{HeapIndexSorter.INSTANCE, 1_000},
			{HeapIndexSorter.INSTANCE, 10_000}
		};
	}

}
