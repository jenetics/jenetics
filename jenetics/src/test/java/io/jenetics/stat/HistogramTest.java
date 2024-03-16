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
package io.jenetics.stat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.random.RandomGenerator;

import org.testng.annotations.Test;

import io.jenetics.DoubleGene;
import io.jenetics.testfixtures.stat.Histogram;
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

		var histogram = Histogram.Builder.of(begin, end, elements).build();
		assertThat(histogram.bucketCount()).isEqualTo(elements + 2);
		assertThat(histogram.frequencies())
			.isEqualTo(new Histogram.Frequencies(new long[elements + 2]));
	}

	@Test
	public void accumulate() {
		final long begin = 0;
		final long end = 10;
		final int binCount = 9;

		final var builder = Histogram.Builder.of(begin, end, binCount);
		for (int i = 0; i < binCount*1000; ++i) {
			final var value = i%binCount + 1;
			builder.accept(value);
		}

		final var histogram = builder.build();
		final long[] expected = new long[binCount + 2];
		Arrays.fill(expected, 1000);
		expected[0] = 0;
		expected[expected.length - 1] = 0;
		assertThat(histogram.frequencies())
			.isEqualTo(new Histogram.Frequencies(expected));
		System.out.println(histogram);
	}

	@Test
	public void streaming() {
		final long sampleCount = 100_000;
		Histogram observation = RandomGenerator.getDefault()
			.doubles(sampleCount)
			.collect(
				() -> Histogram.Builder.of(0,1, 20),
				Histogram.Builder::accept,
				Histogram.Builder::combine
			)
			.build();

		assertThat(observation.sampleCount()).isEqualTo(sampleCount);
	}

	@Test
	public void print() {
		final var builder = Histogram.Builder.of(0, 10, 23);
		final var random = RandomGenerator.getDefault();
		for (int i = 0; i < 10_000; ++i) {
			builder.accept(random.nextGaussian(5, 2));
		}

		final Histogram observation = builder.build();
		observation.print(System.out);
	}

	@Test
	public void toHistogram() {
		final var random = RandomGenerator.getDefault();
		final ISeq<DoubleGene> genes = DoubleGene.of(0, 10)
			.instances()
			.limit(1000)
			.collect(ISeq.toISeq());

		final Histogram observations = genes.stream()
			.collect(Histogram.toHistogram(0, 10, 15, DoubleGene::doubleValue));
		assertThat(observations.sampleCount()).isEqualTo(1000);
	}

}
