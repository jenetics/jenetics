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
package io.jenetics.incubator.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class TypeTokenTest {

	@Test
	public void cast() {
		final var string = new TypeToken<String>() {};
		final var integer = new TypeToken<Integer>() {};

		assertThatNoException().isThrownBy(() -> integer.cast(42));
		assertThatNoException().isThrownBy(() -> string.cast("42"));

		assertThatExceptionOfType(ClassCastException.class)
			.isThrownBy(() -> integer.cast("42"));
		assertThatExceptionOfType(ClassCastException.class)
			.isThrownBy(() -> string.cast(42));
	}

}
