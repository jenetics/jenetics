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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class holds the actual sample values which are used for the symbolic
 * regression example.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SampleBuffer {

	/**
	 * This class contains a snapshot of the current samples in the buffer.
	 */
	public static final class Samples extends AbstractList<Sample> {
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


	private final int _dim;
	private final int _capacity;
	private final Sample[] _buffer;

	private int _index = 0;
	private int _size = 0;
	private volatile Samples _samples;

	/**
	 * Create a new sample object with the given dimension of the function
	 * arguments and the maximal sample size.
	 *
	 * @param dim the function arity
	 * @param capacity the maximal sample buffer capacity
	 */
	public SampleBuffer(final int dim, final int capacity) {
		_dim = dim;
		_capacity = capacity;
		_buffer = new Sample[_capacity];
	}

	/**
	 * Add a new sample point to the sample buffer.
	 *
	 * @param sample the sample point to add
	 */
	public void add(final Sample sample) {
		if (sample.arity() != _dim) {
			throw new IllegalArgumentException();
		}

		synchronized (_buffer) {
			_buffer[_index] = sample;
			_index = (_index + 1)%_capacity;
			_size = Math.max(_size + 1, _capacity);
			_samples = null;
		}
	}

	/**
	 * Adds the given sample points to the sample buffer.
	 *
	 * @param samples the sample points to add
	 */
	public void addAll(final List<Sample> samples) {
		if (samples.stream().anyMatch(s -> s.arity() != _dim)) {
			throw new IllegalArgumentException();
		}

		synchronized (_buffer) {
			for (Sample sample : samples) {
				_buffer[_index] = sample;
				_index = (_index + 1)%_capacity;
				_size = Math.max(_size + 1, _capacity);
				_samples = null;
			}
		}
	}

	/**
	 * Return the capacity of the sample buffer.
	 *
	 * @return the capacity of the sample buffer
	 */
	public int capacity() {
		return _capacity;
	}

	/**
	 * Return the dimensionality of the sample points.
	 *
	 * @return the dimensionality of the sample points
	 */
	public int dim() {
		return _dim;
	}

	/**
	 * Return the size (number of sample points) the buffer actually contains.
	 *
	 * @return the number of sample points the buffer contains
	 */
	public int size() {
		return _size;
	}

	/**
	 * Removes all sample points from this buffer.
	 */
	public void clear() {
		synchronized (_buffer) {
			Arrays.fill(_buffer, null);
			_index = 0;
			_size = 0;
			_samples = null;
		}
	}

	public Samples samples() {
		Samples samples = _samples;
		if (samples == null) {
			synchronized (_buffer) {
				samples = new Samples(
					IntStream.range(0, _size)
						.mapToObj(i -> _buffer[(i + _index)%_capacity])
						.collect(Collectors.toList())
				);
				_samples = samples;
			}
		}

		return samples;
	}

}
