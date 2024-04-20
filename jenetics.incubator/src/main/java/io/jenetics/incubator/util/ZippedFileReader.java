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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import io.jenetics.internal.util.Lifecycle.IOValue;

/**
 * This reader allows reading a file within a ZIP archive.
 * {@snippet class="UtilSnippets" region="ZippedFileReaderSnippets.creation"}
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 8.1
 * @since 8.1
 */
public class ZippedFileReader extends Reader {

	private final IOValue<Reader> reader;

	/**
	 * Create a new reader from the given {@code zip} file and the path to the
	 * zipped file.
	 *
	 * @param file the ZIP file
	 * @param path the path to the file within the ZIP to be read
	 * @param charset the character set used for reading the characters
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws ZipException if a ZIP format error has occurred
	 * @throws FileNotFoundException if no zip entry with the given {@code path}
	 *         is found in the ZIP file
	 * @throws IOException if an I/O error has occurred
	 */
	public ZippedFileReader(final File file, final Path path, final Charset charset)
		throws IOException
	{
		reader = new IOValue<>(resources -> {
			final var zip = resources.use(new ZipFile(file));
			final var entry = zip.getEntry(path.toString());
			if (entry == null) {
				throw new FileNotFoundException(
					"Zip entry not found: '%s:%s'.".formatted(file, path)
				);
			}

			final var stream = resources.use(zip.getInputStream(entry));
			final var reader = resources.use(new InputStreamReader(stream, charset));
			return resources.use(new BufferedReader(reader));
		});
	}

	/**
	 * Create a new reader from the given {@code zip} file and the path to the
	 * zipped file. The {@link Charset#defaultCharset()} is used for decoding
	 * the characters.
	 *
	 * @param zip the ZIP file
	 * @param path the path to the file within the ZIP to be read
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws ZipException if a ZIP format error has occurred
	 * @throws IOException if an I/O error has occurred
	 */
	public ZippedFileReader(final File zip, final Path path) throws IOException {
		this(zip, path, Charset.defaultCharset());
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		return reader.get().read(target);
	}

	@Override
	public int read() throws IOException {
		return reader.get().read();
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		return reader.get().read(cbuf);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.get().read(cbuf, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return reader.get().skip(n);
	}

	@Override
	public boolean ready() throws IOException {
		return reader.get().ready();
	}

	@Override
	public boolean markSupported() {
		return reader.get().markSupported();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		reader.get().mark(readAheadLimit);
	}

	@Override
	public void reset() throws IOException {
		reader.get().reset();
	}

	@Override
	public long transferTo(Writer out) throws IOException {
		return reader.get().transferTo(out);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

}
