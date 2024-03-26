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
 * This class contains classes and helper methods for splitting CSV files into
 * lines a splitting CSV lines into columns. How to do this can be seen in the
 * following example:
 * {@snippet class="io.jenetics.incubator.csv.CsvSupportTest" region="readRows"}
 * Or a shorter variant.
 * {@snippet lang="java":
 * final List<String> lines = CsvSupport.readAllLines(new FileReader("data.csv"));
 * }
 *
 * If you want to read a subset of the CSV columns into a record, the code snippet
 * below, shows how to do this.
 * {@snippet class="io.jenetics.incubator.csv.CsvSupportTest" region="readEntries"}
 *
 * This will create the following list of records:
 * <pre>{@code
 * > City[name=Aixàs, country=ad, lat=42.4833333, lon=1.4666667]
 * > City[name=Aixirivali, country=ad, lat=42.4666667, lon=1.5]
 * > City[name=Aixirivall, country=ad, lat=42.4666667, lon=1.5]
 * > City[name=Aixirvall, country=ad, lat=42.4666667, lon=1.5]
 * > City[name=Aixovall, country=ad, lat=42.4666667, lon=1.4833333]
 * }</pre>
 *
 * The reverse is also possible: Converting a list of records to a CSV string.
 * {@snippet class="io.jenetics.incubator.csv.CsvSupportTest" region="writeEntries"}
 *
 * The generated CSV string will look like this:
 * <pre>{@code
 * > ad,,Aixàs,,,42.4833333,1.4666667
 * > ad,,Aixirivali,,,42.4666667,1.5
 * > ad,,Aixirivall,,,42.4666667,1.5
 * > ad,,Aixirvall,,,42.4666667,1.5
 * > ad,,Aixovall,,,42.4666667,1.4833333
 * }</pre>
 *
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.2
 * @since 7.2
 */
public final class CsvSupport {

	/**
	 * Holds the CSV column <em>separator</em> character.
	 *
	 * @param value the separator character
	 */
	public record Separator(char value) {

		/**
		 * The default separator character, '{@code ,}'.
		 */
		public static final Separator DEFAULT = new Separator(',');

		/**
		 * Creates a new Separator char object.
		 *
		 * @param value the separator character
		 * @throws IllegalArgumentException if the given separator character is
		 *         a line break character
		 */
		public Separator {
			if (isLineBreak(value)) {
				throw new IllegalArgumentException(
					"Given separator char is a line break character."
				);
			}
		}
	}

	/**
	 * Holds the CSV column <em>quote</em> character.
	 *
	 * @param value the quote character
	 */
	public record Quote(char value) {

		/**
		 * The default quote character, '{@code "}'.
		 */
		public static final Quote DEFAULT = new Quote('"');

		/**
		 * The zero '\0' character.
		 */
		public static final Quote ZERO = new Quote('\0');

		/**
		 * Creates a new Quote char object.
		 *
		 * @param value the quote character
		 * @throws IllegalArgumentException if the given quote character is
		 *         a line break character
		 */
		public Quote {
			if (isLineBreak(value)) {
				throw new IllegalArgumentException(
					"Given quote char is a line break character."
				);
			}
		}
	}

	/**
	 * Holds the column indexes, which should be part of the split operation.
	 * If specified, only defined column indexes are part of the split result.
	 *
	 * @param values the column indexes which are part of the split result
	 */
	public record ColumnIndexes(int... values) {

		/**
		 * Indicating that <em>all</em> columns should be part of the split
		 * result.
		 */
		public static final ColumnIndexes ALL = new ColumnIndexes();

		/**
		 * Create a new column indexes object.
		 *
		 * @param values the column indexes
		 */
		public ColumnIndexes {
			values = values.clone();
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(values);
		}

		@Override
		public boolean equals(final Object obj) {
			return obj == this ||
				obj instanceof ColumnIndexes ci &&
				Arrays.equals(values, ci.values);
		}

		@Override
		public String toString() {
			return Arrays.toString(values);
		}
	}

	/**
	 * The newline string used for writing the CSV file: {@code \r\n}.
	 */
	public static final String EOL = "\r\n";


	private CsvSupport() {
	}

	private static boolean isLineBreak(final char c) {
		return c == '\n' || c == '\r';
	}

	/**
	 * Splits the CSV file, given by the {@code reader}, into a  {@code Stream}
	 * of CSV lines. The rows are split at line breaks, as long as they are not
	 * part of a quoted column. For reading the CSV lines, the default quote
	 * character, {@link Quote#DEFAULT}, is used.
	 *
	 * @apiNote
	 * The returned stream must be closed by the caller, which also closes the
	 * CSV {@code reader}.
	 *
	 * @see #readAllLines(Reader)
	 *
	 * @param reader the CSV source reader. The reader is automatically closed
	 *        when the returned line stream is closed.
	 * @return the stream of CSV lines
	 * @throws NullPointerException if the given {@code reader} is {@code null}
	 */
	public static Stream<String> lines(final Reader reader) {
		return LineReader.DEFAULT.read(reader);
	}

	/**
	 * Splits the CSV file, given by the {@code reader}, into a  {@code List}
	 * of CSV lines. The rows are split at line breaks, as long as they are not
	 * part of a quoted column. For reading the CSV lines, the default quote
	 * character, {@link Quote#DEFAULT}, is used.
	 *
	 * @see #lines(Reader)
	 *
	 * @param reader the reader stream to split into CSV lines
	 * @return the list of CSV lines
	 * @throws NullPointerException if the given {@code reader} is {@code null}
	 * @throws IOException if reading the CSV lines fails
	 */
	public static List<String> readAllLines(final Reader reader) throws IOException {
		try (var lines = lines(reader)) {
			return lines.toList();
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	/**
	 * Splits a given CSV {@code line} into columns. The default values for the
	 * separator and quote character are used ({@link Separator#DEFAULT},
	 * {@link Quote#DEFAULT}) for splitting the line.
	 *
	 * @param line the CSV line to split
	 * @return the split CSV lines
	 * @throws NullPointerException if the given {@code line} is {@code null}
	 */
	public static String[] split(final CharSequence line) {
		return new LineSplitter().split(line);
	}

	/**
	 * Joins the given CSV {@code columns} to one CSV line. The default values
	 * for the separator and quote character are used ({@link Separator#DEFAULT},
	 * {@link Quote#DEFAULT}) for joining the columns.
	 *
	 * @see #join(Object[])
	 *
	 * @param columns the CSV columns to join
	 * @return the CSV line, joined from the given {@code columns}
	 * @throws NullPointerException if the given {@code columns} is {@code null}
	 */
	public static String join(final Iterable<?> columns) {
		return ColumnJoiner.DEFAULT.join(columns);
	}

	/**
	 * Joins the given CSV {@code columns} to one CSV line. The default values
	 * for the separator and quote character are used ({@link Separator#DEFAULT},
	 * {@link Quote#DEFAULT}) for joining the columns.
	 *
	 * @see #join(Iterable)
	 *
	 * @param columns the CSV columns to join
	 * @return the CSV line, joined from the given {@code columns}
	 * @throws NullPointerException if the given {@code columns} is {@code null}
	 */
	public static String join(final Object[] columns) {
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
	 * This class reads CSV files and splits it into lines. It takes a quote
	 * character as a parameter, which is needed for not splitting on quoted
	 * line feeds.
	 *
	 * @apiNote
	 * This reader obeys <em>escaped</em> line breaks according
	 * <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>.
	 */
	public static final class LineReader {

		private static final LineReader DEFAULT = new LineReader(Quote.DEFAULT);

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
		 * Create a new line reader with default quote character.
		 */
		public LineReader() {
			this(Quote.DEFAULT);
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
					? resources.use(r, Closeable::close)
					: resources.use(new BufferedReader(reader), Closeable::close);

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
				result.release(UncheckedIOException::new)
			);
		}

		private boolean nextLine(final Reader reader, final StringBuilder line)
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
	 *
	 * @apiNote
	 * A line splitter ist <b>not</b> thread-safe and can't be shared between
	 * different threads.
	 */
	public static final class LineSplitter {

		private final ColumnList columns;
		private final Separator separator;
		private final Quote quote;

		/**
		 * Create a new line splitter with the given parameters.
		 *
		 * @param separator the separator character used by the CSV line to split
		 * @param quote the quote character used by the CSV line to split
		 * @param indexes the column indexes which should be part of the split
		 *        result
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public LineSplitter(
			final Separator separator,
			final Quote quote,
			final ColumnIndexes indexes
		) {
			this.columns = new ColumnList(indexes);
			this.separator = requireNonNull(separator);
			this.quote = requireNonNull(quote);
		}

		/**
		 * Create a new line splitter with the given parameters.
		 *
		 * @param separator the separator character used by the CSV line to split
		 * @param quote the quote character used by the CSV line to split
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public LineSplitter(final Separator separator, final Quote quote) {
			this(separator, quote, ColumnIndexes.ALL);
		}

		/**
		 * Create a new line splitter with the given parameters. The default
		 * quote character, {@link Quote#DEFAULT}, will be used by the created
		 * splitter.
		 *
		 * @param separator the separator character used by the CSV line to split
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public LineSplitter(final Separator separator) {
			this(separator, Quote.DEFAULT, ColumnIndexes.ALL);
		}

		/**
		 * Create a new line splitter with the given parameters. The default
		 * separator character, {@link Separator#DEFAULT}, will be used by the
		 * created splitter.
		 *
		 * @param quote the quote character used by the CSV line to split
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public LineSplitter(final Quote quote) {
			this(Separator.DEFAULT, quote, ColumnIndexes.ALL);
		}

		/**
		 * Create a new line splitter with the given parameters. Only the defined
		 * columns will be part of the split result and the default separator
		 * character, {@link Separator#DEFAULT}, and default quote character,
		 * {@link Quote#DEFAULT}, is used by the created splitter.
		 *
		 * @param indexes the column indexes which should be part of the split
		 *        result
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public LineSplitter(final ColumnIndexes indexes) {
			this(Separator.DEFAULT, Quote.DEFAULT, indexes);
		}

		/**
		 * Create a new line splitter with default values.
		 */
		public LineSplitter() {
			this(Separator.DEFAULT, Quote.DEFAULT, ColumnIndexes.ALL);
		}

		/**
		 * Splitting the given CSV {@code line} into its columns.
		 *
		 * @param line the CSV line to split
		 * @return the split CSV columns
		 * @throws NullPointerException if the CSV {@code line} is {@code null}
		 */
		public String[] split(final CharSequence line) {
			columns.clear();
			final StringBuilder column = new StringBuilder();

			boolean quoted = false;
			boolean escaped = false;
			boolean full = false;

			int quoteIndex = 0;

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
									throw new IllegalArgumentException("""
                                        Only separator character, '%s', allowed \
                                        after quote, but found '%c'.
                                        %s
                                        """.formatted(
											separator.value,
											next,
											toErrorDesc(line, i + 1)
										)
									);
								}

								add(column);
								full = columns.isFull();
								quoted = false;
							}
						}
					} else {
						if (previous != -1 && separator.value != previous) {
							throw new IllegalArgumentException("""
                                Only separator character, '%s', allowed before \
                                quote, but found '%c'.
                                %s
                                """.formatted(
									separator.value,
									previous,
									toErrorDesc(line, Math.max(i - 1, 0))
								)
							);
						}
						quoted = true;
						quoteIndex = i;
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
				throw new IllegalArgumentException("""
					Unbalanced quote character.
					%s
					""".formatted(toErrorDesc(line, quoteIndex))
				);
			}
			if (line.isEmpty() ||
				separator.value == line.charAt(line.length() - 1))
			{
				add(column);
			}

			return columns.toArray();
		}

		private void add(final StringBuilder column) {
			columns.add(column.toString());
			column.setLength(0);
		}

		private boolean isTokenSeparator(final char c) {
			return c == separator.value || c == quote.value;
		}

		private static String toErrorDesc(final CharSequence line, final int pos) {
			return """
                %s
                %s
                """.formatted(
					line.toString().stripTrailing(),
					" ".repeat(pos) + "^"
				);
		}
	}


	/**
	 * Column collection, which is backed up by a string list.
	 */
	private static final class ColumnList {
		private final List<String> columns = new ArrayList<>();
		private final ColumnIndexes indexes;

		private int index = 0;
		private int count = 0;

		ColumnList(final ColumnIndexes indexes) {
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
				while ((pos = indexOf(indexes.values, pos + 1, column)) != -1) {
					for (int i = columns.size(); i <= pos; ++i) {
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

		String[] toArray() {
			for (int i = columns.size(); i < indexes.values.length; ++i) {
				columns.add(null);
			}
			return columns.toArray(String[]::new);
		}

	}

	/**
	 * This class joins an array of columns into one CSV line.
	 */
	public static final class ColumnJoiner {

		private static final ColumnJoiner DEFAULT = new ColumnJoiner(
			Separator.DEFAULT,
			Quote.DEFAULT,
			ColumnIndexes.ALL
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

		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param separator the CSV separator character used by the joiner
		 * @param quote the CSV quote character used by the joiner
		 * @param indexes the column indexes to join
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(
			final Separator separator,
			final Quote quote,
			final ColumnIndexes indexes
		) {
			this.param = new Param(separator.value, quote.value, indexes.values);
			columnCount = Math.max(max(param.indexes) + 1, 0);
		}

		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param separator the CSV separator character used by the joiner
		 * @param quote the CSV quote character used by the joiner
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(final Separator separator, final Quote quote) {
			this(separator, quote, ColumnIndexes.ALL);
		}

		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param separator the CSV separator character used by the joiner
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(final Separator separator) {
			this(separator, Quote.DEFAULT, ColumnIndexes.ALL);
		}

		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param separator the CSV separator character used by the joiner
		 * @param indexes the column indexes to join
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(final Separator separator, final ColumnIndexes indexes) {
			this(separator, Quote.DEFAULT, indexes);
		}


		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param quote the CSV quote character used by the joiner
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(final Quote quote) {
			this(Separator.DEFAULT, quote, ColumnIndexes.ALL);
		}

		/**
		 * Create a new column joiner with the given <em>embedding</em> column
		 * indexes.
		 *
		 * @param indexes the embedding column indexes
		 */
		public ColumnJoiner(final ColumnIndexes indexes) {
			this(Separator.DEFAULT, Quote.DEFAULT, indexes);
		}

		/**
		 * Create a new column joiner with the given parameters.
		 *
		 * @param quote the CSV quote character used by the joiner
		 * @param indexes the column indexes to join
		 * @throws NullPointerException if one of the parameters is {@code null}
		 */
		public ColumnJoiner(final Quote quote, final ColumnIndexes indexes) {
			this(Separator.DEFAULT, quote, indexes);
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

		/**
		 * Joins the given CSV {@code columns}, using the given separator and
		 * quote character.
		 *
		 * @param columns the CSV columns to join
		 * @return the joined CSV columns
		 */
		public String join(final Iterable<?> columns) {
			if (param.indexes.length == 0) {
				return join0(columns);
			} else {
				final var values = new Object[columnCount];
				final var it = columns.iterator();
				int i = 0;
				while (it.hasNext() && i < param.indexes.length) {
					final var col = it.next();
					final var index = param.indexes[i++];
					if (index >= 0) {
						values[index] = col;
					}
				}

				return join0(Arrays.asList(values));
			}
		}

		private String join0(final Iterable<?> cols) {
			final var row = new StringBuilder();
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

		/**
		 * Joins the given CSV {@code columns}, using the given separator and
		 * quote character.
		 *
		 * @param columns the CSV columns to join
		 * @return the joined CSV columns
		 */
		public String join(final Object[] columns) {
			return join(Arrays.asList(columns));
		}

//		/**
//		 * Converts the given {@code record} into a CSV line, by extracting the
//		 * record components.
//		 *
//		 * @param record the record to convert
//		 * @return the CSV line representation of the given {@code record}
//		 */
//		public String toLine(final Record record) {
//			try {
//				final var components = record.getClass().getRecordComponents();
//				final var elements = new Object[components.length];
//				for (int i = 0; i < elements.length; ++i) {
//					elements[i] = components[i].getAccessor().invoke(record);
//				}
//
//				return join(elements);
//			} catch (ReflectiveOperationException e) {
//				throw new IllegalArgumentException(e);
//			}
//		}

	}

}

