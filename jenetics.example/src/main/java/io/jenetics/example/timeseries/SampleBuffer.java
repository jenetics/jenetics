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

import java.util.Arrays;
import java.util.List;

import io.jenetics.prog.regression.Sample;

/**
 * This class holds the actual sample values which are used for the symbolic
 * regression example. This class is <em>thread-safe</em> and can be used in a
 * <em>producer-consumer</em> setup.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SampleBuffer {
	private final int _dim;
	private final Sample[] _buffer;

	private int _index = 0;
	private int _size = 0;


	/**
	 * Create a new sample object with the given dimension of the function
	 * arguments and the maximal sample size.
	 *
	 * @param dim the function arity
	 * @param capacity the maximal sample buffer capacity
	 */
	public SampleBuffer(final int dim, final int capacity) {
		_dim = dim;
		_buffer = new Sample[capacity];
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
			_index = (_index + 1)%_buffer.length;
			_size = Math.max(_size + 1, _buffer.length);
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
				_index = (_index + 1)%_buffer.length;
				_size = Math.max(_size + 1, _buffer.length);
			}
		}
	}

	/**
	 * Return the capacity of the sample buffer.
	 *
	 * @return the capacity of the sample buffer
	 */
	public int capacity() {
		return _buffer.length;
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
		}
	}

	/**
	 * Return the current samples of this buffer.
	 *
	 * @apiNote
	 * This method is thread-safe.
	 *
	 * @return the current samples of this buffer
	 */
	public List<Sample> samples() {
		return null;//snapshot();
	}

	/*
	Samples snapshot() {
		Samples samples = _samples;
		if (samples == null) {
			synchronized (_buffer) {
				samples = _samples;
				if (samples == null) {
					final Sample[] temp = new Sample[_size];
					final int start = (_buffer.length + _index - _size)%_buffer.length;
					for (int i = 0; i < _size; ++i) {
						temp[i] = _buffer[(i + start)%_buffer.length];
					}
					samples = new Samples(temp);
					_samples = samples;
				}
			}
		}

		return samples;
	}
	 */


}
