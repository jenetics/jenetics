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
package io.jenetics.internal.util;

import static io.jenetics.internal.util.IndexSorter.indexes;
import static io.jenetics.internal.util.array.revert;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.Seq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class IndexSorterTest {

	private static Seq<Integer> indexSort(final IndexSorter sorter, final Seq<Integer> values) {
		final int[] indexes = sorter.sort(values, indexes(values.length()));

		final MSeq<Integer> result = MSeq.ofLength(values.length());
		for (int i = 0; i < result.length(); ++i) {
			result.set(i, values.get(indexes[i]));
		}
		return result;
	}

	private static Integer[] indexSort(final IndexSorter sorter, final Integer[] values) {
		final int[] indexes = sorter.sort(values, indexes(values.length));

		final Integer[] result = new Integer[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = values[indexes[i]];
		}
		return result;
	}

	private static Integer[] arraySort(final Integer[] values) {
		final Integer[] result = values.clone();
		Arrays.sort(result);
		revert(result);
		return result;
	}

	private static int[] indexSort(final IndexSorter sorter, final int[] values) {
		final int[] indexes = sorter.sort(values, indexes(values.length));

		final int[] result = new int[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = values[indexes[i]];
		}
		return result;
	}

	private static int[] arraySort(final int[] values) {
		final int[] result = values.clone();
		Arrays.sort(result);
		revert(result);
		return result;
	}

	private static long[] indexSort(final IndexSorter sorter, final long[] values) {
		final int[] indexes = sorter.sort(values, indexes(values.length));

		final long[] result = new long[values.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = values[indexes[i]];
		}
		return result;
	}

	private static long[] arraySort(final long[] values) {
		final long[] result = values.clone();
		Arrays.sort(result);
		revert(result);
		return result;
	}

    private static double[] indexSort(final IndexSorter sorter, final double[] values) {
        final int[] indexes = sorter.sort(values, indexes(values.length));

        final double[] result = new double[values.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = values[indexes[i]];
        }
        return result;
    }

    private static double[] arraySort(final double[] values) {
        final double[] result = values.clone();
        Arrays.sort(result);
		revert(result);
        return result;
    }

    @Test(dataProvider = "specialArray")
    public void sortSpecial(final double[] values) {
        final double[] indexHeapSortedValues = indexSort(new HeapSorter(), values);
        final double[] indexInsertionSortedValues2 = indexSort(new InsertionSorter(), values);
		final double[] arraySorted = arraySort(values);

        Assert.assertEquals(indexHeapSortedValues, arraySorted);
		Assert.assertEquals(indexInsertionSortedValues2, arraySorted);
    }

    @DataProvider(name = "specialArray")
    public Object[][] specialArray() {
        return new Object[][] {
            {new double[]{0.0, 0.0, 0.0, 0.0, 1.0}},
            {new double[]{1.0, 0.0, 0.0, 0.0, 0.0}},
            {new double[]{1.0, 0.0, 0.0, 0.0, 1.0}},
            {new double[]{2.0, 0.0, 1.0, 1.0, 1.0}}
        };
    }

	@Test(dataProvider = "sorters")
	public void sortRandomSeqValues(final IndexSorter sorter, final Integer size) {
		final Integer[] values = new Random().ints(size).boxed().toArray(Integer[]::new);

		final Seq<Integer> actual = indexSort(sorter, ISeq.of(values));
		final Integer[] expected = arraySort(values);
		Assert.assertEquals(actual.toArray(new Integer[0]), expected);
	}

	@Test(dataProvider = "sorters")
	public void sortRandomArrayValues(final IndexSorter sorter, final Integer size) {
		final Integer[] values = new Random().ints(size).boxed().toArray(Integer[]::new);

		final Integer[] actual = indexSort(sorter, values);
		final Integer[] expected = arraySort(values);
		Assert.assertEquals(actual, expected);
	}

    @Test(dataProvider = "sorters")
    public void sortRandomIntValues(final IndexSorter sorter, final Integer size) {
        final int[] values = new Random().ints(size).toArray();

        final int[] actual = indexSort(sorter, values);
        final int[] expected = arraySort(values);
        Assert.assertEquals(actual, expected);
    }

	@Test(dataProvider = "sorters")
	public void sortRandomLongValues(final IndexSorter sorter, final Integer size) {
		final long[] values = new Random().longs(size).toArray();

		final long[] actual = indexSort(sorter, values);
		final long[] expected = arraySort(values);
		Assert.assertEquals(actual, expected);
	}

	@Test(dataProvider = "sorters")
	public void sortRandomValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();

		final double[] actual = indexSort(sorter, values);
		final double[] expected = arraySort(values);
		Assert.assertEquals(actual, expected);
	}

	@Test(dataProvider = "sorters")
	public void sortAscSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);

        final double[] actual = indexSort(sorter, values);
        final double[] expected = arraySort(values);
        Assert.assertEquals(actual, expected);
	}

	@Test(dataProvider = "sorters")
	public void sortDescSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);
		revert(values);

        final double[] actual = indexSort(sorter, values);
        final double[] expected = arraySort(values);
        Assert.assertEquals(actual, expected);
	}

	@DataProvider(name = "sorters")
	public Object[][] sorters() {
		return new Object[][] {
            {new InsertionSorter(), 1},
            {new InsertionSorter(), 2},
            {new InsertionSorter(), 3},
            {new InsertionSorter(), 5},
            {new InsertionSorter(), 33},
			{new HeapSorter(), 1},
			{new HeapSorter(), 2},
			{new HeapSorter(), 3},
			{new HeapSorter(), 5},
			{new HeapSorter(), 11},
			{new HeapSorter(), 1000},
			{new HeapSorter(), 10_000}
		};
	}

}
