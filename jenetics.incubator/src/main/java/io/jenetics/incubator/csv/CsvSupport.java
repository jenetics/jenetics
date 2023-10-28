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
 * try (Stream<String> lines = CsvSupport.lines(new FileReader("some_file.csv"))) {
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

	public record Quote(char value) {
		public static final Quote DEFAULT = new Quote('"');
	}

	public record Separator(char value) {
		public static final Separator DEFAULT = new Separator(',');
	}

	public record Indexes(int... values) {
		public static final Indexes ALL = new Indexes();
	}

	/**
	 * The newline string used for writing the CSV file: {@code \r\n}.
	 */
	public static final String EOL = "\r\n";


	private CsvSupport() {
	}

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
	public static Stream<String> lines(final Reader reader) {
		return LineReader.DEFAULT.read(reader);
	}

	public static String[] split(final CharSequence line) {
		return LineSplitter.DEFAULT.split(line);
	}

	public static String join(final Iterable<?> columns) {
		return ColumnJoiner.DEFAULT.join(columns);
	}


	/**
	 * Return a collector for joining a list of CSV rows into one CSV string.
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
	 * Base CSV classes.
	 * ************************************************************************/

	/**
	 * This class reads CSV files and splits it into lines. It also obeys quoted
	 * new lines.
	 *
	 * @apiNote
	 * This reader obeys <em>escaped</em> line breaks according
	 * <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>.
	 */
	public static final class LineReader {

		static final LineReader DEFAULT = new LineReader(Quote.DEFAULT);

		private final Quote quote;

		/**
		 * Create a new line-reader with the given parameters.
		 *
		 * @param quote the quoting character
		 */
		public LineReader(final Quote quote) {
			this.quote = requireNonNull(quote);
		}

		/**
		 * Reads all CSV lines from the given {@code reader}.
		 *
		 * @apiNote
		 * This method must be used within a try-with-resources statement or
		 * similar control structure to ensure that the stream's open file is
		 * closed promptly after the stream's operations have completed.
		 *
		 * @param reader the reader from which to read the CSV content
		 * @return the CSV lines from the file as a {@code Stream}
		 */
		public Stream<String> read(final Reader reader) {
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

		private boolean nextLine(Reader reader, StringBuilder line)
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

				if (current == '\n' || current == '\r') {
					if (quoted) {
						line.append(current);
					} else {
						eol = true;
					}
				} else if (current == quote.value) {
					if (quoted) {
						if (!escaped && (next = reader.read()) == quote.value) {
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
				} else {
					line.append(current);
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
	}


	/**
	 * Splitting a CSV line into columns (records).
	 */
	public static final class LineSplitter {

		static final LineSplitter DEFAULT = new LineSplitter(
			Separator.DEFAULT,
			Quote.DEFAULT,
			Indexes.ALL
		);

		private final Columns columns;
		private final Separator separator;
		private final Quote quote;

		private LineSplitter(
			final Columns columns,
			final Separator separator,
			final Quote quote
		) {
			this.columns = requireNonNull(columns);
			this.separator = requireNonNull(separator);
			this.quote = requireNonNull(quote);
		}

		public LineSplitter(
			final Separator separator,
			final Quote quote,
			final Indexes indexes
		) {
			this(new Columns(indexes), separator, quote);
		}

		public String[] split(final CharSequence line) {
			columns.clear();
			final StringBuilder column = new StringBuilder(32);

			boolean quoted = false;
			boolean escaped = false;
			boolean full = false;

			for (int i = 0, n = line.length(); i < n && !full; ++i) {
				final int previous = i > 0 ? line.charAt(i - 1) : -1;
				final char current = line.charAt(i);
				final int next = i + 1 < line.length() ? line.charAt(i + 1) : -1;

				if (current == quote.value) {
					if (quoted) {
						if (!escaped && quote.value == next) {
							escaped = true;
						} else {
							if (escaped) {
								column.append(quote.value);
								escaped = false;
							} else {
								if (next != -1 && separator.value != next) {
									throw new IllegalArgumentException(format(
										"No other token than '%s' allowed after " +
											"quote, but found '%s'.",
										separator.value, next
									));
								}


								add(column);
								full = columns.isFull();
								quoted = false;
							}
						}
					} else {
						if (previous != -1 && separator.value != previous) {
							throw new IllegalArgumentException(format(
								"No other token than '%s' allowed before " +
									"quote, but found '%s'.",
								separator.value, previous
							));
						}
						quoted = true;
					}
				} else if (current == separator.value) {
					if (quoted) {
						column.append(current);
					} else if (separator.value == previous || previous == -1) {
						add(column);
						full = columns.isFull();
					}
				} else {
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

			if (quoted) {
				throw new IllegalArgumentException("Unbalanced quote character.");
			}
			if (line.isEmpty() ||
				separator.value == line.charAt(line.length() - 1))
			{
				add(column);
			}

			return columns.columns();
		}

		private void add(final StringBuilder column) {
			columns.add(column.toString());
			column.setLength(0);
		}

		private boolean isTokenSeparator(final char c) {
			return c == separator.value || c == quote.value;
		}
	}


	/**
	 * Column collection, which is backed up by a string list.
	 */
	private static final class Columns {
		private final List<String> columns = new ArrayList<>();
		private final Indexes indexes;

		private int index = 0;
		private int count = 0;

		Columns(final Indexes indexes) {
			this.indexes = requireNonNull(indexes);
		}

		/**
		 * Appends a {@code column} to the column collection.
		 *
		 * @param column the column to add
		 */
		void add(String column) {
			if (!isFull()) {
				count += set(column, index++);
			}
		}

		private int set(String element, int column) {
			int updated = 0;

			if (indexes.values.length == 0) {
				columns.add(element);
				++updated;
			} else {
				int pos = -1;
				while ((pos = Columns.indexOf(indexes.values, pos + 1, column)) != -1) {
					for (int i = columns.size(); i < pos; ++i) {
						columns.add(null);
					}
					columns.set(pos, element);
					++updated;
				}
			}

			return updated;
		}

		private static int indexOf(int[] array, int start, int value) {
			for (int i = start; i < array.length; ++i) {
				if (array[i] == value) {
					return i;
				}
			}

			return -1;
		}

		/**
		 * Checks whether another column can be added.
		 *
		 * @return {@code true} if another column can be added to this
		 *         collection, {@code false} otherwise
		 */
		boolean isFull() {
			return indexes.values.length > 0 && indexes.values.length <= count;
		}

		/**
		 * Removes all columns.
		 */
		public void clear() {
			columns.clear();
			index = 0;
			count = 0;
		}

		String[] columns() {
			return columns.toArray(String[]::new);
		}

	}

	/**
	 * This class joins an array of columns into one CSV line.
	 */
	public static final class ColumnJoiner {

		static final ColumnJoiner DEFAULT = new ColumnJoiner(
			Separator.DEFAULT,
			Quote.DEFAULT,
			Indexes.ALL
		);

		/**
		 * The CSV line splitter parameter.
		 *
		 * @param separator the column separator char
		 * @param quote the qute char
		 * @param indexes the column indices to read. If empty, all split columns
		 *        are used.
		 */
		private record Param(char separator, char quote, int... indexes) {
			public static final Param DEFAULT = new Param(',', '"');

			public Param(int... indexes) {
				this(DEFAULT.separator, DEFAULT.quote, indexes);
			}

			private String escape(Object value) {
				final var quoteStr = String.valueOf(quote);

				if (value == null) {
					return "";
				} else {
					var stringValue = value.toString();
					var string = stringValue.replace(quoteStr, quoteStr + quoteStr);

					if (stringValue.length() != string.length() || mustEscape(string)) {
						return quoteStr + string + quoteStr;
					} else {
						return stringValue;
					}
				}
			}

			private boolean mustEscape(CharSequence value) {
				for (int i = 0; i < value.length(); ++i) {
					final char c = value.charAt(i);
					if (c == separator || c == '\n' || c == '\r') {
						return true;
					}
				}
				return false;
			}
		}

		private final Param param;
		private final int columnCount;

		public ColumnJoiner(
			final Separator separator,
			final Quote quote,
			final Indexes indexes
		) {
			this.param = new Param(separator.value, quote.value, indexes.values);
			columnCount = max(param.indexes) + 1;
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

		public String join(final Iterable<?> cols) {
			if (param.indexes.length == 0) {
				return join0(cols);
			} else {
				final var values = new Object[columnCount];
				int i = 0;
				for (var col : cols) {
					values[param.indexes[i++]] = col;
				}

				return join0(Arrays.asList(values));
			}
		}

		private String join0(final Iterable<?> cols) {
			final var row = new StringBuilder(32);
			final var it = cols.iterator();
			while (it.hasNext()) {
				final var column = it.next();
				row.append(param.escape(column));
				if (it.hasNext()) {
					row.append(param.separator);
				}
			}

			return row.toString();
		}

	}

}

