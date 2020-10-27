package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper functions for handling resource- and life cycle objects.
 */
public final class Lifecycle {

	@FunctionalInterface
	interface Method<A, E extends Exception> {
		void apply(final A arg) throws E;
	}

	@FunctionalInterface
	interface LifecycleFunction<A, R, E extends Exception> {
		R apply(final A arg) throws E;
	}

	public static final class Closeables implements Closeable {
		private final List<Closeable> _closeables = new ArrayList<>();

		public <C extends Closeable> C add(final C closeable) {
			_closeables.add(requireNonNull(closeable));
			return closeable;
		}

		public void uncheckedClose() {
			try {
				close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		public void silentClose(final Throwable previousError) {
			try {
				close();
			} catch (Exception ignore) {
				if (previousError != null) {
					previousError.addSuppressed(ignore);
				}
			}
		}

		public void silentClose() {
			silentClose(null);
		}

		@Override
		public void close() throws IOException {
			Lifecycle.close(_closeables);
		}
	}

	private Lifecycle() {
	}

	/**
	 * Closes all given closeable objects.
	 *
	 * @param closeables the objects to close.
	 * @throws IOException the exception of the first failed stop call.
	 */
	static void close(final Iterable<? extends Closeable> closeables)
		throws IOException
	{
		invokeAll(Closeable::close, closeables);
	}

	/**
	 * Invokes the {@code method}> on all given {@code objects}, no matter if one
	 * of the method invocations throws an exception. The first exception thrown
	 * is rethrown after invoking the method on the remaining objects, all other
	 * exceptions are swallowed.
	 *
	 * @param objects the objects where the methods are called.
	 * @param method the method which is called on the given object.
	 * @throws E the first exception thrown by the one of the method
	 *         invocation.
	 */
	static <A, E extends Exception> void invokeAll(
		final Method<A, E> method,
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
	 *         if no exception is thrown
	 */
	static <A, E extends Exception> Throwable invokeAll0(
		final Method<A, E> method,
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


	public static <T, E extends Exception> T
	trying(final LifecycleFunction<Closeables, T, E> block) throws E {
		final var closeables = new Closeables();
		try {
			return block.apply(closeables);
		} catch (Throwable error) {
			closeables.silentClose(error);
			throw error;
		}
	}

}
