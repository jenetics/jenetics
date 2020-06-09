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
package io.jenetics.prog.regression;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

// https://blog.algorithmia.com/introduction-to-loss-functions/
// https://towardsdatascience.com/common-loss-functions-in-machine-learning-46af0ffc4d23

/**
 * This function evaluates how well an evolved program tree fits the given
 * sample data set. If the predictions are totally off, the loss function will
 * output a higher value. If they’re pretty good, it’ll output a lower number.
 * It is the essential part of the <em>overall</em> {@link Error} function.
 *
 * <pre>{@code
 * final Error<Double> error = Error.of(LossFunction::mse);
 * }</pre>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Loss_function">Loss function</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
@FunctionalInterface
public interface LossFunction<T> {

	/**
	 * Calculates the error between the expected function values and the
	 * values calculated by the actual {@link io.jenetics.prog.ProgramGene}.
	 *
	 * @param calculated the currently calculated function value
	 * @param expected the expected function values
	 * @return the error value
	 * @throws IllegalArgumentException if the length of the two arrays are not
	 *         equal
	 * @throws NullPointerException if one of the {@code double[]} arrays is
	 *         {@code null}
	 */
	double apply(final T[] calculated, final T[] expected);

	/**
	 * Mean square error is measured as the average of squared difference
	 * between predictions and actual observations.
	 *
	 * @see #rmse(Double[], Double[])
	 *
	 * @param calculated the function values calculated with the current program
	 *        tree
	 * @param expected the expected function value as given by the sample points
	 * @return the mean square error
	 * @throws IllegalArgumentException if the length of the two arrays are not
	 *         equal
	 * @throws NullPointerException if one of the {@code double[]} arrays is
	 *         {@code null}
	 */
	static double mse(final Double[] calculated, final Double[] expected) {
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
	 * Root mean square error is measured as the average of squared difference
	 * between predictions and actual observations.
	 *
	 * @see #mse(Double[], Double[])
	 *
	 * @param calculated the function values calculated with the current program
	 *        tree
	 * @param expected the expected function value as given by the sample points
	 * @return the mean square error
	 * @throws IllegalArgumentException if the length of the two arrays are not
	 *         equal
	 * @throws NullPointerException if one of the {@code double[]} arrays is
	 *         {@code null}
	 */
	static double rmse(final Double[] calculated, final Double[] expected) {
		return sqrt(mse(calculated, expected));
	}

	/**
	 * Mean absolute error is measured as the average of sum of absolute
	 * differences between predictions and actual observations.
	 *
	 * @param calculated the function values calculated with the current program
	 *        tree
	 * @param expected the expected function value as given by the sample points
	 * @return the mean absolute error
	 * @throws IllegalArgumentException if the length of the two arrays are not
	 *         equal
	 * @throws NullPointerException if one of the {@code double[]} arrays is
	 *         {@code null}
	 */
	static double mae(final Double[] calculated, final Double[] expected) {
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
