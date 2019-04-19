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
import java.util.stream.IntStream;

import io.jenetics.prog.ProgramGene;

/**
 * This class holds the actual sample values which are used for the symbolic
 * regression example.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class SampleBuffer {
	// Helper class for holding a sample points snapshot.
	private final class Samples {
		private final double[][] arguments;
		private final double[] results;
		private Samples(final double[][] arguments, final double[] results) {
			this.arguments = arguments;
			this.results = results;
		}
	}

	private final int _dim;
	private final int _capacity;
	private final Sample[] _samples;

	private int _index = 0;
	private int _size = 0;
	private volatile Samples _snapshot;

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
			_snapshot = null;
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
				_snapshot = null;
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
			_snapshot = null;
		}
	}

	private Samples snapshot() {
		Samples samples = _snapshot;
		if (samples == null) {
			synchronized (_samples) {
				samples = new Samples(arguments(), results());
				_snapshot = samples;
			}
		}

		return samples;
	}

	private double[][] arguments() {
		return IntStream.range(0, _size)
			.mapToObj(i -> _samples[(i + _index)%_capacity].arguments())
			.toArray(double[][]::new);
	}

	private double[] results() {
		final double[] results = new double[_size];
		for (int i = 0; i < _size; ++i) {
			results[i] = _samples[(i + _index)%_capacity].result();
		}
		return results;
	}

	/**
	 * Calculates the actual error for the given {@code program}.
	 *
	 * @param program the program to calculate the error value for
	 * @param error the error function
	 * @param complexity the program complexity metric
	 * @return the overall error value of the program, including its complexity
	 *         penalty
	 */
	public double error(
		final ProgramGene<Double> program,
		final Error error,
		final Complexity complexity
	) {
		final Samples sample = snapshot();

		final double[] calculated = Arrays.stream(sample.arguments)
			.mapToDouble(args -> eval(program, args))
			.toArray();

		final double err = error.apply(sample.results, calculated);
		final double cpx = complexity.apply(program, err);

		return err + cpx;
	}

	private static double
	eval(final ProgramGene<Double> program, final double[] args) {
		final Double[] value = new Double[args.length];
		for (int i = 0; i < args.length; ++i) {
			value[i] = args[i];
		}

		return program.eval(value);
	}

}
