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
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper functions for handling resource- and life cycle objects.
 */
public final class Lifecycle {

	/**
	 * A method which takes an argument and throws an exception.
	 *
	 * @param <A> the argument type
	 * @param <E> the exception type
	 */
	@FunctionalInterface
	public interface ExceptionMethod<A, E extends Exception> {
		void apply(final A arg) throws E;
	}

	/**
	 * A function which takes an argument and throws an exception.
	 *
	 * @param <A> the argument type
	 * @param <R> the return type
	 * @param <E> the exception type
	 */
	@FunctionalInterface
	public interface ExceptionFunction<A, R, E extends Exception> {
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
	 * This class allows to collect one or more {@link Closeable} objects into
	 * one.
	 * <pre>{@code
	 * return withCloseables((Closeables streams) -> {
	 *     final var fin = streams.add(new FileInputStream(file.toFile()));
	 *     final var bin = streams.add(new BufferedInputStream(fin));
	 *     final var oin = streams.add(new ObjectInputStream(bin));
	 *
	 *     final Supplier<Object> readObject = () -> {
	 *         ...
	 *     };
	 *
	 *     return Stream.generate(readObject)
	 *         .onClose(streams::uncheckedClose)
	 *         .takeWhile(Objects::nonNull);
	 * });
	 * }</pre>
	 *
	 * @see #withCloseables(ExceptionFunction)
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

	/**
	 * Wraps a {@link Path} object which will be deleted on {@link #close()}.
	 */
	public static final class DeletablePath implements ExtendedCloseable {
		private final Path _path;

		private DeletablePath(final Path path) {
			_path = requireNonNull(path);
		}

		public Path path() {
			return _path;
		}

		@Override
		public void close() throws IOException {
			if (Files.isDirectory(_path, LinkOption.NOFOLLOW_LINKS)) {
				final var files = Files.walk(_path)
					.sorted(Comparator.reverseOrder())
					.collect(Collectors.toList());

				for (var file : files) {
					Files.deleteIfExists(file);
				}
			} else {
				Files.deleteIfExists(_path);
			}
		}

		/**
		 * Wraps the given {@code path} into a deletable path object.
		 *
		 * @param path the {@code path} to be wrapped
		 * @return the wrapped path object
		 * @throws NullPointerException if the given {@code path} is {@code null}
		 */
		public static DeletablePath of(final Path path) {
			return new DeletablePath(path);
		}

		/**
		 * Wraps the given {@code file} into a deletable file object.
		 *
		 * @param file the {@code file} to be wrapped
		 * @return the wrapped file object
		 * @throws NullPointerException if the given {@code file} is {@code null}
		 */
		public static DeletablePath of(final File file) {
			return new DeletablePath(file.toPath());
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
	public static <A, E extends Exception> void invokeAll(
		final ExceptionMethod<? super A, ? extends E> method,
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
		final ExceptionMethod<? super A, ? extends E> method,
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

	/**
	 * Opens an kind of {@code try-catch} with resources block. The difference
	 * is, that the resources are only closed in the case of an error.
	 *
	 * <pre>{@code
	 * return withCloseables(streams -> {
	 *     final var fin = streams.add(new FileInputStream(file.toFile()));
	 *     final var bin = streams.add(new BufferedInputStream(fin));
	 *     final var oin = streams.add(new ObjectInputStream(bin));
	 *
	 *     final Supplier<Object> readObject = () -> {
	 *         try {
	 *             return oin.readObject();
	 *         } catch (EOFException|ClassNotFoundException e) {
	 *             return null;
	 *         } catch (IOException e) {
	 *             throw new UncheckedIOException(e);
	 *         }
	 *     };
	 *
	 *     return Stream.generate(readObject)
	 *         .onClose(streams::uncheckedClose)
	 *         .takeWhile(Objects::nonNull);
	 * });
	 * }</pre>
	 *
	 * @param block the <em>protected</em> code block
	 * @param <T> the return type of the <em>closeable</em> block
	 * @param <E> the thrown exception type
	 * @return the result of the <em>protected</em> block
	 * @throws E in the case of an error. If this exception is thrown, all
	 *         <em>registered</em> closeable objects are closed before.
	 */
	public static <T, E extends Exception> T withCloseables(
		final ExceptionFunction<? super Closeables, ? extends T, ? extends E> block
	)
		throws E
	{
		final var closeables = new Closeables();
		try {
			return block.apply(closeables);
		} catch (Throwable error) {
			closeables.silentClose(error);
			throw error;
		}
	}

}
