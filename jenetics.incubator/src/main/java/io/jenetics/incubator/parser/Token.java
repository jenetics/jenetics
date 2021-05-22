package io.jenetics.incubator.parser;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public final class Token {
	private final int _type;
	private final String _value;

	public Token(final int type, final String value) {
		_type = type;
		_value = requireNonNull(value);
	}

	public int type() {
		return _type;
	}

	public String value() {
		return _value;
	}

	@Override
	public String toString() {
		return format("Token[%d, '%s']", _type, _value);
	}

}
