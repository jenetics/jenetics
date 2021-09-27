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
package io.jenetics.incubator.parser;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ASSIGN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.BAR;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.GT;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ID;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LBRACE;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LEND;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LPAREN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LT;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.QUOTED_STRING;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.RBRACE;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.REND;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.RPAREN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.STRING;

final class BnfTokenizer extends Tokenizer {

	enum BnfTokenType implements Token.Type {
		ASSIGN(2),
		LPAREN(3),
		RPAREN(4),
		LBRACE(5),
		RBRACE(6),
		LEND(7),
		REND(8),
		BAR(9),
		GT(10),
		LT(11),
		QUOTED_STRING(12),
		STRING(13),
		ID(14);

		private final int _code;

		BnfTokenType(final int code) {
			_code = code;
		}

		@Override
		public int code() {
			return _code;
		}
	}

	BnfTokenizer(final CharSequence input) {
		super(input);
	}

	@Override
	public Token next() {
		while (isNonEof(c)) {
			switch (c) {
				case ' ', '\r', '\n', '\t':
					WS();
					continue;
				case ':':
					return ASSIGN();
				case ')':
					consume();
					return Token.of(LPAREN, ")");
				case '(':
					consume();
					return Token.of(RPAREN, "(");
				case '}':
					consume();
					return Token.of(LBRACE, "}");
				case '{':
					consume();
					return Token.of(RBRACE, "{");
				case ']':
					consume();
					return Token.of(LEND, "]");
				case '[':
					consume();
					return Token.of(REND, "[");
				case '|':
					consume();
					return Token.of(BAR, "|");
				case '>':
					consume();
					return Token.of(GT, ">");
				case '<':
					consume();
					return Token.of(LT, "<");
				case '\'':
					return QUOTED_STRING();
				default:
					if (isJavaIdentifierStart(c)) {
						return ID();
					} else if (!isWhitespace(c)) {
						return STRING();
					} else {
						throw new IllegalArgumentException(format(
							"Got invalid character '%s' at position '%d'.",
							c, pos
						));
					}
			}
		}

		return Token.EOF;
	}

	private void WS() {
		do {
			consume();
		} while (isNonEof(c) && isWhitespace(c));
	}

	private Token ASSIGN() {
		match(':');
		match(':');
		match('=');
		return Token.of(ASSIGN, "::=");
	}

	private Token QUOTED_STRING() {
		final var value = new StringBuilder();

		consume();
		while (isNonEof(c) && c != '\'') {
			value.append(c);
			consume();
		}
		consume();

		return Token.of(QUOTED_STRING, value.toString());
	}

	private Token ID() {
		final var value = new StringBuilder();

		while (isNonEof(c) && isJavaIdentifierPart(c)) {
			value.append(c);
			consume();
		}

		return Token.of(ID, value.toString());
	}

	private Token STRING() {
		final var value = new StringBuilder();

		while (isNonEof(c) && !isWhitespace(c)) {
			value.append(c);
			consume();
		}

		return Token.of(STRING, value.toString());
	}

}
