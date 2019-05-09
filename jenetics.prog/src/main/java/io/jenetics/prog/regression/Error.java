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

import static java.util.Objects.requireNonNull;

import io.jenetics.ext.util.Tree;

import io.jenetics.prog.op.Op;

/**
 * This function calculates the <em>overall</em> error of a given program tree.
 * The error is calculated from the {@link LossFunction} and, if desired, the
 * program {@link Complexity}.
 *
 * <pre>{@code
 * final Error error = Error.of(LossFunction::mse, Complexity.ofMaxNodeCount(50));
 * }</pre>
 *
 * @see LossFunction
 * @see Complexity
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Error {

	/**
	 * Calculates the <em>overall</em> error of a given program tree. The error
	 * is calculated from the {@link LossFunction} and, if desired, the program
	 * {@link Complexity}.
	 *
	 * @param program the program tree which calculated the {@code calculated}
	 *        values
	 * @param calculated the calculated function values
	 * @param expected the expected function values
	 * @return the overall program error
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public double apply(
		final Tree<Op<Double>, ?> program,
		final double[] calculated,
		final double[] expected
	);


	/**
	 * Creates an error function which only uses the given {@code loss} function
	 * for calculating the program error
	 *
	 * @param loss the loss function to use for calculating the program error
	 * @return an error function which uses the loss function for error
	 *         calculation
	 * @throws NullPointerException if the given {@code loss} function is
	 *         {@code null}
	 */
	public static Error of(final LossFunction loss) {
		requireNonNull(loss);

		return (p, c, e) -> loss.apply(c, e);
	}

	/**
	 * Creates an error function by combining the given {@code loss} function
	 * and program {@code complexity}. The loss function and program complexity
	 * is combined in the following way: {@code error = loss + loss*complexity}.
	 * The complexity function penalizes programs which grows to big.
	 *
	 * @param loss the loss function
 	 * @param complexity the program complexity measure
	 * @return a new error function by combining the given loss and complexity
	 *         function
	 * @throws NullPointerException if one of the functions is {@code null}
	 */
	public static Error of(final LossFunction loss, final Complexity complexity) {
		requireNonNull(loss);
		requireNonNull(complexity);

		return (p, c, e) -> {
			final double lss = loss.apply(c, e);
			final double cpx = complexity.apply(p);
			return lss + lss*cpx;
		};
	}

}
