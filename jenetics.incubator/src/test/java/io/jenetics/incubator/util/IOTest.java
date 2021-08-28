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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Lifecycle.Value;

public class IOTest {

	@Test(dataProvider = "data")
	public void appendReadExistingFile(final List<List<Object>> data) throws IOException {
		final Value<Path, IOException> path = Value.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		appendRead(data, path, APPEND);
	}

	private void appendRead(
		final List<List<Object>> data,
		final Value<Path, IOException> path,
		final OpenOption... options
	)
		throws IOException
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
	public void appendReadNonExistingFile(final List<List<Object>> data) throws IOException {
		final Value<Path, IOException> path = Value.of(
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
	public void writeRead(final List<List<Object>> data) throws IOException {
		final Value<Path, IOException> path = Value.of(
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

	@Test
	public void writeFiledReadFileExample() throws IOException {
		final Value<Path, IOException> path = Value.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		try (path) {
			final var written = IO.write(path.get(), List.of("1", "2", "3"), CREATE);
			Assert.assertEquals(written, Files.size(path.get()));

			List<Object> objects = IO.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3"));

			IO.write(path.get(), List.of("4", "5"), APPEND);
			objects = IO.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

			try (Stream<Object> stream = IO.objects(path.get())) {
				final var count = new AtomicInteger(1);
				stream.forEach(o -> {
					final var expected = String.valueOf(count.getAndIncrement());
					Assert.assertEquals(o, expected);
				});
			}

			IO.write(path.get(), List.of("6", "7", "8"), TRUNCATE_EXISTING);
			objects = IO.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("6", "7", "8"));
		}
	}

	@Test
	public void writeStreamReadFileExample() throws IOException {
		final Value<Path, IOException> path = Value.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		try (path; var out = Files.newOutputStream(path.get())) {
			IO.write(out, List.of("1", "2", "3"), false);
			List<Object> objects = IO.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3"));

			IO.write(out, List.of("4", "5"), true);
			objects = IO.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

			try (Stream<Object> stream = IO.objects(path.get())) {
				final var count = new AtomicInteger(1);
				stream.forEach(o -> {
					final var expected = String.valueOf(count.getAndIncrement());
					Assert.assertEquals(o, expected);
				});
			}
		}
	}

	@Test
	public void writeStreamReadStreamExample() throws IOException {
		final var out = new ByteArrayOutputStream();

		IO.write(out, List.of("1", "2", "3"), false);
		List<Object> objects = IO.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("1", "2", "3"));

		IO.write(out, List.of("4", "5"), true);
		objects = IO.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

		try (Stream<Object> stream = IO.objects(new ByteArrayInputStream(out.toByteArray()))) {
			final var count = new AtomicInteger(1);
			stream.forEach(o -> {
				final var expected = String.valueOf(count.getAndIncrement());
				Assert.assertEquals(o, expected);
			});
		}

		out.reset();
		IO.write(out, List.of("6", "7", "8"), false);
		objects = IO.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("6", "7", "8"));
	}

	@Test
	public void readParallelStream() throws IOException {
		final var objects = IntStream.range(0, 1000)
			.mapToObj(String::valueOf)
			.collect(Collectors.toSet());

		final var out = new ByteArrayOutputStream();
		IO.write(out, objects, false);

		final var read = IO.objects(new ByteArrayInputStream(out.toByteArray()))
			.parallel()
			.collect(Collectors.toSet());

		Assert.assertEquals(read, objects);
	}

}
