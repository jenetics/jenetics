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
import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interfaces for handling resource ({@link Closeable}) objects. The common
 * use cases are shown as follows:
 * <p><b>Wrapping <em>non</em>-closeable values</b></p>
 * <pre>{@code
 * final CloseableValue<Path> file = CloseableValue.of(
 *     Files.createTempFile("test-", ".txt" ),
 *     Files::deleteIfExists
 * );
 *
 * // Automatically delete the file after the test.
 * try (file) {
 *     Files.write(file.get(), "foo".getBytes());
 *     final var writtenText = Files.readString(file.get());
 *     assert "foo".equals(writtenText);
 * }
 * }</pre>
 *
 * <p><b>Building complex closeable values</b></p>
 * <pre>{@code
 * final CloseableValue<Stream<Object>> result = CloseableValue.build(resources -> {
 *     final var fin = resources.add(new FileInputStream(file.toFile()));
 *     final var bin = resources.add(new BufferedInputStream(fin));
 *     final var oin = resources.add(new ObjectInputStream(bin));
 *
 *     return Stream.generate(() -> readNextObject(oin))
 *         .takeWhile(Objects::nonNull);
 * });
 *
 * try (result) {
 *     result.get().forEach(System.out::println);
 * }
 * }</pre>
 *
 * <p><b>Wrapping several closeables into one</b></p>
 * <pre>{@code
 * try (var c = ExtendedCloseable.of(c1, c2, c3)) {
 *     ...
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class Lifecycle {

	/**
	 * A method which takes an argument and can throw an exception.
	 *
	 * @param <A> the argument type
	 * @param <E> the exception type
	 */
	@FunctionalInterface
	public interface ThrowingMethod<A, E extends Exception> {
		void apply(final A arg) throws E;
	}

	/**
	 * A function which takes an argument and can throw an exception.
	 *
	 * @param <A> the argument type
	 * @param <R> the return type
	 * @param <E> the exception type
	 */
	@FunctionalInterface
	public interface ThrowingFunction<A, R, E extends Exception> {
		R apply(final A arg) throws E;
	}

	/**
	 * Specialisation of the {@link Closeable} interface, which throws an
	 * {@link UncheckedIOException} instead of an {@link IOException}.
	 */
	public interface UncheckedCloseable extends Closeable {

		@Override
		void close() throws UncheckedIOException;

		/**
		 * Wraps a given {@code closeable} object and returns an
		 * {@link UncheckedCloseable}.
		 *
		 * @param closeable the <em>normal</em> closeable object to wrap
		 * @return a new unchecked closeable with the given underlying
		 *         {@code closeable} object
		 * @throws NullPointerException if the given {@code closeable} is
		 *         {@code null}
		 */
		static UncheckedCloseable of(final Closeable closeable) {
			requireNonNull(closeable);

			return () -> {
				try {
					closeable.close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};
		}
	}

	/**
	 * Extends the {@link Closeable} with methods for wrapping the thrown
	 * exception into an {@link UncheckedIOException} or ignoring them.
	 */
	public interface ExtendedCloseable extends Closeable {

		/**
		 * Calls the {@link #close()} method and wraps thrown {@link IOException}
		 * into an {@link UncheckedIOException}.
		 *
		 * @throws UncheckedIOException if the {@link #close()} method throws
		 *         an {@link IOException}
		 */
		default void uncheckedClose() {
			try {
				close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		/**
		 * Calls the {@link #close()} method and ignores every thrown exception.
		 */
		default void silentClose() {
			silentClose(null);
		}

		/**
		 * Calls the {@link #close()} method and ignores every thrown exception.
		 * If the given {@code previousError} is <em>non-null</em>, the thrown
		 * exception is appended to the list of suppressed exceptions.
		 *
		 * @param previousError the error, which triggers the close of the given
		 *        {@code closeables}
		 */
		default void silentClose(final Throwable previousError) {
			try {
				close();
			} catch (Exception suppressed) {
				if (previousError != null) {
					previousError.addSuppressed(suppressed);
				}
			}
		}

		/**
		 * Wraps a given {@code closeable} object and returns an
		 * {@link ExtendedCloseable}.
		 *
		 * @param closeable the <em>normal</em> closeable object to wrap
		 * @return a new extended closeable with the given underlying
		 *         {@code closeable} object
		 * @throws NullPointerException if the given {@code closeable} is
		 *         {@code null}
		 */
		static ExtendedCloseable of(final Closeable closeable) {
			requireNonNull(closeable);
			return closeable::close;
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given initial
		 * {@code closeables} objects. The given list of objects are closed in
		 * reversed order.
		 *
		 * @see #of(Collection)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new closeable object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		static ExtendedCloseable of(final Closeable... closeables) {
			return of(Arrays.asList(closeables));
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given
		 * {@code closeables} objects. The given list of objects are closed in
		 * reversed order.
		 *
		 * @see #of(Closeable...)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new closeable object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		static ExtendedCloseable
		of(final Collection<? extends Closeable> closeables) {
			final List<Closeable> list = new ArrayList<>();
			closeables.forEach(c -> list.add(requireNonNull(c)));
			Collections.reverse(list);

			return () -> {
				if (list.size() == 1) {
					list.get(0).close();
				} else if (list.size() > 1) {
					Lifecycle.invokeAll(Closeable::close, list);
				}
			};
		}

	}

	/**
	 * This interface represents a closeable value. It is useful in cases where
	 * the value doesn't implement the {@link Closeable} interface but needs
	 * some cleanup work to do after usage.
	 *
	 * <pre>{@code
	 * final CloseableValue<Path> file = CloseableValue.of(
	 *     Files.createTempFile("test-", ".txt" ),
	 *     Files::deleteIfExists
	 * );
	 *
	 * // Automatically delete the file after the test.
	 * try (file) {
	 *     Files.write(file.get(), "foo".getBytes());
	 *     final var writtenText = Files.readString(file.get());
	 *     assert "foo".equals(writtenText);
	 * }
	 * }</pre>
	 *
	 * @see #of(Object, ThrowingMethod)
	 * @see #build(ThrowingFunction)
	 *
	 * @param <T> the value type
	 */
	public interface CloseableValue<T> extends Supplier<T>, ExtendedCloseable {

		/**
		 * Applies the give {@code block} to the closeable value. If the
		 * {@code block} throws an exception, {@code this} value is closed. The
		 * typical use case for this method is when additional initialization
		 * of the value is needed.
		 *
		 * <pre>{@code
		 * final var file = CloseableValue.of(
		 *     Files.createTempFile("Lifecycle", "TEST").toFile(),
		 *     f -> Files.deleteIfExists(f.toPath())
		 * );
		 * file.using(File::deleteOnExit);
		 *
		 * try (file) {
		 *     // Do something with temp file.
		 * }
		 * }</pre>
		 *
		 * @param block the codec block which is applied to the value
		 * @param <E> the thrown exception type
		 * @throws E if applying the {@code block} throws an exception
		 */
		default <E extends Exception>
		void using(final ThrowingMethod<? super T, ? extends E> block) throws E {
			try {
				block.apply(get());
			} catch (Throwable error) {
				silentClose(error);
				throw error;
			}
		}

		/**
		 * Maps {@code this} closeable value with the given {@code mapper}
		 * function. If the mapping function throws an exception, {@code this}
		 * value is closed.
		 *
		 * <pre>{@code
		 * final var file = CloseableValue.of(
		 *     Files.createTempFile("Lifecycle", "TEST"),
		 *     Files::deleteIfExists
		 * );
		 *
		 * try (var name = file.map(Path::getFileName)) {
		 *     // Do something with the file name.
		 * }
		 * }</pre>
		 *
		 * @param mapper the mapping function to apply to a value
		 * @param <B> the type of the value returned from the mapping function
		 * @param <E> the thrown exception type
		 * @return the mapped closeable value
		 * @throws E if applying the {@code block} throws an exception
		 */
		default <B, E extends Exception> CloseableValue<B>
		map(final ThrowingFunction<? super T, ? extends B, ? extends E> mapper)
			throws E
		{
			try {
				return of(mapper.apply(get()), v -> close());
			} catch (Throwable error) {
				silentClose(error);
				throw error;
			}
		}

		/**
		 * Create a new closeable value with the given {@code value} and the
		 * {@code close} method.
		 *
		 * @param value the actual value
		 * @param close the {@code close} method for the given {@code value}
		 * @param <T> the value type
		 * @return a new closeable value
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		static <T> CloseableValue<T> of(
			final T value,
			final ThrowingMethod<? super T, ? extends IOException> close
		) {
			requireNonNull(value);
			requireNonNull(close);

			return new CloseableValue<>() {
				@Override
				public T get() {
					return value;
				}
				@Override
				public void close() throws IOException {
					close.apply(get());
				}
				@Override
				public String toString() {
					return format("CloseableValue[%s]", get());
				}
			};
		}

		/**
		 * Opens a kind of {@code try-catch} with resources block. The difference
		 * is, that the resources, registered with the
		 * {@link ResourceCollector#add(Closeable)} method, are only closed in
		 * the case of an error. If the <em>value</em> could be created, the
		 * caller is responsible for closing the opened <em>resources</em> by
		 * calling the {@link CloseableValue#close()} method.
		 *
		 * <pre>{@code
		 * final CloseableValue<Stream<Object>> result = CloseableValue.build(resources -> {
		 *     final var fin = resources.add(new FileInputStream(file.toFile()));
		 *     final var bin = resources.add(new BufferedInputStream(fin));
		 *     final var oin = resources.add(new ObjectInputStream(bin));
		 *
		 *     return Stream.generate(() -> readNextObject(oin))
		 *         .takeWhile(Objects::nonNull);
		 * });
		 *
		 * try (result) {
		 *     result.get().forEach(System.out::println);
		 * }
		 * }</pre>
		 *
		 * @see ResourceCollector
		 *
		 * @param builder the builder method
		 * @param <T> the value type of the created <em>closeable</em> value
		 * @param <E> the thrown exception type while building the value
		 * @return the closeable built value
		 * @throws E in the case of an error. If this exception is thrown, all
		 *         <em>registered</em> resources are closed.
		 * @throws NullPointerException if the given {@code builder} is
		 *         {@code null}
		 */
		static <T, E extends Exception> CloseableValue<T>
		build(
			final ThrowingFunction<
				? super ResourceCollector,
				? extends T,
				? extends E> builder
		)
			throws E
		{
			requireNonNull(builder);

			final var resources = ResourceCollector.of();
			try {
				return CloseableValue.of(
					builder.apply(resources),
					value -> resources.close()
				);
			} catch (Throwable error) {
				resources.silentClose(error);
				throw error;
			}
		}

	}

	/**
	 * This class allows to collect one or more {@link Closeable} objects into
	 * one. The registered closeable objects are closed in reverse order.
	 * <p>
	 * Using the {@code ResourceCollector} class can simplify the the creation of
	 * dependent input streams, where it might be otherwise necessary to create
	 * nested {@code try-with-resources} blocks.
	 *
	 * <pre>{@code
	 * try (var resources = ResourceCollector.of()) {
	 *     final var fin = resources.add(new FileInputStream(file));
	 *     if (fin.read() != -1) {
	 *         return;
	 *     }
	 *     final var oin = resources.add(new ObjectInputStream(fin));
	 *     // ...
	 * }
	 * }</pre>
	 *
	 * @see CloseableValue#build(ThrowingFunction)
	 */
	public interface ResourceCollector extends ExtendedCloseable {

		/**
		 * Registers the given {@code closeable} to the list of managed
		 * closeables.
		 *
		 * @param closeable the new closeable to register
		 * @param <C> the closeable type
		 * @return the registered closeable
		 */
		<C extends Closeable> C add(final C closeable);

		/**
		 * Create a new closeable object from a snapshot of the currently
		 * registered resources.
		 *
		 * @see ExtendedCloseable#of(Collection)
		 *
		 * @return a new closeable object
		 */
		ExtendedCloseable toCloseable();

		@Override
		default void close() throws IOException {
			toCloseable().close();
		}

		/**
		 * Create a new {@code ResourceCollector} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @see #of(Closeable...)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new resource collector object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		static ResourceCollector
		of(final Collection<? extends Closeable> closeables) {
			final List<Closeable> resources = new ArrayList<>();
			closeables.forEach(c -> resources.add(requireNonNull(c)));

			return new ResourceCollector() {
				@Override
				public synchronized <C extends Closeable>
				C add(final C closeable) {
					resources.add(requireNonNull(closeable));
					return closeable;
				}
				@Override
				public synchronized ExtendedCloseable toCloseable() {
					return ExtendedCloseable.of(resources);
				}
			};
		}

		/**
		 * Create a new {@code ResourceCollector} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @see #of(Collection)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new closeable object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		static ResourceCollector of(final Closeable... closeables) {
			return of(Arrays.asList(closeables));
		}

	}

	private Lifecycle() {
	}

	/**
	 * Invokes the {@code method} on all given {@code objects}, no matter if one
	 * of the method invocations throws an exception. The first exception thrown
	 * is rethrown after invoking the method on the remaining objects, all other
	 * exceptions are swallowed.
	 *
	 * <pre>{@code
	 * final var streams = new ArrayList<InputStream>();
	 * streams.add(new FileInputStream(file1));
	 * streams.add(new FileInputStream(file2));
	 * streams.add(new FileInputStream(file3));
	 * // ...
	 * invokeAll(Closeable::close, streams);
	 * }</pre>
	 *
	 * @param <A> the closeable object type
	 * @param <E> the exception type
	 * @param objects the objects where the methods are called.
	 * @param method the method which is called on the given object.
	 * @throws E the first exception thrown by the one of the method
	 *         invocation.
	 */
	static <A, E extends Exception> void invokeAll(
		final ThrowingMethod<? super A, ? extends E> method,
		final Collection<? extends A> objects
	)
		throws E
	{
		raise(invokeAll0(method, objects));
	}

	private static <E extends Exception> void raise(final Throwable error)
		throws E
	{
		if (error instanceof RuntimeException) {
			throw (RuntimeException)error;
		} else if (error instanceof Error) {
			throw (Error)error;
		} else if (error != null) {
			@SuppressWarnings("unchecked")
			final var e = (E)error;
			throw e;
		}
	}

	private static final int MAX_SUPPRESSED = 5;

	/**
	 * Invokes the {@code method}> on all given {@code objects}, no matter if one
	 * of the method invocations throws an exception. The first exception thrown
	 * is returned, all other exceptions are swallowed.
	 *
	 * @param objects the objects where the methods are called.
	 * @param method the method which is called on the given object.
	 * @return the first exception thrown by the method invocation or {@code null}
	 *         if no exception has been thrown
	 */
	static <A, E extends Exception> Throwable invokeAll0(
		final ThrowingMethod<? super A, ? extends E> method,
		final Collection<? extends A> objects
	) {
		int suppressedCount = 0;
		Throwable error = null;
		for (var object : objects) {
			if (error != null) {
				try {
					method.apply(object);
				} catch (Exception suppressed) {
					if (suppressedCount++ < MAX_SUPPRESSED) {
						error.addSuppressed(suppressed);
					}
				}
			} else {
				try {
					method.apply(object);
				} catch (Throwable e) {
					error = e;
				}
			}
		}

		return error;
	}

}
