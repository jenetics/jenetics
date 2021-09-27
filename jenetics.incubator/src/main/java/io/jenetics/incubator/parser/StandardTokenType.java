package io.jenetics.incubator.parser;

public enum StandardTokenType implements Token.Type {

	EOF(-1);

	private final int _code;

	StandardTokenType(final int code) {
		_code = code;
	}

	@Override
	public int code() {
		return _code;
	}

}
