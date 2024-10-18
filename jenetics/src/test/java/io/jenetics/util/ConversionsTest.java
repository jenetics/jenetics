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

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class ConversionsTest {

	@Test
	public void intToLongArray() {
		final var source = new int[100];
		Arrays.fill(source, 50);

		final var target = Conversions.intToLongArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo(source[i]);
		}
	}

	@Test
	public void intToDoubleArray() {
		final var source = new int[100];
		Arrays.fill(source, 50);

		final var target = Conversions.intToDoubleArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo(source[i]);
		}
	}

	@Test
	public void longToIntArray() {
		final var source = new long[100];
		Arrays.fill(source, 50);

		final var target = Conversions.longToIntArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo(source[i]);
		}
	}

	@Test
	public void longToDoubleArray() {
		final var source = new long[100];
		Arrays.fill(source, 50);

		final var target = Conversions.longToDoubleArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo(source[i]);
		}
	}

	@Test
	public void doubleToIntArray() {
		final var source = new double[100];
		Arrays.fill(source, 50);

		final var target = Conversions.doubleToIntArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo((int)source[i]);
		}
	}

	@Test
	public void doubleToLongArray() {
		final var source = new double[100];
		Arrays.fill(source, 50);

		final var target = Conversions.doubleToLongArray(source);
		for (int i = 0; i < source.length; ++i) {
			assertThat(target[i]).isEqualTo((long)source[i]);
		}
	}

}
