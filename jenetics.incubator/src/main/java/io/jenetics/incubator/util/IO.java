package io.jenetics.incubator.util;

import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IO {

	private IO() {
	}

	static void __write(final Iterable<?> objects, final Path file)
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

	interface Sup<R, E extends Throwable> {
		R get() throws E;
	}

	static Stream<Object> __read(final Path file) throws IOException {
		final class Closeables implements Closeable {
			private final List<Closeable> _closeables = new ArrayList<>();
			public <C extends Closeable> C add(final C closeable) {
				_closeables.add(requireNonNull(closeable));
				return closeable;
			}
			@Override
			public void close() throws IOException {
				close(_closeables);
			}
			void close(final Throwable mainError) {
				try {
					close();
				} catch (Exception suppressed) {
					mainError.addSuppressed(suppressed);
				}
			}
			void closeUnchecked() {
				try {
					close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
			private void close(final Iterable<? extends Closeable> closeables)
				throws IOException
			{
				Exception error = null;
				for (var closeable : closeables) {
					try {
						closeable.close();
					} catch (Exception e) {
						if (error == null) {
							error = e;
						} else {
							error.addSuppressed(e);
						}
					}
				}
				if (error != null) {
					if (error instanceof IOException) {
						throw (IOException)error;
					} else {
						throw new IOException(error);
					}
				}
			}



			<T, E extends Throwable> T using(Sup<T, E> s) throws E {
				try {
					return s.get();
				} catch (Throwable e) {
					closeUnchecked();
					throw e;
				}
			}
		}

		final var streams = new Closeables();
		streams.using(() -> {
			final var fin = streams.add(new FileInputStream(file.toFile()));
			final var bin = streams.add(new BufferedInputStream(fin));
			final var oin = streams.add(new ObjectInputStream(bin));
			return null;
		});

		try {
			final var fin = streams.add(new FileInputStream(file.toFile()));
			final var bin = streams.add(new BufferedInputStream(fin));
			final var oin = streams.add(new ObjectInputStream(bin));

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
				.onClose(streams::closeUnchecked)
				.takeWhile(Objects::nonNull);
		} catch (Throwable e) {
			streams.close(e);
			throw e;
		}
	}

}
