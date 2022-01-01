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
package io.jenetics.incubator.mathexpr;

import static java.lang.Character.isDigit;
import static java.lang.String.format;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.ID;

import io.jenetics.incubator.parser.CharSequenceTokenizer;
import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;

/**
 * Tokenizer for simple arithmetic expression.
 *
 * <pre>{@code
 * LPAREN: '(';
 * RPAREN: ')';
 * PLUS: '+';
 * MINUS: '-';
 * TIMES: '*';
 * DIV: '/';
 * POINT: '.';
 * POW: '^';
 * ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'-')+;
 * WS: [ \r\n\t] + -> skip;
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @since 7.0
 * @version 7.0
 */
public final class MathExprTokenizer extends CharSequenceTokenizer<Token> {

	enum MathTokenType implements Token.Type {
		LPAREN(1),
		RPAREN(2),
		PLUS(3),
		MINUS(4),
		TIMES(5),
		DIV(6),
		POINT(7),
		POW(8),
		ID(9);

		private final int _code;

		MathTokenType(final int code) {
			_code = code;
		}

		@Override
		public int code() {
			return _code;
		}
	}

	public MathExprTokenizer(final CharSequence input) {
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
				case '+':
					if (isDigit(LA(2))) {

					}
				case '-':
					if (isDigit(LA(2))) {

					}
				default:
					if (isAlphabetic(c)) {
						return ID();
					} else if (isDigit(c)) {
					} else {
						throw new ParsingException(format(
							"Got invalid character '%s' at position '%d'.",
							c, pos
						));
					}
			}
		}

		return Token.EOF;
	}

	private Token ID() {
		final var value = new StringBuilder();

		while (isIdChar(c)) {
			value.append(c);
			consume();
		}

		return ID.token(value.toString());
	}

	static boolean isIdChar(final char c) {
		return isAlphabetic(c) || isDigit(c);
	}

}
