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

import java.util.Arrays;

import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram.Buckets;
import io.jenetics.incubator.stat.Histogram.Builder;
import io.jenetics.incubator.stat.Histogram.Partition;
import io.jenetics.incubator.stat.Histogram.Residual;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramBuilderTest {

	@Test
	public void createFromBuckets() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var frequencies = new long[partition.size()];
		Arrays.fill(frequencies, 123);
		final var buckets = new Buckets(partition, frequencies);

		final var builder = new Builder(buckets);
		final var histogram = builder.build();
		assertThat(histogram.buckets()).isEqualTo(buckets);
		assertThat(histogram.residual()).isEqualTo(Residual.EMPTY);
	}

	@Test
	public void createFromPartition() {
		final var partition = Partition.of(new Interval(12, 123), 10);
		final var buckets = new Buckets(partition);

		final var builder = new Builder(buckets);
		final var histogram = builder.build();
		assertThat(histogram.buckets()).isEqualTo(buckets);
		assertThat(histogram.residual()).isEqualTo(Residual.EMPTY);
	}

	@Test
	public void accept() {
		final var partition = Partition.of(new Interval(0, 100), 10);
		final var builder = new Builder(partition);

		final var values = new double[] {1, 11, 21, 31, 41, 51, 61, 71, 81, 91};
		final var count = 100;
		for (int i = 0; i < count; ++i) {
			for (double value : values) {
				builder.add(value);
			}
		}

		var histogram = builder.build();
		assertThat(histogram.residual()).isEqualTo(Residual.EMPTY);
		for (var bucket : histogram.buckets()) {
			assertThat(bucket.count()).isEqualTo(count);
		}

		for (int i = 0; i < count; ++i) {
			builder.add(-1);
			builder.add(100);
		}

		histogram = builder.build();
		assertThat(histogram.residual()).isEqualTo(new Residual(count,count));
		for (var bucket : histogram.buckets()) {
			assertThat(bucket.count()).isEqualTo(count);
		}
	}

	@Test
	public void build() {
		final var count = 100;
		final var values = new double[] {1, 11, 21, 31, 41, 51, 61, 71, 81, 91};
		final var partition = Partition.of(new Interval(0, 100), 10);

		final var histogram = new Builder(partition).build(samples -> {
			for (int i = 0; i < count; ++i) {
				for (double value : values) {
					samples.add(value);
				}
			}

			for (int i = 0; i < count; ++i) {
				samples.add(-1);
				samples.add(100);
			}
		});

		assertThat(histogram.residual()).isEqualTo(new Residual(count, count));
		for (var bucket : histogram.buckets()) {
			assertThat(bucket.count()).isEqualTo(count);
		}

		final var histogram2 = new Builder(histogram.buckets(), histogram.residual())
			.build(samples -> {
				for (int i = 0; i < count; ++i) {
					for (double value : values) {
						samples.add(value);
					}
				}

				for (int i = 0; i < count; ++i) {
					samples.add(-1);
					samples.add(100);
				}
			});

		assertThat(histogram2.residual()).isEqualTo(new Residual(2*count, 2*count));
		for (var bucket : histogram2.buckets()) {
			assertThat(bucket.count()).isEqualTo(2*count);
		}
	}

}
