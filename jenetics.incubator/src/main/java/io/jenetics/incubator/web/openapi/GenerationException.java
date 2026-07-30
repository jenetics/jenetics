package io.jenetics.incubator.web.openapi;

public class GenerationException extends RuntimeException {

	public GenerationException(Throwable cause) {
		super(cause);
	}

	public GenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationException(String message) {
		super(message);
	}
}
