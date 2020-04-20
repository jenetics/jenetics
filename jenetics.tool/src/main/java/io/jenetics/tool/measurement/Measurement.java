package io.jenetics.tool.measurement;

import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

public class Measurement {
	private final String _name;
	private final ZonedDateTime _createdAt;

	public Measurement(final String name, final ZonedDateTime createdAt) {
		_name = requireNonNull(name);
		_createdAt = requireNonNull(createdAt);
	}

}
