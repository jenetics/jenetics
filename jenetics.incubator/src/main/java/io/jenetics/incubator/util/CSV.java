package io.jenetics.incubator.util;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import io.jenetics.incubator.util.Lifecycle.ExtendedCloseable;

/**
 * Helper methods for splitting CSV rows and merging CSV columns into a valid
 * CSV row.
 *
 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class CSV {

	private static final String LF = "\r\n";

	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';

	private static final String SEPARATOR_STR = ",";
	private static final String QUOTE_STR = "\"";
	private static final String DOUBLE_QUOTE_STR = "\"\"";

	private CSV() {
	}

	/**
	 * Splits the given {@code input} stream into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller.</em>
	 *
	 * @param input the input stream to split into CSV lines
	 * @param cs the charset to use for decoding
	 * @return the stream of CSV lines
	 */
	public static Stream<String> lines(final InputStream input, final Charset cs) {
		final var result = CloseableValue.build(resources -> {
			final var isr = resources.add(new InputStreamReader(input, cs));
			final var reader = resources.add(new BufferedReader(isr));

			final Supplier<String> nextLine = () -> {
				final var line = new StringBuilder();
				try {
					if (nextLine(reader, line)) {
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

	/**
	 * Splits the given {@code input} stream into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller.</em>
	 *
	 * @param input the input stream to split into CSV lines
	 * @return the stream of CSV lines
	 */
	public static Stream<String> lines(final InputStream input) {
		return lines(input, Charset.defaultCharset());
	}

	/**
	 * Splits the given {@code input} stream into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller.</em>
	 *
	 * @param path the CSV file to split
	 * @param cs the charset to use for decoding
	 * @return the stream of CSV lines
	 * @throws IOException if an I/O error occurs
	 */
	public static Stream<String> lines(final Path path, final Charset cs)
		throws IOException
	{
		final var result = CloseableValue.build(resources -> {
			final var fin = resources.add(Files.newInputStream(path));
			return lines(fin, cs);
		});

		return result.get().onClose(result::silentClose);
	}

	/**
	 * Splits the given {@code input} stream into a  {@code Stream} of CSV rows.
	 * The rows are split at line breaks, as long as they are not part of a
	 * quoted column. <em>The returned stream must be closed by the caller.</em>
	 *
	 * @param path the CSV file to split
	 * @return the stream of CSV lines
	 * @throws IOException if an I/O error occurs
	 */
	public static Stream<String> lines(final Path path) throws IOException {
		return lines(path, Charset.defaultCharset());
	}

	/**
	 * Reads all CSV lines form the given {@code input} stream.
	 *
	 * @param input the CSV {@code input} stream
	 * @param cs the charset to use for decoding
	 * @return all CSV lines form the given {@code input} stream
	 * @throws IOException if an error occurs while reading the CSV lines
	 */
	public static List<String> readAllLines(
		final InputStream input,
		final Charset cs
	)
		throws IOException
	{
		try (var stream = lines(input, cs)) {
			return stream.collect(Collectors.toList());
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	/**
	 * Reads all CSV lines form the given {@code input} stream.
	 *
	 * @param input the CSV {@code input} stream
	 * @return all CSV lines form the given {@code input} stream
	 * @throws IOException if an error occurs while reading the CSV lines
	 */
	public static List<String> readAllLines(final InputStream input)
		throws IOException
	{
		return readAllLines(input, Charset.defaultCharset());
	}

	/**
	 * Reads all CSV lines form the given {@code input} stream.
	 *
	 * @param path the CSV file to read
	 * @param cs the charset to use for decoding
	 * @return all CSV lines form the given {@code input} stream
	 * @throws IOException if an error occurs while reading the CSV lines
	 */
	public static List<String> readAllLines(final Path path, final Charset cs)
		throws IOException
	{
		try (var stream = lines(path, cs)) {
			return stream.collect(Collectors.toList());
		} catch (UncheckedIOException e) {
			throw e.getCause();
		}
	}

	/**
	 * Reads all CSV lines form the given {@code input} stream.
	 *
	 * @param path the CSV file to read
	 * @return all CSV lines form the given {@code input} stream
	 * @throws IOException if an error occurs while reading the CSV lines
	 */
	public static List<String> readAllLines(final Path path) throws IOException {
		return readAllLines(path, Charset.defaultCharset());
	}

	/**
	 * Splits a given CSV row into it's columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>.
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
	 * @see #joinCols(Iterable)
	 *
	 * @param line the CSV {@code row} to split
	 * @return the split columns of the given CSV {@code row}
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static List<String> splitLine(final CharSequence line) {
		final List<String> columns = new ArrayList<>();

		boolean quoted = false;
		boolean escaped = false;

		final var col = new StringBuilder();

		final var tokens = tokenize(line);

		for (int i = 0; i < tokens.size(); ++i) {
			final var previous = i > 0 ? tokens.get(i - 1) : null;
			final var current = tokens.get(i);
			final var next = i + 1 < tokens.size() ? tokens.get(i + 1) : null;

			switch (current) {
				case QUOTE_STR:
					if (quoted) {
						if (QUOTE_STR.equals(next) && !escaped) {
							escaped = true;
						} else {
							if (escaped) {
								col.append(QUOTE);
								escaped = false;
							} else {
								if (next != null && !SEPARATOR_STR.equals(next)) {
									throw new IllegalArgumentException(format(
										"No other token than '%s' allowed after " +
											"quote, but found '%s'.",
										SEPARATOR_STR, next
									));
								}

								columns.add(col.toString());
								col.setLength(0);
								quoted = false;
							}
						}
					} else {
						if (previous != null && !SEPARATOR_STR.equals(previous)) {
							throw new IllegalArgumentException(format(
								"No other token than '%s' allowed before " +
									"quote, but found '%s'.",
								SEPARATOR_STR, previous
							));
						}
						quoted = true;
					}
					break;
				case SEPARATOR_STR:
					if (quoted) {
						col.append(current);
					} else if (SEPARATOR_STR.equals(previous) || previous == null) {
						columns.add(col.toString());
						col.setLength(0);
					}
					break;
				default:
					col.append(current);
					if (!quoted) {
						columns.add(col.toString());
						col.setLength(0);
					}
					break;
			}
		}

		if (quoted) {
			throw new IllegalArgumentException("Unbalanced quote character.");
		}
		if (tokens.isEmpty() ||
			SEPARATOR_STR.equals(tokens.get(tokens.size() - 1)))
		{
			columns.add("");
		}

		return columns;
	}

	private static List<String> tokenize(final CharSequence value) {
		final List<String> tokens = new ArrayList<>();
		int pos = 0;
		final StringBuilder token = new StringBuilder(64);
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (isTokenSeparator(c)) {
				if (token.length() > 0) {
					tokens.add(token.toString());
				}
				tokens.add(Character.toString(c));
				token.setLength(0);
			} else {
				token.append(c);
			}
		}
		if (token.length() > 0) {
			tokens.add(token.toString());
		}
		return tokens;
	}

	private static boolean isTokenSeparator(final char c) {
		return c == SEPARATOR || c == QUOTE;
	}

	/**
	 * Joins the given columns to a CSV row string.
	 *
	 * @param cols the CSV columns to join
	 * @return a new CSV row, joined from the given columns
	 */
	public static String joinCols(final Iterable<?> cols) {
		final var row = new StringBuilder(64);

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
			var valueString = value.toString();
			var string = valueString.replace(QUOTE_STR, DOUBLE_QUOTE_STR);

			if (valueString.length() != string.length() ||
				string.contains(SEPARATOR_STR) ||
				string.contains("\n"))
			{
				return QUOTE_STR + string + QUOTE_STR;
			} else {
				return valueString;
			}
		}
	}

	/**
	 * Return a collector for joining a list of CSV rows into one CSV string.
	 *
	 * <pre>{@code
	 * final List<List<String>> values = Stream.generate(() -> nextRow())
	 *     .limit(200)
	 *     .collect(Collectors.toList());
	 *
	 * final String csv = values.stream()
	 *     .map(CSV::join)
	 *     .collect(CSV.linesToCSV());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV rows into one CSV string
	 */
	public static Collector<CharSequence, ?, String> linesToCSV() {
		return Collectors.joining(LF, "", LF);
	}

	/**
	 * Return a collector for joining a list of CSV columns into one CSV string.
	 *
	 * <pre>{@code
	 * final List<List<String>> values = Stream.generate(() -> nextRow())
	 *     .limit(200)
	 *     .collect(Collectors.toList());
	 *
	 * final String csv = values.stream()
	 *     .collect(CSV.rowsToCSV());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV columns into one CSV string
	 */
	public static Collector<Iterable<?>, ?, String> rowsToCSV() {
		return Collector.of(
			ArrayList<String>::new,
			(list, row) -> list.add(joinCols(row)),
			(a, b) -> { a.addAll(b); return a; },
			list -> list.stream().collect(linesToCSV())
		);
	}

}
