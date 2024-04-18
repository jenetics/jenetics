package io.jenetics.incubator.util;

import io.jenetics.ext.util.CsvSupport;
import io.jenetics.internal.util.Lazy;
import io.jenetics.internal.util.Lifecycle;
import io.jenetics.internal.util.Try;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

public class ZippedCvs extends Reader implements Closeable {

	private final File zip;
	private final Path csv;
	private final Charset charset;

	private final Lazy<Try<Lifecycle.Value<Reader, IOException>, IOException>>
		entry = Lazy.of(this::zipEntryReader);

	public ZippedCvs(final File zip, final Path csv, final Charset charset) {
		this.zip = zip;
		this.csv = csv;
		this.charset = charset;
	}

	private Try<Lifecycle.Value<Reader, IOException>, IOException> zipEntryReader() {
		try {
			final var value = new Lifecycle.IOValue<Reader>(resources -> {
				final var file = resources.use(new ZipFile(zip));
				final var entry = file.getEntry(csv.toString());
				if (entry != null) {
					final var stream = resources.use(file.getInputStream(entry));
					return resources.use(new InputStreamReader(stream, charset));
				} else {
					return null;
				}
			});

			return new Try.Success<>(value);
		} catch (IOException e) {
			return new Try.Failure<>(e);
		}
	}

	@Override
	public int read() throws IOException {
		return entry.get().get().get().read();
	}

	@Override
	public int read(final char[] cbuf, final int off, final int len)
		throws IOException
	{
		return entry.get().get().get().read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		try {
			entry.ifEvaluated(v -> {
				try {
					v.get().get().close();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	public static void main(String[] args) throws Exception {
		final var zip = new File("164.zip");
		final var csv = Path.of("EQTEXT.txt");

		final var separator = new CsvSupport.Separator('\t');
		final var quote = new CsvSupport.Quote('\'');
		final var splitter = new CsvSupport.LineSplitter(separator, CsvSupport.Quote.ZERO);

		final var lr = new CsvSupport.LineReader(CsvSupport.Quote.ZERO);

		try (var lines = lr.read(new ZippedCvs(zip, csv, Charset.forName("Cp1252")))) {
			final var count = new AtomicInteger();
			lines.forEach(line -> {
				System.out.println("ASDF---" + count.incrementAndGet() + ": " + line);
			});
		}
	}
}
