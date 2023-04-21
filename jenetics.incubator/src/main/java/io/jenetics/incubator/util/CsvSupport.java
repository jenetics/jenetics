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
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.internal.util.Lifecycle.Value;

/**
 * Helper methods for splitting CSV rows and merging CSV columns into a valid
 * CSV row.
 *
 * <pre>{@code
 * // Read CSV, including multiline CSV files, if properly quoted.
 * final List<List<String>> rows;
 * try (Stream<String> lines = CsvSupport.read(new FileReader("some_file.csv"))) {
 *     rows = lines.map(CsvSupport::split).toList();
 * }
 *
 * // Write CSV.
 * final String csv = rows.stream()
 *     .map(CsvSupport::join)
 *     .collect(CsvSupport.toCsv());
 * Files.writeString(Path.of("some_other_file.csv"), csv);
 * }</pre>
 *
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class CsvSupport {

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

	private CsvSupport() {
	}

	/**
	 * Splits a given CSV line into its columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>. This is
	 * the reverse of the {@link #join(Iterable)} method.
	 *
	 * <pre>{@code
	 * final var line = "a,b,c,d,e,f";
	 * final var cols = CsvSupport.split(line);
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
	public static List<String> split(CharSequence line) {
		final var columns = new ArrayList<String>();
		final var column = new StringBuilder(32);

		boolean quoted = false;
		boolean escaped = false;

		for (int i = 0, n = line.length(); i < n; ++i) {
			final int previous = i > 0 ? line.charAt(i - 1) : -1;
			final char current = line.charAt(i);
			final int next = i + 1 < line.length() ? line.charAt(i + 1) : -1;

			switch (current) {
				case QUOTE -> {
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
				}
				case SEPARATOR -> {
					if (quoted) {
						column.append(current);
					} else if (SEPARATOR == previous || previous == -1) {
						columns.add(column.toString());
						column.setLength(0);
					}
				}
				default -> {
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
				}
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

		return List.copyOf(columns);
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
	 * final var line = CsvSupport.join(cols);
	 * assert "a,b,c,d,e,f".equals(line);
	 * }</pre>
	 *
	 * @see #split(CharSequence)
	 *
	 * @param cols the CSV columns to join
	 * @return a new CSV row, joined from the given columns
	 */
	public static String join(Iterable<?> cols) {
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

	private static String escape(Object value) {
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

	private static boolean mustEscape(CharSequence value) {
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
	 * final List<List<String>> rows = ...;
	 *
	 * final String csv = rows.stream()
	 *     .map(CsvSupport::join)
	 *     .collect(CsvSupport.toCSV());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV rows into one CSV string
	 */
	public static Collector<CharSequence, ?, String> toCsv() {
		return Collectors.joining(EOL, "", EOL);
	}


	/* *************************************************************************
	 * CSV line reader methods
	 * ************************************************************************/

	/**
	 * Splits the given {@code reader} into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller,
	 * which also closes the given reader.</em>
	 *
	 * @param reader the reader stream to split into CSV lines. The reader is
	 *        automatically closed when the returned line stream is closed.
	 * @return the stream of CSV lines
	 */
	public static Stream<String> read(Reader reader) {
		final Value<Stream<String>, IOException> result = new Value<>(resources -> {
			final var br = reader instanceof BufferedReader r
				? resources.add(r, Closeable::close)
				: resources.add(new BufferedReader(reader), Closeable::close);

			final var line = new StringBuilder();
			final Supplier<String> nextLine = () -> {
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

		return result.get().onClose(() ->
			result.uncheckedClose(UncheckedIOException::new)
		);
	}

	private static boolean nextLine(Reader reader, StringBuilder line)
		throws IOException
	{
		boolean quoted = false;
		boolean escaped = false;
		boolean eol = false;

		int next = -2;
		int i = 0;

		while (next >= 0 || (i = reader.read()) != -1) {
			final char current = next != -2 ? (char)next : (char)i;
			next = -2;

			switch (current) {
				case '\n', '\r' -> {
					if (quoted) {
						line.append(current);
					} else {
						eol = true;
					}
				}
				case QUOTE -> {
					if (quoted) {
						if (!escaped && (next = reader.read()) == QUOTE) {
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
					line.append(current);
				}
				default -> line.append(current);
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
