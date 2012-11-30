package org.jenetics.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

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
