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

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.incubator.stat.Histogram.Bucket;
import io.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramTest {

	@Test
	public void create() {
		final double begin = 12;
		final double end = 123;
		final int elements = 10;

		var histogram = Histogram.Builder.of(new Histogram.Interval(begin, end), elements).build();
		assertThat(histogram.buckets().size()).isEqualTo(elements);
		assertThat(
			histogram.buckets().stream()
				.mapToLong(Bucket::count)
				.toArray()
		)
			.isEqualTo(new long[elements]);
	}

	@Test
	public void accumulate() {
		final long begin = 0;
		final long end = 10;
		final int binCount = 9;

		final var builder = Histogram.Builder.of(new Histogram.Interval(begin, end), binCount);
		for (int i = 0; i < binCount*1000; ++i) {
			final var value = i%binCount + 1;
			builder.accept(value);
		}

		final var histogram = builder.build();
		final long[] expected = new long[binCount];
		Arrays.fill(expected, 1000);
		assertThat(histogram.buckets().stream()
			.mapToLong(Bucket::count)
			.toArray())
			.isEqualTo(expected);
		System.out.println(histogram);
	}

	@Test
	public void streaming() {
		final long sampleCount = 100_000;
		Histogram observation = RandomGenerator.getDefault()
			.doubles(sampleCount)
			.collect(
				() -> Histogram.Builder.of(new Histogram.Interval(0, 1), 20),
				Histogram.Builder::accept,
				Histogram.Builder::combine
			)
			.build();

		assertThat(observation.samples()).isEqualTo(sampleCount);
	}

	@Test
	public void build() {
		final double[] values = RandomGenerator.getDefault()
			.doubles(10000, -5, 5)
			.toArray();

		final var histogram = Histogram.Builder.of(new Histogram.Interval(-5, 5), 10)
			.build(samples -> {
				for (double value : values) {
					samples.accept(value);
				}
			});

		System.out.println(histogram);
	}

	@Test
	public void print() {
		final var builder = Histogram.Builder.of(new Histogram.Interval(0, 10), 23);
		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 10_000; ++i) {
			builder.accept(random.nextGaussian(5, 2));
		}

		final Histogram observation = builder.build();
		HistogramFormat.DEFAULT.format(observation, System.out);
	}

//	@Test
//	public void toHistogram() {
//		final var random = RandomGenerator.getDefault();
//		final ISeq<DoubleGene> genes = DoubleGene.of(0, 10)
//			.instances()
//			.limit(1000)
//			.collect(ISeq.toISeq());
//
//		final Histogram observations = genes.stream()
//			.collect(Histogram.toHistogram(0, 10, 15, DoubleGene::doubleValue));
//		assertThat(observations.samples()).isEqualTo(1000);
//	}

	@Test
	public void foo() {
		/*
		final var buckets = Buckets.of(0,1, 10)
			.append(new Bucket(POSITIVE_INFINITY, 0)
			.prepend(POSITIVE_INFINITY);
		buckets.append(last -> last.next(POSITIVE_INFINITY));
		 */

		/*
		new Histogram.Builder(Histogram.Buckets.of(0, 1, 5).open());

		Histogram.Builder.of(0,1, 10).open()

		final var buckets = Histogram.Buckets.of(0, 1, 10)
			.open()
			.leftOpen()
			.rightOpen();

		buckets.leftOpen();
		buckets.rightOpen();
		buckets.open();

		buckets.prepend(Double.NEGATIVE_INFINITY);
		buckets.append(Double.POSITIVE_INFINITY);

		buckets.prepend(new Bucket(Double.NEGATIVE_INFINITY, 0));
		*/
	}

}
