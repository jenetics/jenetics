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

import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 * This function calculates the error between the expected function values
 * and the values calculated by the actual {@link io.jenetics.prog.ProgramGene}.
 */
@FunctionalInterface
public interface LossFunction {

	/**
	 * Calculates the error between the expected function values and the
	 * values calculated by the actual {@link io.jenetics.prog.ProgramGene}.
	 *
	 * @param calculated the currently calculated function value
	 * @param expected the expected function values
	 * @return the error value
	 */
	double apply(final double[] calculated, final double[] expected);

	/**
	 * Mean square error is measured as the average of squared difference
	 * between predictions and actual observations.
	 *
	 * https://towardsdatascience.com/common-loss-functions-in-machine-learning-46af0ffc4d23
	 *
	 * @param calculated the function values calculated with the current program
	 *        tree
	 * @param expected the expected function value as given by the sample points
	 * @return the mean square error
	 */
	public static double mse(final double[] calculated, final double[] expected) {
		if (expected.length != calculated.length) {
			throw new IllegalArgumentException(format(
				"Expected result and calculated results have different " +
					"length: %d != %d",
				expected.length, calculated.length
			));
		}

		double result = 0;
		for (int i = 0; i < expected.length; ++i) {
			result += (expected[i] - calculated[i])*(expected[i] - calculated[i]);
		}
		if (expected.length > 0) {
			result = result/expected.length;
		}

		return result;
	}

	/**
	 * Mean absolute error is measured as the average of sum of absolute
	 * differences between predictions and actual observations.
	 *
	 * https://towardsdatascience.com/common-loss-functions-in-machine-learning-46af0ffc4d23
	 *
	 * @param calculated the function values calculated with the current program
	 *        tree
	 * @param expected the expected function value as given by the sample points
	 * @return the mean absolute error
	 */
	public static double mae(final double[] calculated, final double[] expected) {
		if (expected.length != calculated.length) {
			throw new IllegalArgumentException(format(
				"Expected result and calculated results have different " +
					"length: %d != %d",
				expected.length, calculated.length
			));
		}

		double result = 0;
		for (int i = 0; i < expected.length; ++i) {
			result += abs(expected[i] - calculated[i]);
		}
		if (expected.length > 0) {
			result = result/expected.length;
		}

		return result;
	}

}
