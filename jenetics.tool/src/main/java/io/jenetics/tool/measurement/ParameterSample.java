package io.jenetics.tool.measurement;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class ParameterSample {

	private final Parameter _parameter;
	private final Sample _sample;

	public ParameterSample(final Parameter parameter, final Sample sample) {
		_parameter = requireNonNull(parameter);
		_sample = requireNonNull(sample);
	}

	public Parameter parameter() {
		return _parameter;
	}

	public Sample sample() {
		return _sample;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_parameter, _sample);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof ParameterSample &&
			Objects.equals(_parameter, ((ParameterSample)obj)._parameter) &&
			Objects.equals(_sample, ((ParameterSample)obj)._sample);
	}

}
