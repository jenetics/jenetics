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
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.String.format;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.COMMA;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.DIV;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.ID;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.LPAREN;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.MINUS;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.NUMBER;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.PLUS;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.POW;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.RPAREN;
import static io.jenetics.incubator.mathexpr.MathExprTokenizer.MathTokenType.TIMES;

import io.jenetics.incubator.parser.CharSequenceTokenizer;
import io.jenetics.incubator.parser.ParsingException;
import io.jenetics.incubator.parser.Token;

/**
 * Tokenizer for simple arithmetic expression.
 *
 * <pre>{@code
 * LPAREN: '(';
 * RPAREN: ')';
 * COMMA: ',';
 * PLUS: '+';
 * MINUS: '-';
 * TIMES: '*';
 * DIV: '/';
 * POW: '^';
 * NUMBER: ('0'..'9')+ ('.' ('0'..'9')+)? ((e|E) (+|-)? ('0'..'9'))?
 * ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')+;
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
		COMMA(3),
		PLUS(4),
		MINUS(5),
		TIMES(6),
		DIV(7),
		POW(8),
		NUMBER(9),
		ID(10);

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
				case '(':
					consume();
					return LPAREN.token(value);
				case ')':
					consume();
					return RPAREN.token(value);
				case ',':
					consume();
					return COMMA.token(value);
				case '+':
					consume();
					return PLUS.token(value);
				case '-':
					consume();
					return MINUS.token(value);
				case '*':
					if (LA(2) == '*') {
						consume();
						consume();
						return POW.token("**");
					} else {
						consume();
						return TIMES.token(value);
					}
				case '/':
					consume();
					return DIV.token(value);
				case '^':
					consume();
					return POW.token(value);
				case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
					return NUMBER();
				default:
					if (isJavaIdentifierStart(c)) {
						return ID();
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

	// NUMBER (E SIGN? UNSIGNED_INTEGER)?
	private Token NUMBER() {
		final var value = new StringBuilder();

		SIGNED_NUMBER(value);
		if ('e' == c || 'E' == c) {
			value.append(c);
			consume();

			if ('+' == c || '-' == c) {
				value.append(c);
				consume();
			}
			if (isDigit(c)) {
				UNSIGNED_NUMBER(value);
			}
		}

		return NUMBER.token(value.toString());
	}

	// ('0' .. '9') + ('.' ('0' .. '9') +)?
	private void SIGNED_NUMBER(final StringBuilder value) {
		UNSIGNED_NUMBER(value);
		if ('.' == c) {
			value.append(c);
			consume();
			UNSIGNED_NUMBER(value);
		}
	}

	// ('0' .. '9')+
	private void UNSIGNED_NUMBER(final StringBuilder value) {
		while (isDigit(c)) {
			value.append(c);
			consume();
		}
	}

	private Token ID() {
		final var value = new StringBuilder();

		do {
			value.append(c);
			consume();
		} while (isJavaIdentifierPart(c));

		return ID.token(value.toString());
	}

}
