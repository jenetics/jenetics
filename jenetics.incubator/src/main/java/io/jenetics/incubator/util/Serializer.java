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

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Stream;

import io.jenetics.internal.util.Lifecycle.Resources;
import io.jenetics.internal.util.Lifecycle.Value;

/**
 * Static methods for reading and writing objects using the Java serialisation.
 * The methods of this class allows appending additional objects to an existing
 * file.
 *
 * <pre>{@code
 * // Write three string objects to the given path and read them again.
 * Serializer.write(path, List.of("1", "2", "3"));
 * List<Object> objects = Serializer.readAllObjects(path);
 * assert objects.equals(List.of("1", "2", "3"));
 *
 * // Append another two string object to the same file.
 * Serializer.write(path, List.of("4", "5"));
 * objects = Serializer.readAllObjects(path);
 * assert objects.equals(List.of("1", "2", "3", "4", "5"));
 *
 * // Truncates the the content of an existing file.
 * Serializer.write(path, List.of("6", "7", "8"), TRUNCATE_EXISTING);
 * objects = Serializer.readAllObjects(path);
 * assert objects.equals(List.of("6", "7", "8"));
 * }</pre>
 *
 * It also allows reading object piecewise via a {@link Stream}.
 *
 * <pre>{@code
 * try (Stream<Object> stream = Serializer.objects(Path.of("serialized-objects.bin"))) {
 *     stream.forEach(System.out::println);
 * }
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 6.2
 * @version 6.2
 */
public final class Serializer {
	private Serializer() {}

	/**
	 * Wrapper for {@link OutputStream}s, which prevents the wrapped stream from
	 * being closed.
	 */
	private static final class NonCloseableOutputStream extends OutputStream {
		private final OutputStream _out;

		NonCloseableOutputStream(final OutputStream out) {
			_out = requireNonNull(out);
		}

		@Override
		public void write(final int b) throws IOException {
			_out.write(b);
		}

		@Override
		public void write(final byte[] b) throws IOException {
			_out.write(b);
		}

		@Override
		public void write(final byte[] b, final int off, final int len)
			throws IOException
		{
			_out.write(b, off, len);
		}

		@Override
		public void flush() throws IOException {
			_out.flush();
		}

		@Override
		public void close() {
		}
	}

	/**
	 * This class allows appending objects to a given output stream.
	 */
	private static final class AppendableObjectOutput
		implements Closeable, Flushable
	{
		private final CountingOutputStream _cout;
		private final ObjectOutputStream _out;

		AppendableObjectOutput(final OutputStream out, final boolean append)
			throws IOException
		{
			_cout = new CountingOutputStream(out);
			_out = new ObjectOutputStream(_cout) {
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

		long count() {
			return _cout.count();
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

	/**
	 * Decorator stream for counting the written bytes.
	 */
	private static final class CountingOutputStream extends OutputStream {
		private final OutputStream _out;

		private long _count = 0;

		CountingOutputStream(final OutputStream out) {
			_out = requireNonNull(out);
		}

		long count() {
			return _count;
		}

		@Override
		public void write(final int b) throws IOException {
			_out.write(b);
			_count += 1;
		}

		@Override
		public void write(final byte[] b) throws IOException {
			_out.write(b);
			_count += b.length;
		}

		@Override
		public void write(final byte[] b, final int off, final int len)
			throws IOException
		{
			_out.write(b, off, len);
			_count += len;
		}
	}

	/* *************************************************************************
	 * Read/write methods.
	 * ************************************************************************/

	/**
	 * Writes the given {@code objects} to the given {@code output} stream,
	 * using Java serialization. For the <em>first</em> objects to be written
	 * to the stream, the {@code append} flag must be set to {@code false}.
	 *
	 * <pre>{@code
	 * final var output = new ByteArrayOutputStream();
	 * Serializer.write(output, List.of("1", "2", "3"), false);
	 *
	 * var input = new ByteArrayInputStream(output.toByteArray());
	 * final List<Object> objects = Serializer.readAllObjects(output);
	 * assert objects.equals(List.of("1", "2", "3"));
	 * }</pre>
	 *
	 * When writing additional objects to the same output stream, the
	 * {@code append} must be set to {@code true}.
	 *
	 * <pre>{@code
	 * Serializer.write(output, List.of("4", "5"), true);
	 * input = new ByteArrayInputStream(output.toByteArray());
	 * objects = Serializer.readAllObjects(input);
	 * assert objects.equals(List.of("1", "2", "3", "4", "5"));
	 * }</pre>
	 *
	 * It is the responsibility of the caller to close the given {@code output}
	 * stream when no longer needed.
	 *
	 * @see #write(Iterable, Path, OpenOption...)
	 *
	 * @param objects the objects to write to output stream, in the order defined
	 *        by the given iterable
	 * @param output the output stream where the objects are written to
	 * @param append {@code false} for the first objects written to the given
	 *        {@code output} stream and {@code true} for additional objects
	 *        writing to the same stream
	 * @return the number of bytes written to the output stream
	 * @throws IOException if writing the objects fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static long write(
		final Iterable<?> objects,
		final OutputStream output,
		final boolean append
	)
		throws IOException
	{
		final var it = objects.iterator();
		if (it.hasNext()) {
			return write0(it, output, append);
		} else {
			return 0;
		}
	}

	private static long write0(
		final Iterator<?> objects,
		final OutputStream out,
		final boolean append
	)
		throws IOException
	{
		final var nco = new NonCloseableOutputStream(out);
		final var aoo = new AppendableObjectOutput(nco, append);
		try (aoo) {
			while (objects.hasNext()) {
				aoo.writeObject(objects.next());
				aoo.reset();
			}
		}

		return aoo.count();
	}

	/**
	 * Writes the given {@code objects} to the given {@code path}, using
	 * Java serialization. If the {@code path} already exists and the open
	 * {@code options} contains {@link StandardOpenOption#APPEND}, the objects
	 * are appended to the existing file.
	 *
	 * <pre>{@code
	 * // Write three string objects to the given file. The file is created if
	 * // it not exists or appended if the file already exists.
	 * Serializer.write(path, List.of("1", "2", "3"));
	 * }</pre>
	 *
	 * Truncating an existing file:
	 * <pre>{@code
	 * // Write three string objects to the given file. The file is truncated if
	 * // it exists or created if the file doesn't exists.
	 * Serializer.write(
	 *     path, List.of("1", "2", "3"),
	 *     StandardOpenOption.TRUNCATE_EXISTING
	 * );
	 * }</pre>
	 *
	 * @see #write(Iterable, OutputStream, boolean)
	 *
	 * @param objects the {@code objects} to be written
	 * @param path the destination where the {@code objects} are written to
	 * @param options specifying how the file is opened
	 * @return the number of bytes written to the file
	 * @throws IOException if writing the objects fails
	 * @throws IllegalArgumentException if options contains an invalid
	 *         combination of options
	 * @throws UnsupportedOperationException if an unsupported option is
	 *         specified
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static long write(
		final Iterable<?> objects,
		final Path path,
		final OpenOption... options
	)
		throws IOException
	{
		final OpenOption[] opts;
		if (options.length != 0) {
			opts = options;
		} else {
			if (Files.exists(path)) {
				opts = new OpenOption[] { APPEND };
			} else {
				opts = new OpenOption[] { CREATE, APPEND };
			}
		}

		return write0(objects::iterator, path, opts);
	}

	private static long write0(
		final Supplier<Iterator<?>> objects,
		final Path path,
		final OpenOption... options
	)
		throws IOException
	{
		final var it = objects.get();
		if (it.hasNext()) {
			final var append = isAppendable(options) && !isEmpty(path);
			try (var fos = Files.newOutputStream(path, options);
				 var bos = new BufferedOutputStream(fos))
			{
				return write0(it, bos, append);
			}
		} else {
			return 0;
		}
	}

	private static boolean isAppendable(final OpenOption... options) {
		for (var option : options) {
			if (option == APPEND) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmpty(final Path file) throws IOException {
		return !Files.exists(file) || Files.size(file) == 0;
	}

	/**
	 * Reads the objects from the given {@code input} stream, which were
	 * previously written with one of the {@code write} methods.
	 * The content is read lazily, object after object, and allows to read many
	 * objects efficiently. Note that the caller is responsible for closing the
	 * returned object stream, which also closes the given {@code input} stream.
	 *
	 * <pre>{@code
	 * final InputStream input = ...;
	 * try (Stream<Object> stream = Serializer.objects(input)) {
	 *     stream.forEach(System.out::println);
	 * }
	 * }</pre>
	 *
	 * @see #objects(Path)
	 *
	 * @param input the input stream where the objects are read from
	 * @return a stream of the read objects
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static Stream<Object> objects(final InputStream input) {
		final Value<Stream<Object>, IOException> result = Value.build(resources ->
			objectStream(input, resources)
		);

		return result.get().onClose(() ->
			result.uncheckedClose(UncheckedIOException::new)
		);
	}

	private static Stream<Object>
	objectStream(final InputStream input, final Resources<IOException> resources) {
		final Supplier<Object> readObject = new Supplier<>() {
			private ObjectInputStream _oin = null;

			@Override
			public synchronized Object get() {
				try {
					if (_oin == null) {
						var in = resources.add(input, Closeable::close);
						if (!(in instanceof BufferedInputStream)) {
							in = resources.add(new BufferedInputStream(in), Closeable::close);
						}
						_oin = resources.add(new ObjectInputStream(in), Closeable::close);
					}

					return _oin.readObject();
				} catch (EOFException|ClassNotFoundException e) {
					return null;
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		};

		return Stream.generate(readObject)
			.takeWhile(Objects::nonNull);
	}

	/**
	 * Reads the objects from the given {@code path}, which were previously
	 * written with the {@link #write(Iterable, Path, OpenOption...)} method.
	 * The file content is read lazily, object after object, and allows to
	 * read huge files efficiently. Note that the caller is responsible for
	 * closing the returned object stream.
	 *
	 * <pre>{@code
	 * try (Stream<Object> stream = Serializer.objects(path)) {
	 *     stream.forEach(System.out::println);
	 * }
	 * }</pre>
	 *
	 * @see #objects(InputStream)
	 *
	 * @param path the data path
	 * @return a stream of the read objects
	 * @throws NullPointerException if the given {@code path} is {@code null}
	 * @throws java.io.FileNotFoundException if the given path could not be read
	 * @throws IOException if the object stream couldn't be created
	 */
	public static Stream<Object> objects(final Path path) throws IOException {
		final Value<Stream<Object>, IOException> result = Value.build(resources ->
			objectStream(path, resources)
		);

		return result.get().onClose(() ->
			result.uncheckedClose(UncheckedIOException::new)
		);
	}

	private static Stream<Object>
	objectStream(final Path path, final Resources<IOException> resources)
		throws IOException
	{
		return isEmpty(path)
			? Stream.empty()
			: objectStream(Files.newInputStream(path), resources);
	}

	/**
	 * Reads all objects from the given {@code input} stream, which were
	 * previously written with one of the the {@code write} methods.
	 *
	 * @param input the input stream where the objects are read from
	 * @return a list of all read objects
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	public static List<Object> readAllObjects(final InputStream input)
		throws IOException
	{
		try (var objects = objects(input)) {
			return objects.toList();
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	/**
	 * Reads all objects from the given {@code path}, which were previously
	 * written with one of the the {@code write} methods.
	 *
	 * @param path the data path
	 * @return a list of all read objects
	 * @throws NullPointerException if the given {@code path} is {@code null}
	 * @throws java.io.FileNotFoundException if the given path could not be read
	 * @throws IOException if an I/O error occurs
	 */
	public static List<Object> readAllObjects(final Path path) throws IOException {
		try (var objects = objects(path)) {
			return objects.toList();
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

}
