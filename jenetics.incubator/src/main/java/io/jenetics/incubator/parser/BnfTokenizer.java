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

import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ASSIGN;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.BAR;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.GT;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.ID;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.LT;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.QUOTED_STRING;
import static io.jenetics.incubator.parser.BnfTokenizer.BnfTokenType.STRING;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;

/**
 * https://github.com/antlr/grammars-v4/blob/master/bnf/bnf.g4
 *
 * ASSIGN: '::=';
 * BAR: '|';
 * GT: '>';
 * LT: '<';
 * STRING: ( '%s' | '%i' )? '"' ( ~ '"' )* '"';
 * ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|' ')+;
 * WS: [ \r\n\t] -> skip;
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since !__version__!
 * @version !__version__!
 */
final class BnfTokenizer extends CharSequenceTokenizer {

	enum BnfTokenType implements Token.Type {
		ASSIGN(1),
		BAR(2),
		GT(3),
		LT(4),
		QUOTED_STRING(5),
		STRING(6),
		ID(17);

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
			final char value = c;
			switch (value) {
				case ' ', '\r', '\n', '\t':
					WS();
					continue;
				case ':':
					return ASSIGN();
				case '|':
					consume();
					return BAR.token(value);
				case '>':
					consume();
					return GT.token(value);
				case '<':
					consume();
					return LT.token(value);
				case '\'':
					return QUOTED_STRING();
				default:
					if (isJavaIdentifierStart(c)) {
						return ID();
					} else if (!isWhitespace(c)) {
						return STRING();
					} else {
						throw new TokenizerException(format(
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
		return ASSIGN.token("::=");
	}

	private Token QUOTED_STRING() {
		final var value = new StringBuilder();

		consume();
		while (isNonEof(c) && c != '\'') {
			value.append(c);
			consume();
		}
		consume();

		return QUOTED_STRING.token(value.toString());
	}

	private Token ID() {
		final var value = new StringBuilder();

		while (isNonEof(c) && isJavaIdentifierPart(c)) {
			value.append(c);
			consume();
		}

		return ID.token(value.toString());
	}

	private Token STRING() {
		final var value = new StringBuilder();

		while (isNonEof(c) && isStringChar(c)) {
			value.append(c);
			consume();
		}

		return STRING.token(value.toString());
	}

	private static boolean isStringChar(final char c) {
		return !isWhitespace(c) && c != '<' && c != '>' && c != '|' && c != ':';
	}

}
