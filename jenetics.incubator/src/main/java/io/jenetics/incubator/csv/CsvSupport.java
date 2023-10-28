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
package io.jenetics.incubator.csv;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * {@snippet lang="java":
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
 * }
 *
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
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
	 * the reverse of the {@link #join(Iterable,int...)} method.
	 *
	 * {@snippet lang="java":
	 * final var line = "a,b,c,d,e,f";
	 * final var cols = CsvSupport.split(line);
	 * assert List.of("a", "b", "c", "d", "e", "f").equals(cols);
	 * }
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
	 * @see #split(CharSequence, String[], int...)
	 * @see #join(Iterable,int...)
	 *
	 * @param line the CSV {@code row} to split
	 * @param indexes the subset of CSV column indexes which should returned.
	 *        If no {@code indexes} are given, all split columns are returned
	 * @return the split columns of the given CSV {@code row}
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static List<String> split(final CharSequence line, final int... indexes) {
		if (indexes.length > 0) {
			final String[] columns = new String[indexes.length];
			split(line, columns, indexes);
			return List.of(columns);
		} else {
			final var columns = new ArrayList<String>();
			new Splitter(new ColumnList(columns, indexes)).split(line);
			return List.copyOf(columns);
		}
	}

	/**
	 * Splits a given CSV line into its columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>. This is
	 * the reverse of the {@link #join(Iterable,int...)} method.
	 *
	 * {@snippet lang="java":
	 * final var line = "a,b,c,d,e,f";
	 * final var cols = new String[6];
	 * CsvSupport.split(line, cols);
	 * }
	 *
	 * @param line the CSV {@code row} to split
	 * @param columns the columns, where the split result is written to. The
	 *        splitting stops, if no more columns are available in the
	 *        {@code line} or the {@code columns} array is full.
	 * @param indexes the subset of CSV column indexes which should returned.
	 *        If no {@code indexes} are given, all split columns are returned
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @return the input {@code columns} string array
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static String[] split(
		final CharSequence line,
		final String[] columns,
		final int... indexes
	) {
		new Splitter(new ColumnArray(columns, indexes)).split(line);
		return columns;
	}

	static boolean isTokenSeparator(final char c) {
		return c == SEPARATOR || c == QUOTE;
	}

	/**
	 * Joins the given columns to a CSV line. This is the reverse operation of
	 * the {@link #split(CharSequence, int...)} method.
	 *
	 * {@snippet lang="java":
	 * final List<String> cols = List.of("a", "b", "c", "d", "e", "f");
	 * final String line = CsvSupport.join(cols);
	 * assert "a,b,c,d,e,f".equals(line);
	 * }
	 *
	 * @see #split(CharSequence, int...)
	 *
	 * @param cols the CSV columns to join
	 * @return a new CSV row, joined from the given columns
	 */
	public static String join(Iterable<?> cols, int... indexes) {
		if (indexes.length == 0) {
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
		} else {
			final var values = new Object[max(indexes) + 1];
			int i = 0;
			for (var col : cols) {
				values[indexes[i++]] = col;
			}

			return join(Arrays.asList(values));
		}
	}

	private static int max(int[] array) {
		int max = Integer.MIN_VALUE;
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
		return max;
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

	public static String join(Object[] cols, int... indexes) {
		return join(Arrays.asList(cols), indexes);
	}

	/**
	 * Return a collector for joining a list of CSV rows into one CSV string.
	 *
	 * {@snippet lang="java":
	 * final List<List<String>> rows = null; // @replace substring='null' replacement="..."
	 *
	 * final String csv = rows.stream()
	 *     .map(CsvSupport::join)
	 *     .collect(CsvSupport.toCSV());
	 * }
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
		requireNonNull(reader);

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
				if (!line.isEmpty()) {
					return true;
				}
			}
		}

		return !line.isEmpty();
	}

	/* *************************************************************************
	 * Classes for splitting CSV lines.
	 * ************************************************************************/

	/**
	 * This class is used for splitting a CSV line into columns.
	 */
	public static final class Splitter {
		private final Columns columns;

		Splitter(final Columns columns) {
			this.columns = requireNonNull(columns);
		}

		public Splitter(String[] columns, int... indexes) {
			this(new ColumnArray(columns, indexes));
		}

		public Splitter(List<String> columns, int... indexes) {
			this(new ColumnList(columns, indexes));
		}

		public void split(final CharSequence line) {
			final StringBuilder column = new StringBuilder(32);

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


									add(column);
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
							add(column);
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
							add(column);
							full = columns.isFull();
						}
					}
				}
			}

			if (quoted) {
				throw new IllegalArgumentException("Unbalanced quote character.");
			}
			if (line.isEmpty() ||
				SEPARATOR == line.charAt(line.length() - 1))
			{
				add(column);
			}
		}

		private void add(final StringBuilder column) {
			columns.add(column.toString());
			column.setLength(0);
		}
	}

	/**
	 * Interface abstraction holding the columns of one CSV lines.
	 */
	private sealed interface Columns {

		/**
		 * Appends a {@code column} to the column collection.
		 *
		 * @param column the column to add
		 */
		void add(final String column);

		/**
		 * Checks whether another column can be added.
		 *
		 * @return {@code true} if another column can be added to this
		 *         collection, {@code false} otherwise
		 */
		boolean isFull();

		static int indexOf(int[] array, int start, int value) {
			for (int i = start; i < array.length; ++i) {
				if (array[i] == value) {
					return i;
				}
			}

			return -1;
		}

	}

	/**
	 * Column collection, which is backed up by a {@code String[]} array.
	 */
	private static final class ColumnArray implements Columns {
		private final String[] columns;
		private final int[] indexes;

		private int index = 0;
		private int count = 0;

		ColumnArray(final String[] columns, final int[] indexes) {
			this.columns = requireNonNull(columns);
			this.indexes = requireNonNull(indexes);
		}

		@Override
		public void add(final String column) {
			if (!isFull()) {
				count += set(column, index++);
			}
		}

		private int set(String element, int column) {
			int updated = 0;

			if (indexes.length == 0) {
				columns[column] = element;
				++updated;
			} else {
				int pos = -1;
				while ((pos = Columns.indexOf(indexes, pos + 1, column)) != -1 &&
					pos < columns.length)
				{
					columns[pos] = element;
					++updated;
				}
			}

			return updated;
		}

		@Override
		public boolean isFull() {
			return columns.length <= count ||
				(indexes.length > 0 && indexes.length <= count);

		}

	}

	/**
	 * Column collection, which is backed up by a string list.
	 */
	private static final class ColumnList implements Columns {
		private final List<String> columns;
		private final int[] indexes;

		private int index = 0;
		private int count = 0;

		ColumnList(final List<String> columns, final int[] indexes) {
			this.columns = requireNonNull(columns);
			this.indexes = requireNonNull(indexes);
		}

		@Override
		public void add(String column) {
			if (!isFull()) {
				count += set(column, index++);
			}
		}

		private int set(String element, int column) {
			int updated = 0;

			if (indexes.length == 0) {
				columns.add(element);
				++updated;
			} else {
				int pos = -1;
				while ((pos = Columns.indexOf(indexes, pos + 1, column)) != -1) {
					for (int i = columns.size(); i < pos; ++i) {
						columns.add(null);
					}
					columns.set(pos, element);
					++updated;
				}
			}

			return updated;
		}

		@Override
		public boolean isFull() {
			return indexes.length > 0 && indexes.length <= count;
		}

	}


	interface Spliter {
		String[] split(CharSequence line);
	}

	interface Merger {
		String merge(Object[] values);
	}

}

