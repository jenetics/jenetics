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
package io.jenetics.internal.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Lifecycle.ExtendedCloseable;
import io.jenetics.internal.util.Lifecycle.Value;

public class LifecycleTest {

	final static class Invokable {
		final AtomicBoolean called = new AtomicBoolean(false);
		final Supplier<Exception> error;

		Invokable(final Supplier<Exception> error) {
			this.error = error;
		}

		void invoke() throws Exception {
			called.set(true);
			final var e = error != null ? error.get() : null;
			if (e != null) {
				throw e;
			}
		}
	}

	@Test(dataProvider = "invokables")
	public void invokeAll0(
		final List<Invokable> objects,
		final Class<?> errorType,
		final Class<?> suppressedType
	) {
		Assert.assertTrue(objects.stream().noneMatch(i -> i.called.get()));

		final var exception = Lifecycle.invokeAll0(Invokable::invoke, objects);

		Assert.assertTrue(objects.stream().allMatch(i -> i.called.get()));
		if (exception != null) {
			Assert.assertEquals(exception.getClass(), errorType);

			if (suppressedType != null) {
				Assert.assertEquals(exception.getSuppressed().length, 1);
				Assert.assertEquals(
					exception.getSuppressed()[0].getClass(),
					suppressedType
				);
			}
		}
	}

	@DataProvider
	public Object[][] invokables() {
		return new Object[][] {
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(null)
				),
				null,
				null
			},
			{
				List.of(new Invokable(IllegalArgumentException::new)),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(IllegalArgumentException::new)
				),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(IllegalArgumentException::new),
					new Invokable(null),
					new Invokable(null)
				),
				IllegalArgumentException.class,
				null
			},
			{
				List.of(
					new Invokable(null),
					new Invokable(null),
					new Invokable(UnsupportedOperationException::new),
					new Invokable(null),
					new Invokable(NoSuchFieldException::new),
					new Invokable(null),
					new Invokable(null)
				),
				UnsupportedOperationException.class,
				NoSuchFieldException.class
			}
		};
	}

	@Test(expectedExceptions = IOException.class)
	public void close1() throws IOException {
		final var count = new AtomicInteger();

		try {
			final var closeables = ExtendedCloseable.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IOException(); },
				count::incrementAndGet
			);
			closeables.close();
		} catch (IOException e) {
			Assert.assertEquals(3, count.get());
			throw e;
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void close2() throws Exception {
		final var count = new AtomicInteger();

		try {
			final var closeables = ExtendedCloseable.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IllegalArgumentException(); },
				count::incrementAndGet,
				() -> { throw new IOException(); }
			);
			closeables.close();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(3, count.get());
			Assert.assertEquals(e.getSuppressed().length, 1);
			Assert.assertEquals(
				e.getSuppressed()[0].getClass(),
				IllegalArgumentException.class
			);
			throw e;
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void extendedCloseableClose() throws Exception {
		final var closeable = ExtendedCloseable.of(
			() -> { throw new IOException(); }
		);
		closeable.close();
	}

	@Test(expectedExceptions = UncheckedIOException.class)
	public void extendedCloseableUncheckedClose() {
		final var closeable = ExtendedCloseable.of(
			() -> { throw new IOException(); }
		);
		closeable.uncheckedClose(UncheckedIOException::new);
	}

	@Test
	public void extendedCloseableSilentClose() {
		final var closeable = ExtendedCloseable.of(
			() -> { throw new IOException(); }
		);
		closeable.silentClose();
	}

	@Test
	public void extendedCloseableSilentCloseWithPrimaryError() {
		final var closeable = ExtendedCloseable.of(
			() -> { throw new IOException(); }
		);

		final var primary = new IllegalArgumentException();
		closeable.silentClose(primary);

		Assert.assertEquals(
			primary.getSuppressed()[0].getClass(),
			IOException.class
		);
	}

	@Test
	public void closeableValue() throws Exception {
		final var closeable = Value.of(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);

		Assert.assertEquals(0, closeable.get().get());
		closeable.close();
		Assert.assertEquals(1, closeable.get().get());
	}

	@Test
	public void buildCloseableValue() throws Exception {
		final var resource1 = atomic();
		final var resource2 = atomic();
		final var resource3 = atomic();

		final var closeable = Value.build(resources -> {
			resources.add(resource1, Value::close);
			resources.add(resource2, Value::close);
			resources.add(resource3, Value::close);
			return 123;
		});

		Assert.assertEquals(123, closeable.get().intValue());
		Assert.assertEquals(0, resource1.get().get());
		Assert.assertEquals(0, resource2.get().get());
		Assert.assertEquals(0, resource3.get().get());
		closeable.close();
		Assert.assertEquals(1, resource1.get().get());
		Assert.assertEquals(1, resource2.get().get());
		Assert.assertEquals(1, resource3.get().get());
	}

	private static Value<AtomicInteger, RuntimeException> atomic() {
		return Value.of(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);
	}

	@Test(expectedExceptions = IOException.class)
	public void buildCloseableValueWithError() throws IOException {
		final var resource1 = atomic();
		final var resource2 = atomic();
		final var resource3 = atomic();

		try {
			Value.build(resources -> {
				resources.add(resource1, Value::close);
				resources.add(resource2, Value::close);
				resources.add(resource3, Value::close);
				throw new IOException();
			});
		} catch (IOException e) {
			Assert.assertEquals(1, resource1.get().get());
			Assert.assertEquals(1, resource2.get().get());
			Assert.assertEquals(1, resource3.get().get());
			throw e;
		}
	}

	private static Value<Path, IOException> tempFile() throws IOException {
		final var file = Value.of(
			Files.createFile(Path.of("foo")),
			Files::deleteIfExists
		);
		try (file) {
			System.out.println("asdf: " + file.get());
		}

		return Value.of(
			Files.createTempFile("Lifecycle", "TEST"),
			Files::deleteIfExists
		);
	}

	@Test
	public void closeableValueTrying() throws IOException {
		final var file = tempFile();
		file.trying(f -> f.toFile().deleteOnExit());

		try (file) {
			Assert.assertTrue(Files.exists(file.get()));
			Files.write(file.get(), "foo".getBytes());
			Assert.assertEquals(Files.readString(file.get()), "foo");
		}
		Assert.assertFalse(Files.exists(file.get()));
	}

}
