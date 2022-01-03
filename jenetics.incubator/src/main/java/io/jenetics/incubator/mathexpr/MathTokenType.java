package io.jenetics.incubator.mathexpr;

import io.jenetics.incubator.parser.Token;

public enum MathTokenType implements Token.Type {
	LPAREN(1),
	RPAREN(2),
	COMMA(3),
	PLUS(4),
	MINUS(5),
	TIMES(6),
	DIV(7),
	POW(8),
	NUMBER(9),
	OP(11),
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
