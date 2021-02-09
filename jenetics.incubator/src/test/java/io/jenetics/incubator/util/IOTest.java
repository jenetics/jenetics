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

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.UUID.randomUUID;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Lifecycle.CloseableValue;

public class IOTest {

	@Test(dataProvider = "data")
	public void appendReadExistingFile(final List<List<Object>> data) throws Exception {
		final var path = CloseableValue.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		appendRead(data, path, APPEND);
	}

	private void appendRead(
		final List<List<Object>> data,
		final CloseableValue<Path> path,
		final OpenOption... options
	)
		throws Exception
	{
		try (path) {
			for (var objects : data) {
				IO.write(path.get(), objects, options);
			}

			final List<Object> expected = data.stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

			Assert.assertEquals(IO.readAllObjects(path.get()), expected);
		}
	}

	@Test(dataProvider = "data")
	public void appendReadNonExistingFile(final List<List<Object>> data) throws Exception {
		final var path = CloseableValue.of(
			Path.of(
				System.getProperty("java.io.tmpdir"),
				format("IO-%s-TEST", randomUUID().toString().replace("-", ""))
			),
			Files::deleteIfExists
		);

		appendRead(data, path, APPEND, CREATE);
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
	public void writeRead(final List<List<Object>> data) throws Exception {
		final var path = CloseableValue.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		try (path) {
			for (var objects : data) {
				IO.write(path.get(), objects, TRUNCATE_EXISTING);
			}

			final List<Object> expected = data.isEmpty()
				? List.of()
				: data.get(data.size() - 1);

			Assert.assertEquals(IO.readAllObjects(path.get()), expected);

		}
	}

}
