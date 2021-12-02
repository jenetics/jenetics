package io.jenetics.incubator.parser;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class TokenRing {
	private final Token[] _tokens;

	private int _pos = 0;

	TokenRing(final int k) {
		_tokens = new Token[k];
	}

	void add(final Token token) {
		_tokens[_pos] = token;
		_pos = (_pos + 1)%_tokens.length;
	}

	public Token LT(final int i) {
		return _tokens[(_pos + i - 1)%_tokens.length];
	}

	public int LA(final int i) {
		return LT(i).type().code();
	}

	@Override
	public String toString() {
		return IntStream.rangeClosed(1, _tokens.length)
			.mapToObj(i -> i + ":'" + LT(i).value() + "'")
			.collect(Collectors.joining(", ", "[", "]"));
	}

}
