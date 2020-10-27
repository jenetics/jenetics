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
		return Lifecycle.withCloseables(streams -> {
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
				.onClose(streams::uncheckedClose)
				.takeWhile(Objects::nonNull);
		});
	}

}
