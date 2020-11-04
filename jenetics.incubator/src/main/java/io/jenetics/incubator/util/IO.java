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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;
import io.jenetics.incubator.util.Lifecycle.ResourceCollector;

/**
 * Static methods for reading and writing Java objects.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
public final class IO {

	private IO() {
	}

	/**
	 * Writes the given {@code objects} to the given {@code path}, using the
	 * Java serialization. If the {@code path} already exists, the objects are
	 * appended.
	 *
	 * @see #read(Path)
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
		final var appendable = isAppendable(options);
		final var append = new AtomicBoolean(appendable && !isEmpty(path));

		final class Output extends ObjectOutputStream {
			Output(final OutputStream out) throws IOException {
				super(out);
			}
			@Override
			protected void writeStreamHeader() throws IOException {
				if (!append.get()) {
					super.writeStreamHeader();
				}
			}
		}

		try (var fos = Files.newOutputStream(path, options);
			 var bos = new BufferedOutputStream(fos);
			 var out = new Output(bos))
		{
			while (objects.hasNext()) {
				final var object = objects.next();

				out.writeObject(object);
				out.reset();
				append.set(appendable);
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
	 * Reads the object from the given {@code file}, which were previously
	 * written with the {@link #write(Path, Iterable, OpenOption...)} method.
	 * The caller is responsible for closing the returned object stream
	 *
	 * @param file the data file
	 * @return a stream of the read objects
	 * @throws java.io.FileNotFoundException if the given file could not be read
	 * @throws IOException if the object stream couldn't be created
	 */
	public static Stream<Object> read(final Path file) throws IOException {
		final var result = CloseableValue.build(resources ->
			objectStream(file, resources)
		);

		return result.get().onClose(result::uncheckedClose);
	}

	private static Stream<Object>
	objectStream(final Path file, final ResourceCollector resources)
		throws IOException
	{
		if (isEmpty(file)) {
			return Stream.empty();
		} else {
			final var fin = resources.add(new FileInputStream(file.toFile()));
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

}
