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

import static org.jenetics.internal.util.IndexSorter.indexes;
import static org.jenetics.internal.util.array.revert;
import static org.jenetics.internal.util.array.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.jenetics.internal.math.random;
import org.jenetics.util.LCG64ShiftRandom;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2014-08-26 $</em>
 */
public class IndexSorterTest {

    private static int[] sorted(final double[] array) {
        final long seed = random.seed();
        final Random random = new Random();
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextDouble();
        }

        Arrays.sort(array);
        final int[] indexes = indexes(array.length);

        random.setSeed(seed);
        shuffle(array, random);

        random.setSeed(seed);
        shuffle(indexes, random);

        return indexes;
    }

    private static double[] sort(final IndexSorter sorter, final double[] values) {
        final int[] indexes = sorter.sort(values, indexes(values.length));

        final double[] result = new double[values.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = values[indexes[i]];
        }
        return result;
    }

    @Test(dataProvider = "specialArray")
    public void sortSpecial(final double[] values) {
        final double[] indexSortedValues = sort(new HeapSorter(), values);
        Arrays.sort(values);

        final String actual = Arrays.toString(indexSortedValues);
        final String expected = Arrays.toString(values);
        System.out.println(actual + ":" + expected);

        Assert.assertEquals(actual, expected);
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
    public void sortRandomValues(final IndexSorter sorter, final Integer size) {
        final double[] values = new Random().doubles(size).toArray();

        final double[] indexSortedValues = sort(sorter, values);
        Arrays.sort(values);

        final String actual = Arrays.toString(indexSortedValues);
        final String expected = Arrays.toString(values);
        Assert.assertEquals(actual, expected);
    }

	@Test(dataProvider = "sorters")
	public void sortAscSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);

        final double[] indexSortedValues = sort(sorter, values);

        final String actual = Arrays.toString(indexSortedValues);
        final String expected = Arrays.toString(values);
        Assert.assertEquals(actual, expected);
	}

	@Test(dataProvider = "sorters")
	public void sortDescSortedValues(final IndexSorter sorter, final Integer size) {
		final double[] values = new Random().doubles(size).toArray();
		Arrays.sort(values);
		revert(values);

        final double[] indexSortedValues = sort(sorter, values);
        Arrays.sort(values);

        final String actual = Arrays.toString(indexSortedValues);
        final String expected = Arrays.toString(values);
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
