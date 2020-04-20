package io.jenetics.tool.measurement;

import static java.util.Objects.requireNonNull;

public class Sample {
	private final String _value;

	public Sample(final String value) {
		_value = requireNonNull(value);
	}

	public String value() {
		return _value;
	}
}
