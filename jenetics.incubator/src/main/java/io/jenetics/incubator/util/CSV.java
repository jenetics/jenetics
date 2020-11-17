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

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.incubator.util.Lifecycle.CloseableValue;

/**
 * Helper methods for splitting CSV rows and merging CSV columns into a valid
 * CSV row.
 *
 * <pre>{@code
 * // Read CSV, including multiline CSV files, if proper quoted.
 * final List<String> lines = CSV.LINE_READER.readAll(Path.of("some_file.csv"));
 * final List<List<String>> rows = lines.stream()
 *     .map(CSV::split)
 *     .collect(Collectors.toList());
 *
 * // Write CSV.
 * final String csv = rows.stream()
 *     .map(CSV::join)
 *     .collect(CSV.toCSV());
 * Files.write(Path.of("some_other_file.csv"), csv.getBytes());
 * }</pre>
 *
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class CSV {

	/**
	 * Interface for reading CSV lines from a given input stream. A default CSV
	 * line reader instances can be obtained with the {@link CSV#LINE_READER} field.
	 *
	 * <pre>{@code
	 * // The opened Stream must be closed after usage.
	 * try (Stream<String> lines = CSV.LINE_READER.read(Path.of("file.csv"))) {
	 *     lines.forEach(System.out::println);
	 * }
	 * }</pre>
	 *
	 * @see CSV#LINE_READER
	 */
	public interface LineReader {

		/**
		 * Splits the given content of the given {@code reader} into a
		 * {@code Stream} of CSV lines. The lines are split at line breaks, as
		 * long as they are not part of a quoted column. <em>The returned stream
		 * must be closed by the caller.</em>
		 *
		 * <pre>{@code
		 * try (var lines = CSV.LINE_READER.read(reader)) {
		 *     lines.forEach(System.out::println);
		 * }
		 * }</pre>
		 *
		 * @see #read(Path)
		 *
		 * @param reader the reader stream to split into CSV lines
		 * @return the stream of CSV lines
		 */
		Stream<String> read(final Reader reader);

		/**
		 * Splits the given {@code path} into a {@code Stream} of CSV lines.
		 * The lines are split at line breaks, as long as they are not part of a
		 * quoted column. <em>The returned stream must be closed by the
		 * caller.</em>
		 *
		 * <pre>{@code
		 * try (var lines = CSV.LINE_READER.read(path, UTF_8)) {
		 *     lines.forEach(System.out::println);
		 * }
		 * }</pre>
		 *
		 * @see #read(Reader)
		 *
		 * @param path the CSV file to split
		 * @param cs the charset to use for decoding
		 * @return the stream of CSV lines
		 * @throws IOException if an I/O error occurs
		 */
		default Stream<String> read(final Path path, final Charset cs)
			throws IOException
		{
			final var result = CloseableValue.build(resources -> {
				final var reader =
					resources.add(Files.newBufferedReader(path, cs));
				return read(reader);
			});

			return result.get().onClose(result::uncheckedClose);
		}

		/**
		 * Splits the given {@code path} into a {@code Stream} of CSV lines.
		 * The lines are split at line breaks, as long as they are not part of a
		 * quoted column. <em>The returned stream must be closed by the
		 * caller.</em>
		 *
		 * <pre>{@code
		 * try (var lines = CSV.LINE_READER.read(path, UTF_8)) {
		 *     lines.forEach(System.out::println);
		 * }
		 * }</pre>
		 *
		 * @see #read(Reader)
		 *
		 * @param path the CSV file to split
		 * @return the stream of CSV lines
		 * @throws IOException if an I/O error occurs
		 */
		default Stream<String> read(final Path path) throws IOException {
			return read(path, Charset.defaultCharset());
		}

		/**
		 * Reads all CSV lines form the given {@code reader}.
		 *
		 * @see #readAll(Path)
		 *
		 * @param reader the CSV {@code reader} stream
		 * @return all CSV lines form the given {@code reader} stream
		 * @throws IOException if an error occurs while reading the CSV lines
		 */
		default List<String> readAll(final Reader reader)
			throws IOException
		{
			try (var stream = read(reader)) {
				return stream.collect(Collectors.toList());
			} catch (UncheckedIOException e) {
				throw e.getCause();
			}
		}

		/**
		 * Reads all CSV lines form the given input {@code path}.
		 *
		 * @see #read(Reader)
		 *
		 * @param path the CSV file to read
		 * @param cs the charset to use for decoding
		 * @return all CSV lines form the given {@code input} stream
		 * @throws IOException if an error occurs while reading the CSV lines
		 */
		default List<String> readAll(final Path path, final Charset cs)
			throws IOException
		{
			try (var stream = read(path, cs)) {
				return stream.collect(Collectors.toList());
			} catch (UncheckedIOException e) {
				throw e.getCause();
			}
		}

		/**
		 * Reads all CSV lines form the given input {@code path}.
		 *
		 * @see #read(Reader)
		 *
		 * @param path the CSV file to read
		 * @return all CSV lines form the given {@code input} stream
		 * @throws IOException if an error occurs while reading the CSV lines
		 */
		default List<String> readAll(final Path path)
			throws IOException
		{
			return readAll(path, Charset.defaultCharset());
		}
	}

	/**
	 * The default CSV line reader.
	 *
	 * <pre>{@code
	 * try (var lines = CSV.LINE_READER.read(path)) {
	 *     lines.forEach(System.out::println);
	 * }
	 * }</pre>
	 */
	public static final LineReader LINE_READER = CSV::read;

	/**
	 * The newline string used for writing the CSV file: {@code \r\n}.
	 */
	public static final String EOL = "\r\n";

	/**
	 * The separator character: {@code ,}
	 */
	private static final char SEPARATOR = ',';

	/**
	 * The quote character: {@code "}
	 */
	private static final char QUOTE = '"';

	private static final String SEPARATOR_STR = ",";
	private static final String QUOTE_STR = "\"";
	private static final String DOUBLE_QUOTE_STR = "\"\"";

	private CSV() {
	}

	/**
	 * Splits a given CSV line into it's columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>. This is
	 * the reverse of the {@link #join(Iterable)} method.
	 *
	 * <pre>{@code
	 * final var line = "a,b,c,d,e,f";
	 * final var cols = CSV.split(line);
	 * assert List.of("a", "b", "c", "d", "e", "f").equals(cols)
	 * }</pre>
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
	 * @see #join(Iterable)
	 *
	 * @param line the CSV {@code row} to split
	 * @return the split columns of the given CSV {@code row}
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static List<String> split(final CharSequence line) {
		final List<String> columns = new ArrayList<>();
		final var column = new StringBuilder(32);

		boolean quoted = false;
		boolean escaped = false;

		for (int i = 0, n = line.length(); i < n; ++i) {
			final int previous = i > 0 ? line.charAt(i - 1) : -1;
			final char current = line.charAt(i);
			final int next = i + 1 < line.length() ? line.charAt(i + 1) : -1;

			switch (current) {
				case QUOTE:
					if (quoted) {
						if (!escaped && QUOTE == next) {
							escaped = true;
						} else {
							if (escaped) {
								column.append(QUOTE);
								escaped = false;
							} else {
								if (next != -1 && SEPARATOR != next) {
									throw new IllegalArgumentException(format(
										"No other token than '%s' allowed after " +
										"quote, but found '%s'.",
										SEPARATOR, next
									));
								}

								columns.add(column.toString());
								column.setLength(0);
								quoted = false;
							}
						}
					} else {
						if (previous != -1 && SEPARATOR != previous) {
							throw new IllegalArgumentException(format(
								"No other token than '%s' allowed before " +
								"quote, but found '%s'.",
								SEPARATOR, previous
							));
						}
						quoted = true;
					}
					break;
				case SEPARATOR:
					if (quoted) {
						column.append(current);
					} else if (SEPARATOR == previous || previous == -1) {
						columns.add(column.toString());
						column.setLength(0);
					}
					break;
				default:
					int j = i;

					// Read till the next token separator.
					char c;
					while (j < n && !isTokenSeparator(c = line.charAt(j))) {
						column.append(c);
						++j;
					}

					if (j != i) {
						i = j - 1;
					}

					if (!quoted) {
						columns.add(column.toString());
						column.setLength(0);
					}
					break;
			}
		}

		if (quoted) {
			throw new IllegalArgumentException("Unbalanced quote character.");
		}
		if (line.length() == 0 ||
			SEPARATOR == line.charAt(line.length() - 1))
		{
			columns.add("");
		}

		return columns;
	}

	private static boolean isTokenSeparator(final char c) {
		return c == SEPARATOR || c == QUOTE;
	}

	/**
	 * Joins the given columns to a CSV line. This is the reverse operation of
	 * the {@link #split(CharSequence)} method.
	 *
	 * <pre>{@code
	 * final var cols = List.of("a", "b", "c", "d", "e", "f");
	 * final var line = CSV.join(cols);
	 * assert "a,b,c,d,e,f".equals(line);
	 * }</pre>
	 *
	 * @see #split(CharSequence)
	 *
	 * @param cols the CSV columns to join
	 * @return a new CSV row, joined from the given columns
	 */
	public static String join(final Iterable<?> cols) {
		final var row = new StringBuilder(32);

		final var it = cols.iterator();
		while (it.hasNext()) {
			final var column = it.next();
			row.append(escape(column));
			if (it.hasNext()) {
				row.append(SEPARATOR_STR);
			}
		}

		return row.toString();
	}

	private static String escape(final Object value) {
		if (value == null) {
			return "";
		} else {
			var stringValue = value.toString();
			var string = stringValue.replace(QUOTE_STR, DOUBLE_QUOTE_STR);

			if (stringValue.length() != string.length() || mustEscape(string)) {
				return QUOTE_STR + string + QUOTE_STR;
			} else {
				return stringValue;
			}
		}
	}

	private static boolean mustEscape(final CharSequence value) {
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (c == SEPARATOR || c == '\n' || c == '\r') {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a collector for joining a list of CSV rows into one CSV string.
	 *
	 * <pre>{@code
	 * final List<List<String>> values = ...;
	 *
	 * final String csv = values.stream()
	 *     .map(CSV::join)
	 *     .collect(CSV.toCSV());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV rows into one CSV string
	 */
	public static Collector<CharSequence, ?, String> toCSV() {
		return Collectors.joining(EOL, "", EOL);
	}


	/* *************************************************************************
	 * CSV line reader methods
	 * ************************************************************************/

	/**
	 * Splits the given {@code reader} into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller.</em>
	 *
	 * @param reader the reader stream to split into CSV lines
	 * @return the stream of CSV lines
	 */
	static Stream<String> read(final Reader reader) {
		final var result = CloseableValue.build(resources -> {
			final var br = reader instanceof BufferedReader
				? (BufferedReader)reader
				: resources.add(new BufferedReader(reader));

			final Supplier<String> nextLine = () -> {
				final var line = new StringBuilder();
				try {
					if (nextLine(br, line)) {
						final var l = line.toString();
						line.setLength(0);
						return l;
					} else {
						return null;
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};

			return Stream.generate(nextLine)
				.takeWhile(Objects::nonNull);
		});

		return result.get().onClose(result::uncheckedClose);
	}

	private static boolean nextLine(
		final BufferedReader reader,
		final StringBuilder line
	)
		throws IOException
	{
		boolean quoted = false;
		boolean escaped = false;
		boolean eol = false;

		char current = 0;
		int next = -2;
		int i = 0;

		while (next >= 0 || (i = reader.read()) != -1) {
			current = next != -2 ? (char)next : (char)i;
			next = -2;

			switch (current) {
				case '\n':
				case '\r':
					if (quoted) {
						line.append(current);
					} else {
						eol = true;
					}
					break;
				case QUOTE:
					if (quoted) {
						next = reader.read();
						if (next == QUOTE && !escaped) {
							escaped = true;
						} else {
							if (escaped) {
								escaped = false;
							} else {
								quoted = false;
							}
						}
					} else {
						quoted = true;
					}
					line.append(QUOTE);
					break;
				default:
					line.append(current);
					break;
			}

			if (eol) {
				eol = false;
				if (line.length() > 0) {
					return true;
				}
			}
		}

		return line.length() > 0;
	}

}
