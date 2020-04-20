package io.jenetics.tool.measurement;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Measurement {
	private final String _name;
	private final ZonedDateTime _createdAt;
	private final List<Parameter> _parameters;

	public Measurement(
		final String name,
		final ZonedDateTime createdAt,
		final List<Parameter> parameters
	) {
		_name = requireNonNull(name);
		_createdAt = requireNonNull(createdAt);
		_parameters = List.copyOf(parameters);
	}

	public String name() {
		return _name;
	}

	public ZonedDateTime createdAt() {
		return _createdAt;
	}

	public List<Parameter> parameters() {
		return _parameters;
	}

}
