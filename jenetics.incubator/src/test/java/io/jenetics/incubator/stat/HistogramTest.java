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
package io.jenetics.incubator.stat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import io.jenetics.incubator.stat.Histogram.Buckets;
import io.jenetics.incubator.stat.Histogram.Builder;
import io.jenetics.incubator.stat.Histogram.Partition;
import io.jenetics.incubator.stat.Histogram.Residual;

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

	@Test
	public void serialization() throws IOException {
		final var mapper = new ObjectMapper();

		final Interval interval = new Interval(-5, 5);
		final var random = RandomGenerator.getDefault();
		final Histogram observation = Builder.of(interval, 20)
			.build(samples -> {
				for (int i = 0; i < 1_000_000; ++i) {
					samples.add(random.nextGaussian());
				}
			});

		final var json = mapper
			.writerWithDefaultPrettyPrinter()
			.writeValueAsString(observation);

		final var deserialized = mapper.readValue(json, Histogram.class);
		assertThat(deserialized).isNotSameAs(observation);
		assertThat(deserialized).isEqualTo(observation);
	}

}
