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

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jenetics.internal.util.exception;

/**
 * Helper class for reading test data from file. The file has the following
 * format: {@code $resource[$param1, $param2,...].dat}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class TestData implements Iterable<String[]> {

	private final String _resource;
	private final String[] _parameters;

	private TestData(final String resource, final String... parameters) {
		_resource = resource;
		_parameters = parameters;
	}

	/**
	 * Return the base resource name, without extension and parameters.
	 *
	 * @return the base resource name
	 */
	public String getResource() {
		return _resource;
	}

	/**
	 * Return the full resource path, with parameters and extension.
	 *
	 * @return the full resource path
	 */
	public String getResourcePath() {
		final String param = _parameters.length == 0 ? "" :
			Arrays.stream(_parameters)
				.collect(Collectors.joining(",", "[", "]"));

		return _resource + param + ".dat";
	}

	/**
	 * Return the test data parameters.
	 *
	 * @return the test data parameters
	 */
	public String[] getParameters() {
		return _parameters;
	}

	@Override
	public Iterator<String[]> iterator() {
		return new DataIterator(getResourcePath());
	}

	/**
	 * Return a stream with the data lines.
	 *
	 * @return a stream with the data lines
	 */
	public Stream<String[]> stream() {
		final DataIterator iterator = new DataIterator(getResourcePath());
		final Spliterator<String[]> spliterator = Spliterators
			.spliteratorUnknownSize(iterator, 0);

		return StreamSupport
			.stream(spliterator, false)
			.onClose(iterator::close);
	}

	public LongStream longStream() {
		return stream().mapToLong(line -> Long.parseLong(line[0]));
	}

	@Override
	public String toString() {
		return getResourcePath();
	}

	/**
	 * Create a new {@code TestData} object from the given base resource name
	 * and parameters.
	 *
	 * @param resource the base resource name
	 * @param parameters the test data parameters
	 * @return a new test data object
	 */
	public static TestData of(final String resource, final String... parameters) {
		return new TestData(resource, parameters);
	}

	public static Stream<TestData> list(final String path) {
		return resources(path)
			.map(name -> of(parseResource(name), parseParameters(name)));
	}

	private static String parseResource(final String name) {
		final int end = name.indexOf('[');
		return name.substring(0, end);
	}

	private static String[] parseParameters(final String name) {
		final int start = name.indexOf('[');
		final int end = name.lastIndexOf(']');

		return (start != -1 && end != -1) ?
			name.substring(start + 1, end).split(",") :
			new String[0];
	}

	private static Stream<String> resources(final String path) {
		try {
			final String className = TestData.class.getName();
			final String classPath = className.replace(".", "/") + ".class";

			final URL url = TestData.class.getClassLoader().getResource(classPath);
			final String absoluteClassPath = new File(url.toURI()).getAbsolutePath();
			final String basePath = absoluteClassPath.substring(
				0, absoluteClassPath.length() - classPath.length()
			);

			return ofNullable(new File(basePath, path).list())
				.map(Arrays::stream)
				.map(lines -> lines
					.filter(line -> line.endsWith(".dat"))
					.map(line -> (path + "/" + line)))
				.orElse(Stream.empty());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int[] toInt(final String[] line) {
		return Arrays.stream(line).mapToInt(Integer::parseInt).toArray();
	}

	public static int[] toInt(final double[] array) {
		final int[] result = new int[array.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (int)array[i];
		}
		return result;
	}

	public static long[] toLong(final String[] line) {
		return Arrays.stream(line).mapToLong(Long::parseLong).toArray();
	}

	public static double[] toDouble(final String[] line) {
		return Arrays.stream(line).mapToDouble(Double::parseDouble).toArray();
	}

	/**
	 * The closeable line iterator.
	 */
	private static final class DataIterator
		implements Iterator<String[]>, Closeable
	{
		private final Reader _reader;

		private String[] _data;

		DataIterator(final String resource) {
			_reader = new Reader(resource);
			_data = _reader.read();
		}

		@Override
		public boolean hasNext() {
			return _data != null;
		}

		@Override
		public String[] next() {
			final String[] current = _data;
			_data = _reader.read();
			if (_data == null) {
				close();
			}
			return current;
		}

		@Override
		public void close() {
			_reader.close();
		}
	}

	/**
	 * The reader class used for reading the test data.
	 */
	private static final class Reader implements Closeable {
		private final BufferedReader _reader;

		Reader(final String resource) {
			_reader = ofNullable(Reader.class.getResourceAsStream(resource))
				.map(InputStreamReader::new)
				.map(BufferedReader::new)
				.orElseThrow(() -> new IllegalArgumentException(format(
						"Resource '%s' not found.", resource
					)));
		}

		String[] read() {
			try {
				String line = null;
				do {
					line = _reader.readLine();
				} while (line != null &&
					(line.trim().startsWith("#") || line.trim().isEmpty()));

				return line != null ? line.split(",") : null;
			} catch (IOException e) {
				exception.ignore(UncheckedIOException.class, this::close);
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public void close() {
			try {
				_reader.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
