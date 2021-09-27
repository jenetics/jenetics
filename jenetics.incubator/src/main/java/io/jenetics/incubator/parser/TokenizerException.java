package io.jenetics.incubator.parser;

import java.io.Serial;

public class TokenizerException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1;

	public TokenizerException(final String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
