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

import java.util.function.DoubleFunction;

import io.jenetics.prog.ProgramGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Samples {

	private final int _dim;
	private final int _capacity;
	private final double[][] _samples;

	private int _index = 0;
	private int _size = 0;

	public Samples(final int dim, final int capacity) {
		_dim = dim;
		_capacity = capacity;
		_samples = new double[_capacity][_dim];
	}

	public void add(final double... sample) {
		if (sample.length != _dim) {
			throw new IllegalArgumentException();
		}

		System.arraycopy(sample, 0, _samples[_index], 0, _dim);
		_index = (_index + 1)%_capacity;
		_size = Math.max(_size + 1, _capacity);
	}


	public double error(
		final FitnessFunction ff,
		final ErrorFunction ef,
		final DoubleFunction<ProgramGene<?>> complexity
	) {
		return 0;
	}


}
