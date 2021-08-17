/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.tool.measurement;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Evaluator implements Runnable {

	private final Measurement _measurement;
	private final Meter _meter;

	private final Sampling _sampling;

	public Evaluator(
		final Measurement measurement,
		final Meter meter,
		final Sampling sampling
	) {
		_measurement = requireNonNull(measurement);
		_meter = requireNonNull(meter);
		_sampling = requireNonNull(sampling);
	}

	public List<ParameterSample> samples() {
		return _sampling.samples();
	}

	@Override
	public void run() {
		Parameter parameter;
		while (!Thread.currentThread().isInterrupted() &&
			(parameter = _sampling.next()) != null)
		{
			calculate(parameter);
		}
	}

	private void calculate(final Parameter parameter) {
		final Object[] args = parameter.values().toArray();
		final Number[] result = _meter.measure(args);
		final Sample sample = new Sample(List.of(result));

		_sampling.add(parameter, sample);
	}

}
