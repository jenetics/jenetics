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

import io.jenetics.incubator.parser.Token;

/**
 * Token types as they are used in <em>mathematical</em> (arithmetic) expressions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 7.0
 * @since 7.0
 */
public enum MathTokenType implements Token.Type {
	LPAREN(1),
	RPAREN(2),
	COMMA(3),

	PLUS(4),
	MINUS(5),
	TIMES(6),
	DIV(7),
	MOD(8),
	POW(9),

	NUMBER(10),
	IDENTIFIER(11),

	UNARY_OPERATOR(12),
	BINARY_OPERATOR(13),
	FUN(14),
	ATOM(15);

	private final int _code;

	MathTokenType(final int code) {
		_code = code;
	}

	@Override
	public int code() {
		return _code;
	}

}
