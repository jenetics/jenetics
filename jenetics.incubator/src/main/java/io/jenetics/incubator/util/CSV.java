package io.jenetics.incubator.util;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

public class CSV {
	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';

	private static final String QUOTE_STR = "\"";
	private static final String SEPARATOR_STR = ",";

	/**
	 * Tokenize the given CSV row.
	 *
	 * @param value the CSV row
	 * @return the CSV row tokens
	 * @throws NullPointerException if the given {@code value} is {@code null}
	 */
	static List<String> tokenize(final String value) {
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

	public static List<String> split(final String row) {
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
		if (tokens.isEmpty() || ",".equals(tokens.get(tokens.size() - 1))) {
			columns.add("");
		}

		return columns;
	}

}
