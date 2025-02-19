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
package io.jenetics.incubator.math;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ErfTest {

	private static final int LOOPS = 1_000_000;

	private static RandomGenerator rand() {
		return RandomGeneratorFactory.of("L128X1024MixRandom").create(123123);
	}

	@Test
	public void erf() {
		final var offset = Offset.offset(Math.pow(2, -45));
		final var random = rand();

		for (int i = 0; i < LOOPS; ++i) {
			final double x = random.nextDouble(-100, 100);
			final double result = Erf.erf(x);
			final double expected = org.apache.commons.numbers.gamma.Erf.value(x);

			assertThat(result)
				.withFailMessage(() ->
					"Expected erf(%s) = %s, but was %s."
						.formatted(x, expected, result))
				.isCloseTo(expected, offset);
		}
	}

	@Test
	public void erfc() {
		final var offset = Offset.offset(Math.pow(2, -40));
		final var random = rand();

		for (int i = 0; i < LOOPS; ++i) {
			final double x = random.nextDouble(-100, 100);
			final double result = Erf.erfc(x);
			final double expected = org.apache.commons.numbers.gamma.Erfc.value(x);

			assertThat(result)
				.withFailMessage(() ->
					"Expected erfc(%s) = %s, but was %s."
						.formatted(x, expected, result))
				.isCloseTo(expected, offset);
		}
	}

	@Test
	public void erfinv() {
		final var offset = Offset.offset(Math.pow(2, -45));
		final var random = rand();

		for (int i = 0; i < LOOPS; ++i) {
			final double x = random.nextDouble(-100, 100);
			final double result = Erf.erfinv(x);
			final double expected = org.apache.commons.numbers.gamma.InverseErf.value(x);

			assertThat(result)
				.withFailMessage(() ->
					"Expected erfinv(%s) = %s, but was %s."
						.formatted(x, expected, result))
				.isCloseTo(expected, offset);
		}
	}

	@Test
	public void erfcinv() {
		final var offset = Offset.offset(Math.pow(2, -45));
		final var random = rand();

		for (int i = 0; i < LOOPS; ++i) {
			final double x = random.nextDouble(-100, 100);
			final double result = Erf.erfcinv(x);
			final double expected = org.apache.commons.numbers.gamma.InverseErfc.value(x);

			assertThat(result)
				.withFailMessage(() ->
					"Expected erfcinv(%s) = %s, but was %s."
						.formatted(x, expected, result))
				.isCloseTo(expected, offset);
		}
	}

}
