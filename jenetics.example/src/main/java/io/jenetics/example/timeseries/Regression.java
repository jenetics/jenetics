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

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.function.Supplier;

import io.jenetics.prog.ProgramGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Regression {

	private final Supplier<Samples> _samples;
	private final Error _error;
	private final Complexity _complexity;

	private Regression(
		final Supplier<Samples> samples,
		final Error error,
		final Complexity complexity
	) {
		_samples = requireNonNull(samples);
		_error = requireNonNull(error);
		_complexity = requireNonNull(complexity);
	}

	/**
	 * Calculates the actual error for the given {@code program}.
	 *
	 * @param program the program to calculate the error value for
	 * @return the overall error value of the program, including its complexity
	 *         penalty
	 */
	public double error(final ProgramGene<Double> program) {
		final Samples samples = _samples.get();
		assert samples != null;

		final double[] calculated = Arrays.stream(samples.arguments())
			.mapToDouble(args -> eval(program, args))
			.toArray();

		final double err = _error.apply(samples.results(), calculated);
		final double cpx = _complexity.apply(program, err);

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


	public static Regression of(
		final Error error,
		final Complexity complexity,
		final Sample... samples
	) {
		final Samples s = new Samples(Arrays.asList(samples));
		return new Regression(() -> s, error, complexity);
	}

	public static Regression of(
		final Error error,
		final Complexity complexity,
		final SampleBuffer buffer
	) {
		return new Regression(buffer::samples, error, complexity);
	}

}
