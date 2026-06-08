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

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class StreamsTest {

	@Test(dataProvider = "toIntervalBestData")
	public void toIntervalMax(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(Streams.toIntervalMax(sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*(i + 1) - 1 );
		}
	}

	@Test(dataProvider = "toIntervalBestData")
	public void toIntervalMin(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(Streams.toIntervalMin(sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*i);
		}
	}

	@Test(dataProvider = "toIntervalBestData")
	public void toIntervalBest(final int streamSize, final int sliceSize) {
		final ISeq<Integer> values = IntStream.range(0, streamSize).boxed()
			.flatMap(Streams.toIntervalBest(Comparator.reverseOrder(), sliceSize))
			.collect(ISeq.toISeq());

		for (int i = 0; i < streamSize/sliceSize; ++i) {
			Assert.assertEquals(values.get(i).intValue(), sliceSize*i);
		}
	}

	@DataProvider(name = "toIntervalBestData")
	public Object[] toIntervalBestData() {
		return new Object[][] {
			{10, 1},
			{100, 10},
			{333, 5},
			{1111, 3},
			{10, 100}
		};
	}

	private final static class TestClock extends Clock {
		long count = 0;
		@Override
		public ZoneId getZone() { return null; }
		@Override
		public Clock withZone(ZoneId zoneId) { return null; }
		@Override
		public Instant instant() {
			++count;
			return Instant.ofEpochMilli(count);
		}
	}

	@Test
	public void toTimespanIntervalMax() {
		final ISeq<Integer> values = IntStream.range(1, 101).boxed()
			.flatMap(Streams.toIntervalMax(Duration.ofMillis(10), new TestClock()))
			.collect(ISeq.toISeq());

		Assert.assertEquals(values.length(),10);

		int start = 0;
		for (int i = 0; i < values.size(); ++i) {
			final int diff = values.get(i) - start;
			Assert.assertEquals(diff, 10);
			start = values.get(i);
		}
	}

	@Test
	public void toTimespanIntervalMin() {
		final ISeq<Integer> values = IntStream.range(0, 100).boxed()
			.flatMap(Streams.toIntervalMin(Duration.ofMillis(10), new TestClock()))
			.collect(ISeq.toISeq());

		Assert.assertEquals(values.length(),10);
		Assert.assertEquals(values.get(0).intValue(), 0);

		int start = values.get(0);
		for (int i = 1; i < values.size(); ++i) {
			final int diff = values.get(i) - start;
			Assert.assertEquals(diff, 10);
			start = values.get(i);
		}
	}

	@Test
	public void strictlyIncreasing() {
		final ISeq<Integer> values = Stream.of(3, 2, 5, 4, 7, 7, 4, 9)
			.gather(Streams.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(3, 5, 7, 9));
	}

	@Test
	public void strictlyIncreasingWithDuplicates() {
		final ISeq<Integer> values = Stream.of(1, 1, 1, 2, 2, 1, 3)
			.gather(Streams.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(1, 2, 3));
	}

	@Test
	public void strictlyIncreasingEmptyStream() {
		final ISeq<Integer> values = Stream.<Integer>empty()
			.gather(Streams.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.empty());
	}

	@Test
	public void strictlyIncreasingSameAsStreamsFlatMap() {
		final ISeq<Integer> source = ISeq.of(3, 2, 5, 4, 7, 7, 4, 9);

		@SuppressWarnings("removal")
		final ISeq<Integer> flatMapped = source.stream()
			.flatMap(Streams.toStrictlyIncreasing())
			.collect(ISeq.toISeq());

		final ISeq<Integer> gathered = source.stream()
			.gather(Streams.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(gathered).isEqualTo(flatMapped);
	}

	@Test
	public void strictlyImproving() {
		final ISeq<Integer> values = Stream.of(9, 8, 9, 5, 6, 6, 2, 9)
			.gather(Streams.strictlyImproving(Comparator.reverseOrder()))
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(9, 8, 5, 2));
	}

	@Test
	public void strictlyImprovingNullComparator() {
		assertThatNullPointerException()
			.isThrownBy(() -> Streams.strictlyImproving(null));
	}

	@Test
	public void strictlyImprovingWithNullValues() {
		final ISeq<Integer> values = Stream.<Integer>of(null, 1, null, 2)
			.gather(Streams.strictlyIncreasing())
			.collect(ISeq.toISeq());

		assertThat(values).isEqualTo(ISeq.of(1, 2));
	}

}
