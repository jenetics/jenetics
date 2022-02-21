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

public class SerializerTest {

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
				Serializer.write(objects, path.get(), options);
			}

			final List<Object> expected = data.stream()
				.flatMap(Collection::stream)
				.toList();

			Assert.assertEquals(Serializer.readAllObjects(path.get()), expected);
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
				Serializer.write(objects, path.get(), TRUNCATE_EXISTING);
			}

			final List<Object> expected = data.isEmpty()
				? List.of()
				: data.get(data.size() - 1);

			Assert.assertEquals(Serializer.readAllObjects(path.get()), expected);
		}
	}

	@Test
	public void writeFiledReadFileExample() throws IOException {
		final Value<Path, IOException> path = Value.of(
			Files.createTempFile("IO-", "-TEST"),
			Files::deleteIfExists
		);

		try (path) {
			final var written = Serializer.write(List.of("1", "2", "3"), path.get());
			Assert.assertEquals(written, Files.size(path.get()));

			List<Object> objects = Serializer.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3"));

			Serializer.write(List.of("4", "5"), path.get());
			objects = Serializer.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

			try (Stream<Object> stream = Serializer.objects(path.get())) {
				final var count = new AtomicInteger(1);
				stream.forEach(o -> {
					final var expected = String.valueOf(count.getAndIncrement());
					Assert.assertEquals(o, expected);
				});
			}

			Serializer.write(List.of("6", "7", "8"), path.get(), TRUNCATE_EXISTING);
			objects = Serializer.readAllObjects(path.get());
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
			Serializer.write(List.of("1", "2", "3"), out, false);
			List<Object> objects = Serializer.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3"));

			Serializer.write(List.of("4", "5"), out, true);
			objects = Serializer.readAllObjects(path.get());
			Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

			try (Stream<Object> stream = Serializer.objects(path.get())) {
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

		Serializer.write(List.of("1", "2", "3"), out, false);
		List<Object> objects = Serializer.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("1", "2", "3"));

		Serializer.write(List.of("4", "5"), out, true);
		objects = Serializer.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("1", "2", "3", "4", "5"));

		try (Stream<Object> stream = Serializer.objects(new ByteArrayInputStream(out.toByteArray()))) {
			final var count = new AtomicInteger(1);
			stream.forEach(o -> {
				final var expected = String.valueOf(count.getAndIncrement());
				Assert.assertEquals(o, expected);
			});
		}

		out.reset();
		Serializer.write(List.of("6", "7", "8"), out, false);
		objects = Serializer.readAllObjects(new ByteArrayInputStream(out.toByteArray()));
		Assert.assertEquals(objects, List.of("6", "7", "8"));
	}

	@Test
	public void readParallelStream() throws IOException {
		final var objects = IntStream.range(0, 1000)
			.mapToObj(String::valueOf)
			.collect(Collectors.toSet());

		final var out = new ByteArrayOutputStream();
		Serializer.write(objects, out, false);

		final var read = Serializer.objects(new ByteArrayInputStream(out.toByteArray()))
			.parallel()
			.collect(Collectors.toSet());

		Assert.assertEquals(read, objects);
	}

}
