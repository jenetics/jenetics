package io.jenetics.incubator.util;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
	 * Splits a given CSV row into it's columns. It supports CSV records defined
	 * in <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>.
	 *
	 * @see <a href="https://tools.ietf.org/html/rfc4180">RFC-4180</a>
	 * @see #join(Iterable)
	 *
	 * @param row the CSV {@code row} to split
	 * @return the split columns of the given CSV {@code row}
	 * @throws IllegalArgumentException if the given {@code roes} isn't a valid
	 *         CSV row
	 * @throws NullPointerException if the given {@code row} is {@code null}
	 */
	public static List<String> split(final CharSequence row) {
		final List<String> columns = new ArrayList<>();

		boolean quoted = false;
		boolean escaped = false;

		final var col = new StringBuilder();

		final var tokens = tokenize(row);

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
	 * @param columns the CSV columns to join
	 * @return a new CSV row, joined from the given {@code columns}
	 */
	public static String join(final Iterable<?> columns) {
		final var row = new StringBuilder(64);

		final var it = columns.iterator();
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
	 *     .collect(CSV.join());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV rows into one CSV string
	 */
	public static Collector<CharSequence, ?, String> join() {
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
	 *     .collect(CSV.toCSV());
	 * }</pre>
	 *
	 * @return a collector for joining a list of CSV columns into one CSV string
	 */
	public static Collector<Iterable<?>, ?, String> toCSV() {
		return Collector.of(
			ArrayList<String>::new,
			(list, row) -> list.add(join(row)),
			(a, b) -> { a.addAll(b); return a; },
			list -> list.stream().collect(join())
		);
	}

}
