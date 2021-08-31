package io.jenetics.incubator.parser;

import static java.util.Objects.requireNonNull;

public final record Token(int type, String value) {
	public Token {
		requireNonNull(value);
	}
}
