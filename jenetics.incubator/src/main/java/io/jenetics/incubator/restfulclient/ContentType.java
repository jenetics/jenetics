package io.jenetics.incubator.restfulclient;

import java.net.http.HttpHeaders;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public enum ContentType implements Parameter.Header {
	JSON("application/json"),
	XML("application/xml"),
	JSON_PROBLEM_DETAILS("application/problem+json"),
	XML_PROBLEM_DETAILS("application/problem+json");

	private static final String CONTENT_TYPE_KEY = "Content-Type";

	private final String value;

	ContentType(final String value) {
		this.value = requireNonNull(value);
	}

	public String key() {
		return CONTENT_TYPE_KEY;
	}

	public String value() {
		return value;
	}

	static Optional<ContentType> of(final String name) {
		return Stream.of(values())
			.filter(ct -> ct.key().equals(name))
			.findFirst();
	}

	static Optional<ContentType> of(final HttpHeaders headers) {
		return headers.firstValue(CONTENT_TYPE_KEY)
			.flatMap(ContentType::of);
	}

}
