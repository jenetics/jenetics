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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;
import io.jenetics.incubator.util.Lifecycle.ResourceCollector;

public final class IO {

	private IO() {
	}

	/**
	 * Writes the given {@code objects} to the given {@code file}, using the
	 * Java serialization. If the {@code file} already exists, the objects are
	 * appended.
	 *
	 * @see #read(Path)
	 *
	 * @param file the destination where the {@code objects} are written to
	 * @param objects the {@code objects} to write
	 * @throws IOException if writing the objects fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static void write(final Path file, final Collection<?> objects)
		throws IOException
	{
		if (!objects.isEmpty()) {
			final var append = new AtomicBoolean(!isEmpty(file));

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

			try (var fos = new FileOutputStream(file.toFile(), true);
				 var bos = new BufferedOutputStream(fos);
				 var out = new Output(bos))
			{
				for (var obj : objects) {
					out.writeObject(obj);
					out.reset();
					append.set(true);
				}
			}
		}
	}

	private static boolean isEmpty(final Path file) throws IOException {
		return !Files.exists(file) || Files.size(file) == 0;
	}

	/**
	 * Writes the given {@code objects} to the given {@code file}, using the
	 * Java serialization. If the {@code file} already exists, the objects are
	 * appended.
	 *
	 * @param file the destination where the {@code objects} are written to
	 * @param objects the {@code objects} to write
	 * @throws IOException if writing the objects fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static void write(final Path file, final Object... objects)
		throws IOException
	{
		write(file, Arrays.asList(objects));
	}

	/**
	 * Reads the object from the given {@code file}, which were previously
	 * written with the {@link #write(Path, Collection)} method. The caller is
	 * responsible for closing the returned object stream
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
	objectStream(final Path file, final ResourceCollector resources) throws IOException {
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
