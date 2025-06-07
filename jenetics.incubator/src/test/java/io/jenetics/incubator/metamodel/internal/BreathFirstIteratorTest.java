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
package io.jenetics.incubator.metamodel.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class BreathFirstIteratorTest {

	@Test
	public void iterate() {
		final var value = "abcdefghij";
		final var iterator = BreathFirstIterator.of(
			value,
			BreathFirstIteratorTest::split,
			Function.identity()
		);

		assertThat(iterator.asStream().toList()).isEqualTo(
			List.of(
				"abcde",
				"fghij",
				"ab",
				"cde",
				"fg",
				"hij",
				"a",
				"b",
				"c",
				"de",
				"f",
				"g",
				"h",
				"ij",
				"d",
				"e",
				"i",
				"j"
			)
		);
	}

	static Stream<String> split(String value) {
		if (value.length() <= 1) {
			return Stream.empty();
		} else  {
			final int mid = value.length()/2;
			return Stream.of(value.substring(0, mid), value.substring(mid));
		}
	}

}
