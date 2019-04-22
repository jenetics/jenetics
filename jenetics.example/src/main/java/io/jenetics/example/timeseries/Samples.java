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

import java.util.AbstractList;
import java.util.stream.Stream;

/**
 * This class contains an immutable snapshot of a {@link SampleBuffer}.
 *
 * @see SampleBuffer
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Samples extends AbstractList<Sample> {
	private final Sample[] _samples;

	private final double[][] _arguments;
	private final double[] _results;

	Samples(final Sample[] samples) {
		_samples = samples;

		_arguments = Stream.of(samples)
			.map(Sample::arguments)
			.toArray(double[][]::new);
		_results = Stream.of(samples)
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
		return _samples[index];
	}

	@Override
	public int size() {
		return _samples.length;
	}

}
