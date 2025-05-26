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
package io.jenetics.distassert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram.Buckets;
import io.jenetics.incubator.stat.Histogram.Partition;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramBucketsTest {

	@Test
	public void create() {
		assertThatNoException()
			.isThrownBy(() -> new Buckets(Partition.of(new Interval(0, 10), 5)));
	}

	@Test
	public void createNullPartition() {
		assertThatExceptionOfType(NullPointerException.class)
			.isThrownBy(() -> new Buckets(null, 5));
	}

	@Test
	public void createWrongFrequencySize() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				new Buckets(Partition.of(new Interval(0, 10), 5), 5));
	}

	@Test
	public void createNegativeFrequency() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				new Buckets(Partition.of(new Interval(0, 10), 3), 5, 5, -1));
	}

	@Test
	public void size() {
		var buckets = new Buckets(Partition.of(new Interval(0, 10), 3));
		assertThat(buckets.size()).isEqualTo(3);
		assertThat(buckets).hasSize(3);
	}

	@Test
	public void get() {
		var buckets = new Buckets(new Partition(
			new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9
		));
		assertThat(buckets.size()).isEqualTo(10);

		for (int i = 0; i < buckets.size(); ++i) {
			var bucket = buckets.get(i);
			assertThat(bucket.interval()).isEqualTo(new Interval(i, i + 1));
			assertThat(bucket.count()).isZero();
		}
	}

	@Test
	public void iterator() {
		var buckets = new Buckets(new Partition(
			new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9
		));
		assertThat(buckets.size()).isEqualTo(10);

		int i = 0;
		for (var bucket : buckets) {
			assertThat(bucket.interval()).isEqualTo(new Interval(i, i + 1));
			assertThat(bucket.count()).isZero();
			++i;
		}
	}

	@Test
	public void stream() {
		var buckets = new Buckets(new Partition(
			new Interval(0, 10), 1, 2, 3, 4, 5, 6, 7, 8, 9
		));
		assertThat(buckets.size()).isEqualTo(10);

		var bucks = buckets.stream().toList();
		assertThat(bucks).hasSize(10);
	}

}
