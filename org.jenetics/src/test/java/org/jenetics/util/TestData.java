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
package org.jenetics.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-04-27 $</em>
 */
public class TestData implements Iterable<String[]> {

	private final String _resource;

	public TestData(final String resource) {
		_resource = resource;
	}

	@Override
	public Iterator<String[]> iterator() {
		return new Iterator<String[]>() {

			private final Reader _reader = new Reader(_resource);

			private String[] _data = _reader.read();

			@Override
			public boolean hasNext() {
				return _data != null;
			}

			@Override
			public String[] next() {
				final String[] current = _data;
				_data = _reader.read();
				if (_data == null) {
					_reader.close();
				}
				return current;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private static final class Reader implements Closeable {

		private final BufferedReader _reader;

		Reader(final String resource) {
			_reader = new BufferedReader(new InputStreamReader(
				Reader.class.getResourceAsStream(resource)
			));
		}

		String[] read() {
			try {
				String line = null;
				while ((line = _reader.readLine()) != null &&
						(line.trim().startsWith("#") ||
						line.trim().isEmpty()))
				{
				}

				return line != null ? line.split(",") : null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void close() {
			try {
				if (_reader != null) {
					_reader.close();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
