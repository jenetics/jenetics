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
import java.util.Spliterator;

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

	private static final char SEPARATOR = ',';
	private static final char QUOTE = '"';

	private static final String SEPARATOR_STR = ",";
	private static final String QUOTE_STR = "\"";
	private static final String DOUBLE_QUOTE_STR = "\"\"";

	private static final  int QUOTED = 1;
	private static final int ESCAPED = 2;

	static List<Token> tokenize(final CharSequence value) {
		final var tokens = new ArrayList<Token>();
		final var token = new StringBuilder(32);

		boolean quoted = false;
		boolean escaped = false;

		int state = 0;


		final char[] buffer = new char[3];

		for (int i = 0, n = value.length(); i < n; ++i) {
			buffer[2] = buffer[1];       // previous
			buffer[1] = buffer[0];       // current
			buffer[0] = value.charAt(i); // next

			Token t = null;
			switch (buffer[0]) {
				case '"':
					t = readQuoted(value, i);
					tokens.add(t);
					i = t.end();
					break;
				case '|':
					t = readOr(value, i);
					tokens.add(t);
					i = t.end();
					break;
				case ':':
					t = readAssignment(value, i);
					tokens.add(t);
					i = t.end();
					break;
				case '<':
					t = readNonTerminal(value, i);
					tokens.add(t);
					i = t.end();
					break;
				default:
					t = readTerminal(value, i);
					tokens.add(t);
					i = t.end();
					break;
			}
		}

		return tokens;
	}


	private static Token readAssignment(final CharSequence value, final int start) {
		if (value.charAt(start) != ':') {
			throw new IllegalArgumentException("" + value.charAt(start));
		}

		final var token = new StringBuilder();

		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (token.length() == 3 && (c == ':' || c == '=')) {
				throw new IllegalArgumentException("" + c);
			}

			if (c != ':' && c != '=') {
				throw new IllegalArgumentException("" + c + "__" + token.toString().length() + "_" + start);
			}

			if (token.length() < 3) {
				token.append(c);
			}

			if (token.length() == 3) {
				return new Token(token.toString(), start, i, Token.ASSIGNMENT);
			}
		}

		throw new IllegalArgumentException();
	}

	private static Token readOr(final CharSequence value, final int start) {
		if (value.charAt(start) != '|') {
			throw new IllegalArgumentException("" + value.charAt(start));
		}

		final var token = new StringBuilder();

		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (token.length() == 1 && c == '|') {
				throw new IllegalArgumentException("" + c);
			}
			if (token.length() == 1) {
				return new Token(token.toString(), start, i - 1, Token.ASSIGNMENT);
			}

			if (token.length() == 0) {
				token.append(c);
			}

		}

		throw new IllegalArgumentException();
	}

	private static Token readNonTerminal(final CharSequence value, final int start) {
		if (value.charAt(start) != '<') {
			throw new IllegalArgumentException("" + value.charAt(start));
		}

		final var token = new StringBuilder();

		for (int i = start + 1; i < value.length(); ++i) {
			final char c = value.charAt(i);
			if (c == '|' || c == ':' || c == '=') {
				throw new IllegalArgumentException("" + c);
			}
			if (c == '>') {
				return new Token(token.toString(), start, i, Token.NON_TERMINAL);
			} else {
				token.append(c);
			}
		}

		throw new IllegalArgumentException();
	}

	private static Token readQuoted(final CharSequence value, final int start) {
		if (value.charAt(start) != '"') {
			throw new IllegalArgumentException("" + value.charAt(start));
		}

		final var token = new StringBuilder();
		for (int i = start + 1; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (c != '"') {
				token.append(c);
			} else { // c == '"'
				if (i + 1 < value.length()) {
					if (value.charAt(i + 1) == '"') {
						token.append(value.charAt(i + 1));
						++i;
					} else {
						return new Token(token.toString(), start, i, Token.TERMINAL);
					}
				} else {
					return new Token(token.toString(), start, i, Token.TERMINAL);
				}
			}

		}

		throw new IllegalArgumentException();
	}

	private static Token readTerminal(final CharSequence value, final int start) {

		final var token = new StringBuilder();
		for (int i = start; i < value.length(); ++i) {
			final char c = value.charAt(i);

			if (c != '"' && c != '<' && c != '>' && c != '|') {
				token.append(c);
			} else { // c == '"'
				return new Token(token.toString(), start, i, Token.TERMINAL);
			}

		}

		return new Token(token.toString(), start, value.length(), Token.TERMINAL);
	}

	static Grammar parse(final CharSequence value) {

		return null;
	}

}
