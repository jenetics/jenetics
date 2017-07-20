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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.util;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.jenetics.util.Random64;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0
 */
public class DevURandom extends Random64 implements Closeable {
	private static final long serialVersionUID = 1L;

	private final DataInputStream _input = input();

	private static DataInputStream input() {
		try {
			return new DataInputStream(
				new BufferedInputStream(
					new FileInputStream("/dev/urandom")
				)
			);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public long nextLong() {
		try {
			return _input.readLong();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		_input.close();
	}

}
