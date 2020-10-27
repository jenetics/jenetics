package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
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
	interface ExceptionMethod<A, E extends Exception> {
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
	interface ExceptionFunction<A, R, E extends Exception> {
		R apply(final A arg) throws E;
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
	 * @see #withCloseables(ExceptionFunction)
	 */
	public static final class Closeables implements Closeable {
		private final List<Closeable> _closeables = new ArrayList<>();

		/**
		 * Create a new {@code Closeables} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @param closeables the initial closeables objects
		 */
		public Closeables(final Iterable<? extends Closeable> closeables) {
			closeables.forEach(_closeables::add);
		}

		/**
		 * Create a new {@code Closeables} object with the given initial
		 * {@code closeables} objects.
		 *
		 * @param closeables the initial closeables objects
		 */
		public Closeables(final Closeable... closeables) {
			this(Arrays.asList(closeables));
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

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is wrapped into an {@link UncheckedIOException}.
		 */
		public void uncheckedClose() {
			uncheckedClose(_closeables);
		}

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is ignored, or appended to the suppressed exception list of the given
		 * {@code previousError}, if not {@code null}.
		 *
		 * @param previousError the error, which triggers the close of the
		 *        registered closeables
		 */
		public void silentClose(final Throwable previousError) {
			silentClose(_closeables, previousError);
		}

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is ignored
		 */
		public void silentClose() {
			silentClose(_closeables,null);
		}

		@Override
		public void close() throws IOException {
			close(_closeables);
		}

		/**
		 * Closes all given {@link Closeable} objects. It is guaranteed that all
		 * {@link Closeable#close()} are called, even if a closeable in between
		 * throws an exception. In the case of an error, the first thrown exception
		 * is thrown, the other errors are added to the <em>suppressed</em> exception
		 * list.
		 *
		 * @param closeables the objects to close.
		 * @throws IOException the exception of the first failed stop call.
		 */
		public static void close(final Iterable<? extends Closeable> closeables)
			throws IOException
		{
			Lifecycle.invokeAll(Closeable::close, closeables);
		}

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is wrapped into an {@link UncheckedIOException}.
		 *
		 * @see #close(Iterable)
		 *
		 * @param closeables the objects to close
		 */
		public static void uncheckedClose(final Iterable<? extends Closeable> closeables) {
			try {
				close(closeables);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is ignored, or appended to the suppressed exception list of the given
		 * {@code previousError}, if not {@code null}.
		 *
		 * <pre>{@code
		 * final var closeables = new ArrayList<MyCloseable>();
		 * try {
		 *     closeable.add(new MyCloseable());
		 *     closeable.add(new MyCloseable());
		 *     closeable.add(new MyCloseable());
		 *
		 *     // The caller is responsible for closing resources after the
		 *     // successful creation of the closeable resources.
		 *     return new ResultObject(closeables);
		 * } catch (Throwable error) {
		 *     // Closes the already created resources, in the case of an error.
		 *     closeables.silentClose(closeables, error);
		 *     throw error;
		 * }
		 * }</pre>
		 *
		 * @see #silentClose(Iterable)
		 *
		 * @param closeables the objects to close
		 * @param previousError the error, which triggers the close of the given
		 *        {@code closeables}
		 */
		public static void silentClose(
			final Iterable<? extends Closeable> closeables,
			final Throwable previousError
		) {
			try {
				close(closeables);
			} catch (Exception ignore) {
				if (previousError != null) {
					previousError.addSuppressed(ignore);
				}
			}
		}

		/**
		 * Closes all given {@link Closeable} objects. A thrown {@link IOException}
		 * is ignored.
		 *
		 * @see #silentClose(Iterable, Throwable)
		 *
		 * @param closeables the objects to close
		 */
		public static void silentClose(final Iterable<? extends Closeable> closeables) {
			silentClose(closeables, null);
		}

	}

	/**
	 * Wraps a {@link Path} object which will be deleted on {@link #close()}.
	 */
	public static class DeletablePath implements Closeable {
		private final Path _path;

		public DeletablePath(final Path path) {
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
		final ExceptionMethod<A, E> method,
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
		final ExceptionMethod<A, E> method,
		final Iterable<? extends A> objects
	) {
		Throwable error = null;
		for (var object : objects) {
			if (error != null) {
				try {
					method.apply(object);
				} catch (Exception suppressed) {
					error.addSuppressed(suppressed);
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
	public static <T, E extends Exception> T
	withCloseables(final ExceptionFunction<Closeables, T, E> block) throws E {
		final var closeables = new Closeables();
		try {
			return block.apply(closeables);
		} catch (Throwable error) {
			closeables.silentClose(error);
			throw error;
		}
	}

}
