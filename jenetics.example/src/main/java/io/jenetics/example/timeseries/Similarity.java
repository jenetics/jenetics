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

/**
 * This function calculates the error between the expected function values
 * and the values calculated by the actual {@link io.jenetics.prog.ProgramGene}.
 */
@FunctionalInterface
public interface Similarity {

	/**
	 * The <a href="https://en.wikipedia.org/wiki/Mean_squared_error">
	 *     Mean Square Error</a> of
	 */
	public static final Similarity MSE = Errors::mse;

	/**
	 * Calculates the error between the expected function values and the
	 * values calculated by the actual {@link io.jenetics.prog.ProgramGene}.
	 *
	 * @param expected the expected function values
	 * @param calculated the currently calculated function value
	 * @return the error value
	 */
	double apply(final double[] expected, final double[] calculated);

}
