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
package io.jenetics.incubator.grammar_old;

import static java.lang.String.format;
import static java.util.function.Predicate.not;

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
				case '"' -> {
					yield quotedTerminal(value, i);
				}
				case '|' -> alternative(value, i);
				case ':' -> assignment(value, i);
				case '<' ->  nonTerminal(value, i);
				default -> terminal(value, i);
			};

			tokens.add(token);
			i = token.end();
		}

		return tokens;
	}

	private static Token quotedTerminal(final CharSequence value, final int start) {
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
						token.append(current);
						if (escaped) {
							escaped = false;
						} else {
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

	static String errorLine(final CharSequence value, final int index) {
		final var line = new StringBuilder();

		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			line.append(c);

			if (c == '\n' || c == '\r') {
				if (i >= index) {
					break;
				}
				line.setLength(0);
			}
		}

		return line.toString();
	}

	private static Token assignment(final CharSequence value, final int start) {
		final var token = new StringBuilder();

		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (!isAssignmentChar(c)) {
				throw new IllegalArgumentException(format(
					"Unexpected character for assignment symbol at position %d: '%s'",
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

	private static Token alternative(final CharSequence value, final int start) {
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

	private static Token nonTerminal(final CharSequence value, final int start) {
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

	private static Token terminal(final CharSequence value, final int start) {
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
		return c == ':' || c == '=' || c == '<' ||
			c == '>' || c == '|' || c == '"';
	}

	static Grammar parse(final CharSequence bnf) {
		final List<Token> tokens = tokenize(bnf).stream()
			.filter(not(BnfParser::isWhitespace))
			.toList();

		final var rules = new ArrayList<Grammar.Rule>();
		final var symbols = new ArrayList<Grammar.Symbol>();
		final var alternatives = new ArrayList<Grammar.Expression>();

		Grammar.NonTerminal start = null;

		for (int i = 0; i < tokens.size(); ++i) {
			final var token = tokens.get(i);

			switch (token.kind) {
				case Token.NON_TERMINAL:
					if (isStart(tokens, i)) {
						if (start != null) {
							if (!symbols.isEmpty()) {
								alternatives.add(new Grammar.Expression(symbols));
								symbols.clear();

								rules.add(new Grammar.Rule(start, alternatives));
								alternatives.clear();
							}
						}

						var value = token.value().trim();
						final var quoted = value.startsWith("<") && value.endsWith(">");
						if (quoted) {
							value = value.substring(1, value.length() - 1);
						}
						start = new Grammar.NonTerminal(value);
					} else {
						var value = token.value().trim();
						final var quoted = value.startsWith("<") && value.endsWith(">");
						if (quoted) {
							value = value.substring(1, value.length() - 1);
						}
						symbols.add(new Grammar.NonTerminal(value));
					}
					break;
				case Token.ASSIGNMENT:
					if (i - 1 >= 0 && tokens.get(i - 1).kind() != Token.NON_TERMINAL) {
						throw new IllegalArgumentException(format(
							"Expected non-terminal symbol, but got '%s'.",
							tokens.get(i - 1).value().trim()
						));
					}
					break;
				case Token.OR:
					if (!symbols.isEmpty()) {
						alternatives.add(new Grammar.Expression(symbols));
						symbols.clear();
					}
					break;
				case Token.TERMINAL:
					var value = token.value().trim();
					final var quoted = value.startsWith("\"") && value.endsWith("\"");
					if (quoted) {
						value = value.substring(1, value.length() - 1);
					}

					symbols.add(new Grammar.Terminal(value));
					break;
			}
		}

		if (start != null) {
			if (!symbols.isEmpty()) {
				alternatives.add(new Grammar.Expression(symbols));
				symbols.clear();

				rules.add(new Grammar.Rule(start, alternatives));
				alternatives.clear();
			}
		}

		return new Grammar(rules);
	}

	private static boolean isWhitespace(final Token token) {
		return token.kind() == Token.TERMINAL && token.value().trim().isEmpty();
	}

	private static boolean isStart(final List<Token> tokens, final int index) {
		return tokens.get(index).kind() == Token.NON_TERMINAL &&
			index + 1 < tokens.size() &&
			tokens.get(index + 1).kind() == Token.ASSIGNMENT;
	}

	static String escape(final Object value) {
		if (value == null) {
			return "";
		} else {
			var stringValue = value.toString();
			var string = stringValue.replace("\"", "\"\"");

			if (stringValue.length() != string.length() || mustEscape(string)) {
				return "\"" + string + "\"";
			} else {
				return stringValue;
			}
		}
	}

	private static boolean mustEscape(final CharSequence value) {
		for (int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (isTokenSeparator(c)) {
				return true;
			}
		}
		return false;
	}

}
