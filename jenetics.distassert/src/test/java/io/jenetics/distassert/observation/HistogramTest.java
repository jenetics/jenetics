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
package io.jenetics.distassert.observation;

import static java.lang.Math.nextDown;
import static java.lang.Math.nextUp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.distassert.Interval;
import io.jenetics.distassert.observation.Histogram.Buckets;
import io.jenetics.distassert.observation.Histogram.Partition;
import io.jenetics.distassert.observation.Histogram.Residual;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramTest {

	@Test
	public void create() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var buckets = new Buckets(partition);
		final var residual = new Residual(10, 5);

		assertThatNoException().isThrownBy(() -> new Histogram(buckets, residual));
	}

	@Test
	public void createWithNullBuckets() {
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> new Histogram(null));
	}

	@Test
	public void createWithNullResidual() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var buckets = new Buckets(partition);

		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> new Histogram(buckets, null));
	}

	@Test
	public void degreesOfFreedom() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var buckets = new Buckets(partition);
		final var histogram = new Histogram(buckets);

		assertThat(histogram.degreesOfFreedom()).isEqualTo(buckets.size() - 1);
	}

	@Test
	public void samples() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var frequencies = new long[partition.size()];
		Arrays.fill(frequencies, 123);
		final var buckets = new Buckets(partition, frequencies);
		final var histogram = new Histogram(buckets, new Residual(10, 5));

		assertThat(histogram.samples()).isEqualTo(frequencies.length*123L);
	}

	@Test(dataProvider = "intervals")
	public void elements(Interval interval, long size) {
		assertThat(Histogram.elements(interval)).isEqualTo(size);
	}

	@DataProvider
	public Object[][] intervals() {
		return new Object[][] {
			{new Interval(-100.0, up(-100.0, 1)), 1},
			{new Interval(-100.0, up(-100.0, 2)), 2},
			{new Interval(-100.0, up(-100.0, 3)), 3},
			{new Interval(-100.0, up(-100.0, 5)), 5},
			{new Interval(-100.0, up(-100.0, 20)), 20},

			{new Interval(down(-100.0, 1), up(-100.0, 1)), 2},
			{new Interval(down(-100.0, 2), up(-100.0, 2)), 4},
			{new Interval(down(-100.0, 3), up(-100.0, 3)), 6},
			{new Interval(down(-100.0, 5), up(-100.0, 5)), 10},
			{new Interval(down(-100.0, 20), up(-100.0, 20)), 40},

			{new Interval(0.0, up(0.0, 1)), 1},
			{new Interval(0.0, up(0.0, 2)), 2},
			{new Interval(0.0, up(0.0, 3)), 3},
			{new Interval(0.0, up(0.0, 5)), 5},
			{new Interval(0.0, up(0.0, 20)), 20},

			{new Interval(down(0.0, 1), up(0.0, 1)), 2},
			{new Interval(down(0.0, 2), up(0.0, 2)), 4},
			{new Interval(down(0.0, 3), up(0.0, 3)), 6},
			{new Interval(down(0.0, 5), up(0.0, 5)), 10},
			{new Interval(down(0.0, 20), up(0.0, 20)), 40},

			{new Interval(100.0, up(100.0, 1)), 1},
			{new Interval(100.0, up(100.0, 2)), 2},
			{new Interval(100.0, up(100.0, 3)), 3},
			{new Interval(100.0, up(100.0, 5)), 5},
			{new Interval(100.0, up(100.0, 20)), 20},

			{new Interval(down(100.0, 1), up(100.0, 1)), 2},
			{new Interval(down(100.0, 2), up(100.0, 2)), 4},
			{new Interval(down(100.0, 3), up(100.0, 3)), 6},
			{new Interval(down(100.0, 5), up(100.0, 5)), 10},
			{new Interval(down(100.0, 20), up(100.0, 20)), 40},

			{new Interval(-2, 2), Long.MAX_VALUE}
		};
	}

	private static double up(double d, int steps) {
		var result = d;
		for (int i = 0; i < steps; ++i) {
			result = nextUp(result);
		}
		return result;
	}

	private static double down(double d, int steps) {
		var result = d;
		for (int i = 0; i < steps; ++i) {
			result = nextDown(result);
		}
		return result;
	}

}
