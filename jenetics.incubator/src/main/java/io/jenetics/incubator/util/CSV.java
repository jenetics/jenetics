package io.jenetics.incubator.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CSV {
	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';

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
		final StringBuilder token = new StringBuilder();
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
		final List<String> result = new ArrayList<>();

		boolean quoted = false;
		boolean escaped = false;

		final var col = new StringBuilder();

		final var tokens = tokenize(row);
		for (int i = 0; i < tokens.size(); ++i) {
			final var token = tokens.get(i);
			switch (token) {
				case "\"":
					if (quoted) {
						if (i + 1 < tokens.size() && "\"".equals(tokens.get(i + 1)) && !escaped) {
							escaped = true;
						} else {
							if (escaped) {
								col.append("\"");
								escaped = false;
							} else {
								result.add(col.toString());
								col.setLength(0);
								quoted = false;
							}
						}
					} else {
						if (i - 1 >= 0 && !",".equals(tokens.get(i - 1))) {
							throw new IllegalArgumentException(
								"No other token than ',' allowed before or after quote."
							);
						}
						quoted = true;
					}
					break;
				case ",":
					if (quoted) {
						col.append(token);
					} else if (i > 0 && ",".equals(tokens.get(i - 1))) {
						result.add(col.toString());
						col.setLength(0);
					} else if (i == 0) {
						result.add(col.toString());
						col.setLength(0);
					}
					break;
				default:
					col.append(token);
					if (!quoted) {
						result.add(col.toString());
						col.setLength(0);
					}
					break;
			}
		}

		if (tokens.isEmpty() || ",".equals(tokens.get(tokens.size() - 1))) {
			result.add("");
		}
		return result;
	}

}
