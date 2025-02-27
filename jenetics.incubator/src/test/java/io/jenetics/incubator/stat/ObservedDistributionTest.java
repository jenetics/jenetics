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

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

import io.jenetics.internal.math.DoubleAdder;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ObservedDistributionTest {

	@Test
	public void pdf() {
		final var interval = new Interval(0, Math.PI);
		final var arguments = IntStream.rangeClosed(0, 100)
			.mapToDouble(i -> i/100.0*interval.size())
			.toArray();
		final var frequencies = DoubleStream.of(arguments)
			.map(Math::sin)
			.toArray();

		final var sum = DoubleAdder.sum(frequencies);

		final var distribution = ObservedDistribution.of(
			interval, frequencies
		);

		final var pdf = distribution.pdf();
		for (var arg : arguments) {
			assertThat(pdf.apply(arg))
				.isCloseTo(Math.sin(arg)/sum, Offset.offset(0.00001));
		}
	}

	@Test
	public void cdf() {
		final var interval = new Interval(0, Math.PI);
		final var arguments = IntStream.rangeClosed(0, 100)
			.mapToDouble(i -> i/100.0*interval.size())
			.toArray();
		final var frequencies = DoubleStream.of(arguments)
			.map(Math::sin)
			.toArray();

		final var distribution = ObservedDistribution.of(
			interval, frequencies
		);

		final var cdf = distribution.cdf();
		for (var arg : arguments) {
			assertThat(cdf.apply(arg))
				.isCloseTo((1 - Math.cos(arg))/2.0, Offset.offset(0.01));
		}
	}

}
