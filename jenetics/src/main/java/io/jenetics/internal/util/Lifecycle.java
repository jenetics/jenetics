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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interfaces and classes for handling resource ({@link AutoCloseable}) objects.
 * The common use cases are shown as follows:
 * <p><b>Wrapping <em>non</em>-closeable values</b></p>
 * <pre>{@code
 * final Value<Path, IOException> file = Value.of(
 *     Files.createFile(Path.of("some_file")),
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
 * final Value<Stream<Object>, IOException> result = Value.build(resources -> {
 *     final var fin = resources.add(new FileInputStream(file.toFile()), Closeable::close);
 *     final var bin = resources.add(new BufferedInputStream(fin), Closeable::close);
 *     final var oin = resources.add(new ObjectInputStream(bin), Closeable::close);
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
 * try (var __ = ExtendedCloseable.of(c1, c2, c3)) {
 *     ...
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.2
 * @version 6.3
 */
public class Lifecycle {

	/* *************************************************************************
	 *  General interfaces.
	 * ************************************************************************/

	/**
	 * Runnable task/method, which might throw an exception {@code E}.
	 *
	 * @param <E> the exception which might be thrown
	 */
	@FunctionalInterface
	public interface ThrowingRunnable<E extends Exception> {

		/**
		 * Running the task.
		 *
		 * @throws E if an error occurs while running the task
		 */
		void run() throws E;

	}

	/**
	 * A method which takes an argument and can throw an exception.
	 *
	 * @param <A> the argument type
	 * @param <E> the exception type
	 */
	@FunctionalInterface
	public interface ThrowingConsumer<A, E extends Exception> {

		/**
		 * Performs this operation on the given argument.
		 *
		 * @param arg the input argument
		 * @throws E if an error occurs while executing the operation
		 */
		void accept(final A arg) throws E;

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

		/**
		 * Applies this function to the given argument.
		 *
		 * @param arg the function argument
		 * @return the function result
		 * @throws E if an error occurs while applying the function
		 */
		R apply(final A arg) throws E;

	}

	/* *************************************************************************
	 *  Lifecycle interfaces/classes.
	 * ************************************************************************/

	/**
	 * Extends the {@link AutoCloseable} with methods for wrapping the thrown
	 * exception into <em>unchecked</em> exceptions or ignoring them.
	 *
	 * @param <E> the exception thrown by the {@link #close()} method
	 */
	@FunctionalInterface
	public interface ExtendedCloseable<E extends Exception>
		extends AutoCloseable
	{

		@Override
		void close() throws E;

		/**
		 * Calls the {@link #close()} method and wraps thrown {@link Exception}
		 * into an {@link RuntimeException}, mapped by the given {@code mapper}.
		 *
		 * @throws RuntimeException if the {@link #close()} method throws
		 *         an {@link Exception}
		 */
		default void uncheckedClose(
			final Function<
				? super E,
				? extends RuntimeException> mapper
		) {
			try {
				close();
			} catch (Exception e) {
				@SuppressWarnings("unchecked")
				final var error = (E)e;
				throw mapper.apply(error);
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
		 * Wraps a given {@code release} method and returns an
		 * {@link ExtendedCloseable}.
		 *
		 * @param release the release method to wrap
		 * @return a new extended closeable with the given underlying
		 *         {@code release} method
		 * @throws NullPointerException if the given {@code release} method is
		 *         {@code null}
		 */
		static <E extends Exception> ExtendedCloseable<E>
		of(final ThrowingRunnable<? extends E> release) {
			return release::run;
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given initial
		 * release <em>methods</em>>. The given list of objects are closed in
		 * reversed order.
		 *
		 * @see #of(ThrowingRunnable...)
		 *
		 * @param releases the initial release methods
		 * @return a new closeable object which collects the given
		 *        {@code releases}
		 * @throws NullPointerException if one of the {@code releases} is
		 *         {@code null}
		 */
		static <E extends Exception> ExtendedCloseable<E>
		of(final Collection<? extends ThrowingRunnable<? extends E>> releases) {
			final List<ThrowingRunnable<? extends E>> list = new ArrayList<>();
			releases.forEach(c -> list.add(requireNonNull(c)));
			Collections.reverse(list);

			return () -> Lifecycle.invokeAll(ThrowingRunnable::run, list);
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given initial
		 * release <em>methods</em>>. The given list of objects are closed in
		 * reversed order.
		 *
		 * @see #of(Collection)
		 *
		 * @param releases the release methods
		 * @return a new closeable object which collects the given
		 *        {@code releases}
		 * @throws NullPointerException if one of the {@code releases} is
		 *         {@code null}
		 */
		@SafeVarargs
		static <E extends Exception> ExtendedCloseable<E>
		of(final ThrowingRunnable<? extends E>... releases) {
			return of(Arrays.asList(releases));
		}

	}

	/**
	 * This class represents a <em>closeable</em> value. It is useful in cases
	 * where the object value doesn't implement the {@link AutoCloseable}
	 * interface but needs some cleanup work to do after usage. In the following
	 * example the create {@code file} is automatically deleted when leaving the
	 * {@code try} block.
	 *
	 * <pre>{@code
	 * // Create the closeable file.
	 * final Value<Path, IOException> file = Value.of(
	 *     Files.createFile(Path.of("some_file")),
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
	 * @see #of(Object, ThrowingConsumer)
	 * @see #build(ThrowingFunction)
	 *
	 * @param <T> the value type
	 */
	public static final class Value<T, E extends Exception>
		implements Supplier<T>, ExtendedCloseable<E>
	{

		private final T _value;
		private final ThrowingConsumer<? super T, ? extends E> _release;

		private Value(
			final T value,
			final ThrowingConsumer<? super T, ? extends E> release
		) {
			_value = requireNonNull(value);
			_release = requireNonNull(release);
		}

		@Override
		public T get() {
			return _value;
		}

		@Override
		public void close() throws E {
			_release.accept(get());
		}

		@Override
		public String toString() {
			return format("Value[%s]", get());
		}

		/**
		 * Applies the give {@code block} to the already created closeable value.
		 * If the {@code block} throws an exception, the  resource value is
		 * released, by calling the defined <em>release</em> method. The typical
		 * use case for this method is when additional initialization of the
		 * value is needed.
		 *
		 * <pre>{@code
		 * final var file = CloseableValue.of(
		 *     Files.createFile(Path.of("some_file")),
		 *     Files::deleteIfExists
		 * );
		 * // Trying to do additional setup, e.g. setting the 'delete-on-exit'
		 * // flag.
		 * file.trying(f -> f.toFile().deleteOnExit());
		 *
		 * try (file) {
		 *     // Do something with temp file.
		 * }
		 * }</pre>
		 *
		 * @param block the codec block which is applied to the value
		 * @param releases additional release methods, which are called in the
		 *        case of an error
		 * @param <E> the thrown exception type
		 * @throws E if applying the {@code block} throws an exception
		 */
		@SafeVarargs
		public final <E extends Exception> void trying(
			final ThrowingConsumer<? super T, ? extends E> block,
			final ThrowingRunnable<? extends E>... releases
		)
			throws E
		{
			try {
				block.accept(get());
			} catch (Throwable error) {
				ExtendedCloseable.of(releases).silentClose(error);
				silentClose(error);
				throw error;
			}
		}

		/**
		 * Create a new closeable value with the given resource {@code value}
		 * and its {@code release} method.
		 *
		 * @param value the actual resource value
		 * @param release the {@code release} method for the given {@code value}
		 * @param <T> the value type
		 * @return a new closeable value
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static <T, E extends Exception> Value<T, E> of(
			final T value,
			final ThrowingConsumer<? super T, ? extends E> release
		) {
			return new Value<>(value,release);
		}

		/**
		 * Opens a kind of {@code try-catch} with resources block. The difference
		 * is, that the resources, registered with the
		 * {@link Resources#add(Object, ThrowingConsumer)} method, are only closed
		 * in the case of an error. If the <em>value</em> could be created, the
		 * caller is responsible for closing the opened <em>resources</em> by
		 * calling the {@link Value#close()} method.
		 *
		 * <pre>{@code
		 * final Value<Stream<Object>, IOException> result = Value.build(resources -> {
		 *     final var fin = resources.add(new FileInputStream(file.toFile()), Closeable::close);
		 *     final var bin = resources.add(new BufferedInputStream(fin), Closeable::close);
		 *     final var oin = resources.add(new ObjectInputStream(bin), Closeable::close);
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
		 * @see Resources
		 *
		 * @param builder the builder method
		 * @param <T> the value type of the created <em>closeable</em> value
		 * @param <BE> the exception type which might be thrown while building
		 *             the value
		 * @param <VE> the exception type which might be thrown when releasing
		 *              the returned <em>closeable</em> value
		 * @return the built closeable value
		 * @throws BE in the case of an error. If this exception is thrown, all
		 *         already <em>registered</em> resources are closed.
		 * @throws NullPointerException if the given {@code builder} is
		 *         {@code null}
		 */
		public static <T, BE extends Exception, VE extends Exception> Value<T, VE>
		build(
			final ThrowingFunction<
				? super Resources<VE>,
				? extends T,
				? extends BE> builder
		)
			throws BE
		{
			requireNonNull(builder);

			final var resources = new Resources<VE>();
			try {
				return Value.of(
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
	 * This class allows to collect one or more {@link AutoCloseable} objects
	 * into one. The registered closeable objects are closed in reverse order.
	 * <p>
	 * Using the {@code Resources} class can simplify the the creation of
	 * dependent input streams, where it might be otherwise necessary to create
	 * nested {@code try-with-resources} blocks.
	 *
	 * <pre>{@code
	 * try (var resources = new Resources<IOException>()) {
	 *     final var fin = resources.add(new FileInputStream(file), Closeable::close);
	 *     if (fin.read() != -1) {
	 *         return;
	 *     }
	 *     final var oin = resources.add(new ObjectInputStream(fin), Closeable::close);
	 *     // ...
	 * }
	 * }</pre>
	 *
	 * @see Value#build(ThrowingFunction)
	 */
	public static final class Resources<E extends Exception>
		implements ExtendedCloseable<E>
	{

		private final List<ThrowingRunnable<? extends E>> _resources = new ArrayList<>();

		/**
		 * Create a new {@code Resources} object, initialized with the given
		 * resource <em>release</em> methods.
		 *
		 * @param releases the release methods
		 */
		public Resources(
			final Collection<? extends ThrowingRunnable<? extends E>> releases
		) {
			_resources.addAll(releases);
		}

		/**
		 * Create a new {@code Resources} object, initialized with the given
		 * resource <em>release</em> methods.
		 *
		 * @param releases the release methods
		 */
		@SafeVarargs
		public Resources(final ThrowingRunnable<? extends E>... releases) {
			this(Arrays.asList(releases));
		}

		/**
		 * Create a new, empty {@code Resources} object.
		 */
		public Resources() {
		}

		/**
		 * Registers the given {@code resource} to the list of managed
		 * resources.
		 *
		 * @param resource the new resource to register
		 * @param release the method, which <em>releases</em> the the acquired
		 *        resource
		 * @param <C> the resource type
		 * @return the registered resource
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public <C> C add(
			final C resource,
			final ThrowingConsumer<? super C, ? extends E> release
		) {
			requireNonNull(resource);
			requireNonNull(release);

			_resources.add(() -> release.accept(resource));
			return resource;
		}

		@Override
		public void close() throws E {
			if (!_resources.isEmpty()) {
				ExtendedCloseable.of(_resources).close();
			}
		}

	}

	/* *************************************************************************
	 *  Helper methods.
	 * ************************************************************************/

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
		final ThrowingConsumer<? super A, ? extends E> method,
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
		final ThrowingConsumer<? super A, ? extends E> method,
		final Collection<? extends A> objects
	) {
		int suppressedCount = 0;
		Throwable error = null;
		for (var object : objects) {
			if (error != null) {
				try {
					method.accept(object);
				} catch (Exception suppressed) {
					if (suppressedCount++ < MAX_SUPPRESSED) {
						error.addSuppressed(suppressed);
					}
				}
			} else {
				try {
					method.accept(object);
				} catch (VirtualMachineError|ThreadDeath|LinkageError e) {
					throw e;
				} catch (Throwable e) {
					error = e;
				}
			}
		}

		return error;
	}

}
