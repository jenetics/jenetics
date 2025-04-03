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
package io.jenetics.incubator.restful.client;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 8.2
 * @version 8.2
 */
final class ErrorPropagatingPipedInputStream extends InputStream {
	private final AtomicReference<Throwable> error = new AtomicReference<>();
	private final PipedInputStream in;

	ErrorPropagatingPipedInputStream(PipedInputStream out) {
		this.in = requireNonNull(out);
	}

	void connect(PipedOutputStream src) throws IOException {
		in.connect(src);
	}

	void error(final Throwable throwable) {
		error.set(throwable);
	}

	void checkError() throws IOException {
		final var err = error.get();
		switch (err) {
			case IOException e -> throw e;
			case UncheckedIOException e -> throw e.getCause();
			case RuntimeException e -> throw e;
			case Exception e -> throw new IOException(e);
			case Error e -> throw e;
			case Throwable e -> throw new AssertionError(e.getMessage());
		}
	}

	@Override
	public int read() throws IOException {
		checkError();
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkError();
		return in.read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		checkError();
		return in.available();
	}

	@Override
	public void close() throws IOException {
		checkError();
		in.close();
	}

	@Override
	public int read(byte[] b) throws IOException {
		checkError();
		return in.read(b);
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		checkError();
		return in.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		checkError();
		return in.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		checkError();
		return in.readNBytes(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		checkError();
		return in.skip(n);
	}

	@Override
	public void skipNBytes(long n) throws IOException {
		checkError();
		in.skipNBytes(n);
	}

	@Override
	public void mark(int readlimit) {
		in.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		checkError();
		in.reset();
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public long transferTo(OutputStream out) throws IOException {
		checkError();
		return this.in.transferTo(out);
	}
}
