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

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interfaces and classes for handling resource ({@link Closeable}) objects.
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
		public static UncheckedCloseable of(final Closeable closeable) {
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
		public default void uncheckedClose() {
			try {
				close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		/**
		 * Calls the {@link #close()} method and ignores every thrown exception.
		 */
		public default void silentClose() {
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
		public default void silentClose(final Throwable previousError) {
			try {
				close();
			} catch (Exception ignore) {
				if (previousError != null) {
					previousError.addSuppressed(ignore);
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
		public static ExtendedCloseable of(final Closeable closeable) {
			return closeable::close;
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
	 *     Files.write(file.value(), "foo".getBytes());
	 *     final var writtenText = Files.readString(file.value());
	 *     assert "foo".equals(writtenText);
	 * }
	 * }</pre>
	 *
	 * @see #build(ThrowingFunction)
	 *
	 * @param <T> the value type
	 */
	public interface CloseableValue<T> extends ExtendedCloseable {

		/**
		 * Return the wrapped value.
		 *
		 * @return the wrapped value
		 */
		public T value();

		/**
		 * Create a new closeable value with the given {@code value} and the
		 * {@code close} method.
		 *
		 * @param value the actual value
		 * @param close the {@code close} method for the given {@code value}
		 * @param <T> the value type
		 * @return a new closeable value
		 */
		public static <T> CloseableValue<T> of(
			final T value,
			final ThrowingMethod<? super T, ? extends IOException> close
		) {
			return new CloseableValue<T>() {
				@Override
				public T value() {
					return value;
				}

				@Override
				public void close() throws IOException {
					close.apply(value());
				}
			};
		}

		/**
		 * Opens a kind of {@code try-catch} with resources block. The difference
		 * is, that the resources, registered with the {@link Closeables#add(Closeable)}
		 * method, are only closed in the case of an error. If the <em>value</em>
		 * could be created, the caller is responsible for closing the opened
		 * <em>resources</em> by calling the {@link CloseableValue#close()} method.
		 *
		 * <pre>{@code
		 * final CloseableValue<Stream<Object>> result = build(resources -> {
		 *     final var fin = resources.add(new FileInputStream(file.toFile()));
		 *     final var bin = resources.add(new BufferedInputStream(fin));
		 *     final var oin = resources.add(new ObjectInputStream(bin));
		 *
		 *     return Stream.generate(() -> readNextObject(oin))
		 *         .takeWhile(Objects::nonNull);
		 * });
		 *
		 * try (result) {
		 *     result.value().forEach(System.out::println);
		 * }
		 * }</pre>
		 *
		 * @see Closeables
		 *
		 * @param builder the <em>protected</em> builder method
		 * @param <T> the value type of the created <em>closeable</em> value
		 * @param <E> the thrown exception type while building the value
		 * @return the closeable built value
		 * @throws E in the case of an error. If this exception is thrown, all
		 *         <em>registered</em> resources are closed.
		 */
		public static <T, E extends Exception> CloseableValue<T>
		build(
			final ThrowingFunction<
				? super Closeables,
				? extends T,
				? extends E> builder
		)
			throws E
		{
			final var closeables = new Closeables();
			try {
				return CloseableValue.of(
					builder.apply(closeables),
					value -> closeables.close()
				);
			} catch (Throwable error) {
				closeables.silentClose(error);
				throw error;
			}
		}

	}

	/**
	 * This class allows to collect one or more {@link Closeable} objects into
	 * one. Calling the {@link #close()} method of this class will call the
	 * close methods of all registered resources, added with the
	 * {@link #add(Closeable)} method, even if one of this resources throws an
	 * exception.
	 * <p>
	 * Using the {@code Closeables} class can simplify the the creation of
	 * dependent input streams, where it might be otherwise necessary to create
	 * nested {@code try-with-resources} blocks.
	 *
	 * <pre>{@code
	 * try (var resources = new Closeables()) {
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
	public static final class Closeables implements ExtendedCloseable {
		private final List<Closeable> _closeables = new ArrayList<>();

		/**
		 * Create a new {@code Closeables} object.
		 */
		public Closeables() {
		}

		/**
		 * Registers the given {@code closeable} to the list of managed
		 * closeables.
		 *
		 * @param closeable the new closeable to register
		 * @param <C> the closeable type
		 * @return the registered closeable
		 */
		public <C extends Closeable> C add(final C closeable) {
			_closeables.add(requireNonNull(closeable));
			return closeable;
		}

		@Override
		public void close() throws IOException {
			if (_closeables.size() == 1) {
				_closeables.get(0).close();
			} else if (_closeables.size() > 1) {
				Lifecycle.invokeAll(Closeable::close, _closeables);
			}
		}

		/**
		 * Create a new {@code Closeables} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @see #of(Closeable...)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new closeable object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		public static Closeables of(final Iterable<? extends Closeable> closeables) {
			final var result = new Closeables();
			closeables.forEach(c -> result._closeables.add(requireNonNull(c)));
			return result;
		}

		/**
		 * Create a new {@code Closeables} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @see #of(Iterable)
		 *
		 * @param closeables the initial closeables objects
		 * @return a new closeable object which collects the given
		 *        {@code closeables}
		 * @throws NullPointerException if one of the {@code closeables} is
		 *         {@code null}
		 */
		public static Closeables of(final Closeable... closeables) {
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
		final Iterable<? extends A> objects
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
		final Iterable<? extends A> objects
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
