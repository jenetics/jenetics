package io.jenetics.tool.measurement;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Function;

public class Runner {

	private final List<Object[]> _params;
	private final Function<Object[], Number[]> _meter;

	public Runner(
		final List<Object[]> params,
		final Function<Object[], Number[]> meter
	) {
		_params = requireNonNull(params);
		_meter = requireNonNull(meter);
	}

}
