package io.jenetics.tool.measurement;

import static java.util.Objects.requireNonNull;

import java.util.List;

public class Parameter {
	private final String _value;
	private final List<Sample> _samples;

	public Parameter(final String value, final List<Sample> samples) {
		_value = requireNonNull(value);
		_samples = List.copyOf(samples);
	}

	public String value() {
		return _value;
	}

	public List<Sample> samples() {
		return _samples;
	}

}
