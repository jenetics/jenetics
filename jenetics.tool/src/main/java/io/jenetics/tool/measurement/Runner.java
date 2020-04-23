package io.jenetics.tool.measurement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Runner {

	private final Measurement _measurement;
	private final Meter _meter;

	private final Map<Parameter, List<ParameterSample>> _samples = new HashMap<>();
	private final Comparator<Parameter> _comparator;

	public Runner(
		final Measurement measurement,
		final Meter meter,
		final List<ParameterSample> samples
	) {
		_measurement = requireNonNull(measurement);
		_meter = requireNonNull(meter);

		samples.forEach(this::put);

		final Map<Parameter, Integer> order = new HashMap<>();
		for (int i = 0; i < _measurement.parameters().size(); ++i) {
			order.put(_measurement.parameters().get(i), i);
		}

		_comparator = Comparator
			.comparingInt((Parameter a) -> _samples.get(a).size())
			.thenComparingInt(order::get);
	}

	private void putNewSample(final Parameter parameter, final Sample sample) {
		_samples.putIfAbsent(parameter, new ArrayList<>());
		_samples.get(parameter).add(new ParameterSample(parameter, sample));
	}

	private void put(final ParameterSample sample) {
		_samples.putIfAbsent(sample.parameter(), new ArrayList<>());
		_samples.get(sample.parameter()).add(sample);
	}


	public List<ParameterSample> samples() {
		return null; //Collections.unmodifiableList(_samples);
	}

	public void run() {

	}

	private Parameter next() {
		final Parameter param = _measurement.parameters().stream()
			.min(_comparator)
			.orElseThrow();

		return _samples.get(param).size() < _measurement.sampleCount()
			? param
			: null;
	}

	private void calculate(final Parameter parameter) {
		final Object[] args = parameter.values().toArray();
		final Number[] result = _meter.measure(args);
		final Sample sample = new Sample(List.of(result));

		putNewSample(parameter, sample);
	}

}
