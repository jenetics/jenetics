package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

public class ResponseException extends RuntimeException {

	private final Response.Failure<?> failure;

	public ResponseException(final Response.Failure<?> failure) {
		this.failure = requireNonNull(failure);
	}

	public ResponseException(final String message, final Response.Failure<?> failure) {
		super(message);
		this.failure = requireNonNull(failure);
	}

	public Response.Failure<?> failure() {
		return failure;
	}

}
