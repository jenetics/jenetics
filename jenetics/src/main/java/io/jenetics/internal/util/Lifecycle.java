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

import java.io.Closeable;
import java.io.IOException;
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
 * {@snippet lang="java":
 * final Value<Path, IOException> file = new Value<>(
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
 * }
 *
 * <p><b>Building complex closeable values</b></p>
 * {@snippet lang = "java":
 * final IOValue<Stream<Object>> result = new IOValue<>(resources -> {
 *     final var fin = resources.use(new FileInputStream(file.toFile()));
 *     final var bin = resources.use(new BufferedInputStream(fin));
 *     final var oin = resources.use(new ObjectInputStream(bin));
 *
 *     return Stream.generate(() -> readNextObject(oin))
 *         .takeWhile(Objects::nonNull);
 * });
 *
 * try (result) {
 *     result.get().forEach(System.out::println);
 * }
 *}
 *
 * <p><b>Wrapping several closeable into one</b></p>
 * {@snippet lang = "java":
 * try (var __ = Releasable.of(c1, c2, c3)) {
 *     // ...
 * }
 *}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.2
 * @version 7.2
 */
public final class Lifecycle {

	/* *************************************************************************
	 * Throwing functional interfaces.
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
	public interface Releasable<E extends Exception>
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
		default <R extends RuntimeException> void
		release(final Function<? super E, ? extends R> mapper) throws R {
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
		default void silentRelease() {
			silentRelease(null);
		}

		/**
		 * Calls the {@link #close()} method and ignores every thrown exception.
		 * If the given {@code previousError} is <em>non-null</em>, the thrown
		 * exception is appended to the list of suppressed exceptions.
		 *
		 * @param previousError the error, which triggers the close of the given
		 *        {@code closeable}
		 */
		default void silentRelease(final Throwable previousError) {
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
		 * {@link Releasable}.
		 *
		 * @param release the release method to wrap
		 * @return a new extended closeable with the given underlying
		 *         {@code release} method
		 * @throws NullPointerException if the given {@code release} method is
		 *         {@code null}
		 */
		static <E extends Exception> Releasable<E>
		of(final ThrowingRunnable<? extends E> release) {
			return release::run;
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given initial
		 * release <em>methods</em>>. The given list of objects is closed in
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
		static <E extends Exception> Releasable<E>
		of(final Collection<? extends ThrowingRunnable<? extends E>> releases) {
			final List<ThrowingRunnable<? extends E>> list = new ArrayList<>();
			releases.forEach(c -> list.add(requireNonNull(c)));
			Collections.reverse(list);

			return () -> Lifecycle.invokeAll(ThrowingRunnable::run, list);
		}

		/**
		 * Create a new {@code ExtendedCloseable} object with the given initial
		 * release <em>methods</em>>. The given list of objects is closed in
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
		static <E extends Exception> Releasable<E>
		of(final ThrowingRunnable<? extends E>... releases) {
			return of(Arrays.asList(releases));
		}

	}

	/**
	 * This class represents a <em>closeable</em> value. It is useful in cases
	 * where the object value doesn't implement the {@link AutoCloseable}
	 * interface but needs some cleanup work to do after usage. In the following
	 * example the created {@code file} is automatically deleted when leaving the
	 * {@code try} block.
	 *
	 * {@snippet lang="java":
	 * // Create the closeable file.
	 * final Value<Path, IOException> file = new Value<>(
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
	 * }
	 *
	 * @param <T> the value type
	 */
	public static sealed class Value<T, E extends Exception>
		implements Supplier<T>, Releasable<E>
	{

		T _value;
		ThrowingConsumer<? super T, ? extends E> _release;

		private Value() {
		}

		/**
		 * Create a new closeable value with the given resource {@code value}
		 * and its {@code release} method.
		 *
		 * @param value the actual resource value
		 * @param release the {@code release} method for the given {@code value}
		 * @throws NullPointerException if the {@code release} function is
		 *         {@code null}
		 */
		public Value(
			final T value,
			final ThrowingConsumer<? super T, ? extends E> release
		) {
			_value = value;
			_release = requireNonNull(release);
		}

		/**
		 * Opens a kind of {@code try-catch} with resources block. The difference
		 * is, that the resources, registered with the
		 * {@link Resources#use(Object, ThrowingConsumer)} method, are only closed
		 * in the case of an error. If the <em>value</em> could be created, the
		 * caller is responsible for closing the opened <em>resources</em> by
		 * calling the {@link Value#close()} method.
		 *
		 * {@snippet lang = "java":
		 * final Value<Stream<Object>, IOException> result = new Value<>(resources -> {
		 *     final var fin = resources.use(new FileInputStream(file.toFile()), Closeable::close);
		 *     final var bin = resources.use(new BufferedInputStream(fin), Closeable::close);
		 *     final var oin = resources.use(new ObjectInputStream(bin), Closeable::close);
		 *
		 *     return Stream.generate(() -> readNextObject(oin))
		 *         .takeWhile(Objects::nonNull);
		 * });
		 *
		 * try (result) {
		 *     result.get().forEach(System.out::println);
		 * }
		 *}
		 *
		 * @see Resources
		 *
		 * @param builder the builder method
		 * @param <BE> the exception type which might be thrown while building
		 *             the value
		 * @throws BE in the case of an error. If this exception is thrown, all
		 *         already <em>registered</em> resources are closed.
		 * @throws NullPointerException if the given {@code builder} is
		 *         {@code null}
		 */
		public <BE extends Exception> Value(
			final ThrowingFunction<
				? super Resources<E>,
				? extends T,
				? extends BE> builder
		)
			throws BE
		{
			requireNonNull(builder);

			final var resources = new Resources<E>();
			try {
				_value = builder.apply(resources);
				_release = value -> resources.close();
			} catch (Throwable error) {
				resources.silentRelease(error);
				throw error;
			}
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
		 * If the {@code block} throws an exception, the resource value is
		 * released, by calling the defined <em>release</em> method. The typical
		 * use case for this method is when additional initialization of the
		 * value is needed.
		 *
		 * {@snippet lang="java":
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
		 * }
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
				Releasable.of(releases).silentRelease(error);
				silentRelease(error);
				throw error;
			}
		}

	}

	/**
	 * This class represents a <em>closeable</em> value. It is useful in cases
	 * where the object value doesn't implement the {@link AutoCloseable}
	 * interface but needs some cleanup work to do after usage. In the following
	 * example the created {@code file} is automatically deleted when leaving the
	 * {@code try} block.
	 *
	 * {@snippet lang="java":
	 * // Create the closeable file.
	 * final IOValue<Path> file = new IOValue<>(
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
	 * }
	 *
	 * @param <T> the value type
	 */
	public static final class IOValue<T> extends Value<T, IOException> {

		/**
		 * Create a new closeable value with the given resource {@code value}
		 * and its {@code release} method.
		 *
		 * @param value the actual resource value
		 * @param release the {@code release} method for the given {@code value}
		 * @throws NullPointerException if the {@code release} function is
		 *         {@code null}
		 */
		public IOValue(
			final T value,
			final ThrowingConsumer<? super T, ? extends IOException> release
		) {
			super(value, release);
		}

		/**
		 * Opens a kind of {@code try-catch} with resources block. The difference
		 * is, that the resources, registered with the
		 * {@link Resources#use(Object, ThrowingConsumer)} method, are only closed
		 * in the case of an error. If the <em>value</em> could be created, the
		 * caller is responsible for closing the opened <em>resources</em> by
		 * calling the {@link Value#close()} method.
		 *
		 * {@snippet lang = "java":
		 * final IOValue<Stream<Object>> result = new IOValue<>(resources -> {
		 *     final var fin = resources.use(new FileInputStream(file.toFile()));
		 *     final var bin = resources.use(new BufferedInputStream(fin));
		 *     final var oin = resources.use(new ObjectInputStream(bin));
		 *
		 *     return Stream.generate(() -> readNextObject(oin))
		 *         .takeWhile(Objects::nonNull);
		 * });
		 *
		 * try (result) {
		 *     result.get().forEach(System.out::println);
		 * }
		 *}
		 *
		 * @see Resources
		 *
		 * @param builder the builder method
		 * @param <BE> the exception type which might be thrown while building
		 *             the value
		 * @throws BE in the case of an error. If this exception is thrown, all
		 *         already <em>registered</em> resources are closed.
		 * @throws NullPointerException if the given {@code builder} is
		 *         {@code null}
		 */
		public <BE extends Exception> IOValue(
			final ThrowingFunction<
				? super IOResources,
				? extends T,
				? extends BE> builder
		)
			throws BE
		{
			requireNonNull(builder);

			final var resources = new IOResources();
			try {
				_value = builder.apply(resources);
				_release = value -> resources.close();
			} catch (Throwable error) {
				resources.silentRelease(error);
				throw error;
			}
		}

	}

	/**
	 * This class allows collecting one or more {@link AutoCloseable} objects
	 * into one. The registered closeable objects are closed in reverse order.
	 * <p>
	 * Using the {@code Resources} class can simplify the creation of
	 * dependent input streams, where it might be otherwise necessary to create
	 * nested {@code try-with-resources} blocks.
	 *
	 * {@snippet lang = "java":
	 * try (var resources = new Resources<IOException>()) {
	 *     final var fin = resources.use(new FileInputStream(file), Closeable::close);
	 *     if (fin.read() != -1) {
	 *         return;
	 *     }
	 *     final var oin = resources.use(new ObjectInputStream(fin), Closeable::close);
	 *     // ...
	 * }
	 * }
	 */
	public static sealed class Resources<E extends Exception>
		implements Releasable<E>
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
		 * @param release the method, which <em>releases</em> the acquired
		 *        resource
		 * @param <C> the resource type
		 * @return the registered resource
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public <C> C use(
			final C resource,
			final ThrowingConsumer<? super C, ? extends E> release
		) {
			requireNonNull(resource);
			requireNonNull(release);

			_resources.add(() -> release.accept(resource));
			return resource;
		}

		/**
		 * Registers the given {@code resource} to the list of managed
		 * resources.
		 *
		 * @param resource the new resource to register
		 * @param <C> the resource type
		 * @return the registered resource
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public <C extends Releasable<? extends E>> C use(final C resource) {
			return use(resource, C::close);
		}

		@Override
		public void close() throws E {
			if (!_resources.isEmpty()) {
				Releasable.of(_resources).close();
			}
		}

	}

	/**
	 * This class allows collecting one or more {@link AutoCloseable} objects
	 * into one. The registered closeable objects are closed in reverse order.
	 * <p>
	 * Using the {@code IOResources} class can simplify the creation of
	 * dependent input streams, where it might be otherwise necessary to create
	 * nested {@code try-with-resources} blocks.
	 *
	 * {@snippet lang = "java":
	 * try (var resources = new IOResources()) {
	 *     final var fin = resources.use(new FileInputStream(file));
	 *     if (fin.read() != -1) {
	 *         return;
	 *     }
	 *     final var oin = resources.use(new ObjectInputStream(fin));
	 *     // ...
	 * }
	 * }
	 */
	public static final class IOResources extends Resources<IOException> {

		/**
		 * Create a new {@code IOResources} object, initialized with the given
		 * resource <em>release</em> methods.
		 *
		 * @param releases the release methods
		 */
		public IOResources(
			final Collection<
				? extends ThrowingRunnable<? extends IOException>> releases
		) {
			super(releases);
		}

		/**
		 * Create a new {@code IOResources} object, initialized with the given
		 * resource <em>release</em> methods.
		 *
		 * @param releases the release methods
		 */
		@SafeVarargs
		public IOResources(
			final ThrowingRunnable<
				? extends IOException>... releases
		) {
			super(releases);
		}

		/**
		 * Create a new, empty {@code IOResources} object.
		 */
		public IOResources() {
		}

		/**
		 * Registers the given {@code resource} to the list of managed
		 * resources.
		 *
		 * @param resource the new resource to register
		 * @param <C> the resource type
		 * @return the registered resource
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public <C extends Closeable> C use(final C resource) {
			return use(resource, C::close);
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
	 * {@snippet lang="java":
	 * final var streams = new ArrayList<InputStream>();
	 * streams.add(new FileInputStream(file1));
	 * streams.add(new FileInputStream(file2));
	 * streams.add(new FileInputStream(file3));
	 * // ...
	 * invokeAll(Closeable::close, streams);
	 * }
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
		final Iterable<? extends A> objects
	)
		throws E
	{
		raise(invokeAll0(method, objects));
	}

	private static <E extends Exception> void raise(final Throwable error)
		throws E
	{
		switch (error) {
			case null -> {}
			case RuntimeException e -> throw e;
			case Error e -> throw e;
			default ->  {
				@SuppressWarnings("unchecked")
				final var e = (E)error;
				throw e;
			}
		}
	}

	private static final int MAX_SUPPRESSED = 5;

	/**
	 * Invokes the {@code method} on all given {@code objects}, no matter if one
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
		final Iterable<? extends A> objects
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
				} catch (VirtualMachineError|LinkageError e) {
					throw e;
				} catch (Throwable e) {
					error = e;
				}
			}
		}

		return error;
	}

}
