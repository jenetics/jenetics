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

import io.jenetics.prog.ProgramGene;

/**
 * Represents a (relative) measure of the program complexity. The complexity
 * is added to the error value for the overall error metric.
 */
@FunctionalInterface
public interface Complexity {

	/**
	 * Calculates the complexity of the current program (possibly) relative
	 * to the actual error value.
	 *
	 * @param program the actual program
	 * @param error the error value calculated with the given program
	 * @return the measure of the program complexity
	 */
	double apply(final ProgramGene<Double> program, final double error);

}
