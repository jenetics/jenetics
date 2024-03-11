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

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.testfixtures.stat.Histogram;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class HistogramTest {

	@Test
	public void create() {
		final double begin = 12;
		final double end = 123;
		final int elements = 10;

		var histogram = Histogram.of(begin, end, elements);
		Assert.assertEquals(histogram.bucketCount(), elements + 2);
		Assert.assertEquals(histogram.table(), new long[elements + 2]);
	}

	@Test
	public void accumulate() {
		final long begin = 0;
		final long end = 10;
		final int binCount = 9;

		var histogram = Histogram.of(begin, end, binCount);
		for (int i = 0; i < binCount*1000; ++i) {
			final var value = i%binCount + 1;
			histogram.accept(value);
		}

		final long[] expected = new long[binCount + 2];
		Arrays.fill(expected, 1000);
		expected[0] = 0;
		expected[expected.length - 1] = 0;
		assertThat(histogram.table()).isEqualTo(expected);
		System.out.println(histogram);
	}

	@Test
	public void streaming() {
		final long sampleCount = 100_000;
		Histogram observation = RandomGenerator.getDefault()
			.doubles(sampleCount)
			.collect(
				() -> Histogram.of(0,1, 20),
				Histogram::accept,
				Histogram::combine
			);

		assertThat(observation.sampleCount()).isEqualTo(sampleCount);
	}

}
