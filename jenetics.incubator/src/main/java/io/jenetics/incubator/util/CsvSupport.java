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
import static java.util.Objects.requireNonNull;
import static io.jenetics.incubator.util.CsvSupport.QUOTE;
import static io.jenetics.incubator.util.CsvSupport.SEPARATOR;
import static io.jenetics.incubator.util.CsvSupport.isTokenSeparator;

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
	static final char SEPARATOR = ',';

	/**
	 * The quote character: {@code "}
	 */
	static final char QUOTE = '"';

	static final String SEPARATOR_STR = ",";
	static final String QUOTE_STR = "\"";
	static final String DOUBLE_QUOTE_STR = "\"\"";

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
	 * @see #split(CharSequence, String[], int...)
	 * @see #join(Iterable)
	 *
	 * @param line the CSV {@code row} to split
	 * @param indexes the sub-set of CSV column indexes which should returned.
	 *        If no {@code indexes} are given, all split columns are returned
	 * @return the split columns of the given CSV {@code row}
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static List<String> split(final CharSequence line, final int... indexes) {
		final var columns = new ArrayList<String>();
		final var splitter = new Splitter(new ColumnsList(columns, indexes));
		splitter.split(line);

		return List.copyOf(columns);
	}

	/**
	 * Splits a given CSV line into its columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>. This is
	 * the reverse of the {@link #join(Iterable)} method.
	 *
	 * <pre>{@code
	 * final var line = "a,b,c,d,e,f";
	 * final var cols = new String[6]
	 * CsvSupport.split(line, cols);
	 * }</pre>
	 *
	 * @param line the CSV {@code row} to split
	 * @param columns the columns, where the split result is written to. The
	 *        splitting stops, if no more columns are available in the
	 *        {@code line} or the {@code columns} array is full.
	 * @param indexes the sub-set of CSV column indexes which should returned.
	 *        If no {@code indexes} are given, all split columns are returned
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static void split(CharSequence line, String[] columns, int... indexes) {
		new Splitter(new ColumnsArray(columns, indexes)).split(line);
	}

	static boolean isTokenSeparator(final char c) {
		return c == SEPARATOR || c == QUOTE;
	}

	/**
	 * Joins the given columns to a CSV line. This is the reverse operation of
	 * the {@link #split(CharSequence, int...)} method.
	 *
	 * <pre>{@code
	 * final var cols = List.of("a", "b", "c", "d", "e", "f");
	 * final var line = CsvSupport.join(cols);
	 * assert "a,b,c,d,e,f".equals(line);
	 * }</pre>
	 *
	 * @see #split(CharSequence, int...)
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
					return nextLine(br, line) ? line.toString() : null;
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
		line.setLength(0);

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

sealed interface Columns {
	void append(final String column);
	boolean isFull();
}

final class ColumnsArray implements Columns {
	private final String[] columns;
	private final int[] indexes;

	private int index = 0;
	private int csvIndex = 0;

	ColumnsArray(final String[] columns, final int[] indexes) {
		this.columns = columns;
		this.indexes = indexes;
	}

	@Override
	public void append(final String column) {
		if (index < columns.length) {
			if (indexes.length == 0 ||
				(indexes.length > index && csvIndex == indexes[index]))
			{
				columns[index++] = column;
			}
		}

		++csvIndex;
	}

	@Override
	public boolean isFull() {
		return columns.length <= index ||
			(indexes.length > 0 && indexes.length <= index);
	}
}

final class ColumnsList implements Columns {
	private final List<String> columns;
	private final int[] indexes;

	private int index = 0;
	private int csvIndex = 0;

	ColumnsList(final List<String> columns, final int... indexes) {
		this.columns = columns;
		this.indexes = indexes;
	}

	@Override
	public void append(String column) {
		if (indexes.length == 0 ||
			(indexes.length > index && csvIndex == indexes[index]))
		{
			columns.add(column);
			++index;
		}

		++csvIndex;
	}

	@Override
	public boolean isFull() {
		return indexes.length > 0 && indexes.length <= index;
	}
}

final class Splitter {
	private final Columns columns;
	private final StringBuilder column = new StringBuilder(32);

	Splitter(final Columns columns) {
		this.columns = requireNonNull(columns);
	}

	void split(final CharSequence line) {
		boolean quoted = false;
		boolean escaped = false;
		boolean full = false;

		for (int i = 0, n = line.length(); i < n && !full; ++i) {
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


								addColumn();
								full = columns.isFull();
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
						addColumn();
						full = columns.isFull();
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
						addColumn();
						full = columns.isFull();
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
			addColumn();
		}
	}

	private void addColumn() {
		columns.append(column.toString());
		column.setLength(0);
	}
}
