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
package io.jenetics.example.timeseries;

import static java.util.Collections.unmodifiableList;

import java.util.AbstractList;
import java.util.List;

/**
 * This class contains a snapshot of the current samples in the buffer.
 */
public final class Samples extends AbstractList<Sample> {
	private final List<Sample> _samples;

	private final double[][] _arguments;
	private final double[] _results;

	Samples(final List<Sample> samples) {
		_samples = unmodifiableList(samples);

		_arguments = samples.stream()
			.map(Sample::arguments)
			.toArray(double[][]::new);
		_results = samples.stream()
			.mapToDouble(Sample::result)
			.toArray();
	}

	double[][] arguments() {
		return _arguments;
	}

	double[] results() {
		return _results;
	}

	@Override
	public Sample get(int index) {
		return _samples.get(index);
	}

	@Override
	public int size() {
		return _samples.size();
	}
}
