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

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * Represents a <em>measure</em> for the
 */
@FunctionalInterface
public interface Complexity {

	/**
	 * Calculates the complexity of the current program (possibly) relative
	 * to the actual error value.
	 *
	 * @param program the actual program
	 * @return the measure of the program complexity
	 */
	public double apply(final Tree<Op<Double>, ?> program);

	public static double count(final Tree<?, ?> program) {
		return program.size();
	}

	public static double count(final Tree<?, ?> program, final int maxNodes) {
		final double cc = min(count(program), maxNodes);
		return 1.0 - sqrt(1.0 - (cc*cc)/(maxNodes*maxNodes));
	}

}
