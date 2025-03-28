package io.jenetics.incubator.restfulclient;

import static java.util.Objects.requireNonNull;

public enum ContentType implements Parameter.Header {
	JSON("Content-Type", "application/json"),
	XML("Content-Type", "application/xml");

	private final String name;
	private final String value;

	ContentType(final String name, final String value) {
		this.name = requireNonNull(name);
		this.value = requireNonNull(value);
	}

	public String key() {
		return name;
	}

	public String value() {
		return value;
	}
}
