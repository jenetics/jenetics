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

import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * Represents a complexity <em>measure</em> if a given program tree. The
 * program complexity ensures, that simpler programs with similar loss function
 * values are preferred. It is part of the <em>overall</em> {@link Error}
 * function.
 *
 * <pre>{@code
 * final Error<Double> error = Error.of(LossFunction::mse, Complexity.ofMaxNodeCount(50));
 * }</pre>
 *
 * @see LossFunction
 * @see Error
 *
 * @param <T> the sample type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 5.0
 * @since 5.0
 */
@FunctionalInterface
public interface Complexity<T> {

	/**
	 * Calculates the complexity of the current program (possibly) relative
	 * to the actual error value.
	 *
	 * @param program the actual program
	 * @return the measure of the program complexity
	 */
	double apply(final Tree<? extends Op<T>, ?> program);

	/**
	 * Return a complexity measure which counts the number of nodes of a program.
	 *
	 * @see #count(Tree, int)
	 *
	 * @param <T> the sample type
	 * @param maxNodeCount the maximal node count. The returned complexity will
	 *        be one of the program node count is greater or equal the given
	 *        {@code count}
	 * @return a program node count complexity measure
	 * @throws IllegalArgumentException if the max node {@code count} is smaller
	 *         than one
	 */
	static <T> Complexity<T> ofNodeCount(final int maxNodeCount) {
		if (maxNodeCount < 1) {
			throw new IllegalArgumentException(
				"Max node count must be greater than zero: " + maxNodeCount
			);
		}

		return p -> count(p, maxNodeCount);
	}

	/**
	 * This method uses the node count of a program tree for calculating its
	 * complexity. The returned node count <em>measure</em> is within the range
	 * of {@code [0, 1]}. If the program contains only one node, zero is returned.
	 * If the node count is bigger or equal {@code maxNodes}, one is returned.
	 * <p>
	 * The complexity is calculated in the following way:
	 * <pre>{@code
	 * final double cc = min(program.size() - 1, maxNodes);
	 * return 1.0 - sqrt(1.0 - (cc*cc)/(maxNodes*maxNodes));
	 * }</pre>
	 *
	 * @see #ofNodeCount(int)
	 *
	 * @param program the program used for the complexity measure
	 * @param maxNodes the maximal expected node count
	 * @return the complexity measure of the given {@code program}
	 * @throws NullPointerException if the given {@code program} is {@code null}
	 * @throws IllegalArgumentException if {@code maxNodes} is smaller than one
	 */
	static double count(final Tree<?, ?> program, final int maxNodes) {
		if (maxNodes < 1) {
			throw new IllegalArgumentException(
				"Max node count must be greater than zero: " + maxNodes
			);
		}

		final double cc = min(program.size() - 1, maxNodes);
		return 1.0 - sqrt(1.0 - (cc*cc)/(maxNodes*maxNodes));
	}

}
