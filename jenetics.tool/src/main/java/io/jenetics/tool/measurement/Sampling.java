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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Sampling {

	private final List<Parameter> _parameters;
	private final int _sampleCount;

	private final Map<Parameter, Integer> _sampleCounts = new HashMap<>();
	private final List<ParameterSample> _samples = new ArrayList<>();

	private final Comparator<Parameter> _comparator;

	Sampling(
		final List<Parameter> parameters,
		final Map<Parameter, Integer> sampleCounts,
		final int sampleCount
	) {
		_parameters = List.copyOf(parameters);
		_sampleCount = sampleCount;
		_sampleCounts.putAll(sampleCounts);

		final Map<Parameter, Integer> order = new HashMap<>();
		for (int i = 0; i < parameters.size(); ++i) {
			order.put(parameters.get(i), i);
		}

		_comparator = Comparator
			.comparingInt((ToIntFunction<Parameter>)_sampleCounts::get)
			.thenComparingInt(order::get);
	}

	Parameter next() {
		final Parameter param = _parameters.stream()
			.min(_comparator)
			.orElseThrow();

		return _sampleCounts.get(param) < _sampleCount ? param : null;
	}

	void add(final Parameter parameter, final Sample sample) {
		_samples.add(new ParameterSample(parameter, sample));
		_sampleCounts.putIfAbsent(parameter, 0);
		_sampleCounts.put(parameter, _sampleCounts.get(parameter) + 1);
	}

	List<ParameterSample> samples() {
		return List.copyOf(_samples);
	}

	static Sampling of(
		final List<Parameter> parameters,
		final List<ParameterSample> existing,
		final int sampleCount
	) {
		final Map<Parameter, Integer> sampleCounts = new HashMap<>();
		for (var sample : existing) {
			final var parameter = sample.parameter();
			sampleCounts.putIfAbsent(parameter, 0);
			sampleCounts.put(parameter, sampleCounts.get(parameter) + 1);
		}

		return new Sampling(parameters, sampleCounts, sampleCount);
	}

}
