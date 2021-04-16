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

import static io.jenetics.internal.util.Lifecycle.IO_EXCEPTION;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.internal.util.Lifecycle.CloseableValue;
import io.jenetics.internal.util.Lifecycle.ResourceCollector;

/**
 * Static methods for reading and writing Java objects. The methods of this
 * class allows to append additional objects to an existing files.
 *
 * <pre>{@code
 * // Write three string objects to the given path and read them again.
 * IO.write(path, List.of("1", "2", "3"), StandardOpenOption.CREATE);
 * List<Object> objects = IO.readAllObjects(path);
 * assert objects.equals(List.of("1", "2", "3"));
 *
 * // Append another two string object to the same file.
 * IO.write(path, List.of("4", "5"), StandardOpenOption.APPEND);
 * objects = IO.readAllObjects(path);
 * assert objects.equals(List.of("1", "2", "3", "4", "5"));
 *
 * // Truncates the the content of an existing file.
 * IO.write(path, List.of("6", "7", "8"), StandardOpenOption.TRUNCATE_EXISTING);
 * objects = IO.readAllObjects(path);
 * assert objects.equals(List.of("6", "7", "8"));
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.2
 * @version 6.2
 */
public final class IO {

	/**
	 * This class allows to append objects to a given output stream.
	 */
	private static final class AppendableObjectOutput implements Closeable, Flushable {
		private final ObjectOutputStream _out;

		AppendableObjectOutput(final OutputStream out, final boolean append)
			throws IOException
		{
			_out = new ObjectOutputStream(out) {
				private boolean _first = true;
				@Override
				protected void writeStreamHeader() throws IOException {
					if (_first || !append) {
						super.writeStreamHeader();
						_first = false;
					}
				}
			};
		}

		void writeObject(final Object object) throws IOException {
			_out.writeObject(object);
		}

		void reset() throws IOException {
			_out.reset();
		}

		@Override
		public void flush() throws IOException {
			_out.flush();
		}

		@Override
		public void close() throws IOException {
			_out.close();
		}
	}

	private IO() {
	}

	/**
	 * Writes the given {@code objects} to the given {@code path}, using the
	 * Java serialization. If the {@code path} already exists, the objects are
	 * appended.
	 *
	 * <pre>{@code
	 * // Write three string objects to the given file. The file is created if
	 * // it not exists or appended if the file already exists.
	 * IO.write(
	 *     path,
	 *     List.of("1", "2", "3"),
	 *     StandardOpenOption.CREATE, StandardOpenOption.APPEND
	 * );
	 * }</pre>
	 *
	 * @see #objects(Path)
	 *
	 * @param path the destination where the {@code objects} are written to
	 * @param objects the {@code objects} to be written
	 * @param options specifying how the file is opened
	 * @throws IOException if writing the objects fails
	 * @throws IllegalArgumentException if options contains an invalid
	 *         combination of options
	 * @throws UnsupportedOperationException if an unsupported option is
	 *         specified
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static void write(
		final Path path,
		final Iterable<?> objects,
		final OpenOption... options
	)
		throws IOException
	{
		final var it = objects.iterator();
		if (it.hasNext()) {
			write0(path, it, options);
		}
	}

	private static void write0(
		final Path path,
		final Iterator<?> objects,
		final OpenOption... options
	)
		throws IOException
	{
		final var append = isAppendable(options) && !isEmpty(path);
		try (var fos = Files.newOutputStream(path, options);
			 var bos = new BufferedOutputStream(fos);
			 var out = new AppendableObjectOutput(bos, append))
		{
			while (objects.hasNext()) {
				out.writeObject(objects.next());
				out.reset();
			}
		}
	}

	private static boolean isAppendable(final OpenOption... options) {
		for (var option : options) {
			if (option == StandardOpenOption.APPEND) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmpty(final Path file) throws IOException {
		return !Files.exists(file) || Files.size(file) == 0;
	}

	/**
	 * Reads the objects from the given {@code path}, which were previously
	 * written with the {@link #write(Path, Iterable, OpenOption...)} method.
	 * The file content is read lazily, object after object, and allows to
	 * read huge files efficiently. Note that the caller is responsible for
	 * closing the returned object stream.
	 *
	 * <pre>{@code
	 * try (Stream<Object> stream = IO.objects(path)) {
	 *     stream.forEach(System.out::println);
	 * }
	 * }</pre>
	 *
	 * @param path the data path
	 * @return a stream of the read objects
	 * @throws java.io.FileNotFoundException if the given path could not be read
	 * @throws IOException if the object stream couldn't be created
	 */
	public static Stream<Object> objects(final Path path) throws IOException {
		final var result = CloseableValue.build(resources ->
			objectStream(path, resources)
		);

		return result.get().onClose(() -> result.uncheckedClose(IO_EXCEPTION));
	}

	private static Stream<Object>
	objectStream(final Path path, final ResourceCollector resources)
		throws IOException
	{
		if (isEmpty(path)) {
			return Stream.empty();
		} else {
			final var fin = resources.add(Files.newInputStream(path));
			final var bin = resources.add(new BufferedInputStream(fin));
			final var oin = resources.add(new ObjectInputStream(bin));

			final Supplier<Object> readObject = () -> {
				try {
					return oin.readObject();
				} catch (EOFException|ClassNotFoundException e) {
					return null;
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};

			return Stream.generate(readObject)
				.takeWhile(Objects::nonNull);
		}
	}

	/**
	 * Reads all objects from the given {@code path}, which were previously
	 * written with the {@link #write(Path, Iterable, OpenOption...)} method.
	 *
	 * <pre>{@code
	 * IO.write(path, List.of("1", "2", "3"), CREATE);
	 * final List<Object> objects = IO.readAllObjects(path.get());
	 * assert  objects.equals(List.of("1", "2", "3"));
	 * }</pre>
	 *
	 * @param path the data path
	 * @return a list of all objects
	 * @throws java.io.FileNotFoundException if the given path could not be read
	 * @throws IOException if the object stream couldn't be created
	 */
	public static List<Object> readAllObjects(final Path path) throws IOException {
		try (var objects = objects(path)) {
			return objects.collect(Collectors.toList());
		}
	}

}
