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
package io.jenetics.ext.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class FindingTest {

	@Test
	public void argmin() {
		assertThat(Finding.argmin(List.of(7, 6, 9, 3, 5), Function.identity()))
			.isEqualTo(3);

		assertThat(Finding.argmin(List.<Integer>of(), Function.identity()))
			.isNull();
	}

	@Test
	public void argmax() {
		assertThat(Finding.argmax(List.of(7, 6, 9, 3, 5), Function.identity()))
			.isEqualTo(9);

		assertThat(Finding.argmax(List.<Integer>of(), Function.identity()))
			.isNull();
	}

	@Test
	public void doubleArgmin() {
		assertThat(Finding.argmin(List.of(7, 6, 9, 3, 5), Integer::doubleValue))
			.isEqualTo(3);

		assertThat(Finding.argmin(List.<Integer>of(), Integer::doubleValue))
			.isNull();
	}

	@Test
	public void doubleArgmax() {
		assertThat(Finding.argmax(List.of(7, 6, 9, 3, 5), Integer::doubleValue))
			.isEqualTo(9);

		assertThat(Finding.argmax(List.<Integer>of(), Integer::doubleValue))
			.isNull();
	}

}
