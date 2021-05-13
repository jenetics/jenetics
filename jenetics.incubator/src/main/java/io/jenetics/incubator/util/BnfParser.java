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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class BnfParser {
	private BnfParser() {}


	static record Token(String value, int start, int end, int kind) {
		static final int ASSIGNMENT = 0;
		static final int OR = 1;
		static final int TERMINAL = 2;
		static final int NON_TERMINAL = 3;
	}

	static List<Token> tokenize(final CharSequence value) {
		final var tokens = new ArrayList<Token>();

		for (int i = 0, n = value.length(); i < n;) {
			final Token token = switch (value.charAt(i)) {
				case '"' -> readQuoted(value, i);
				case '|' -> readOr(value, i);
				case ':' -> readAssignment(value, i);
				case '<' ->  readNonTerminal(value, i);
				default -> readTerminal(value, i);
			};

			tokens.add(token);
			i = token.end();
		}

		return tokens;
	}

	private static Token readQuoted(final CharSequence value, final int start) {
		assert value.charAt(start) == '"';

		final var token = new StringBuilder();
		boolean quoted = false;
		boolean escaped = false;

		for (int i = start; i < value.length(); ++i) {
			final char current = value.charAt(i);
			final char next = i + 1 < value.length() ? value.charAt(i + 1) : '\0';

			if (current == '"') {
				if (quoted) {
					if (!escaped && next == '"') {
						escaped = true;
					} else {
						if (escaped) {
							token.append(current);
							escaped = false;
						} else {
							token.append(current);
							return new Token(
								token.toString(),
								start, i + 1,
								Token.TERMINAL
							);
						}
					}
				} else {
					token.append(current);
					quoted = true;
				}
			} else {
				token.append(current);
			}
		}

		throw new IllegalArgumentException("Unbalanced quote character.");
	}

	private String errorMessage(final CharSequence value, final int index) {
		return "";
	}

	private static Token readAssignment(final CharSequence value, final int start) {
		final var token = new StringBuilder();

		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (!isAssignmentChar(c)) {
				throw new IllegalArgumentException(format(
					"Unexpected assignment character at position %d: '%s'",
					i, c
				));
			}

			token.append(c);

			if (token.length() == 3) {
				if (i + 1 < value.length() && isAssignmentChar(value.charAt(i + 1))) {
					throw new IllegalArgumentException(format(
						"Unexpected character at position %d: '%s'",
						i + 1, c
					));
				}

				return new Token(token.toString(), start, i + 1, Token.ASSIGNMENT);
			}
		}

		throw new IllegalArgumentException("Incomplete assignment token: " + token);
	}

	private static boolean isAssignmentChar(final char c) {
		return c == ':' || c == '=';
	}

	private static Token readOr(final CharSequence value, final int start) {
		final char c = value.charAt(start);

		if (c != '|') {
			throw new IllegalArgumentException(format(
				"Unexpected 'or' character at position %d: '%s'",
				start, c
			));
		}
		if (start + 1 < value.length() && isAssignmentChar(value.charAt(start + 1))) {
			throw new IllegalArgumentException(format(
				"Unexpected character at position %d: '%s'",
				start + 1, value.charAt(start + 1)
			));
		}

		return new Token("|", start, start + 1, Token.OR);
	}

	private static Token readNonTerminal(final CharSequence value, final int start) {
		if (value.charAt(start) != '<') {
			throw new IllegalArgumentException(format(
				"Non-terminals must start with a '<' character, but got '%s' at position %d.",
				value.charAt(start), start
			));
		}

		final var token = new StringBuilder();
		token.append(value.charAt(start));

		for (int i = start + 1; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (!Character.isJavaIdentifierPart(c) && c != '>') {
				throw new IllegalArgumentException(format(
					"Illegal character for non-terminal name at position %d: '%s'.",
					i, c
				));
			}

			if (c == '>') {
				token.append(c);
				return new Token(token.toString(), start, i + 1, Token.NON_TERMINAL);
			} else {
				token.append(c);
			}
		}

		throw new IllegalArgumentException(
			"Non-terminals must terminated with a '>' character."
		);
	}

	private static Token readTerminal(final CharSequence value, final int start) {
		final var token = new StringBuilder();

		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (!isTokenSeparator(c)) {
				token.append(c);
			} else {
				return new Token(token.toString(), start, i, Token.TERMINAL);
			}
		}

		return new Token(token.toString(), start, value.length(), Token.TERMINAL);
	}

	private static boolean isTokenSeparator(final char c) {
		return c == ':' || c == '=' || c == '<' || c == '>' || c == '|' || c == '"';
	}

	private static boolean isTokenSeparator1(final char c) {
		return c == ':' || c == '=' || c == '<' || c == '>' || c == '|';
	}

	static Grammar parse(final CharSequence value) {

		return null;
	}

}
