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

import static java.nio.file.StandardOpenOption.APPEND;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;

public class IOTest {

	@Test(dataProvider = "data")
	public void appendRead(final List<List<Object>> data) throws IOException {
		final var file = CloseableValue.of(
			Files.createTempFile("IO", "TEST"),
			Files::deleteIfExists
		);

		try (file) {
			for (var objects : data) {
				IO.write(file.get(), objects, APPEND);
			}

			final List<Object> expected = data.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

			try (var objects = IO.read(file.get())) {
				final var list = objects.collect(Collectors.toList());
				Assert.assertEquals(list, expected);
			}

		}
	}

	@DataProvider
	public Object[][] data() {
		return new Object[][] {
			{List.of()},
			{List.of(
				List.of(1)
			)},
			{List.of(
				List.of(1.1)
			)},
			{List.of(
				List.of("one")
			)},
			{List.of(
				List.of("one", 2, 3.0, "four")
			)},
			{List.of(
				List.of("one"),
				List.of(2)
			)},
			{List.of(
				List.of("one"),
				List.of(2),
				List.of(3.0)
			)},
			{List.of(
				List.of("one"),
				List.of(1, 2, 3)
			)},
			{List.of(
				List.of("one"),
				List.of(2),
				List.of(1, 2, 3, 4, 5),
				List.of(1.1, 1.2, 1.3)
			)},
			{List.of(
				List.of("one"),
				List.of(2),
				List.of(1, 2, 3, 4, 5),
				List.of(1.1, 1.2, 1.3),
				List.of("two"),
				List.of(2)
			)}
		};
	}

	@Test(dataProvider = "data")
	public void writeRead(final List<List<Object>> data) throws IOException {
		final var file = CloseableValue.of(
			Files.createTempFile("IO", "TEST"),
			Files::deleteIfExists
		);

		try (file) {
			for (var objects : data) {
				IO.write(file.get(), objects);
			}

			final List<Object> expected = data.isEmpty()
				? List.of()
				: data.get(data.size() - 1);

			try (var objects = IO.read(file.get())) {
				final var list = objects.collect(Collectors.toList());
				Assert.assertEquals(list, expected);
			}

		}
	}

}
