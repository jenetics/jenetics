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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.internal.util.Lifecycle.IOValue;
import io.jenetics.internal.util.Lifecycle.Releasable;
import io.jenetics.internal.util.Lifecycle.Value;

public class LifecycleTest {

	static final class Invokable {
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
		final Class<? extends Throwable> errorType,
		final Class<? extends Throwable> suppressedType
	) {
		assertThat(objects).allMatch(i -> !i.called.get());

		final var exception = Lifecycle.invokeAll0(Invokable::invoke, objects);

		assertThat(objects).allMatch(i -> i.called.get());
		if (exception != null) {
			assertThat(exception).isExactlyInstanceOf(errorType);

			if (suppressedType != null) {
				assertThat(exception.getSuppressed()).hasSize(1);
				assertThat(exception.getSuppressed()[0])
					.isExactlyInstanceOf(suppressedType);
			}
		} else {
			assertThat(errorType).isNull();
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

	public void close1() throws IOException {
		final var count = new AtomicInteger();

		assertThatThrownBy(() -> {
			final var closeables = Releasable.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IOException(); },
				count::incrementAndGet
			);
			closeables.close();
		})
			.isExactlyInstanceOf(IOException.class);

		assertThat(count.get()).isEqualTo(3);
	}

	public void close2() throws Exception {
		final var count = new AtomicInteger();

		assertThatThrownBy(() -> {
			final var closeables = Releasable.of(
				count::incrementAndGet,
				count::incrementAndGet,
				() -> { throw new IllegalArgumentException(); },
				count::incrementAndGet,
				() -> { throw new IOException(); }
			);
			closeables.close();
		})
			.isExactlyInstanceOf(IllegalArgumentException.class)
			.satisfies(e -> {
				assertThat(count.get()).isEqualTo(3);
				assertThat(e.getSuppressed()).hasSize(1);
				assertThat(e.getSuppressed()[0])
					.isExactlyInstanceOf(IllegalArgumentException.class);
			});
	}

	public void extendedCloseableClose() throws Exception {
		final var closeable = Releasable.of(
			() -> { throw new IOException(); }
		);

		assertThatThrownBy(closeable::close)
			.isExactlyInstanceOf(IOException.class);
	}

	public void extendedCloseableUncheckedClose() {
		final var closeable = Releasable.of(
			() -> { throw new IOException(); }
		);

		assertThatThrownBy(() -> closeable.release(UncheckedIOException::new))
			.isExactlyInstanceOf(UncheckedIOException.class);
	}

	@Test
	public void extendedCloseableSilentClose() {
		final var closeable = Releasable.of(
			() -> { throw new IOException(); }
		);
		closeable.silentRelease();
	}

	@Test
	public void extendedCloseableSilentCloseWithPrimaryError() {
		final var closeable = Releasable.of(
			() -> { throw new IOException(); }
		);

		final var primary = new IllegalArgumentException();
		closeable.silentRelease(primary);

		assertThat(primary.getSuppressed()).hasSize(1);
		assertThat(primary.getSuppressed()[0]).isExactlyInstanceOf(IOException.class);
	}

	@Test
	public void closeableValue() throws Exception {
		final var closeable = new Value<>(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);

		assertThat(closeable.get().get()).isZero();
		closeable.close();
		assertThat(closeable.get().get()).isEqualTo(1);
	}

	@Test
	public void closeableValueCanOnlyBeClosedOnce() throws Exception {
		final var closeable = new Value<>(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);

		closeable.close();
		closeable.close();

		assertThat(closeable.get().get()).isEqualTo(1);
	}

	@Test
	public void closeableValueConcurrentCloseIsThreadSafe() throws Exception {
		final var closeable = new Value<>(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);
		final var start = new CountDownLatch(1);
		final var done = new CountDownLatch(16);
		final var error = new AtomicReference<Throwable>();

		for (int i = 0; i < 16; ++i) {
			Thread.ofVirtual().start(() -> {
				try {
					start.await();
					closeable.close();
				} catch (Throwable e) {
					error.compareAndSet(null, e);
				} finally {
					done.countDown();
				}
			});
		}

		start.countDown();
		done.await();

		assertThat(error.get()).isNull();
		assertThat(closeable.get().get()).isEqualTo(1);
	}

	@Test
	public void buildCloseableValue() throws Exception {
		final var resource1 = atomic();
		final var resource2 = atomic();
		final var resource3 = atomic();

		final var closeable = new Value<>(resources -> {
			resources.use(resource1, Value::close);
			resources.use(resource2, Value::close);
			resources.use(resource3, Value::close);
			return 123;
		});

		assertThat(closeable.get()).isEqualTo(123);
		assertThat(resource1.get().get()).isZero();
		assertThat(resource2.get().get()).isZero();
		assertThat(resource3.get().get()).isZero();
		closeable.close();
		assertThat(resource1.get().get()).isEqualTo(1);
		assertThat(resource2.get().get()).isEqualTo(1);
		assertThat(resource3.get().get()).isEqualTo(1);
	}

	private static Value<AtomicInteger, RuntimeException> atomic() {
		return new Value<>(
			new AtomicInteger(),
			AtomicInteger::incrementAndGet
		);
	}

	public void buildCloseableValueWithError() throws IOException {
		final var resource1 = atomic();
		final var resource2 = atomic();
		final var resource3 = atomic();

		assertThatThrownBy(() -> {
			new Value<>(resources -> {
				resources.use(resource1, Value::close);
				resources.use(resource2, Value::close);
				resources.use(resource3, Value::close);
				throw new IOException();
			});
		})
			.isExactlyInstanceOf(IOException.class);

		assertThat(resource1.get().get()).isEqualTo(1);
		assertThat(resource2.get().get()).isEqualTo(1);
		assertThat(resource3.get().get()).isEqualTo(1);
	}

	private static Value<Path, IOException> tempFile() throws IOException {
		final var file = new IOValue<>(
			Files.createFile(Path.of("foo")),
			Files::deleteIfExists
		);
		try (file) {
			System.out.println("asdf: " + file.get());
		}

		return new Value<>(
			Files.createTempFile("Lifecycle", "TEST"),
			Files::deleteIfExists
		);
	}

	@Test
	public void closeableValueTrying() throws IOException {
		final var file = tempFile();
		file.trying(f -> f.toFile().deleteOnExit());

		try (file) {
			assertThat(Files.exists(file.get())).isTrue();
			Files.write(file.get(), "foo".getBytes());
			assertThat(Files.readString(file.get())).isEqualTo("foo");
		}
		assertThat(Files.exists(file.get())).isFalse();
	}

}
