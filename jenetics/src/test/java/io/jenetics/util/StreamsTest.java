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

import java.time.Duration;
import java.util.Comparator;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.stat.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StreamsTest {

	@Test(dataProvider = "toSliceBestData")
	public void toSliceMax(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(MinMax.toSliceMax(sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*(i + 1) - 1 );
		}
	}

	@Test(dataProvider = "toSliceBestData")
	public void toSliceMin(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(MinMax.toSliceMin(sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*i);
		}
	}

	@Test(dataProvider = "toSliceBestData")
	public void toSliceBest(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(MinMax.toSliceBest(Comparator.reverseOrder(), sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*i);
		}
	}

	@DataProvider(name = "toSliceBestData")
	public Object[] toSliceBestData() {
		return new Object[][] {
			{10, 1},
			{100, 10},
			{333, 5},
			{1111, 3},
			{10, 100}
		};
	}

	@Test
	public void toTimespanSliceMax() {
		final ISeq<Integer> values = IntStream.range(0, 100).boxed()
			.peek(i -> {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			})
			.flatMap(MinMax.toSliceMax(Duration.ofMillis(10)))
			.collect(ISeq.toISeq());

		Assert.assertTrue(
			values.size() > 1,
			"Expect more than one element: " + values.size()
		);

		int start = 0;
		for (int i = 0; i < values.size(); ++i) {
			final int diff = values.get(i) - start;
			Assert.assertTrue(
				diff > 4 && diff < 20,
				"Expected value between [4, 20]: " + diff
			);
			start = values.get(i);
		}
	}

	@Test
	public void toTimespanSliceMin() {
		final ISeq<Integer> values = IntStream.range(0, 100).boxed()
			.peek(i -> {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					throw new AssertionError(e);
				}
			})
			.flatMap(MinMax.toSliceMin(Duration.ofMillis(10)))
			.collect(ISeq.toISeq());

		Assert.assertTrue(
			values.size() > 1,
			"Expect more than one element: " + values.size()
		);
		Assert.assertEquals(values.get(0).intValue(), 0);

		int start = values.get(0);
		for (int i = 1; i < values.size(); ++i) {
			final int diff = values.get(i) - start;
			Assert.assertTrue(
				diff > 4 && diff < 20,
				"Expected value between [4, 20]: " + diff
			);
			start = values.get(i);
		}
	}

}
