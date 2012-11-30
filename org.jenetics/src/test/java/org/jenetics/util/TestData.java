/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
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
