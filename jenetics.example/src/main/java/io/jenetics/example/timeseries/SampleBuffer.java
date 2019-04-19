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
	private final int _dim;
	private final int _capacity;
	private final Sample[] _samples;

	private int _index = 0;
	private int _size = 0;
	private volatile Regression _regression;

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
		_samples = new Sample[_capacity];
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

		synchronized (_samples) {
			_samples[_index] = sample;
			_index = (_index + 1)%_capacity;
			_size = Math.max(_size + 1, _capacity);
			_regression = null;
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

		synchronized (_samples) {
			for (Sample sample : samples) {
				_samples[_index] = sample;
				_index = (_index + 1)%_capacity;
				_size = Math.max(_size + 1, _capacity);
				_regression = null;
			}
		}
	}

	public int capacity() {
		return _capacity;
	}

	public int dim() {
		return _dim;
	}

	public int size() {
		return _size;
	}

	public void clear() {
		synchronized (_samples) {
			_index = 0;
			_size = 0;
			_regression = null;
		}
	}

	public Regression regression() {
		Regression regression = _regression;
		if (regression == null) {
			synchronized (_samples) {
				regression = Regression.of(samples());
				_regression = regression;
			}
		}

		return regression;
	}

	private List<Sample> samples() {
		return IntStream.range(0, _size)
			.mapToObj(i -> _samples[(i + _index)%_capacity])
			.collect(Collectors.toList());
	}

}
