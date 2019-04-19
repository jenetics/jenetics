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
import java.util.List;

import io.jenetics.prog.ProgramGene;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class Regression {

	// Helper class for holding a sample points snapshot.
	private static final class Samples {
		private final double[][] arguments;
		private final double[] results;
		private Samples(final double[][] arguments, final double[] results) {
			this.arguments = arguments;
			this.results = results;
		}
	}

	private final Samples _samples;

	private Regression(final Samples samples) {
		_samples = requireNonNull(samples);
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
		final double[] calculated = Arrays.stream(_samples.arguments)
			.mapToDouble(args -> eval(program, args))
			.toArray();

		final double err = error.apply(_samples.results, calculated);
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


	public static Regression of(final List<Sample> samples) {
		final double[][] arguments = samples.stream()
			.map(Sample::arguments)
			.toArray(double[][]::new);

		final double[] results = samples.stream()
			.mapToDouble(Sample::result)
			.toArray();

		return new Regression(new Samples(arguments, results));
	}
}
