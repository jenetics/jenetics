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
package org.jenetics.internal.util;

import static org.jenetics.internal.util.arrays.indexes;
import static org.jenetics.internal.util.arrays.revert;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-11 $</em>
 */
public class IndexSorterTest {

	@Test(dataProvider = "sorters")
	public void sortRandomValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();

		final int[] indexes = sorter.sort(values, indexes(values.length));

		final double[] sorted = values.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(values[indexes[i]], sorted[i]);
		}
	}

	@Test(dataProvider = "sorters")
	public void sortAscSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);

		final int[] indexes = sorter.sort(values, indexes(values.length));

		final double[] sorted = values.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(values[indexes[i]], sorted[i]);
		}
	}

	@Test(dataProvider = "sorters")
	public void sortDescSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);
		revert(values);

		final int[] indexes = sorter.sort(values, indexes(values.length));

		final double[] sorted = values.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(values[indexes[i]], sorted[i]);
		}
	}

	@DataProvider(name = "sorters")
	public Object[][] sorters() {
		return new Object[][] {
			{new HeapSorter(), 1},
			{new HeapSorter(), 2},
			{new HeapSorter(), 3},
			{new HeapSorter(), 5},
			{new HeapSorter(), 11},
			{new HeapSorter(), 1000},
			{new HeapSorter(), 250_000}
		};
	}

}
