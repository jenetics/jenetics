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
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.jenetics.incubator.util.Lifecycle.ResourceAppender;
import io.jenetics.incubator.util.Lifecycle.CloseableValue;

public class IO {

	private IO() {
	}

	static void write(final Iterable<?> objects, final Path file)
		throws IOException
	{
		final class Output extends ObjectOutputStream {
			private final boolean _append;
			Output(final OutputStream out, final boolean append)
				throws IOException
			{
				super(out);
				_append = append;
			}
			@Override
			protected void writeStreamHeader() throws IOException {
				if (!_append) {
					super.writeStreamHeader();
				}
			}
		}

		try (var fos = new FileOutputStream(file.toFile(), true);
			 var bos = new BufferedOutputStream(fos);
			 var out = new Output(bos, Files.exists(file)))
		{
			for (var obj : objects) {
				out.writeObject(obj);
				out.reset();
			}
		}

	}

	static Stream<Object> read(final Path file) throws IOException {
		final var result = CloseableValue.build(resources ->
			objectStream(file, resources)
		);

		return result.get().onClose(result::uncheckedClose);
	}

	private static Stream<Object>
	objectStream(final Path file, final ResourceAppender resources) throws IOException {
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
